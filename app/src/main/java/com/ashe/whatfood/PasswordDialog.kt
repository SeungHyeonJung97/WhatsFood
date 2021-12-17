package com.ashe.whatfood

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Window
import androidx.databinding.DataBindingUtil
import com.ashe.whatfood.databinding.PasswordDialogBinding

class PasswordDialog : Activity(){
    private lateinit var binding: PasswordDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        binding = DataBindingUtil.setContentView(this, R.layout.password_dialog )

        binding.btnOk.setOnClickListener {
            val data = binding.etPassword.text.toString()
            val intent = Intent()
            intent.putExtra("data", data)
            setResult(RESULT_OK, intent)
            finish()
        }
        binding.btnCancel.setOnClickListener { finish() }
    }
}