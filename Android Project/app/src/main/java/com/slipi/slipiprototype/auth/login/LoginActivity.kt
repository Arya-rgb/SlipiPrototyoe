package com.slipi.slipiprototype.auth.login

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jakewharton.rxbinding2.widget.RxTextView
import com.slipi.slipiprototype.databinding.ActivityLoginBinding
import android.util.Patterns
import androidx.core.content.ContextCompat
import com.slipi.slipiprototype.R
import io.reactivex.Observable


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonLogin.isEnabled = false
        binding.buttonLogin.setBackgroundColor(
            ContextCompat.getColor(
                this,
                android.R.color.darker_gray
            )
        )

        supportActionBar?.hide()
        reactiveValidation()

    }

    @SuppressLint("CheckResult")
    private fun reactiveValidation() {

        val emailStream = RxTextView.textChanges(binding.edtEmail)
            .skipInitialValue()
            .map { email ->
                !Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }
        emailStream.subscribe {
            showEmailExistAlert(it)
        }

        val passwordStream = RxTextView.textChanges(binding.edtPassword)
            .skipInitialValue()
            .map { password ->
                password.length < 8
            }
        passwordStream.subscribe {
            showPasswordMinimalAlert(it)
        }

        val invalidFieldStream = Observable.combineLatest(
            emailStream,
            passwordStream
        ) { emailInvalid: Boolean, passwordInvalid: Boolean ->
            !emailInvalid && !passwordInvalid
        }

        invalidFieldStream.subscribe { isValid ->
            if (isValid) {
                binding.buttonLogin.isEnabled = true
                binding.buttonLogin.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.button_color
                    )
                )
            } else {
                binding.buttonLogin.isEnabled = false
                binding.buttonLogin.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        android.R.color.darker_gray
                    )
                )
            }
        }


    }

    private fun showEmailExistAlert(it: Boolean) {
        binding.edtEmail.error = if (it) getString(R.string.email_not_valid) else null
    }

    private fun showPasswordMinimalAlert(isNotValid: Boolean) {
        binding.edtPassword.error = if (isNotValid) getString(R.string.password_not_valid) else null
    }
}