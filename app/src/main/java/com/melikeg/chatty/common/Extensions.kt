package com.melikeg.chatty.common

import android.content.Context
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.melikeg.chatty.R
import com.melikeg.chatty.domain.model.User
import com.melikeg.chatty.presentation.signin.SignInFragmentDirections
import java.util.Locale

fun TextView.spannable(fullText: String, clickableText: String, navController: NavController){

    val startIndex = fullText.indexOf(clickableText)
    val endIndex = startIndex + clickableText.length

    val spannableStringBuilder = SpannableStringBuilder(fullText)

    // Add a BackgroundColorSpan to highlight the clickable part
    val backgroundColorSpan = BackgroundColorSpan(Color.TRANSPARENT)
    spannableStringBuilder.setSpan(backgroundColorSpan, startIndex, endIndex, 0)

    // Add a ClickableSpan to make the clickable part clickable
    val clickableSpan = object : ClickableSpan() {
        override fun onClick(widget: View) {
            navController.navigate(SignInFragmentDirections.actionSignInFragmentToSignUpFragment())
        }
    }
    spannableStringBuilder.setSpan(clickableSpan, startIndex, endIndex, 0)

    // Set the SpannableStringBuilder to the TextView
    this.text = spannableStringBuilder
    this.movementMethod = android.text.method.LinkMovementMethod.getInstance()

}

fun Context.showCustomToast(message: String, iconResId: Int) {
    val inflater = LayoutInflater.from(this)
    val layout = inflater.inflate(R.layout.custom_toast_layout, null)

    val toastText = layout.findViewById<TextView>(R.id.customToastText)
    toastText.text = message

    val toastIcon = layout.findViewById<ImageView>(R.id.customToastIcon)
    toastIcon.setImageResource(iconResId)

    val toast = Toast(this)
    toast.duration = Toast.LENGTH_SHORT
    toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 100) // Adjust the position as needed
    toast.view = layout
    toast.show()
}

fun <T : RecyclerView.Adapter<*>> RecyclerView.setRecyclerView(adapter: T){
    this.layoutManager = LinearLayoutManager(context)
    this.adapter = adapter
}





