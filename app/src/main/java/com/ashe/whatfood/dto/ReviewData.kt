package com.ashe.whatfood.dto

data class ReviewData(
    var restaurantName: String,
    var grade: Int,
    var comment: String,
    var image: List<String>,
    var datetime: String
) {
    companion object {
        fun from(map: Map<Any, Any>) = object{
            val restaurantName:String by map
            val grade:Int by map
            val comment:String by map
            val image:List<String> by map
            val datetime:String by map

            val data = ReviewData(restaurantName, grade, comment, image, datetime)
        }.data
    }
}
