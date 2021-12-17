package com.ashe.whatfood

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ashe.whatfood.adapter.ReviewAdapter
import com.ashe.whatfood.databinding.ActivityReviewBinding
import com.ashe.whatfood.dto.ReviewData
import com.ashe.whatfood.viewmodel.ReviewActivityViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import org.jetbrains.anko.toast


class ReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewBinding
    private lateinit var viewModel: ReviewActivityViewModel
    private val PICK_IMAGE = 100
    private val RETURN_DIALOG = 200
    private var reviewImageList = mutableListOf<String>()
    private var imagePaths = mutableListOf<String>()
    private val adapter = ReviewAdapter(reviewImageList)
    private var iv = listOf<ImageView>()
    private var postPassword = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_review)

        viewModel = ViewModelProvider(this).get(ReviewActivityViewModel::class.java)
        binding.activity = viewModel

        Settings()
        clickListener()
    }

    private fun Settings() {
        binding.rvImage.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvImage.setHasFixedSize(true)
        binding.rvImage.adapter = adapter

        if (intent.hasExtra("itemName")) {
            viewModel.itemName = intent.getStringExtra("itemName").toString()
        }

        iv = listOf(
            binding.star1Iv,
            binding.star2Iv,
            binding.star3Iv,
            binding.star4Iv,
            binding.star5Iv
        )
    }

    private fun clickListener() {
        iv.forEach { imageView ->
            imageView.setOnClickListener {
                viewModel.setGrade(imageView, iv)
            }
        }
        binding.clImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE)
        }

        binding.finishBtn.setOnClickListener {
            dialogShow()
        }
    }


    fun dialogShow(){
        val intent = Intent(this, PasswordDialog::class.java)
        startActivityForResult(intent, RETURN_DIALOG)
    }

    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK){
            when(requestCode){
                RETURN_DIALOG -> {
                    if(data == null) return
                    postPassword = data.getStringExtra("data").toString()
                    viewModel.upload( postPassword, imagePaths)
                }
                PICK_IMAGE -> {
                    if (data == null) return
                    val cursor =
                        contentResolver.query(Uri.parse(data.data.toString()), null, null, null, null)
                    cursor!!.moveToFirst()
                    val realPath = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA))
                    imagePaths.add(realPath)
                    reviewImageList.add(data.data.toString())
                    binding.countPhotoTv.text =
                        resources.getString(R.string.count_photo, reviewImageList.size)
                    adapter.imageList = reviewImageList
                    adapter.notifyDataSetChanged()
                }
            }
        }else{
            toast("오류가 발생했습니다. 다시 시도해주세요")
        }
    }

}