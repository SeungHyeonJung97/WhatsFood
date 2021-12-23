package com.ashe.whatfood

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Window
import androidx.databinding.DataBindingUtil
import com.ashe.whatfood.databinding.CheckDialogBinding
import com.ashe.whatfood.databinding.PasswordDialogBinding
import com.ashe.whatfood.other.Util
import com.ashe.whatfood.other.Util.savedPost
import com.ashe.whatfood.other.Util.targetKey
import com.google.firebase.database.FirebaseDatabase
import org.jetbrains.anko.toast

class CheckDialog : Activity(){
    private lateinit var binding: CheckDialogBinding
    val database = FirebaseDatabase.getInstance()
    val dbRef = database.reference
    var password = ""
    var key = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        binding = DataBindingUtil.setContentView(this, R.layout.check_dialog)

        if (intent.hasExtra("password")) {
            password = intent.getStringExtra("password")!!
        }
        if (intent.hasExtra("key")) {
            key = intent.getStringExtra("key")!!
        }

        key = targetKey
        password = savedPost.password

        binding.btnOk.setOnClickListener {
            if ((binding.etPassword.text.toString()).equals(password)) {
                dbRef.child("Review").child(Util.itemName).child(key).removeValue()
                finish()
            }else{
                toast("비밀번호가 틀리셨습니다.")
            }
        }
        binding.btnCancel.setOnClickListener { finish() }
    }
}