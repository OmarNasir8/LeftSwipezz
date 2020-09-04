package com.omarn.upswipezz.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.omarn.upswipezz.R
import com.omarn.upswipezz.fragments.MatchesFragment
import com.omarn.upswipezz.fragments.ProfileFragment
import com.omarn.upswipezz.fragments.SwipeFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.io.IOException

const val request_code = 1234

class MainActivity : AppCompatActivity(), LeftSwipezCallBack {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userId = firebaseAuth.currentUser?.uid
    private lateinit var userDatabase: DatabaseReference
    private lateinit var chatDatabase: DatabaseReference

    private var profileFragment: ProfileFragment? = null
    private var swipeFragment: SwipeFragment? = null
    private var matchesFragment: MatchesFragment? = null

    private var profileTab: TabLayout.Tab? = null
    private var swipeTab: TabLayout.Tab? = null
    private var matchesTab: TabLayout.Tab? = null

    private var resultImageUrl: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(userId.isNullOrEmpty()) {
            onSignOut()
        }

        userDatabase = FirebaseDatabase.getInstance().reference.child("Users")
        chatDatabase = FirebaseDatabase.getInstance().reference.child("Chats")

        profileTab = navigationTabs.newTab()
        swipeTab = navigationTabs.newTab()
        matchesTab = navigationTabs.newTab()

        profileTab?.icon = ContextCompat.getDrawable(this, R.drawable.tab_file)
        swipeTab?.icon = ContextCompat.getDrawable(this, R.drawable.tab_swipe)
        matchesTab?.icon = ContextCompat.getDrawable(this, R.drawable.tab_matches)

        navigationTabs.addTab(profileTab!!)
        navigationTabs.addTab(swipeTab!!)
        navigationTabs.addTab(matchesTab!!)



        navigationTabs.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                onTabSelected(tab)
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab) {
                    profileTab -> {
                        if(profileFragment == null) {
                            profileFragment = ProfileFragment()
                            profileFragment!!.setCallBack(this@MainActivity)
                        }
                        replaceFragments(profileFragment!!)
                    }
                    swipeTab -> {
                        if(swipeFragment == null) {
                            swipeFragment = SwipeFragment()
                            swipeFragment!!.setCallBack(this@MainActivity)
                        }
                        replaceFragments(swipeFragment!!)
                    }
                    matchesTab -> {
                        if(matchesFragment == null) {
                            matchesFragment = MatchesFragment()
                            matchesFragment!!.setCallBack(this@MainActivity)
                        }
                        replaceFragments(matchesFragment!!)
                    }
                }
            }

        })

        profileTab?.select()

        }

        private fun replaceFragments(fragment: Fragment) {
            val fragmentManager = supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, fragment)
            transaction.commit()
    }

    companion object{
        fun newIntent(context: Context?) = Intent(context, MainActivity::class.java)
    }

    override fun onSignOut() {
        firebaseAuth.signOut()
        startActivity(StartupActivity.newIntent(this))
        finish()
    }

    override fun onGetUserId(): String = userId!!

    override fun getUserDatabase(): DatabaseReference = userDatabase

    override fun getChatDatabase(): DatabaseReference = chatDatabase

    override fun profileComplete() {
       swipeTab?.select()
    }

    override fun startActivityForPhoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, request_code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK && requestCode == request_code) {
            resultImageUrl = data?.data
            storeImage()
        }
    }

    fun storeImage() {
        if(resultImageUrl != null && userId != null) {
            val filePath = FirebaseStorage.getInstance().reference.child("profileImage").child(userId)
            var bitmap: Bitmap? = null
            try {
                bitmap = MediaStore.Images.Media.getBitmap(application.contentResolver, resultImageUrl)
            }catch(e: IOException) {
                e.printStackTrace()
            }

            val baos = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 20, baos)
            val data = baos.toByteArray()

            val uploadTask = filePath.putBytes(data)
            uploadTask.addOnFailureListener{e -> e.printStackTrace()}
            uploadTask.addOnSuccessListener { taskSnapshot ->
                filePath.downloadUrl.addOnSuccessListener { uri ->
                    profileFragment?.updateImageUri(uri.toString())
                }
                    .addOnFailureListener { e -> e.printStackTrace() }
            }
        }

    }
}



