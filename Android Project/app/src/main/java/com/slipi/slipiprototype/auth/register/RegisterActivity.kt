package com.slipi.slipiprototype.auth.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.slipi.slipiprototype.databinding.ActivityRegisterBinding
import kotlin.math.floor

class RegisterActivity : AppCompatActivity() {

    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!

    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.navBackRegister.setOnClickListener {
            finish()
        }

        binding.autoGeneratePassword.setOnClickListener {
            val chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
            var passWord = ""
            for (i in 0..10) {
                passWord += chars[floor(Math.random() * chars.length).toInt()]
            }
            binding.edtPassword.setText(passWord)
        }

    }
}