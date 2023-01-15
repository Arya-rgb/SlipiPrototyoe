package com.slipi.slipiprototype.auth.splashscreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import com.slipi.slipiprototype.auth.login.LoginActivity
import com.slipi.slipiprototype.core.data.source.local.preference.SharedPrefAuth
import com.slipi.slipiprototype.databinding.ActivityRegisterBinding
import com.slipi.slipiprototype.databinding.ActivitySplashScreenBinding
import com.slipi.slipiprototype.home.HomeActivity

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private var _binding: ActivitySplashScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedprefAuth: SharedPrefAuth
    private lateinit var firebaseAuth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()

        sharedprefAuth = SharedPrefAuth(this@SplashScreenActivity)

        firebaseAuth = FirebaseAuth.getInstance()

        if (sharedprefAuth.getState() && firebaseAuth.currentUser != null) {
            Handler(Looper.getMainLooper()).postDelayed({
                val myIntent = Intent(this, HomeActivity::class.java)
                startActivity(myIntent)
                finish()
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                finish()
            }, 3000)
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                val myIntent = Intent(this, LoginActivity::class.java)
                startActivity(myIntent)
                finish()
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                finish()
            }, 3000)
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

}