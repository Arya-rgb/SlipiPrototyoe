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
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import com.slipi.slipiprototype.R
import com.slipi.slipiprototype.auth.register.RegisterActivity
import com.slipi.slipiprototype.auth.register.RegisterViewModel
import com.slipi.slipiprototype.core.data.Resource
import com.slipi.slipiprototype.core.data.source.local.preference.SharedPrefAuth
import com.slipi.slipiprototype.core.ui.ViewModelFactory
import com.slipi.slipiprototype.home.HomeActivity
import io.reactivex.Observable


class LoginActivity : AppCompatActivity() {

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAuth: FirebaseAuth

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var sharedprefAuth: SharedPrefAuth

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

        sharedprefAuth = SharedPrefAuth(this@LoginActivity)

        binding.signUpNav.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        binding.buttonLogin.setOnClickListener {
            showBottomSheetRole()
        }

        supportActionBar?.hide()
        reactiveValidation()

        mAuth = FirebaseAuth.getInstance()

        //initialize viewmodel
        val factory = ViewModelFactory.getInstance(this)
        loginViewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        binding.edtClient.setOnClickListener {
            showBottomSheetClient()
        }

    }

    private fun showBottomSheetClient() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottom_sheet_client)
        val cluster: TextView = dialog.findViewById(R.id.choose_cluster)
        val parking: TextView = dialog.findViewById(R.id.choose_parking)
        val close: ImageView = dialog.findViewById(R.id.close_bottom_sheet)
        cluster.setOnClickListener {
            binding.edtClient.text = "Cluster"
            dialog.dismiss()
        }
        parking.setOnClickListener {
            binding.edtClient.text = "Parking"
            dialog.dismiss()
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

    private fun showBottomSheetRole() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottomsheet_choose_role)
        val admin: TextView = dialog.findViewById(R.id.choose_admin_client)
        val user: TextView = dialog.findViewById(R.id.choose_user)
        val approval : TextView = dialog.findViewById(R.id.choose_approval)
        val close: ImageView = dialog.findViewById(R.id.close_bottom_sheet)
        admin.setOnClickListener {
            dialog.dismiss()
            loginAndCheckRole("admin")
        }
        user.setOnClickListener {
            dialog.dismiss()
            loginAndCheckRole("user")
        }
        approval.setOnClickListener {
            dialog.dismiss()
            loginAndCheckRole("approval")
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

    private fun loginAndCheckRole(role: String) {

        binding.progressBarLogin.visibility = View.VISIBLE

        mAuth.signInWithEmailAndPassword(
            binding.edtEmail.text.toString().trim(),
            binding.edtPassword.text.toString().trim()
        )
            .addOnSuccessListener {
                binding.progressBarLogin.visibility = View.GONE
                checkRole(role)
            }
            .addOnFailureListener {
                binding.progressBarLogin.visibility = View.GONE
                DynamicToast.makeError(this@LoginActivity, "when login"+it.message).show()
            }

    }

    private fun checkRole(role: String) {

//        loginViewModel.dataUser.observe(this) { dataUser ->
//            if (dataUser != null) {
//                when(dataUser) {
//                    is Resource.Loading -> binding.progressBarLogin.visibility = View.VISIBLE
//                    is Resource.Success -> {
//                        binding.progressBarLogin.visibility = View.GONE
//                        val actualRole = dataUser.data?.role
//                        if (role == actualRole) {
//                            DynamicToast.makeSuccess(this@LoginActivity, "Login Success").show()
//                            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
//                        } else {
//                            DynamicToast.makeError(this@LoginActivity, "Role does'nt Match").show()
//                        }
//                    }
//                    is Resource.Error -> {
//                        binding.progressBarLogin.visibility = View.GONE
//                        DynamicToast.makeError(this@LoginActivity, "pas cekrole"+dataUser.message).show()
//                    }
//                }
//            }
//        }

        loginViewModel.getData()

        loginViewModel.getLiveData.observe(this) {

            if (it.role != role) {
                DynamicToast.makeError(this@LoginActivity, "Role Doesn't Match").show()
            } else if (it.client != binding.edtClient.text.toString().trim()) {
                DynamicToast.makeError(this@LoginActivity, "Client does'nt Match").show()
            } else {
                sharedprefAuth.saveState()
                startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                finish()
            }
        }

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