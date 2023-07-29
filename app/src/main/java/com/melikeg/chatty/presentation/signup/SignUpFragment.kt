package com.melikeg.chatty.presentation.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.melikeg.chatty.R
import com.melikeg.chatty.common.Resource
import com.melikeg.chatty.common.showCustomToast
import com.melikeg.chatty.databinding.FragmentSignupBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    val binding get() = _binding!!

    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentSignupBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
        signUpButton()

    }


    private fun observeData(){
        viewModel.signUpStatus.observe(viewLifecycleOwner){
            when(it){
                is Resource.Loading -> { }
                is Resource.Success ->{
                    requireContext().showCustomToast(getString(R.string.signed_up_successfully), R.drawable.baseline_check_24)
                    requireActivity().onBackPressed()
                }
                is Resource.Error -> requireContext().showCustomToast(it.exception.toString(), R.drawable.baseline_warning_24)

            }

        }

        /*viewModel.isUsernameExist.observe(viewLifecycleOwner){
            when(it){
                true -> requireContext().showCustomToast("Username is already taken", R.drawable.baseline_warning_24)
                false -> {}
            }
        }*/

    }

    private fun signUpButton(){

        binding.btnSignup.setOnClickListener {
            if(binding.email.text.toString().isEmpty() ||
                binding.password.text.toString().isEmpty() ||
                binding.username.text.toString().isEmpty()){
                requireContext().showCustomToast(getString(R.string.fill_the_blanks), R.drawable.baseline_warning_24)

            }

            else{
                viewModel.signUpWithEmailAndPassword(
                    binding.email.text.toString(), binding.password.text.toString(), binding.username.text.toString(),requireContext())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}