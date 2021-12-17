package com.ashe.whatfood.dto

class ListLayout(val name: String = "",      // 장소명
                 val road: String = "",      // 도로명 주소
                 val address: String = "",   // 지번 주소
                 val x: Double = 0.0,         // 경도(Longitude)
                 val y: Double = 0.0,       // 위도(Latitude)
                 val category: String = "", // 카테고리
                 val phone: String = "",      // 전화번호
                 val url: String = ""
)
