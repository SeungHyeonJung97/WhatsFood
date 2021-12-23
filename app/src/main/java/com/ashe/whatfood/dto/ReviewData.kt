package com.ashe.whatfood.dto

data class ReviewData(
    var postId: String = "",
    var grade: Int = 0,
    var comment: String = "",
    var image: List<String> = listOf(),
    var datetime: String = "",
    var password: String = ""
) {
    companion object {
        fun from(map: Map<Any, Any>) = object{
            val postId:String by map
            val grade:Int by map
            val comment:String by map
            val image:List<String> by map
            val datetime:String by map
            val password:String by map

            val data = ReviewData(postId, grade, comment, image, datetime, password)
        }.data
    }
}
