package com.ashe.whatfood.other

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ashe.whatfood.dto.Place
import java.util.*

object RecommendMenu {
    var recentMenu = ""
    var recentPlace = ""
    var recentCategory = ""
    var recommendFood = ""
    val korean =
        listOf(
            "불고기", "두루치기", "닭 볶음", "쌈밥", "비빔밥", "생선 구이", "낙지 볶음", "게장", "떡갈비", "냉면", "곱창", "보쌈"
        )
    val stew = listOf("김치 찌개", "순두부 찌개", "된장 찌개", "부대 찌개", "청국장", "갈비탕", "추어탕", "삼계탕")
    val chinese =
        listOf("탕수육", "깐풍기", "칠리 새우", "짬뽕", "사천 탕수육", "짜장면", "유린기", "볶음밥", "마파두부", "양장피", "고추잡채")
    val japanese = listOf("초밥", "라멘", "낫또", "오니기리", "덮밥", "우동", "소바", "돈카츠", "회", "오코노미야끼", "돈부리")
    val western = listOf("토마토 스파게티", "봉골레 파스타", "크림 파스타", "피자", "함박 스테이크", "리조또", "스테이크", "햄버거")
    val hangover = listOf("북엇국", "콩나물 국밥", "순대국", "뼈 해장국", "우거지국", "선지 해장국", "올갱이 국", "매운 라면")
    val another = listOf("쌀국수", "팟타이", "카레", "찜닭", "수제비", "칼국수", "아구찜", "닭갈비", "월남쌈", "치킨", "떡볶이")
    val randomCategory = listOf(korean, stew, chinese, japanese, western, hangover, another)

    fun savePreference(keyword: String, context: Context) {
        val sharedPreference = context.getSharedPreferences("place", AppCompatActivity.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreference.edit()

        if (recentCategory.contains(keyword) && keyword.isEmpty()) {
            val stringFilter = recentCategory.substring(recentCategory.lastIndexOf(">"))
            recentMenu = if (stringFilter.contains(",")) {
                stringFilter.substring(stringFilter.lastIndexOf(","))
            } else {
                stringFilter
            }
            recentMenu = recentMenu.trim()
        } else {
            recentMenu = ""
        }

        editor.putString("place", recentPlace)
        editor.putString("category", recentCategory)
        editor.putString("menu", recentMenu)
        editor.apply()
    }

    fun loadPreference(context: Context) {
        val sharedPreference = context.getSharedPreferences("place", AppCompatActivity.MODE_PRIVATE)
        val place = sharedPreference.getString("place", "")
        val menu = sharedPreference.getString("menu", "")
        val category = sharedPreference.getString("category", "")

        recentPlace = place!!
        recentMenu = menu!!
        recentCategory = category!!
    }

    fun recommend(): String {
        var result = ""
        var removeItem = listOf<String>()
        if (recentCategory.contains("술")) {
            val random = Random().nextInt(hangover.size)
            result = hangover[random]
        }else{
            if(recentCategory.contains("한식")){
                removeItem = korean
            }else if(recentCategory.contains("중식")){
                removeItem = chinese
            }else if(recentCategory.contains("일식")){
                removeItem = japanese
            }else if(recentCategory.contains("탕") || recentCategory.contains("찌개")){
                removeItem = stew
            }else if(recentCategory.contains("양식")){
                removeItem = western
            }
        }

        val list = randomCategory.toMutableList()
        list.remove(removeItem)
        val categoryRandom = Random().nextInt(list.size)
        val category = list[categoryRandom]
        val randomResult = Random().nextInt(category.size)
        result = category[randomResult]

        return result
    }
}