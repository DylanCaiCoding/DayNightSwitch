package com.dylanc.daynightswitch.sample

import android.animation.ArgbEvaluator
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.dylanc.daynightswitch.sample.databinding.ActivityNightModeBinding

class NightModeSmoothActivity : AppCompatActivity() {

  private val binding by lazy { ActivityNightModeBinding.inflate(layoutInflater) }
  private val windowInsetsController by lazy { WindowCompat.getInsetsController(window, window.decorView) }
  private val evaluator = ArgbEvaluator()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)
    binding.apply {
      btnBack.setOnClickListener { finish() }
      dayNightSwitch.toggleNightModeOnAnimatorEnd(this@NightModeSmoothActivity) { isChecked ->
        windowInsetsController.isAppearanceLightStatusBars = !isChecked
      }
      dayNightSwitch.setOnFractionChangedListener { fraction ->
        if (fraction == 0f) {
          windowInsetsController.isAppearanceLightStatusBars = true
        } else if (fraction == 1f) {
          windowInsetsController.isAppearanceLightStatusBars = false
        }
        window.decorView.setBackgroundColor(evaluator.evaluate(fraction, getColor(R.color.background_day), getColor(R.color.background_night)) as Int)
        window.statusBarColor = evaluator.evaluate(fraction, getColor(R.color.background_day), getColor(R.color.background_night)) as Int
        tvNightMode.setTextColor(evaluator.evaluate(fraction, getColor(R.color.text_primary_day), getColor(R.color.text_primary_night)) as Int)
        btnBack.imageTintList =
          ColorStateList.valueOf(evaluator.evaluate(fraction, getColor(R.color.text_primary_day), getColor(R.color.text_primary_night)) as Int)
      }
    }
  }
}