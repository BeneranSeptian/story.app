package com.seftian.storyapp.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import com.seftian.storyapp.R
import org.json.JSONException
import org.json.JSONObject

object Helper {

    fun customDialog(context: Context): Dialog {
        val dialogBinding = LayoutInflater.from(context).inflate(R.layout.pop_up_window_layout, null)

        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(dialogBinding)
        dialog.setCancelable(false)

        return dialog
    }


    fun extractErrorMessage(errorBody: String?): String? {
        errorBody?.let {
            try {
                val json = JSONObject(it)
                return json.getString("message")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return null
    }
}
