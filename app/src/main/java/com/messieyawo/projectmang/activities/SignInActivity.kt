package com.messieyawo.projectmang.activities

import android.R.attr
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.messieyawo.projectmang.R
import android.widget.Toast

import com.google.firebase.auth.FirebaseUser

import com.google.firebase.auth.AuthResult

import androidx.annotation.NonNull

import com.google.android.gms.tasks.OnCompleteListener

import android.R.attr.password
import android.content.ContentValues.TAG
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import androidx.appcompat.widget.AppCompatEditText
import com.messieyawo.projectmang.models.User


class SignInActivity : BaseActivity() {
    private lateinit var toolbar_sign_in_activity:Toolbar
    private lateinit var auth: FirebaseAuth

    private lateinit var et_login_email : AppCompatEditText
    private lateinit var et_login_password : AppCompatEditText
    private lateinit var SignInBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        et_login_email = findViewById(R.id.et_login_email)
        et_login_password = findViewById(R.id.passwordSign)
        SignInBtn = findViewById(R.id.SignInBtn)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        toolbar_sign_in_activity = findViewById(R.id.toolbar_sign_in_activity)

        setUpActionBar()

        SignInBtn.setOnClickListener{
            signInRegisteredUser()
        }
    }

    private fun setUpActionBar(){
        setSupportActionBar(toolbar_sign_in_activity)
        val actioBar = supportActionBar
        if (actioBar != null){
            actioBar.setDisplayHomeAsUpEnabled(true)
            actioBar.setHomeAsUpIndicator(R.drawable.ic_back_home)
        }

        toolbar_sign_in_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }


    fun signInSuccess(user : User){
      HideProgressDialog()
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }



    private fun signInRegisteredUser(){

        val email : String = et_login_email.text.toString().trim()
        val password : String = et_login_password.text.toString().trim()

            if (validateForm(email,password)){
                showProgressDialog(resources.getString(R.string.please_wait))


                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->

                        HideProgressDialog()
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SignIn", "createUserWithEmail:success")
                            val user = auth.currentUser



                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("SignIn", "createUserWithEmail:failure", task.exception)
                            Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                            
                        }
                    }

            }

    }


    private fun validateForm(email:String,password:String):Boolean{
        return when {

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