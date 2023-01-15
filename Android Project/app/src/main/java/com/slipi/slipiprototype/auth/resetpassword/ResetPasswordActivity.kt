package com.slipi.slipiprototype.auth.resetpassword

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import com.slipi.slipiprototype.auth.login.LoginActivity
import com.slipi.slipiprototype.databinding.ActivityAccountActivationBinding
import com.slipi.slipiprototype.databinding.ActivityResetPasswordBinding

class ResetPasswordActivity : AppCompatActivity() {

    private var _binding: ActivityResetPasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.navBackForgotPassword.setOnClickListener {
            finish()
        }

        binding.btnReset.setOnClickListener {
            resetPassword()
        }

        auth = FirebaseAuth.getInstance()


    }

    private fun resetPassword() {
        if (binding.edtEmailForgotPassword.text.isNullOrEmpty()) {
            DynamicToast.makeError(this@ResetPasswordActivity, "Email Must Be Not Empty.").show()
        } else {
            auth.sendPasswordResetEmail(binding.edtEmailForgotPassword.text.toString().trim())
                .addOnCompleteListener {
                    DynamicToast.makeSuccess(this@ResetPasswordActivity, "Email Sent").show()
                    Intent(this@ResetPasswordActivity, LoginActivity::class.java).also {
                        startActivity(it)
                        finish()
                    }
                }
                .addOnFailureListener {
                    DynamicToast.makeError(this@ResetPasswordActivity, it.message).show()
                }
        }
    }
}