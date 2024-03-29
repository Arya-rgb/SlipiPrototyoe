package com.slipi.slipiprototype.camera

import android.Manifest
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.media.Image
import android.media.ImageReader
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.util.TypedValue
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.slipi.slipiprototype.R
import com.slipi.slipiprototype.databinding.ActivityCameraBinding
import com.slipi.slipiprototype.ml.detectormachine.Detector
import com.slipi.slipiprototype.ml.detectormachine.TFLiteObjectDetectionAPIModel
import com.slipi.slipiprototype.ml.drawing.BorderedText
import com.slipi.slipiprototype.ml.drawing.MultiBoxTracker
import com.slipi.slipiprototype.ml.drawing.OverlayView
import com.slipi.slipiprototype.ml.livefeed.CameraConnectionFragment
import com.slipi.slipiprototype.ml.livefeed.ImageUtils.convertYUV420ToARGB8888
import com.slipi.slipiprototype.ml.livefeed.ImageUtils.getTransformationMatrix
import com.slipi.slipiprototype.ml.livefeed.JavaImageUtils
import com.slipi.slipiprototype.result.ResultActivity
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


open class CameraActivity : AppCompatActivity(), ImageReader.OnImageAvailableListener {

    private var _binding: ActivityCameraBinding? = null
    private val binding get() = _binding!!

    var resultTV: TextView? = null
    private var detector: Detector? = null
    private var frameToCropTransform: Matrix? = null
    private var cropToFrameTransform: Matrix? = null
    var minimumConfidence: Float = 0.5f

    // Configuration values for the prepackaged SSD model.
    private val MAINTAIN_ASPECT = false
    private val TEXT_SIZE_DIP = 10f

    var trackingOverlay: OverlayView? = null
    private var borderedText: BorderedText? = null

    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraExecutor = Executors.newSingleThreadExecutor()

        //TODO show live camera footage
        //TODO ask for permission of camera upon first launch of application
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
            ) {
                val permission = arrayOf(
                    Manifest.permission.CAMERA
                )
                requestPermissions(permission, 1122)
            } else {
                //TODO show live camera footage
                setFragment()
            }
        } else {
            //TODO show live camera footage
            setFragment()
        }
        resultTV = findViewById(R.id.textView)


        //TODO inialize object detector
        try {
            detector = TFLiteObjectDetectionAPIModel.create(
                this,
                "tflite_slipiv1.0.tflite",
                "labelmap.txt",
                384,
                true
            )
            Log.d("tryLog", "success")
        } catch (e: IOException) {
            Log.d("tryException", "error in town" + e.message)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //TODO show live camera footage
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //TODO show live camera footage
            setFragment()
        } else {
            finish()
        }
    }

    //TODO fragment which show llive footage from camera
    var previewHeight = 0
    var previewWidth = 0
    private var sensorOrientation = 0
    private fun setFragment() {
        val manager =
            getSystemService(Context.CAMERA_SERVICE) as CameraManager
        var cameraId: String? = null
        try {
            cameraId = manager.cameraIdList[0]
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
        val fragment: Fragment
        val camera2Fragment = CameraConnectionFragment.newInstance(
            object :
                CameraConnectionFragment.ConnectionCallback {
                override fun onPreviewSizeChosen(size: Size?, cameraRotation: Int) {
                    previewHeight = size!!.height
                    previewWidth = size.width
                    val textSizePx = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        TEXT_SIZE_DIP,
                        resources.displayMetrics
                    )
                    borderedText = BorderedText(textSizePx)
                    borderedText!!.setTypeface(Typeface.MONOSPACE)
                    tracker = MultiBoxTracker(this@CameraActivity)

                    val cropSize = 300
                    previewWidth = size.width
                    previewHeight = size.height
                    sensorOrientation = cameraRotation - getScreenOrientation()
                    croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Bitmap.Config.ARGB_8888)
                    bitmap2 = Bitmap.createBitmap(cropSize, cropSize, Bitmap.Config.ARGB_8888)

                    frameToCropTransform = getTransformationMatrix(
                        previewWidth, previewHeight,
                        cropSize, cropSize,
                        sensorOrientation, MAINTAIN_ASPECT
                    )
                    cropToFrameTransform = Matrix()
                    frameToCropTransform!!.invert(cropToFrameTransform)

                    trackingOverlay =
                        findViewById<View>(R.id.tracking_overlay) as OverlayView
                    trackingOverlay!!.addCallback(
                        object : OverlayView.DrawCallback {
                            override fun drawCallback(canvas: Canvas?) {
                                tracker!!.draw(canvas!!)
                            }
                        })
                    tracker!!.setFrameConfiguration(previewWidth, previewHeight, sensorOrientation)

                }
            },
            this,
            R.layout.camera_fragment,
            Size(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        )
        camera2Fragment.setCamera(cameraId)
        fragment = camera2Fragment
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }


    //TODO getting frames of live camera footage and passing them to model
    private var isProcessingFrame = false
    private val yuvBytes = arrayOfNulls<ByteArray>(3)
    private var rgbBytes: IntArray? = null
    private var yRowStride = 0
    private var postInferenceCallback: Runnable? = null
    private var imageConverter: Runnable? = null
    private var rgbFrameBitmap: Bitmap? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onImageAvailable(reader: ImageReader) {
        // We need wait until we have some size from onPreviewSizeChosen
        if (previewWidth == 0 || previewHeight == 0) {
            return
        }
        if (rgbBytes == null) {
            rgbBytes = IntArray(previewWidth * previewHeight)
        }
        try {
            val image = reader.acquireLatestImage() ?: return
            if (isProcessingFrame) {
                image.close()
                return
            }
            isProcessingFrame = true
            val planes = image.planes
            fillBytes(planes, yuvBytes)
            yRowStride = planes[0].rowStride
            val uvRowStride = planes[1].rowStride
            val uvPixelStride = planes[1].pixelStride
            imageConverter = Runnable {
                convertYUV420ToARGB8888(
                    yuvBytes[0]!!,
                    yuvBytes[1]!!,
                    yuvBytes[2]!!,
                    previewWidth,
                    previewHeight,
                    yRowStride,
                    uvRowStride,
                    uvPixelStride,
                    rgbBytes!!
                )
            }
            postInferenceCallback = Runnable {
                image.close()
                isProcessingFrame = false
            }
            processImage()
        } catch (e: Exception) {
            Log.d("tryError", e.message + "abc ")
            return
        }
    }

    var croppedBitmap: Bitmap? = null
    var bitmap2: Bitmap? = null
    private var tracker: MultiBoxTracker? = null

    @RequiresApi(Build.VERSION_CODES.N)
    fun processImage() {

        Thread {
            imageConverter!!.run()
            rgbFrameBitmap =
                Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888)
            rgbFrameBitmap!!.setPixels(rgbBytes, 0, previewWidth, 0, 0, previewWidth, previewHeight)

            val canvas = Canvas(croppedBitmap!!)
            canvas.drawBitmap(rgbFrameBitmap!!, frameToCropTransform!!, null)

            //TODO pass image to model and get results
            val results = detector!!.recognizeImage(croppedBitmap)
            results.removeIf { t -> t.confidence < minimumConfidence }
            for (result in results) {
                val location: RectF = result.location
                cropToFrameTransform!!.mapRect(location)
                result.location = location

                if (result.confidence >= 0.8f) {

                    val bitmapCropped = cropImage(croppedBitmap!!, result.location)

                    val stream = ByteArrayOutputStream()
                    croppedBitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    val byteArray: ByteArray = stream.toByteArray()

                    val intent = Intent(this, ResultActivity::class.java)
                    intent.putExtra("picture", byteArray)
                    startActivity(intent)

                }

            }
            tracker?.trackResults(results, 5)
            trackingOverlay?.postInvalidate()
            postInferenceCallback!!.run()
        }.start()


    }

    fun cropImage(originalBitmap: Bitmap, rectF: RectF): Bitmap {
        // Calculate the actual crop rectangle based on the RectF values
        val actualRect = Rect(rectF.left.toInt(), rectF.top.toInt(), rectF.right.toInt(), rectF.bottom.toInt())

        // Create a new bitmap with the cropped image
        val croppedBitmap = Bitmap.createBitmap(actualRect.width(), actualRect.height(), Bitmap.Config.ARGB_8888)

        // Create a canvas object with the cropped bitmap
        val canvas = Canvas(croppedBitmap)

        // Draw the original bitmap onto the canvas with the crop rectangle
        canvas.drawBitmap(originalBitmap, actualRect, Rect(0, 0, actualRect.width(), actualRect.height()), null)

        // Return the cropped bitmap
        return croppedBitmap
    }

    private fun fillBytes(
        planes: Array<Image.Plane>,
        yuvBytes: Array<ByteArray?>,
    ) {
        // Because of the variable row stride it's not possible to know in
        // advance the actual necessary dimensions of the yuv planes.
        for (i in planes.indices) {
            val buffer = planes[i].buffer
            if (yuvBytes[i] == null) {
                yuvBytes[i] = ByteArray(buffer.capacity())
            }
            buffer[yuvBytes[i]!!]
        }
    }

    protected fun getScreenOrientation(): Int {
        return when (windowManager.defaultDisplay.rotation) {
            Surface.ROTATION_270 -> 270
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_90 -> 90
            else -> 0
        }
    }


}