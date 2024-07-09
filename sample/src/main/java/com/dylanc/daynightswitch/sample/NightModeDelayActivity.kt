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

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dylanc.daynightswitch.sample.databinding.ActivityNightModeBinding

class NightModeDelayActivity : AppCompatActivity() {

  private val binding by lazy { ActivityNightModeBinding.inflate(layoutInflater) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)
    binding.btnBack.setOnClickListener { finish() }
    binding.dayNightSwitch.toggleNightModeOnAnimatorEnd()
  }
}