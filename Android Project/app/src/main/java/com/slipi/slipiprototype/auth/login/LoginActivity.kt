package com.slipi.slipiprototype.auth.login

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jakewharton.rxbinding2.widget.RxTextView
import com.slipi.slipiprototype.databinding.ActivityLoginBinding
import android.util.Patterns
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.slipi.slipiprototype.R
import com.slipi.slipiprototype.auth.register.RegisterActivity
import io.reactivex.Observable


class LoginActivity : AppCompatActivity() {

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonLogin.isEnabled = false
        binding.buttonLogin.setBackgroundColor(
            ContextCompat.getColor(
                this,
                android.R.color.darker_gray
            )
        )

        binding.signUpNav.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        binding.buttonLogin.setOnClickListener {
            showBottomSheetRole()
        }

        supportActionBar?.hide()
        reactiveValidation()

    }

    private fun showBottomSheetRole() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottomsheet_choose_role)
        val admin: TextView = dialog.findViewById(R.id.choose_admin_client)
        val user: TextView = dialog.findViewById(R.id.choose_user)
        val approval : TextView = dialog.findViewById(R.id.choose_approval)
        val close: ImageView = dialog.findViewById(R.id.close_bottom_sheet)
        admin.setOnClickListener {
            Toast.makeText(this@LoginActivity, "admin", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
            TODO("login here")
        }
        user.setOnClickListener {
            Toast.makeText(this@LoginActivity, "admin", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
            TODO("login here")
        }
        approval.setOnClickListener {
            Toast.makeText(this@LoginActivity, "approval", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
            TODO("login here")
        }
        close.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
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