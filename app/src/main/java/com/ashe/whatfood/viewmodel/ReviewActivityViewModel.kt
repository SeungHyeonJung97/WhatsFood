package com.ashe.whatfood.viewmodel

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ashe.whatfood.R
import com.ashe.whatfood.other.Util.ivIdList
import com.ashe.whatfood.dto.ReviewData
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.text.SimpleDateFormat

class ReviewActivityViewModel() : ViewModel() {

    init {
    }

    var itemName = ""
    private val database = FirebaseDatabase.getInstance()
    private val dbRef = database.reference
    private val storage = FirebaseStorage.getInstance()
    private var storageReference = storage.getReference()
    private var _grade = MutableLiveData<Int>()
    private var downloadPath = mutableListOf<String>()

    val grade: LiveData<Int>
        get() = _grade

    fun setGrade(iv: ImageView, ivList: List<ImageView>) {
        val count = ivIdList.indexOf(iv.id)

        Log.d("count", "$count")
        for (i in 0..4) {
            ivList[i].setImageResource(R.drawable.star_empty)
            if (i <= count) ivList[i].setImageResource(R.drawable.star_max)
        }

        _grade.postValue(count + 1)
    }

    fun saveData(comment: String) {

        val currentTime = SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis())
        val data = ReviewData(
            itemName,
            _grade.value!!,
            comment,
            downloadPath,
            currentTime
        )

        dbRef.child("Review").child(itemName).push().setValue(data)

    }


    fun upload(comment: String, images: List<String>) {
        var result = false
        images.forEach {
            val file = Uri.fromFile(File(it))
            val ref = storageReference.child("images/" + file.lastPathSegment)
            Log.e("Review", "${file.lastPathSegment}, ${file}")
            val uploadTask = ref.putFile(file)

            uploadTask.addOnSuccessListener { snapshot ->
                val url = snapshot.metadata?.reference?.downloadUrl

                url?.addOnSuccessListener { uri ->
                    downloadPath.add(uri.toString())
                    Log.e("Error",uri.toString())
                    if (downloadPath.size == images.size) {
                        saveData(comment)
                    }
                }?.addOnFailureListener{
                    Log.e("Error",it.toString())
                }
            }
        }
    }
}

