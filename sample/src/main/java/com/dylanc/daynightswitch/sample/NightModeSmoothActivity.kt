/*
 * Copyright (c) 2024. Dylan Cai
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dylanc.daynightswitch.sample

import android.animation.ArgbEvaluator
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.dylanc.daynightswitch.sample.databinding.ActivityNightModeBinding

class NightModeSmoothActivity : AppCompatActivity() {

  private val binding by lazy { ActivityNightModeBinding.inflate(layoutInflater) }
  private val windowInsetsController by lazy { WindowCompat.getInsetsController(window, window.decorView) }
  private val evaluator = ArgbEvaluator()
  private val backgroundDayColor by lazy { ContextCompat.getColor(this, R.color.background_day) }
  private val backgroundNightColor by lazy { ContextCompat.getColor(this, R.color.background_night) }
  private val textPrimaryDayColor by lazy { ContextCompat.getColor(this, R.color.text_primary_day) }
  private val textPrimaryNightColor by lazy { ContextCompat.getColor(this, R.color.text_primary_night) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)
    binding.btnBack.setOnClickListener { finish() }
    binding.dayNightSwitch.toggleNightModeOnAnimatorEnd { isChecked ->
      windowInsetsController.isAppearanceLightStatusBars = !isChecked
    }
    binding.dayNightSwitch.setOnFractionChangedListener { fraction ->
      if (fraction == 0f) {
        windowInsetsController.isAppearanceLightStatusBars = true
      } else if (fraction == 1f) {
        windowInsetsController.isAppearanceLightStatusBars = false
      }
      window.decorView.setBackgroundColor(evaluator.evaluate(fraction, backgroundDayColor, backgroundNightColor) as Int)
      window.statusBarColor = evaluator.evaluate(fraction, backgroundDayColor, backgroundNightColor) as Int
      binding.tvNightMode.setTextColor(evaluator.evaluate(fraction, textPrimaryDayColor, textPrimaryNightColor) as Int)
      binding.btnBack.imageTintList = ColorStateList.valueOf(evaluator.evaluate(fraction, textPrimaryDayColor, textPrimaryNightColor) as Int)
    }
  }
}