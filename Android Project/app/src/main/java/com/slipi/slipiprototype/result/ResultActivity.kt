package com.slipi.slipiprototype.result

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.slipi.slipiprototype.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    private var _binding: ActivityResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        val byteArray = extras!!.getByteArray("picture")

        val bmp = byteArray?.size?.let { BitmapFactory.decodeByteArray(byteArray, 0, it) }

        binding.imageView5.setImageBitmap(bmp)

        if (bmp != null) {
            processImageUsingMLKit(bmp) { recognizedText ->
                binding.textView6.text = recognizedText
            }
        }


    }


    private fun processImageUsingMLKit(bitmap: Bitmap, onResult: (String) -> Unit) {
        val image = InputImage.fromBitmap(bitmap, 0)

        val options = TextRecognizerOptions.Builder()
            .build()
        val recognizer = TextRecognition.getClient(options)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val text = visionText.text
                onResult(text)
            }
            .addOnFailureListener { e ->
                Log.e("processImageUsingMLKit", "Text recognition failed", e)
            }
    }

}