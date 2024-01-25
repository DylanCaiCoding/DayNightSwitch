package com.dylanc.daynightswitch

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.startup.Initializer

/**
 * @author Dylan Cai
 */
@Suppress("unused")
class DayNightInitializer : Initializer<Unit> {
  override fun create(context: Context) {
    DayNightRepository.init(context)
    val mode = when{
      DayNightRepository.isFollowSystem -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
      DayNightRepository.isNightMode -> AppCompatDelegate.MODE_NIGHT_YES
      else -> AppCompatDelegate.MODE_NIGHT_NO
    }
    AppCompatDelegate.setDefaultNightMode(mode)
  }

  override fun dependencies() = emptyList<Class<out Initializer<*>>>()
}