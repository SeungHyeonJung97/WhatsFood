package com.ashe.whatfood.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.ashe.whatfood.CheckDialog
import com.ashe.whatfood.R
import com.ashe.whatfood.other.Util.lifecycleOwner
import com.ashe.whatfood.databinding.ItemReviewBinding
import com.ashe.whatfood.dto.ReviewData
import com.ashe.whatfood.other.Util
import com.ashe.whatfood.other.Util.itemName
import com.ashe.whatfood.other.Util.savedPost
import com.ashe.whatfood.other.Util.targetKey
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import timber.log.Timber

class ReviewFragAdapter(val reviewDatas: MutableList<ReviewData>) :
    RecyclerView.Adapter<ReviewFragAdapter.ViewHolder>() {

    val database = FirebaseDatabase.getInstance()
    val dbRef = database.reference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(reviewDatas[position])
    }

    override fun getItemCount(): Int {
        return reviewDatas.size
    }

    inner class ViewHolder(val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root) {
        val ivList = listOf(
            binding.ivStar1,
            binding.ivStar2,
            binding.ivStar3,
            binding.ivStar4,
            binding.ivStar5
        )
        var currentPosition = MutableLiveData<Int>()
        fun bind(data: ReviewData) {
            currentPosition.value = 0

            when (data.grade) {
                0 -> binding.ivGrade.setImageResource(R.drawable.grade1)
                1 -> binding.ivGrade.setImageResource(R.drawable.grade2)
                2 -> binding.ivGrade.setImageResource(R.drawable.grade3)
                3 -> binding.ivGrade.setImageResource(R.drawable.grade4)
                4 -> binding.ivGrade.setImageResource(R.drawable.grade5)
            }
            binding.tvDate.text = data.datetime
            (0 until data.grade).forEach { i -> ivList[i].setImageResource(R.drawable.star_max) }

            if (data.image.isNotEmpty()) {
                binding.ivRight.visibility = View.VISIBLE
            }

            currentPosition.observe(itemView.context.lifecycleOwner()!!) {
                Glide.with(itemView.context)
                    .load(data.image[currentPosition.value!!])
                    .placeholder(R.drawable.loading)
                    .into(binding.vpImage)

                binding.ivLeft.visibility = View.VISIBLE
                binding.ivRight.visibility = View.VISIBLE

                if (currentPosition.value == 0) binding.ivLeft.visibility = View.GONE
                if (currentPosition.value == data.image.size - 1) binding.ivRight.visibility =
                    View.GONE
            }

            binding.ivRight.setOnClickListener {
                val index = currentPosition.value!!.plus(1)
                currentPosition.postValue(index)
            }


            binding.ivLeft.setOnClickListener {
                val index = currentPosition.value!!.minus(1)
                currentPosition.postValue(index)
            }

            val adapter = ArrayAdapter.createFromResource(
                itemView.context,
                R.array.settings,
                android.R.layout.simple_spinner_dropdown_item
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            binding.ivSetting.setOnClickListener {
                if (binding.clDropdown.visibility == View.GONE) {
                    binding.clDropdown.visibility = View.VISIBLE
                } else {
                    binding.clDropdown.visibility = View.GONE
                }
            }

            binding.btnEdit.setOnClickListener {

            }
            binding.btnDelete.setOnClickListener {
                val intent = Intent(itemView.context, CheckDialog::class.java)
                intent.putExtra("password", reviewDatas[position].password)
                intent.putExtra("key", targetKey)
                itemView.context.startActivity(intent)

            }
            binding.tvComment.text = data.comment
        }
    }


}
