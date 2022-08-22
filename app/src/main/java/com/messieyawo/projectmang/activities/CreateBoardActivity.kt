package com.messieyawo.projectmang.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.messieyawo.projectmang.R
import com.messieyawo.projectmang.firebase.FireStoreClass
import com.messieyawo.projectmang.models.Board
import com.messieyawo.projectmang.utils.Constants
import java.io.IOException

class CreateBoardActivity : BaseActivity() {
    private var mSelectedImageUri:Uri? = null
    private lateinit var img_Create_board_profile:ImageView
    private lateinit var tool_create_board_activity:Toolbar
    private lateinit var mUserName :String
    private var mBoardImageUrl : String = ""
    private lateinit var et_name_board:EditText
    private lateinit var btn_create_board:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_board)

        tool_create_board_activity = findViewById(R.id.tool_create_board_activity)
        et_name_board = findViewById(R.id.create_board_editext)
        btn_create_board = findViewById(R.id.btn_create_board)
        setUpActionBar()
        if (intent.hasExtra(Constants.NAME)){
            mUserName = intent.getStringExtra(Constants.NAME).toString()
        }
        img_Create_board_profile = findViewById(R.id.img_Create_board_profile)

        img_Create_board_profile.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
                //TODO Show image chooser
                Constants.showImageChooser(this)

            }else{
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE)
            }
        }

        btn_create_board.setOnClickListener {
            if (mSelectedImageUri != null){
                uploadBoardImage()
            }else{
                //showProgressDialog("Please wait while we create board")
                createBoard()
            }
        }
    }

    fun createBoard(){
        val assignedUsersArrayList:ArrayList<String> = ArrayList()
        assignedUsersArrayList.add(getCurrentUserID())
        var board = Board(
           et_name_board.text.toString(),
            mBoardImageUrl,
            mUserName,
            assignedUsersArrayList
        )
        FireStoreClass().createBoard(this,board)
    }

   private fun uploadBoardImage(){
        //showProgressDialog("Please wait.....")
       val sRef: StorageReference =
           FirebaseStorage.getInstance()
               .reference.child("BOARD_IMAGE"+
                       System.currentTimeMillis()+"."+Constants.getFileExtension(this,mSelectedImageUri))
       sRef.putFile(mSelectedImageUri!!).addOnSuccessListener {
               taskSnapshot ->
           Log.e("Board Image URL",
               taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
           taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                   uri ->
               Log.i("Downloadable Image URL",uri.toString())
               mBoardImageUrl = uri.toString()
               //TODO update user profile image
               //HideProgressDialog()
              createBoard()


           }
       }.addOnFailureListener{
               exception ->
           Toast.makeText(baseContext, exception.message,
               Toast.LENGTH_SHORT).show()
           HideProgressDialog()
       }

    }

    fun boardCreatedSuccessfully(){
        HideProgressDialog()
        finish()
    }

    private fun setUpActionBar(){
        setSupportActionBar(tool_create_board_activity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_home)
            actionBar.title = "Create Board Activity"

        }
        tool_create_board_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //TODO Show image chooser
                Constants.showImageChooser(this)
            }
        }else{
            Toast.makeText(baseContext, "Please you should allow us permission to read external storage.",
                Toast.LENGTH_SHORT).show()
        }
    }


//    p

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data!!.data != null){
            mSelectedImageUri = data.data

            try {
                Glide
                    .with(this)
                    .load(mSelectedImageUri)
                    .centerCrop()
                    .placeholder(R.drawable.loading_spinner)
                    .into(img_Create_board_profile);
            }catch (e: IOException){
                e.printStackTrace()
            }

        }
    }

}