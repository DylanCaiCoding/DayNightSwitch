package com.dylanc.daynightswitch

import android.content.Context
import android.content.SharedPreferences

/**
 * @author Dylan Cai
 */
internal object DayNightRepository {
  private var sharedPreferences: SharedPreferences? = null

  fun init(context: Context) {
    sharedPreferences = context.getSharedPreferences("day_night_switch", Context.MODE_PRIVATE)
  }

  var isNightMode: Boolean
    get() = sharedPreferences?.getBoolean("is_night_mode", false) == true
    set(value) {
      sharedPreferences?.edit()?.putBoolean("is_night_mode", value)?.apply()
    }

  var isFollowSystem: Boolean
    get() = sharedPreferences?.getBoolean("is_follow_system", false) == true
    set(value) {
      sharedPreferences?.edit()?.putBoolean("is_follow_system", value)?.apply()
    }
}