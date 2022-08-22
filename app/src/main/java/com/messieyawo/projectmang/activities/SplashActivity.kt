package com.messieyawo.projectmang.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import com.messieyawo.projectmang.R
import com.messieyawo.projectmang.firebase.FireStoreClass

class SplashActivity : AppCompatActivity() {
   lateinit var  tv_app_name : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        tv_app_name = findViewById(R.id.tv_app_name)
//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                        WindowManager.LayoutParams.FLAG_FULLSCREEN)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        val typeFace:Typeface = Typeface.createFromAsset(assets,"SplineSans-VariableFont_wght.ttf")
        tv_app_name.typeface = typeFace

        Handler().postDelayed({

                var currentUserID = FireStoreClass().getCurentUserId()

               if (currentUserID.isNotEmpty()){
                   startActivity(Intent(this, MainActivity::class.java))
               }else{
                   startActivity(Intent(this, IntroActivity::class.java))
               }

                finish()

        },2500)
    }
}