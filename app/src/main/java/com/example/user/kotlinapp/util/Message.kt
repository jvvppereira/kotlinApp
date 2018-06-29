package com.example.user.kotlinapp.util

import android.app.Activity
import android.support.design.widget.Snackbar

class Message  {

    var context: Activity? = null

    constructor(context: Activity) {
        this.context = context
    }

    fun showMessage(text: String) {
        showMessage(text, Snackbar.LENGTH_SHORT)
    }

    private fun showMessage(text: String, msgLenght: Int) {
        Snackbar.make(
                context!!.findViewById(android.R.id.content), // Parent view
                text, // Message to show
                msgLenght // How long to display the message.
        ).show()
    }
}