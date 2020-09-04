package com.omarn.upswipezz.activities

import com.google.firebase.database.DatabaseReference

interface LeftSwipezCallBack {

    fun onSignOut()
    fun onGetUserId(): String
    fun getUserDatabase(): DatabaseReference
    fun getChatDatabase(): DatabaseReference
    fun profileComplete()
    fun startActivityForPhoto()
}