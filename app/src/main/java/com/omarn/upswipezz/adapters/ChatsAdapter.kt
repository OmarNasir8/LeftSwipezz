package com.omarn.upswipezz.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.omarn.upswipezz.R
import com.omarn.upswipezz.activities.ChatActivity
import com.omarn.upswipezz.util.Chat
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class ChatsAdapter(private var chats: ArrayList<Chat>): RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder>() {

    class ChatsViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        private var layout = view.findViewById<View>(R.id.chatLayout)
        private var image = view.findViewById<ImageView>(R.id.chatPictureIV)
        private var name = view.findViewById<TextView>(R.id.chatNameTV)

            fun bind(chat: Chat) {
                name.text = chat.name
                if(image != null) {
                    Glide.with(view)
                        .load(chat.imageURL)
                        .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation(128,0,RoundedCornersTransformation.CornerType.ALL)))
                        .into(image)
                }

                layout.setOnClickListener {
                  val intent =  ChatActivity.newIntent(view.context, chat.chatId, chat.userId, chat.imageURL, chat.otherUserId)
                    view.context.startActivity(intent)
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
    = ChatsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false))



    override fun getItemCount() = chats.size



    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {

        holder.bind(chats[position])

    }

    fun addElement(chat: Chat) {
        chats.add(chat)
        notifyDataSetChanged()
    }

}