package com.omarn.upswipezz.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.omarn.upswipezz.R
import com.omarn.upswipezz.util.User
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : AppCompatActivity() {

    private val firebaseDatabase = FirebaseDatabase.getInstance().reference
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseAuthListener = FirebaseAuth.AuthStateListener {
        val user = firebaseAuth.currentUser
        if(user != null) {
            startActivity(MainActivity.newIntent(this))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(firebaseAuthListener)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(firebaseAuthListener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
    }

    fun onSignToM(v: View){
        if(!emailSignUp.text.toString().isNullOrEmpty() && !passwordSignUp.text.toString().isNullOrEmpty()) {
            firebaseAuth.createUserWithEmailAndPassword(emailSignUp.text.toString(),passwordSignUp.text.toString())
                .addOnCompleteListener { task ->
                    if(!task.isSuccessful){
                        Toast.makeText(this, "Sign Up Error! ${task.exception?.localizedMessage}",Toast.LENGTH_SHORT).show()
                    } else {
                        val email = emailSignUp.text.toString()
                        val userId = firebaseAuth.currentUser?.uid ?: ""
                        val user = User(userId, "", "", email, "","", "")
                        firebaseDatabase.child("Users").child(userId).setValue(user)
                    }
                }
        }

    }

    companion object{
        fun newIntent(context: Context?) = Intent(context, SignupActivity::class.java)
    }
}
