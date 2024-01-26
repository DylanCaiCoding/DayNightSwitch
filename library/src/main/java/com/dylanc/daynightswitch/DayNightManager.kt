package com.dylanc.daynightswitch

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode

/**
 * @author Dylan Cai
 */
object DayNightManager {
  private const val SP_NAME = "day_night"
  private const val KEY_NIGHT_MODE = "night_mode"
  private const val KEY_FOLLOW_SYSTEM = "follow_system"

  private lateinit var application: Application
  private lateinit var sharedPreferences: SharedPreferences

  fun init(application: Application) {
    this.application = application
    sharedPreferences = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)

    val mode = when {
      isFollowSystem -> MODE_NIGHT_FOLLOW_SYSTEM
      isNightMode -> MODE_NIGHT_YES
      else -> MODE_NIGHT_NO
    }
    setDefaultNightMode(mode)
  }

  var isNightMode: Boolean
    get() = sharedPreferences.getBoolean(KEY_NIGHT_MODE, false)
    set(value) {
      sharedPreferences.edit().putBoolean(KEY_NIGHT_MODE, value).apply()
      if (value) {
        setDefaultNightMode(MODE_NIGHT_YES)
      } else {
        setDefaultNightMode(MODE_NIGHT_NO)
      }
    }

  var isFollowSystem: Boolean
    get() = sharedPreferences.getBoolean(KEY_FOLLOW_SYSTEM, false)
    set(value) {
      sharedPreferences.edit().putBoolean(KEY_FOLLOW_SYSTEM, value).apply()
      if (value) {
        setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
      } else {
        isNightMode = application.resources.configuration.uiMode and UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
      }
    }
}