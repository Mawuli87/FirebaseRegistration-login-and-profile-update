package com.messieyawo.projectmang.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.ProgressBar


import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar


import com.google.firebase.auth.FirebaseAuth

import com.messieyawo.projectmang.R



open class BaseActivity : AppCompatActivity() {


    private var doubleBackToExitOnce = false
    private lateinit var mProgressDialog: Dialog
    private lateinit var tv_progress_text : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        //mProgressDialog = findViewById(R.id.progressBar)

    }



    fun showProgressDialog(text:String){

        mProgressDialog = Dialog(this)


        /**
         * set the screen content from a layout resource
         * the resource will be inflated, adding all top-level to the screen
         * **/
        mProgressDialog.setContentView(R.layout.dialog_progress)

        tv_progress_text = mProgressDialog.findViewById(R.id.tv_progress_text)
        tv_progress_text.text = text
        //start the dialog and display on the screen
        mProgressDialog.show()

    }


    fun HideProgressDialog(){
        mProgressDialog.dismiss()
    }

    fun getCurrentUserID(): String {
      return FirebaseAuth.getInstance().currentUser!!.uid
    }

fun doubleBackToExit(){
    if (doubleBackToExitOnce){
        super.onBackPressed()
        return
    }
    this.doubleBackToExitOnce = true
    Toast.makeText(this,resources.getString(R.string.please_click_back_again_to_exit),
    Toast.LENGTH_LONG).show()

    val handler = Handler()
    handler.postDelayed({
        doubleBackToExitOnce = false
    },2500)
}


    fun showErrorSnackBar(activity: BaseActivity, message: String){
        val snackBar = Snackbar.make(findViewById(R.id.content),
            message,Snackbar.LENGTH_LONG)
        val snacBarView = snackBar.view
        snacBarView.setBackgroundColor(ContextCompat.getColor(activity,R.color.snackBarErrorColor))
        snackBar.show()
    }



}