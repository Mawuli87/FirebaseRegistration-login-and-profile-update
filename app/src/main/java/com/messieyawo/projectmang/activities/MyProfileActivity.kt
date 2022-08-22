package com.messieyawo.projectmang.activities


import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.messieyawo.projectmang.R
import com.messieyawo.projectmang.firebase.FireStoreClass
import com.messieyawo.projectmang.models.User
import com.messieyawo.projectmang.utils.Constants
import com.messieyawo.projectmang.utils.Constants.showImageChooser
import java.io.IOException



class MyProfileActivity : BaseActivity() {



    private var mSelectedImageUri: Uri? = null
    private var mProfileImageUri: String = ""
    private lateinit var et_profile_update_email : AppCompatEditText
    private lateinit var et_profile_update_phone : AppCompatEditText
    private lateinit var et_update_name : AppCompatEditText
    private lateinit var btn_update_details : Button
    private lateinit var img_profile: de.hdodenhof.circleimageview.CircleImageView
    private lateinit var toolbar_activity_profile:Toolbar
    private lateinit var mUserDetails:User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        toolbar_activity_profile = findViewById(R.id.toolbar_activity_profile)
        et_profile_update_phone = findViewById(R.id.et_phone_number)
        et_profile_update_email = findViewById(R.id.et_update_email)
        et_update_name = findViewById(R.id.et_update_name)
        img_profile = findViewById(R.id.profile_image)
        btn_update_details = findViewById(R.id.btn_update)
        setUpActionBar()

        FireStoreClass().loadUserData(this)

        img_profile.setOnClickListener {
           if(ContextCompat.checkSelfPermission(this,
                   android.Manifest.permission.READ_EXTERNAL_STORAGE)
                     == PackageManager.PERMISSION_GRANTED){
                 //TODO Show image chooser
               showImageChooser(this)

           }else{
               ActivityCompat.requestPermissions(this,
                   arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                   Constants.READ_STORAGE_PERMISSION_CODE)
           }
        }
        btn_update_details.setOnClickListener{
            if (mSelectedImageUri != null){
                uploadUserImage()
            }else{
                showProgressDialog("Please wait...")
                updateUserProfileData()
            }
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
                showImageChooser(this)
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
                    .into(img_profile);
            }catch (e:IOException){
                e.printStackTrace()
            }

        }
    }
    private fun setUpActionBar(){
        setSupportActionBar(toolbar_activity_profile)
        val actionBar = supportActionBar
         if (actionBar != null){
             actionBar.setDisplayHomeAsUpEnabled(true)
             actionBar.setHomeAsUpIndicator(R.drawable.ic_back_home)
             actionBar.title = "My Profile"

         }
        toolbar_activity_profile.setNavigationOnClickListener {
            onBackPressed()
        }
    }


    fun setUserDataInUI(user:User){
        mUserDetails = user
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.loading_spinner)
            .into(img_profile);
        et_update_name.setText(user.name)
        et_profile_update_email.setText(user.email)
        if (user.mobile != 0L){
           et_profile_update_phone.setText(user.mobile.toString())
        }
    }

    private fun updateUserProfileData(){
        val userHashMap = HashMap<String,Any>()
        var anyChangesMade = false
        if (mProfileImageUri.isNotEmpty()
            && mProfileImageUri != mUserDetails.image){
         userHashMap[Constants.IMAGE] = mProfileImageUri
            anyChangesMade = true
        }
        if (et_update_name.text.toString() != mUserDetails.name){
            userHashMap[Constants.NAME] = et_update_name.text.toString()
            anyChangesMade = true
        }

        if (et_profile_update_phone.text.toString() != mUserDetails.mobile.toString()){
            userHashMap[Constants.MOBILE] = et_profile_update_phone.text.toString().toLong()
            anyChangesMade = true
        }
        if (anyChangesMade) {
            FireStoreClass().updateUserProfileData(this, userHashMap)
        }
    }

    private fun uploadUserImage(){
        showProgressDialog("Please wait....")
        if (mSelectedImageUri != null){
            val sRef:StorageReference =
                FirebaseStorage.getInstance()
                    .reference.child("USER_IMAGE"+
                            System.currentTimeMillis()+"."+Constants.getFileExtension(this,mSelectedImageUri))
                   sRef.putFile(mSelectedImageUri!!).addOnSuccessListener {
                       taskSnapshot ->
                       Log.e("Firebase Image URL",
                       taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
                       taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                           uri ->
                           Log.i("Downloadable Image URL",uri.toString())
                           mProfileImageUri = uri.toString()
                           //TODO update user profile image
                           //HideProgressDialog()
                           updateUserProfileData()


                       }
                   }.addOnFailureListener{
                       exception ->
                       Toast.makeText(baseContext, exception.message,
                           Toast.LENGTH_SHORT).show()
                       HideProgressDialog()
                   }
        }
    }


    fun profileUpdateSuccess(){
        HideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
}