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

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.dylanc.daynightswitch.DayNightManager
import com.dylanc.daynightswitch.DayNightSwitch
import com.dylanc.daynightswitch.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
  private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)
    binding.btnAtOnce.setOnClickListener {
      startActivity(Intent(this, NightModeAtOnceActivity::class.java))
    }
    binding.btnDelay.setOnClickListener {
      startActivity(Intent(this, NightModeDelayActivity::class.java))
    }
    binding.btnSmooth.setOnClickListener {
      startActivity(Intent(this, NightModeSmoothActivity::class.java))
    }

    binding.cbFollowSystem.isChecked = DayNightSwitch.isFollowSystem
    binding.cbFollowSystem.setOnCheckedChangeListener { _, isChecked ->
      DayNightSwitch.isFollowSystem = isChecked
    }
  }
}