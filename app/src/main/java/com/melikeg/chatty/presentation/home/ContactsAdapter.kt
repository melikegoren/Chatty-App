package com.melikeg.chatty.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.recyclerview.widget.RecyclerView
import com.melikeg.chatty.common.searchFilter
import com.melikeg.chatty.databinding.ContactItemBinding
import com.melikeg.chatty.domain.model.User

class ContactsAdapter(private val userList: ArrayList<User>, private val onHomeClickListener: OnHomeClickListener) :
    RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {


    private val initialUserList = ArrayList<User>().apply {
        addAll(userList)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder{
        val inflater = LayoutInflater.from(parent.context)
        val binding = ContactItemBinding.inflate(inflater, parent, false)
        return ContactViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val currentUser = userList[position]


        holder.binding.contactUsername.text = currentUser.username

        holder.binding.cvContact.setOnClickListener {
            onHomeClickListener.onCardviewClick(currentUser.email, currentUser.username)
        }

        holder.binding.btnAdd.setOnClickListener{
            onHomeClickListener.onAddButtonClick(currentUser)

        }


    }
    override fun getItemCount(): Int {
        return userList.size
    }

    inner class ContactViewHolder(val binding: ContactItemBinding) : RecyclerView.ViewHolder(binding.root){
    }
    fun getFilter(): Filter {
        return searchFilter(initialUserList,userList, this)
    }
}