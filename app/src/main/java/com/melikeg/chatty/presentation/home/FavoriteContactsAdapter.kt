package com.melikeg.chatty.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.recyclerview.widget.RecyclerView
import com.melikeg.chatty.common.searchFilter
import com.melikeg.chatty.databinding.FavContactItemBinding
import com.melikeg.chatty.domain.model.User

class FavoriteContactsAdapter (private val favUserList: ArrayList<User>, private val onHomeClickListener: OnHomeClickListener) :
RecyclerView.Adapter<FavoriteContactsAdapter.FavoriteContactViewHolder>() {

    private val initialUserList = ArrayList<User>().apply {
        addAll(favUserList)
    }

    inner class FavoriteContactViewHolder(val binding: FavContactItemBinding): RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteContactViewHolder{
        val inflater = LayoutInflater.from(parent.context)
        val binding = FavContactItemBinding.inflate(inflater, parent, false)
        return FavoriteContactViewHolder(binding)
    }

    override fun getItemCount(): Int = favUserList.size


    override fun onBindViewHolder(holder: FavoriteContactViewHolder, position: Int) {
        val currentUser = favUserList[position]
        holder.binding.contactUsername.text = currentUser.username
        holder.binding.cvContact.setOnClickListener {
            onHomeClickListener.onCardviewClick(currentUser.email,currentUser.username)
        }
        holder.binding.btnRemove.setOnClickListener {
            onHomeClickListener.onRemoveButtonClick(currentUser)
        }
    }

    fun getFilter(): Filter {
        return searchFilter(initialUserList,favUserList, this)
    }
}