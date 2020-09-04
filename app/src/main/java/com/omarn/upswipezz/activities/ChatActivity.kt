package com.omarn.upswipezz.activities


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.omarn.upswipezz.R
import com.omarn.upswipezz.adapters.MessagesAdapter
import com.omarn.upswipezz.util.Message
import com.omarn.upswipezz.util.User
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*
import kotlin.collections.ArrayList


class ChatActivity : AppCompatActivity() {

    private var chatId: String? = null
    private var userId: String? = null
    private var imageURL: String? = null
    private var otherUserId: String? = null

    private lateinit var chatDatabase: DatabaseReference
    private lateinit var messagesAdapter: MessagesAdapter

    private val chatMessageListner = object : ChildEventListener {
        override fun onCancelled(p0: DatabaseError) {

        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {

        }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) {

        }

        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
            val message = p0.getValue(Message::class.java)
            if(message != null) {
                messagesAdapter.addMessage(message)
                messagesRV.post {
                    messagesRV.smoothScrollToPosition(messagesAdapter.itemCount - 1)
                }
            }
        }

        override fun onChildRemoved(p0: DataSnapshot) {

        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chatId = intent.extras?.getString(PARAM_CHAT_ID)
        userId = intent.extras?.getString(PARAM_USER_ID)
        imageURL = intent.extras?.getString(PARAM_IMAGEURL)
        otherUserId = intent.extras?.getString(PARAM_OTHER_USER_ID)

        if(chatId.isNullOrEmpty() || userId.isNullOrEmpty() || imageURL.isNullOrEmpty() || otherUserId.isNullOrEmpty()) {
            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show()
            finish()
        }

        chatDatabase = FirebaseDatabase.getInstance().reference.child("Chats")
        messagesAdapter = MessagesAdapter(ArrayList(), userId!!)

        messagesRV.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(context)
            adapter = messagesAdapter
        }

        chatDatabase.child(chatId!!).child("Messages").addChildEventListener(chatMessageListner)

        chatDatabase.child(chatId!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach { value ->
                    val key = value.key
                    val user = value.getValue(User::class.java)
                    if(!key.equals(userId)) {
                        topNameTv.text = user?.name
                        Glide.with(this@ChatActivity)
                            .load(user?.imageURL)
                            .into(topPhotoIV)
                    }
                }

            }

        })

    }




    fun onSend(v: View) {
        val message = Message(userId, messageInput.text.toString(), Calendar.getInstance().time.toString())
        val key = chatDatabase.child(chatId!!).child("Messages").push().key
        if(!key.isNullOrEmpty()) {
            chatDatabase.child(chatId!!).child("Messages").child(key).setValue(message)
        }

        messageInput.setText("", TextView.BufferType.EDITABLE)
    }

    companion object {

        private val PARAM_CHAT_ID = "Chat id"
        private val PARAM_USER_ID = "User id"
        private val PARAM_IMAGEURL = "Image url"
        private val PARAM_OTHER_USER_ID = "Other user id"

        fun newIntent(context: Context, chatId: String?, userId: String?, imageURL: String?, otherUserId: String?): Intent {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(PARAM_CHAT_ID, chatId)
            intent.putExtra(PARAM_USER_ID, userId)
            intent.putExtra(PARAM_IMAGEURL, imageURL)
            intent.putExtra(PARAM_OTHER_USER_ID, otherUserId)
            return intent
        }
    }
}
