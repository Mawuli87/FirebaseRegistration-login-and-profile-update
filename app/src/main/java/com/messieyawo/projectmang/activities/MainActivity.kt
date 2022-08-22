package com.messieyawo.projectmang.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.messieyawo.projectmang.R
import com.messieyawo.projectmang.adapters.BoardItemsAdapter
import com.messieyawo.projectmang.firebase.FireStoreClass
import com.messieyawo.projectmang.models.Board
import com.messieyawo.projectmang.models.User
import com.messieyawo.projectmang.utils.Constants

class MainActivity : BaseActivity(),NavigationView.OnNavigationItemSelectedListener {


    private lateinit var toolbar_main_activity: Toolbar
    private lateinit var drawer_layout: DrawerLayout
    private lateinit var activity_nav_view_main : NavigationView
    private lateinit var profile_image : de.hdodenhof.circleimageview.CircleImageView
    private lateinit var tv_name: TextView
    private lateinit var fab_create_board:FloatingActionButton
    private lateinit var rv_boards_list : RecyclerView
    private lateinit var no_board_available: TextView

    companion object {
        const val MY_PROFILE_REQUEST_CODE: Int = 11
    }

        private lateinit var mUserName:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar_main_activity = findViewById(R.id.toolbar_main_activity)
        drawer_layout = findViewById(R.id.drawer_layout)
        activity_nav_view_main = findViewById(R.id.activity_nav_view_main)
        setUpActionBar()
        activity_nav_view_main.setNavigationItemSelectedListener (this)
        fab_create_board = findViewById(R.id.fab_create_board)
        rv_boards_list = findViewById(R.id.rv_boards_list)
        no_board_available = findViewById(R.id.no_board_available)



        FireStoreClass().loadUserData(this)
        fab_create_board.setOnClickListener{
        val intent = Intent(this,CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME,mUserName)
        startActivity(intent)
        }

    }

    fun populateBoardListToUI(boardList:ArrayList<Board>){
       // HideProgressDialog()
        if (boardList.size > 0){
            rv_boards_list.visibility = View.VISIBLE
            no_board_available.visibility = View.GONE
            rv_boards_list.layoutManager = LinearLayoutManager(this)
            rv_boards_list.setHasFixedSize(true)
            val adapter = BoardItemsAdapter(this,boardList)
            rv_boards_list.adapter = adapter
        }else {
            rv_boards_list.visibility = View.VISIBLE
            no_board_available.visibility = View.GONE
        }

    }

    private fun setUpActionBar() {
        setSupportActionBar(toolbar_main_activity)
        toolbar_main_activity.setNavigationIcon(R.drawable.ic_navigation_icon)

        toolbar_main_activity.setNavigationOnClickListener {
            //toggle drawer
            toggleDrawer()
        }

    }

    private fun toggleDrawer() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        }else{
            drawer_layout.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        }else{
            doubleBackToExit()
        }
    }


    fun updateNavigationUserDetails(loggedInUser: User) {
        mUserName = loggedInUser.name
        profile_image = findViewById(R.id.profile_image_nav)
        tv_name = findViewById(R.id.tv_name)
        Glide
            .with(this)
            .load(loggedInUser.image)
            .centerCrop()
            .placeholder(R.drawable.loading_spinner)
            .into(profile_image);
        tv_name.text = loggedInUser.name

        FireStoreClass().getBoaerList(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
           if (resultCode == Activity.RESULT_OK && requestCode == MY_PROFILE_REQUEST_CODE){
               FireStoreClass().loadUserData(this)
           }else{
               Log.e("Canceled","Canceled")
           }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_profile -> {
                startActivityForResult(Intent(this,MyProfileActivity::class.java),
                    MY_PROFILE_REQUEST_CODE)
//                val intentPro = Intent(this,MyProfileActivity::class.java)
//                startActivity(intentPro)
         // showErrorSnackBar(this,"You clicked on Profile")
               // Toast.makeText(this,"You clicked${item.itemId}",Toast.LENGTH_LONG).show()
            }
            R.id.nav_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this,IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }

        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}