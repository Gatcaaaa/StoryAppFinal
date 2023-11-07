package com.submisson.aleggappstory.view.login

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.submisson.aleggappstory.R
import com.submisson.aleggappstory.data.Result
import com.submisson.aleggappstory.data.pref.UserModel
import com.submisson.aleggappstory.databinding.ActivityLoginBinding
import com.submisson.aleggappstory.view.ViewModelFactory
import com.submisson.aleggappstory.view.main.MainActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory: ViewModelFactory  = ViewModelFactory.getInstance(this)
        loginViewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        setupLogin()
        setupView()
        setupAction()
    }

    private fun setupLogin() {
        loginViewModel.loginViewModel.observe(this){
            when(it){
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    AlertDialog.Builder(this).apply {
                        setTitle("Yeah!")
                        setMessage("Anda Berhasil Login. Gassskun boscu!!")
                        setPositiveButton("Lanjut Ngab"){_, _ ->
                            val intent = Intent(context, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                        create()
                        show()
                    }
                }
                is Result.Error -> {
                    toastFailed()
                    showLoading(false)
                }
            }
        }
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

    private fun setupAction(){
       binding.loginButton.setOnClickListener {
           binding.apply {
               if (emailEditText.error.isNullOrEmpty() && passwordEditText.error.isNullOrEmpty()){
                   val email = emailEditText.text.toString().trim()
                   val password = passwordEditText.text.toString().trim()
                   loginViewModel.login(email, password)
               }
           }
       }
    }

    private fun showLoading(isLoading: Boolean){
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
    private fun toastFailed(){
        Toast.makeText(
            this,
            R.string.registrasi_gagal,
            Toast.LENGTH_SHORT
        ).show()
    }
}