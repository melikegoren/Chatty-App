package com.melikeg.chatty.presentation.signin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.melikeg.chatty.R
import com.melikeg.chatty.common.Resource
import com.melikeg.chatty.common.showCustomToast
import com.melikeg.chatty.common.spannable
import com.melikeg.chatty.databinding.FragmentSigninBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private var _binding: FragmentSigninBinding? = null
    val binding: FragmentSigninBinding get() = _binding!!

    private val viewModel: SigninViewModel by viewModels()

    @Inject
    lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSigninBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
        signInButton()
        navigateSignUp()
        isUserSignedIn()


    }

    private fun observeData(){
        viewModel.signInStatus.observe(viewLifecycleOwner){
            when(it){
                is Resource.Loading -> { _binding = null }
                is Resource.Success ->{
                    requireContext().showCustomToast( getString(R.string.signed_in_successfully), R.drawable.baseline_check_24)
                    findNavController().navigate(R.id.action_signInFragment_to_homeFragment)
                }
                is Resource.Error -> requireContext().showCustomToast(it.exception.toString(), R.drawable.baseline_warning_24)

            }
        }
    }

    private fun signInButton(){

        binding.btnSignin.setOnClickListener {
            if(binding.email.text.toString().isBlank() || binding.password.text.toString().isBlank()){
                requireContext().showCustomToast(getString(R.string.fill_the_blanks), R.drawable.baseline_warning_24)
            }
            else{
                viewModel.signInWithEmailAndPassword(binding.email.text.toString(),
                    binding.password.text.toString())
            }
        }
    }

    private fun navigateSignUp() =
        binding.tvSignUp.spannable(getString(R.string.register), "Sign Up.", findNavController())

    private fun isUserSignedIn(){
        val currentUser = auth.currentUser
        if(currentUser != null){
            findNavController().navigate(R.id.action_signInFragment_to_homeFragment)
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        requireActivity().finish()
        requireActivity().onBackPressed()


    }





}