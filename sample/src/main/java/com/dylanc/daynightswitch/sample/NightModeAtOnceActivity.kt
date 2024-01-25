package com.dylanc.daynightswitch.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dylanc.daynightswitch.sample.databinding.ActivityNightModeBinding

class NightModeAtOnceActivity : AppCompatActivity() {

  private val binding by lazy { ActivityNightModeBinding.inflate(layoutInflater) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)
    binding.btnBack.setOnClickListener { finish() }
    binding.dayNightSwitch.toggleNightModeOnAnimatorStart(this)
  }
}