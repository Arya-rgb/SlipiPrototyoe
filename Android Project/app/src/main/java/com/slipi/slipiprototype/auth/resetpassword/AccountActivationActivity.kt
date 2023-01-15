package com.slipi.slipiprototype.auth.resetpassword

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.slipi.slipiprototype.databinding.ActivityAccountActivationBinding
import com.slipi.slipiprototype.databinding.ActivityRegisterBinding

class AccountActivationActivity : AppCompatActivity() {

    private var _binding: ActivityAccountActivationBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAccountActivationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()



    }
}