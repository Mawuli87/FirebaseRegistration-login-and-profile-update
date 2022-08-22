package com.messieyawo.projectmang.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import com.messieyawo.projectmang.R

class IntroActivity : BaseActivity() {

    private lateinit var signUpBtn : Button
    private lateinit var signInBtn : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        signUpBtn = findViewById(R.id.signUpBtn)
        signInBtn = findViewById(R.id.signFromIntro)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        signUpBtn.setOnClickListener {
          startActivity(Intent(this, SignUpActivity::class.java))
        }

        signInBtn.setOnClickListener{
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }


}