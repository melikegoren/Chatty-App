package com.melikeg.chatty.common

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.melikeg.chatty.domain.model.User
import java.util.Locale

    fun <T : RecyclerView.Adapter<*>> searchFilter(initialList: ArrayList<User>, currentList: ArrayList<User>, adapter: T): android.widget.Filter {
            val filter = object : android.widget.Filter(){
                override fun performFiltering(constraint: CharSequence?): FilterResults {
                    val filteredList: ArrayList<User> = ArrayList()
                    if(constraint.isNullOrEmpty()){
                        initialList.let {
                            filteredList.addAll(it)
                        }
                    }
                    else{
                        val query = constraint.toString().trim().lowercase()
                        initialList.forEach {
                            if (it.username.lowercase(Locale.ROOT).contains(query)) {
                                filteredList.add(it)
                            }
                        }
                    }
                    val results = FilterResults()
                    results.values = filteredList
                    return results
                }

                @SuppressLint("NotifyDataSetChanged")
                override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                    if(results?.values is ArrayList<*>){
                        currentList.clear()
                        currentList.addAll(results.values as ArrayList<User>)
                        adapter.notifyDataSetChanged()

                    }
                }
            }
            return filter
        }
