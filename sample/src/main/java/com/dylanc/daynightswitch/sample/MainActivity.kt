package com.dylanc.daynightswitch.sample

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
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
      if(isChecked) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
      }
    }
  }
}