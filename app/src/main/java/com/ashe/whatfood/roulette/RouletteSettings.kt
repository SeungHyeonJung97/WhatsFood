package com.ashe.whatfood.roulette

import com.ashe.whatfood.R

object RouletteSettings {
    var isSet = false
    val rouletteData = mutableListOf<String>()

    fun getRouletteSetting(roulette: Roulette){
        if(!isSet){
            roulette.setRouletteDataList(listOf("한식", "중식", "양식", "일식"))
            roulette.setRouletteSize(roulette.getRouletteDataList().size)
            roulette.setRouletteTextColor(R.color.black)
            roulette.setRouletteBorderLineColor(R.color.white)

            roulette.getRouletteTextColor()
            roulette.getRouletteTextSize()
        }else{
            roulette.setRouletteDataList(rouletteData)
            roulette.setRouletteSize(roulette.getRouletteDataList().size)
            roulette.setRouletteTextColor(R.color.black)
            roulette.setRouletteBorderLineColor(R.color.white)

            roulette.getRouletteTextColor()
            roulette.getRouletteTextSize()
        }
    }

}