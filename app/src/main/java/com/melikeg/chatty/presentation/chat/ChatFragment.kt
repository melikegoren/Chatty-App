package com.melikeg.chatty.presentation.chat

import ChatAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.melikeg.chatty.R
import com.melikeg.chatty.common.Resource
import com.melikeg.chatty.common.showCustomToast
import com.melikeg.chatty.databinding.FragmentChatBinding
import com.melikeg.chatty.domain.model.Message
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    val binding: FragmentChatBinding get() = _binding!!

    private val navArgs: ChatFragmentArgs by navArgs()

    private lateinit var chatAdapter: ChatAdapter

    private val viewModel: ChatViewModel by viewModels()

    @Inject
    lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        _binding = FragmentChatBinding.inflate(layoutInflater)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
        observeData()
        sendButton()
        viewModel.listenForMessages(navArgs.senderUsername, navArgs.username)
        backToHomePage()
    }

    private fun initComponents(){
        binding.tvUserTextWith.text = navArgs.username
    }

    private fun observeData(){

        viewModel.sendMessageResult.observe(viewLifecycleOwner){
            when(it){
                is Resource.Loading -> requireContext().showCustomToast("Sending message...", R.drawable.baseline_check_24)
                is Resource.Error -> requireContext().showCustomToast(it.exception.toString(), R.drawable.baseline_warning_24)
                is Resource.Success -> {}
            }
        }

        viewModel.messagesList.observe(viewLifecycleOwner){
            when(it){
                is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Resource.Error -> {
                    requireContext().showCustomToast("Error while receiving messages", R.drawable.baseline_warning_24)
                    binding.progressBar.visibility = View.INVISIBLE
                }
                is Resource.Success -> {
                    setRecyclerView(binding.rvMessages, it.result as ArrayList<Message>)
                    binding.progressBar.visibility = View.INVISIBLE
                }


            }
        }


    }

    private fun sendButton(){
        binding.btnSend.setOnClickListener {
            val messageContent = binding.message.text.toString().trim()

            if (messageContent.isNotEmpty()) {
                viewModel.sendMessage(navArgs.senderUsername , navArgs.username, messageContent)
                binding.message.text?.clear()
            }
        }
    }

    private fun setRecyclerView(recyclerView: RecyclerView, messageList: ArrayList<Message>){
        chatAdapter = ChatAdapter(navArgs.senderUsername,messageList)
        recyclerView.adapter = chatAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.scrollToPosition(messageList.size.minus(1))
    }

    private fun backToHomePage(){
        binding.btnBack.setOnClickListener{
            findNavController().navigate(R.id.action_chatFragment_to_homeFragment)

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }



}