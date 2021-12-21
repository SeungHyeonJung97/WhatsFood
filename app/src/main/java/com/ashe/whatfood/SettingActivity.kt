package com.ashe.whatfood

import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ashe.whatfood.databinding.ActivitySettingBinding
import com.ashe.whatfood.roulette.Constant
import com.ashe.whatfood.roulette.Constant.DEFAULT_ROULETTE_SIZE
import com.ashe.whatfood.roulette.RouletteSettings
import com.ashe.whatfood.roulette.RouletteSettings.isSet
import com.ashe.whatfood.roulette.RouletteSettings.rouletteData
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding
    var count = MutableLiveData<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting)

        count.postValue(DEFAULT_ROULETTE_SIZE)
        rouletteData.clear()

        val optionList = listOf(
            binding.option1,
            binding.option2,
            binding.option3,
            binding.option4,
            binding.option5,
            binding.option6,
            binding.option7,
            binding.option8
        )

        val etList = listOf(
            binding.etOption1,
            binding.etOption2,
            binding.etOption3,
            binding.etOption4,
            binding.etOption5,
            binding.etOption6,
            binding.etOption7,
            binding.etOption8
        )


        count.observe(this) {
            binding.tvRouletteNumber.text =
                resources.getString(R.string.roulette_count, count.value!!)

            binding.roulette.setRouletteSize(count.value!!)
        }

        binding.btnSet.setOnClickListener {
            for (i in 0 until count.value!!) {
                if (etList[i].text.isNotEmpty()) {
                    rouletteData.add(etList[i].text.toString())
                } else {
                    toast("옵션 ${i}이 입력되지 않았어요 !")
                }
            }
            binding.roulette.setRouletteDataList(rouletteData)
            if(rouletteData.size == count.value){
                isSet = true
                this.startActivity<MainActivity>()
                finish()
            }
        }

        binding.ivPlus.setOnClickListener {
            if (count.value!! + 1 > 8) {
                binding.ivPlus.isClickable = false
            } else {
                count.value = count.value!!.plus(1)
                binding.ivMinus.isClickable = true
                optionList[count.value!! - 1].visibility = View.VISIBLE
            }
        }
        binding.ivMinus.setOnClickListener {
            if (2 > count.value!! - 1) {
                binding.ivMinus.isClickable = false
            } else {
                count.value = count.value!!.minus(1)
                binding.ivPlus.isClickable = true
                optionList[count.value!!].visibility = View.GONE
                optionList[count.value!! - 1].visibility = View.VISIBLE
            }
        }
    }
}