package com.ashe.whatfood

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ashe.whatfood.other.Util.itemName
import com.ashe.whatfood.adapter.ReviewFragAdapter
import com.ashe.whatfood.databinding.PageReviewBinding
import com.ashe.whatfood.dto.ReviewData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ReviewFragment : Fragment() {
    private lateinit var binding: PageReviewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PageReviewBinding.inflate(inflater, container, false)

        binding.rvReview.setHasFixedSize(true)
        binding.rvReview.layoutManager = LinearLayoutManager(activity)

        val reviewData = mutableListOf<ReviewData>()
        val adapter = ReviewFragAdapter(reviewData)

        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("Review")

        dbRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                reviewData.clear()
                snapshot.children.forEach{ restaurant ->
                    if(restaurant.key!!.equals(itemName)){
                        restaurant.children.forEach {
                            reviewData.add(ReviewData.from(it.value as Map<Any, Any>))
                            Log.e("review","${reviewData}")
                        }
                    }
                }
                adapter.notifyDataSetChanged()
                if(reviewData.isNullOrEmpty()){
                    binding.reviewContainer.visibility = View.VISIBLE
                }else{
                    binding.reviewContainer.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        binding.rvReview.adapter = adapter
        return binding.root
    }
}