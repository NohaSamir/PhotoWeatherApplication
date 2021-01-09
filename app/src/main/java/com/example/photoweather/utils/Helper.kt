package com.example.photoweather.utils

import android.content.Context
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

fun showToast(context: Context?, msg: String?) {
    if (context != null && msg.isNullOrBlank()) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }
}

fun showToast(context: Context?, msgId: Int?) {
    if (context != null && msgId != null && msgId != 0)
        Toast.makeText(context, msgId, Toast.LENGTH_LONG).show()
}

fun getCurrentDate(): String {
    val sdf = SimpleDateFormat("HH:mm dd MMM", Locale.getDefault())
    return sdf.format(Date())
}