package com.omarn.upswipezz.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.omarn.upswipezz.R
import com.omarn.upswipezz.util.User

class CardsAdapter(context: Context?, recourseId: Int, users: List<User>): ArrayAdapter<User>(context!!, recourseId, users) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var user = getItem(position)
        var finalView = convertView ?: LayoutInflater.from(context).inflate(R.layout.item, parent, false)

        var name = finalView.findViewById<TextView>(R.id.nameTV)
        var image = finalView.findViewById<ImageView>(R.id.photoIV)

        name.text = "${user?.name}, ${user?.age}"

        Glide.with(context)
            .load(user?.imageURL)
            .into(image)

        return finalView
    }

}