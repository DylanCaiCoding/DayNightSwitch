package com.dylanc.daynightswitch

import android.app.Application
import android.content.Context
import androidx.startup.Initializer

/**
 * @author Dylan Cai
 */
@Suppress("unused")
class DayNightInitializer : Initializer<Unit> {
  override fun create(context: Context) {
    DayNightManager.init(context as Application)
  }

  override fun dependencies() = emptyList<Class<out Initializer<*>>>()
}