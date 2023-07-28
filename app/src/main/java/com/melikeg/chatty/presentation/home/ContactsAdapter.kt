package com.melikeg.chatty.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.melikeg.chatty.databinding.ContactItemBinding
import com.melikeg.chatty.domain.model.User

class UsersAdapter(private val userList: List<User>, private val onHomeClickListener: OnHomeClickListener) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ContactItemBinding.inflate(inflater, parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]


        holder.binding.apply {
            contactUsername.text = user.username
            cvContact.setOnClickListener {
                onHomeClickListener.onCardviewClick(user.email, user.username )
            }

        }
    }


    override fun getItemCount(): Int {
        return userList.size
    }

    class UserViewHolder(val binding: ContactItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}