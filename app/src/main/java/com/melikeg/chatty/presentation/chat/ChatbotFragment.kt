package com.melikeg.chatty.presentation.chat

import ChatAdapter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.melikeg.chatty.R
import com.melikeg.chatty.common.setRecyclerView
import com.melikeg.chatty.common.showCustomToast
import com.melikeg.chatty.data.remote.ChatbotService
import com.melikeg.chatty.databinding.FragmentChatBotBinding
import com.melikeg.chatty.domain.model.ChatbotMessage
import com.melikeg.chatty.domain.model.ChatbotRequest
import com.melikeg.chatty.domain.model.ChatbotResponse
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Response
import java.util.ArrayList
import javax.inject.Inject

@AndroidEntryPoint
class ChatbotFragment : Fragment() {

    private var _binding: FragmentChatBotBinding? = null
    val binding: FragmentChatBotBinding get() = _binding!!

    @Inject
    lateinit var chatbotService: ChatbotService

    private lateinit var chatAdapter: ChatAdapter<ChatbotMessage>

    private val navArgs: ChatbotFragmentArgs by navArgs()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val messageList = mutableListOf<ChatbotMessage>()
        chatAdapter = ChatAdapter(navArgs.username,messageList as ArrayList<ChatbotMessage>)
        _binding = FragmentChatBotBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sendButton()
        onBackPressed()
    }

    private fun sendButton(){
        binding.btnSend.setOnClickListener {
            val userMessage = binding.message.text.toString().trim()
            if (userMessage.isNotEmpty()) {
                val userMessageObject = ChatbotMessage(navArgs.username, "chatbot",userMessage)
                chatAdapter.addMessage(userMessageObject)
                binding.rvMessages.setRecyclerView(chatAdapter)
                binding.rvMessages.scrollToPosition(chatAdapter.itemCount - 1)
                binding.message.text?.clear()

                getChatbotResponse(userMessage)
            }
        }
    }

    private fun getChatbotResponse(userMessage: String) {
        val apiKey = com.melikeg.chatty.common.Constants.API_KEY
        val request = ChatbotRequest("text-davinci-003",userMessage, 100, 0)

        val call = chatbotService.getChatbotResponse(apiKey,request)

        call.enqueue(object : retrofit2.Callback<ChatbotResponse> {
            override fun onResponse(
                call: Call<ChatbotResponse>,
                response: Response<ChatbotResponse>
            ) {
                Log.d("response code", response.code().toString())
                if (response.isSuccessful) {
                    val chatbotResponse = response.body()

                    chatbotResponse?.let {
                        val chatbotMessage = ChatbotMessage("chatbot", navArgs.username,it.choices[0].text.trim())
                        chatAdapter.addMessage(chatbotMessage)
                        binding.rvMessages.setRecyclerView(chatAdapter)
                        binding.rvMessages.scrollToPosition(chatAdapter.itemCount - 1)
                    }
                } else {
                    requireContext().showCustomToast(response.errorBody().toString(), R.drawable.baseline_warning_24 )
                }
            }

            override fun onFailure(call: Call<ChatbotResponse>, t: Throwable) {
                requireContext().showCustomToast(t.message.toString(), R.drawable.baseline_warning_24 )
            }
        })
    }

    private fun onBackPressed(){
        requireActivity().onBackPressedDispatcher.addCallback(this@ChatbotFragment) {
            findNavController().navigate(R.id.action_chatBotFragment2_to_homeFragment)
        }

        binding.btnBack.setOnClickListener{
            findNavController().navigate(R.id.action_chatBotFragment2_to_homeFragment)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}