package com.omarn.upswipezz.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

import com.omarn.upswipezz.R
import com.omarn.upswipezz.activities.LeftSwipezCallBack
import com.omarn.upswipezz.util.User
import kotlinx.android.synthetic.main.fragment_profile.*


class ProfileFragment : Fragment() {

    private lateinit var userId: String
    private lateinit var userDatabase: DatabaseReference
    private var callBack: LeftSwipezCallBack? = null

    fun setCallBack(callback: LeftSwipezCallBack) {
        this.callBack = callback
        userId = callback.onGetUserId()
        userDatabase = callback.getUserDatabase().child(userId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressLayout.setOnTouchListener {view, event -> true}

        populateInfo()

        photoIV.setOnClickListener{callBack?.startActivityForPhoto()}

        applyBT.setOnClickListener { onApply() }
        signOutBT.setOnClickListener { callBack?.onSignOut() }
    }


    fun populateInfo() {
        progressLayout.visibility = View.VISIBLE

        userDatabase.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                progressLayout.visibility = View.GONE
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(isAdded) {
                    val user = p0.getValue(User::class.java)
                    profileNameET.setText(user?.name, TextView.BufferType.EDITABLE)
                    profileEmailET.setText(user?.email, TextView.BufferType.EDITABLE)
                    profileAgeET.setText(user?.age, TextView.BufferType.EDITABLE)
                    if(user?.gender == "male") {
                        radioMan1.isChecked = true
                    }
                    if(user?.gender == "female"){
                        radioWoman1.isChecked = true
                    }
                    if(user?.preferredGender == "male") {
                        radioMan2.isChecked = true
                    }
                    if(user?.preferredGender == "female") {
                        radioWoman2.isChecked = true
                    }
                    if(!user?.imageURL.isNullOrEmpty()) {
                        populateImage(user?.imageURL!!)
                    }
                    progressLayout.visibility = View.GONE
                }
            }

        })
    }

    fun onApply() {

        if(profileNameET.text.toString().isNullOrEmpty() || profileEmailET.text.toString().isNullOrEmpty() || profileAgeET.text.toString().isNullOrEmpty() ||
                radio1.checkedRadioButtonId == -1 || radio2.checkedRadioButtonId == -1) {
            Toast.makeText(context, "Please complete your profile", Toast.LENGTH_SHORT).show()
        } else {
            val name = profileNameET.text.toString()
            val email = profileEmailET.text.toString()
            val age = profileAgeET.text.toString()
            val gender =
                if(radioMan1.isChecked) "male"
                else "female"
            val preferredGender =
                if(radioMan2.isChecked) "male"
                else "female"

            userDatabase.child("name").setValue(name)
            userDatabase.child("email").setValue(email)
            userDatabase.child("age").setValue(age)
            userDatabase.child("gender").setValue(gender)
            userDatabase.child("preferredGender").setValue(preferredGender)

            callBack?.profileComplete()
        }
    }

    fun updateImageUri(uri: String) {
        userDatabase.child("imageURL").setValue(uri)
        Glide.with(this)
            .load(uri)
            .into(photoIV)
        populateImage(uri)
    }

    fun populateImage(uri: String) {
        Glide.with(this)
            .load(uri)
            .into(photoIV)
    }
}
