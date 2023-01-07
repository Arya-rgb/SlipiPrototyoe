package com.slipi.slipiprototype.auth.register

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.rxbinding2.widget.RxTextView
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import com.slipi.slipiprototype.R
import com.slipi.slipiprototype.auth.login.LoginActivity
import com.slipi.slipiprototype.databinding.ActivityRegisterBinding
import com.slipi.slipiprototype.home.HomeActivity
import io.reactivex.Observable
import kotlin.math.floor

class RegisterActivity : AppCompatActivity() {

    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!

    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonRegister.isEnabled = false
        binding.buttonRegister.setBackgroundColor(
            ContextCompat.getColor(
                this,
                android.R.color.darker_gray
            )
        )

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

        binding.buttonRegister.setOnClickListener {
            showBottomSheetChooseRole()
        }

        binding.signInNav.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        }

        reactiveValidation()

    }

    private fun showBottomSheetChooseRole() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottomsheet_choose_role)
        val admin: TextView = dialog.findViewById(R.id.choose_admin_client)
        val user: TextView = dialog.findViewById(R.id.choose_user)
        val approval : TextView = dialog.findViewById(R.id.choose_approval)
        val close: ImageView = dialog.findViewById(R.id.close_bottom_sheet)
        admin.setOnClickListener {
            dialog.dismiss()
            loginUser("admin")
        }
        user.setOnClickListener {
            dialog.dismiss()
            loginUser("user")
        }
        approval.setOnClickListener {
            dialog.dismiss()
            loginUser("approval")
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

    private fun loginUser(role: String) {

        binding.progressBarRegister.visibility = View.VISIBLE

        firebaseAuth.createUserWithEmailAndPassword(
            binding.edtEmail.text.toString().trim(),
            binding.edtEmail.text.toString().trim()
        )
            .addOnSuccessListener {
                binding.progressBarRegister.visibility = View.GONE
                uploadDataToFireStore(role)
            }
            .addOnFailureListener { error ->
                DynamicToast.makeError(this@RegisterActivity, error.message).show()
                binding.progressBarRegister.visibility = View.GONE

            }

    }

    private fun uploadDataToFireStore(role: String) {
        DynamicToast.makeSuccess(this@RegisterActivity, "Success").show();
        //if success move to login activity
        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
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
                binding.buttonRegister.isEnabled = true
                binding.buttonRegister.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.button_color
                    )
                )
            } else {
                binding.buttonRegister.isEnabled = false
                binding.buttonRegister.setBackgroundColor(
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