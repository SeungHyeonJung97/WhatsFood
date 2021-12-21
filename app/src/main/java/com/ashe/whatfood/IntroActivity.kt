package com.ashe.whatfood

import android.animation.Animator
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ashe.whatfood.databinding.ActivityIntroBinding
import org.jetbrains.anko.startActivity

class IntroActivity: AppCompatActivity() {

    private lateinit var binding: ActivityIntroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro)

        binding.lottieView.addAnimatorListener(object: Animator.AnimatorListener{
            override fun onAnimationStart(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                val fadeOut = AnimationUtils.loadAnimation(this@IntroActivity, R.anim.image_fade_out)
                binding.lottieView.startAnimation(fadeOut)
                val fadeIn = AnimationUtils.loadAnimation(this@IntroActivity, R.anim.image_fade_in)

                fadeOut.setAnimationListener(object: Animation.AnimationListener{
                    override fun onAnimationStart(animation: Animation?) {
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        binding.clContainer.startAnimation(fadeIn)
                        binding.lottieView.visibility = View.GONE
                        binding.clContainer.visibility = View.VISIBLE
                        binding.clContainer
                    }

                    override fun onAnimationRepeat(animation: Animation?) {
                    }
                })

                fadeIn.setAnimationListener(object:Animation.AnimationListener{
                    override fun onAnimationStart(animation: Animation?) {
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        startActivity<HomeActivity>()
                        finish()
                    }

                    override fun onAnimationRepeat(animation: Animation?) {
                    }

                })
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }
        })
    }
}