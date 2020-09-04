package com.omarn.upswipezz.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

import com.omarn.upswipezz.R
import com.omarn.upswipezz.activities.LeftSwipezCallBack
import com.omarn.upswipezz.adapters.ChatsAdapter
import com.omarn.upswipezz.util.Chat
import com.omarn.upswipezz.util.User
import kotlinx.android.synthetic.main.fragment_matches.*

class MatchesFragment : Fragment() {

    private lateinit var userId: String
    private lateinit var userDatabase: DatabaseReference
    private lateinit var chatsDatabase: DatabaseReference

    private var callBack: LeftSwipezCallBack? = null

    private val chatsAdapter = ChatsAdapter(ArrayList())

    fun setCallBack(callback: LeftSwipezCallBack) {
        this.callBack = callback
        userId = callback.onGetUserId()
        userDatabase = callback.getUserDatabase()
        chatsDatabase = callback.getChatDatabase()

        fetchData()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_matches, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        matchesRV.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(context)
            adapter = chatsAdapter
        }
    }


    private fun fetchData() {
        userDatabase.child(userId).child("matches").addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                if(p0.hasChildren()){
                    p0.children.forEach { child ->
                        val matchId = child.key
                        val chatId = child.value.toString()
                        if(!matchId.isNullOrEmpty()) {
                            userDatabase.child(matchId).addListenerForSingleValueEvent(object : ValueEventListener {

                                override fun onCancelled(p0: DatabaseError) {

                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    val user = p0.getValue(User::class.java)
                                    if(user != null) {
                                        val chat = Chat(userId, chatId, user.uid, user.name, user.imageURL)
                                        chatsAdapter.addElement(chat)
                                    }
                                }

                            })
                        }
                    }
                }
            }

        })
    }

}
