package com.submisson.aleggappstory.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.submisson.aleggappstory.R
import com.submisson.aleggappstory.data.Result
import com.submisson.aleggappstory.databinding.ActivitySignupBinding
import com.submisson.aleggappstory.view.ViewModelFactory
import com.submisson.aleggappstory.view.login.LoginActivity

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var signupViewModel: SignupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        signupViewModel = ViewModelProvider(this, factory)[SignupViewModel::class.java]

        setupLogin()
        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupLogin() {
        signupViewModel.registerViewModel.observe(this){
            when(it){
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    AlertDialog.Builder(this).apply {
                        setTitle("Yeah!")
                        setMessage("Akunmu sudah jadi nih. Yuk gaskun login")
                        setCancelable(false)
                        setPositiveButton("Masuk"){_, _ ->
                            val intent = Intent(context, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        create()
                        show()
                    }
                }
                is Result.Error ->{
                    toastFailed()
                    showLoading(true)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setupView(){
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun  setupAction(){
        binding.signupButton.setOnClickListener {
            binding.apply {
                if (nameEditText.error.isNullOrEmpty() && emailEditText.error.isNullOrEmpty() && passwordEditText.error.isNullOrEmpty()){
                    val name = nameEditText.text.toString().trim()
                    val email = emailEditText.text.toString().trim()
                    val password = passwordEditText.text.toString().trim()
                    signupViewModel.register(name, email, password)
                } else {
                   toastFailed()
                }
            }
        }
    }

    private fun playAnimation(){
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE


            val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
            val name = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
            val nameEdit = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(100)
            val email = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
            val emailEdit = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
            val password = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
            val passwordEdit = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
            val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)

            AnimatorSet().apply {
                playSequentially(
                    title,
                    name,
                    nameEdit,
                    email,
                    emailEdit,
                    password,
                    passwordEdit,
                    signup
                )
                startDelay = 100
            }.start()
        }
    }
    private fun toastFailed(){
        Toast.makeText(
            this,
            R.string.registrasi_gagal,
            Toast.LENGTH_SHORT
        ).show()
    }

}