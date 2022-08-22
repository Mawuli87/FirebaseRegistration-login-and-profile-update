package com.messieyawo.projectmang.activities


import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.messieyawo.projectmang.R
import com.messieyawo.projectmang.firebase.FireStoreClass
import com.messieyawo.projectmang.models.User


class SignUpActivity : BaseActivity() {
     private lateinit var toolbar_sign_up_activity:androidx.appcompat.widget.Toolbar

     private lateinit var et_name: AppCompatEditText
     private lateinit var et_email :AppCompatEditText
     private lateinit var et_password : AppCompatEditText
     private lateinit var btn_sign_up : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        toolbar_sign_up_activity = findViewById(R.id.toolbar_sign_up_activity)

        et_name = findViewById(R.id.et_name)
        et_email = findViewById(R.id.et_email)
        et_password = findViewById(R.id.et_password)
        btn_sign_up = findViewById(R.id.register)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setUpActionBar()

        btn_sign_up.setOnClickListener{
            registerUser()
        }
    }


    fun userRegisteredSuccessfully(){
        Toast.makeText(this@SignUpActivity,"You have registered successfully registered",
            Toast.LENGTH_LONG).show()
         HideProgressDialog()
        FirebaseAuth.getInstance().signOut()
        finish()
    }

    private fun setUpActionBar(){
         setSupportActionBar(toolbar_sign_up_activity)
        val actioBar = supportActionBar
        if (actioBar != null){
            actioBar.setDisplayHomeAsUpEnabled(true)
            actioBar.setHomeAsUpIndicator(R.drawable.ic_back_home)
        }

        toolbar_sign_up_activity.setNavigationOnClickListener { 
            onBackPressed()
        }
    }


    private fun registerUser(){
        val name : String = et_name.text.toString().trim()
        val email : String = et_email.text.toString().trim()
        val password : String = et_password.text.toString().trim()

        if (validateForm(name,email,password)){
           // Toast.makeText(this@SignUpActivity,"Now we can register a new user",
             //   Toast.LENGTH_LONG).show()
            showProgressDialog(resources.getString(R.string.please_wait))

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    taskId ->
                   // HideProgressDialog()
                    if (taskId.isSuccessful){
                        val firebaseUser : FirebaseUser = taskId.result!!.user!!
                        val registerEmail = firebaseUser.email!!
                        val user = User(firebaseUser.uid,name,registerEmail)
                        FireStoreClass().registerUser(this,user)
                    }else {
                         Toast.makeText(this@SignUpActivity,taskId.exception!!.message,
                           Toast.LENGTH_LONG).show()
                    }
                }
        }
    }


    private fun validateForm(name:String,email:String,password:String):Boolean{
        return when {
            TextUtils.isEmpty(name) ->{
                showErrorSnackBar(this,"Please enter your name")
                false
            }
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar(this,"Please enter your email")
                false
            }
            TextUtils.isEmpty(password) ->{
                showErrorSnackBar(this,"Please enter a password")
                false
            }else -> {
                true
            }

        }
    }
}