package com.aira.app

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pad = (16 * resources.displayMetrics.density).toInt()
        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(pad, pad * 2, pad, pad)

        val phone = EditText(this)
        phone.hint = "Number with country code, e.g. 8801XXXXXXXXX"
        phone.inputType = InputType.TYPE_CLASS_PHONE
        val msg = EditText(this)
        msg.hint = "Message"
        val send = Button(this)
        send.text = "Send via WhatsApp"
        val acc = Button(this)
        acc.text = "Turn on Accessibility (one time)"

        acc.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        send.setOnClickListener {
            val number = phone.text.toString().filter { it.isDigit() }
            val text = msg.text.toString().trim()
            if (number.length < 8 || text.isEmpty()) {
                Toast.makeText(this, "Enter number and message", Toast.LENGTH_SHORT).show()
            } else {
                AlertDialog.Builder(this)
                    .setTitle("Send this to +$number?")
                    .setMessage(text)
                    .setPositiveButton("Yes, send") { _, _ -> openWhatsApp(number, text) }
                    .setNegativeButton("No", null)
                    .show()
            }
        }

        root.addView(phone)
        root.addView(msg)
        root.addView(send)
        root.addView(acc)
        setContentView(root)
    }

    private fun openWhatsApp(number: String, text: String) {
        val uri = Uri.parse("https://wa.me/$number?text=" + Uri.encode(text))
        SendAccessibilityService.arm()
        try {
            startActivity(Intent(Intent.ACTION_VIEW, uri).setPackage("com.whatsapp"))
        } catch (e: ActivityNotFoundException) {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, uri))
            } catch (e2: ActivityNotFoundException) {
                Toast.makeText(this, "WhatsApp not found", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
