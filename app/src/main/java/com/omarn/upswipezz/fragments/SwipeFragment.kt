package com.omarn.upswipezz.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.lorentzos.flingswipe.SwipeFlingAdapterView

import com.omarn.upswipezz.R
import com.omarn.upswipezz.activities.LeftSwipezCallBack
import com.omarn.upswipezz.adapters.CardsAdapter
import com.omarn.upswipezz.util.User
import kotlinx.android.synthetic.main.fragment_swipe.*


class SwipeFragment : Fragment() {

    private var callBack: LeftSwipezCallBack? = null
    private lateinit var userId: String
    private lateinit var userDatabase: DatabaseReference
    private lateinit var chatDatabase: DatabaseReference
    private var cardsAdapter: ArrayAdapter<User>? = null
    private var rowItems = ArrayList<User>()
    private var preferredGender: String? = null
    private var userName: String? = null
    private var imageURL: String? = null

    fun setCallBack(callback: LeftSwipezCallBack) {
        this.callBack = callback
        userId = callback.onGetUserId()
        userDatabase = callback.getUserDatabase()
        chatDatabase = callback.getChatDatabase()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_swipe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userDatabase.child(userId).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)
                preferredGender = user?.preferredGender
                userName = user?.name
                imageURL = user?.imageURL
                populateItems()
            }

        })



        cardsAdapter = CardsAdapter(context, R.layout.item, rowItems)
        frame.adapter = cardsAdapter
        frame.setFlingListener(object : SwipeFlingAdapterView.onFlingListener{
            override fun removeFirstObjectInAdapter() {

                rowItems.removeAt(0)
                cardsAdapter?.notifyDataSetChanged()

            }


            //his swipe right
            override fun onLeftCardExit(p0: Any?) {

                val selectedUser = p0 as User
                val selectedUserId = selectedUser.uid

                if(!selectedUserId.isNullOrEmpty()){
                    userDatabase.child(userId).child("swipesRight").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if(p0.hasChild(selectedUserId)) {
                                Toast.makeText(context, "Match!", Toast.LENGTH_SHORT).show()

                                val chatKey = chatDatabase.push().key

                                if(chatKey != null) {
                                    userDatabase.child(userId).child("swipesRight").child(selectedUserId).removeValue()
                                    userDatabase.child(userId).child("matches").child(selectedUserId).setValue(chatKey)
                                    userDatabase.child(selectedUserId).child("matches").child(userId).setValue(chatKey)

                                    chatDatabase.child(chatKey).child(userId).child("name").setValue(userName)
                                    chatDatabase.child(chatKey).child(userId).child("imageURL").setValue(imageURL)

                                    chatDatabase.child(chatKey).child(selectedUserId).child("name").setValue(selectedUser.name)
                                    chatDatabase.child(chatKey).child(selectedUserId).child("imageURL").setValue(selectedUser.imageURL)
                                }
                            } else {

                                userDatabase.child(selectedUserId).child("swipesRight").child(userId).setValue(true)

                            }
                        }

                    })
                }

            }


            //his swipe left
            override fun onRightCardExit(p0: Any?) {

                var user = p0 as User
                userDatabase.child(user.uid.toString()).child("swipesLeft").child(userId).setValue(true)

            }

            override fun onAdapterAboutToEmpty(p0: Int) {

            }

            override fun onScroll(p0: Float) {

            }

        })
    }

    fun populateItems() {

        val cardsQuery = userDatabase.orderByChild("gender").equalTo(preferredGender)

        cardsQuery.addListenerForSingleValueEvent(object  : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                noUsersLayout.visibility = View.GONE
                progressLayoutSwipe.visibility = View.VISIBLE
                p0.children.forEach{child ->
                    val user = child.getValue(User::class.java)
                    if(user != null) {
                        var showUser = true
                        if(child.child("swipesLeft").hasChild(userId) || child.child("swipesRight").hasChild(userId) || child.child("matches").hasChild(userId)) {
                            showUser = false
                        }

                        if(showUser) {
                            rowItems.add(user)
                            cardsAdapter?.notifyDataSetChanged()
                        }
                    }
                }
                progressLayoutSwipe.visibility = View.GONE

                if(rowItems.isEmpty()) {
                    noUsersLayout.visibility = View.VISIBLE
                }
            }

        })

    }

}
