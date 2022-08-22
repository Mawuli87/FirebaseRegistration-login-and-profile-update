package com.messieyawo.projectmang.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.messieyawo.projectmang.activities.*
import com.messieyawo.projectmang.models.Board
import com.messieyawo.projectmang.models.User
import com.messieyawo.projectmang.utils.Constants


class FireStoreClass{

private val  mFireStore = FirebaseFirestore.getInstance()


    fun registerUser(activity: SignUpActivity, userInfo: User){
        mFireStore.collection(Constants.USERS)
            .document(getCurentUserId())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccessfully()
            }.addOnFailureListener {
                e->
                Log.e(activity.javaClass.simpleName,"Error writing document")
            }

    }


    fun getBoaerList(activity:MainActivity){
        mFireStore.collection(Constants.BOARDS)
            .whereArrayContains(Constants.ASSIGN_TO,getCurentUserId())
            .get()
            .addOnSuccessListener {
                document ->
                Log.i("TAG",document.documents.toString())
                val boardList :ArrayList<Board> = ArrayList()
                for (i in document.documents){
                    val board = i.toObject(Board::class.java)!!
                        board.documentID = i.id
                    boardList.add(board)
                }
                activity.populateBoardListToUI(boardList)
            }.addOnFailureListener {
              activity.HideProgressDialog()
              Log.e(activity.javaClass.simpleName,"Error while loading data")
            }
    }

    fun updateUserProfileData(activity: MyProfileActivity,
                             userHashMap: HashMap<String,Any>){
        mFireStore.collection(Constants.USERS)
                 .document(getCurentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName,"Profile data updated successfully")
                activity.profileUpdateSuccess()
            }.addOnFailureListener{
                e->
                activity.HideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error updating profile.",e)
            }
       // Toast.makeText(activity,"Error when updating the profile ",Toast.LENGTH_LONG).show()

    }

    fun createBoard(activity: CreateBoardActivity,board: Board){
         mFireStore.collection(Constants.BOARDS)
             .document()
             .set(board, SetOptions.merge())
             .addOnSuccessListener {
                 Log.e(activity.javaClass.simpleName,"Board created successfully")
                 Toast.makeText(activity,"Board created successfully",Toast.LENGTH_LONG).show()
                 activity.boardCreatedSuccessfully()
             }.addOnFailureListener {
                 exception ->
                 activity.HideProgressDialog()
                 Log.e(activity.javaClass.simpleName,"Error creating the board",exception)
                 Toast.makeText(activity,"Board failed to be created",Toast.LENGTH_LONG).show()
             }
    }


    fun loadUserData(activity: Activity){
        mFireStore.collection(Constants.USERS)
            .document(getCurentUserId())
            .get()
            .addOnSuccessListener {document ->
                val loggedInUser = document.toObject(User::class.java)
                  if (loggedInUser != null){
                      when(activity){
                          is SignInActivity -> {
                              activity.signInSuccess(loggedInUser)
                          }
                          is MainActivity -> {
                              activity.updateNavigationUserDetails(loggedInUser)
                          }
                          is MyProfileActivity -> {
                              activity.setUserDataInUI(loggedInUser)
                          }
                      }

                  }

            }.addOnFailureListener {
                    e->

                when(activity){
                    is SignInActivity -> {
                        activity.HideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.HideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName,"Error writing document")
            }
    }

    fun getCurentUserId():String {
        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""

        if (currentUser != null){
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

}