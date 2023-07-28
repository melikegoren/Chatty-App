package com.melikeg.chatty.presentation.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.melikeg.chatty.R
import com.melikeg.chatty.common.Resource
import com.melikeg.chatty.common.showCustomToast
import com.melikeg.chatty.databinding.FragmentHomeBinding
import com.melikeg.chatty.domain.model.User
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(), OnHomeClickListener {

    private var _binding: FragmentHomeBinding? = null
    val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var auth: FirebaseAuth

    private lateinit var usersAdapter: UsersAdapter



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




    }

    private fun observeData(){

        viewModel.signOutStatus.observe(viewLifecycleOwner){ isSignedOut ->
            if(isSignedOut){
                findNavController().navigate(R.id.action_homeFragment_to_signInFragment)
            }
            else requireContext().showCustomToast("Error occured when signing out.", R.drawable.baseline_warning_24)
        }

        viewModel.usernameLiveData.observe(viewLifecycleOwner){
            binding.tvUsername.text = it?.username
            viewModel.getUsers(binding.tvUsername.text.toString())
        }

        viewModel.userList.observe(viewLifecycleOwner){
            when(it){
                is Resource.Loading -> binding.homeProgressBar.visibility = View.VISIBLE
                is Resource.Error ->{
                    requireContext().showCustomToast("Can't load users.", R.drawable.baseline_warning_24)
                    binding.homeProgressBar.visibility = View.INVISIBLE

                }
                is Resource.Success -> {
                    binding.homeProgressBar.visibility = View.INVISIBLE
                    setRecycler(binding.rvContacts, it.result!!)

                }
            }
        }
    }

    private fun setRecycler(recyclerView: RecyclerView, messageList: List<User>){
        usersAdapter = UsersAdapter(messageList, this)
        recyclerView.adapter = usersAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        requireActivity().finish()
        requireActivity().onBackPressed()
    }

}

interface OnHomeClickListener{
    fun onCardviewClick(email: String, username: String)
}