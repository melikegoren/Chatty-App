package com.melikeg.chatty.presentation.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.melikeg.chatty.R
import com.melikeg.chatty.common.Resource
import com.melikeg.chatty.common.setRecyclerView
import com.melikeg.chatty.common.showCustomToast
import com.melikeg.chatty.databinding.FragmentHomeBinding
import com.melikeg.chatty.domain.model.FavUser
import com.melikeg.chatty.domain.model.User
import com.melikeg.chatty.domain.repository.FirebaseRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(), OnHomeClickListener {

    private var _binding: FragmentHomeBinding? = null
    val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var firebaseRepository: FirebaseRepository

    private lateinit var contactsAdapter: ContactsAdapter
    private lateinit var favContactsAdapter: FavoriteContactsAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        signOut()
        observeData()
        viewModel.fetchUser(auth.currentUser?.email.toString())
        viewModel.provideUser(auth.currentUser?.email.toString())
        searchview()
        favButtonClick()





    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeData(){

        viewModel.signOutStatus.observe(viewLifecycleOwner){ isSignedOut ->
            if(isSignedOut){
                findNavController().navigate(R.id.action_homeFragment_to_signInFragment)
            }
            else requireContext().showCustomToast("Error occured when signing out.", R.drawable.baseline_warning_24)
        }

        viewModel.usernameLiveData.observe(viewLifecycleOwner){
            binding.tvUsername.text = it?.username
            viewModel.getUsers(it!!.username)
            viewModel.getFavUsers(it.username)
        }

        viewModel.userList.observe(viewLifecycleOwner){
            when(it){
                is Resource.Loading -> binding.homeProgressBar.visibility = View.VISIBLE
                is Resource.Error ->{
                    requireContext().showCustomToast(it.exception.toString(), R.drawable.baseline_warning_24)
                    binding.homeProgressBar.visibility = View.INVISIBLE

                }
                is Resource.Success -> {
                    binding.homeProgressBar.visibility = View.INVISIBLE
                    contactsAdapter = ContactsAdapter(it.result!! as ArrayList<User>, this)
                    binding.rvContacts.setRecyclerView(contactsAdapter)
                    contactsAdapter.notifyDataSetChanged()

                    binding.swipeRef.setOnRefreshListener {
                        binding.rvContacts.setRecyclerView(contactsAdapter)
                        binding.tvContacts.text = resources.getString(R.string.contacts)
                        binding.swipeRef.isRefreshing = false
                    }

                }
            }
        }


    }


    private fun searchview(){
        binding.searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                contactsAdapter.getFilter().filter(query)
                favContactsAdapter.getFilter().filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                contactsAdapter.getFilter().filter(newText)
                favContactsAdapter.getFilter().filter(newText)
                return true
            }

        })

    }

    private fun favButtonClick(){
        binding.btnFav.setOnClickListener {
            binding.tvContacts.text = resources.getString(R.string.s_favorite_contacts)

            viewModel.favUserList.observe(viewLifecycleOwner){
                when(it){
                    is Resource.Loading -> {
                        binding.homeProgressBar.visibility = View.VISIBLE
                        requireContext().showCustomToast("Can't load favorite users.", R.drawable.baseline_warning_24)

                    }
                    is Resource.Error -> {
                        requireContext().showCustomToast(it.exception.toString(), R.drawable.baseline_warning_24)
                        binding.homeProgressBar.visibility = View.INVISIBLE
                    }
                    is Resource.Success -> {
                        favContactsAdapter = FavoriteContactsAdapter(it.result!! as ArrayList<User>, this)
                        binding.rvContacts.setRecyclerView(favContactsAdapter)
                        Log.d("userr", it.result.size.toString())
                        binding.homeProgressBar.visibility = View.INVISIBLE
                        favContactsAdapter.notifyDataSetChanged()

                    }
                }
            }
        }
    }



    private fun signOut(){
        binding.btnLogout.setOnClickListener {
            viewModel.signOut()
        }
    }


    override fun onCardviewClick(email: String, username: String) {
        val action = HomeFragmentDirections.actionHomeFragmentToChatFragment(email, username, binding.tvUsername.text.toString())
        findNavController().navigate(action)
    }



    override fun onAddButtonClick(favUser: User) {
        binding.tvContacts.text = getString(R.string.s_favorite_contacts)
        viewModel.checkIfFav(binding.tvUsername.text.toString(), favUser)
        viewModel.userIsFav.observe(viewLifecycleOwner){ check ->

                if(check){
                    requireContext().showCustomToast("User has already added to the favorites list", R.drawable.baseline_check_24)

                }
                else{
                    viewModel.saveFavUser(binding.tvUsername.text.toString(), favUser)
                    requireContext().showCustomToast("User has added to favorites list", R.drawable.baseline_check_24)

                }

            }

    }

   override fun onRemoveButtonClick(user: User) {
        viewModel.userLiveData.observe(viewLifecycleOwner){
            viewModel.removeFromFav(it!!,user)
            requireContext().showCustomToast("User has been removed from the favorites list.", R.drawable.baseline_check_24)


        }

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        requireActivity().finish()
        requireActivity().onBackPressed()
    }

}

interface OnHomeClickListener{
    fun onCardviewClick(email: String, username: String)
    fun onAddButtonClick(favUser: User)

    fun onRemoveButtonClick(user: User)
}