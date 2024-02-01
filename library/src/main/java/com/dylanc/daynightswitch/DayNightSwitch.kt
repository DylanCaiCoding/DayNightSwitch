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

@file:Suppress("unused")

package com.dylanc.daynightswitch

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.Checkable
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * @author Dylan Cai
 */
class DayNightSwitch(context: Context, attrs: AttributeSet? = null) : View(context, attrs), Checkable {
  private val paint = Paint().apply { isAntiAlias = true }
  private val path = Path()
  private var width = 0
  private var height = 0
  private var top = 0f
  private var left = 0f
  private var right = 0f
  private var bottom = 0f
  private var downX = 0f
  private var downY = 0f
  private var isClick = false
  private val aspectRatio = 7.8f / 3f
  private val animatorDuration = 500L
  private val skyDayColor = Color.parseColor("#3B76AA")
  private val sunColor = Color.parseColor("#F1C429")
  private val skyNightColor = Color.parseColor("#1D1E2B")
  private val moonColor = Color.parseColor("#C2C8D4")
  private val moonHoleColor = Color.parseColor("#959CAF")
  private val starColor = Color.parseColor("#FBFDFE")
  private val cloudColor = Color.parseColor("#F2FBFE")
  private val cloudSecondaryColor = Color.parseColor("#A0C6E4")
  private val rippleColor = Color.parseColor("#1AFFFFFF")
  private val sunShadowColor = Color.parseColor("#80000000")
  private val outSideShadowColor = Color.parseColor("#CC000000")
  private val argbEvaluator = ArgbEvaluator()
  private var isChecked = false
  private var onCheckedChangeListener: OnCheckedChangeListener? = null
  private var onAnimatorEndListener: OnCheckedChangeListener? = null
  private var onFractionChangedListener: (OnFractionChangedListener)? = null
  private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
  private var animator: ObjectAnimator? = null
  private var fraction: Float = 0f
    set(value) {
      field = if (value < 0) 0f else if (value > 1) 1f else value
      onFractionChangedListener?.onFractionChanged(field)
      invalidate()
    }

  var isDefaultNightMode = false
    set(value) {
      field = value
      isChecked = value
      fraction = if (isChecked) 1f else 0f
    }

  init {
    isDefaultNightMode = when (resources.configuration.uiMode and UI_MODE_NIGHT_MASK) {
      UI_MODE_NIGHT_YES -> true
      UI_MODE_NIGHT_NO -> false
      else -> false
    }
    setLayerType(LAYER_TYPE_SOFTWARE, null)
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val widthMode = MeasureSpec.getMode(widthMeasureSpec)
    val heightMode = MeasureSpec.getMode(heightMeasureSpec)
    width = MeasureSpec.getSize(widthMeasureSpec)
    height = MeasureSpec.getSize(heightMeasureSpec)

    // Ensure that the aspect ratio is 7.8:3
    when {
      widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY -> {
        val originWidth = width
        val originHeight = height
        if (width / height > aspectRatio) {
          width = (height * aspectRatio).toInt()
          left = (originWidth - width) / 2f
        } else {
          height = (width / aspectRatio).toInt()
          top = (originHeight - height) / 2f
        }
        bottom = top + height
        right = left + width
        return super.onMeasure(
          MeasureSpec.makeMeasureSpec(originWidth, MeasureSpec.EXACTLY),
          MeasureSpec.makeMeasureSpec(originHeight, MeasureSpec.EXACTLY)
        )
      }

      widthMode == MeasureSpec.EXACTLY -> {
        height = (width / aspectRatio).toInt()
      }

      heightMode == MeasureSpec.EXACTLY -> {
        width = (height * aspectRatio).toInt()
      }

      else -> {
        width = 180.dp
        height = (width / aspectRatio).toInt()
      }
    }
    bottom = top + height
    right = left + width
    super.onMeasure(
      MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
      MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
    )
  }


  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    canvas.save()
    path.reset()
    path.addRoundRect(left, top, right, bottom, height / 2f, height / 2f, Path.Direction.CCW)
    canvas.clipPath(path)

    // Draw sky
    paint.style = Paint.Style.FILL
    paint.color = argbEvaluator.evaluate(fraction, skyDayColor, skyNightColor) as Int
    canvas.drawPath(path, paint)

    // Draw clouds of second layer
    val heightOffset = fraction * height + top
    paint.color = cloudSecondaryColor
    path.reset()
    path.moveTo(left + width * 0.038f, height + heightOffset)
    path.quadTo(left + width * 0.09f, height * 0.767f + heightOffset, left + width * 0.218f, height * 0.86f + heightOffset)
    path.quadTo(left + width * 0.24f, height * 0.68f + heightOffset, left + width * 0.346f, height * 0.733f + heightOffset)
    path.quadTo(left + width * 0.41f, height * 0.48f + heightOffset, left + width * 0.513f, height * 0.633f + heightOffset)
    path.quadTo(left + width * 0.54f, height * 0.60f + heightOffset, left + width * 0.551f, height * 0.617f + heightOffset)
    path.quadTo(left + width * 0.6f, height * 0.367f + heightOffset, left + width * 0.705f, height * 0.433f + heightOffset)
    path.quadTo(left + width * 0.744f, height * 0.367f + heightOffset, left + width * 0.808f, height * 0.367f + heightOffset)
    path.quadTo(left + width * 0.83f, height * -0.05f + heightOffset, left + width, 0f + heightOffset)
    path.lineTo(left + width, height + heightOffset)
    path.close()
    canvas.drawPath(path, paint)

    // Draw ripple
    val radius = width * 0.15f
    val circleCenterX = height / 2 + fraction * (width - height)
    val circleCenterY = height / 2
    paint.color = rippleColor
    canvas.drawCircle(left + circleCenterX, top + circleCenterY, radius * 3.9f, paint)
    canvas.drawCircle(left + circleCenterX, top + circleCenterY, radius * 3.1f, paint)
    canvas.drawCircle(left + circleCenterX, top + circleCenterY, radius * 2.2f, paint)

    // Draw clouds of first layer
    paint.color = cloudColor
    path.reset()
    path.moveTo(left + width * 0.10f, height + heightOffset)
    path.quadTo(left + width * 0.165f, height * 0.85f + heightOffset, left + width * 0.23f, height * 0.98f + heightOffset)
    path.quadTo(left + width * 0.28f, height * 0.70f + heightOffset, left + width * 0.385f, height * 0.867f + heightOffset)
    path.quadTo(left + width * 0.47f, height * 0.64f + heightOffset, left + width * 0.564f, height * 0.833f + heightOffset)
    path.quadTo(left + width * 0.59f, height * 0.8f + heightOffset, left + width * 0.628f, height * 0.833f + heightOffset)
    path.quadTo(left + width * 0.70f, height * 0.74f + heightOffset, left + width * 0.769f, height * 0.767f + heightOffset)
    path.quadTo(left + width * 0.78f, height * 0.58f + heightOffset, left + width * 0.833f, height * 0.533f + heightOffset)
    path.quadTo(left + width * 0.87f, height * 0.2f + heightOffset, left + width, height * 0.2f + heightOffset)
    path.lineTo(left + width, height + heightOffset)
    path.close()
    canvas.drawPath(path, paint)

    // Draw stars
    paint.color = starColor
    canvas.drawStar(left + width * 0.103f, height * 0.317f - height + heightOffset, height * 0.045f)
    canvas.drawStar(left + width * 0.185f, height * 0.2f - height + heightOffset, height * 0.075f)
    canvas.drawStar(left + width * 0.439f, height * 0.267f - height + heightOffset, height * 0.03f)
    canvas.drawStar(left + width * 0.55f, height * 0.3f - height + heightOffset, height * 0.085f)
    canvas.drawStar(left + width * 0.19f, height * 0.467f - height + heightOffset, height * 0.045f)
    canvas.drawStar(left + width * 0.385f, height * 0.5f - height + heightOffset, height * 0.035f)
    canvas.drawStar(left + width * 0.526f, height * 0.583f - height + heightOffset, height * 0.035f)
    canvas.drawStar(left + width * 0.449f, height * 0.733f - height + heightOffset, height * 0.055f)
    canvas.drawStar(left + width * 0.115f, height * 0.8f - height + heightOffset, height * 0.025f)
    canvas.drawStar(left + width * 0.134f, height * 0.7f - height + heightOffset, height * 0.035f)
    canvas.drawStar(left + width * 0.195f, height * 0.833f - height + heightOffset, height * 0.03f)

    // Draw inner shadow
    paint.color = skyNightColor
    paint.style = Paint.Style.STROKE
    var strokeWidth = width * 0.1f
    paint.strokeWidth = strokeWidth
    path.reset()
    path.addRoundRect(
      left - strokeWidth / 2, top - strokeWidth / 2, right + strokeWidth / 2, bottom + strokeWidth / 2,
      height.toFloat(), height.toFloat(), Path.Direction.CCW
    )
    paint.setShadowLayer(width * 0.033f, 0f, width * 0.017f, outSideShadowColor)
    canvas.drawPath(path, paint)

    // Draw sun/moon
    paint.style = Paint.Style.FILL
    paint.color = argbEvaluator.evaluate(fraction, sunColor, moonColor) as Int
    paint.setShadowLayer(radius * 0.15f, width * 0.01f, width * 0.02f, sunShadowColor)
    canvas.drawCircle(left + circleCenterX, top + circleCenterY, radius, paint)
    paint.clearShadowLayer()

    // Draw moon hole
    paint.color = argbEvaluator.evaluate(fraction, sunColor, moonHoleColor) as Int
    canvas.drawCircle(left + circleCenterX, top + circleCenterY - radius / 2, radius * 0.2f, paint)
    canvas.drawCircle(left + circleCenterX - radius * 0.3f, top + circleCenterY + radius * 0.2f, radius * 0.36f, paint)
    canvas.drawCircle(left + circleCenterX + radius * 0.5f, top + circleCenterY + radius * 0.32f, radius * 0.25f, paint)
    canvas.restore()

    // Draw the light shadow of sun/moon
    canvas.save()
    path.reset()
    path.addCircle(left + circleCenterX, top + circleCenterY, radius, Path.Direction.CCW)
    canvas.clipPath(path)
    paint.style = Paint.Style.STROKE
    paint.color = skyNightColor
    strokeWidth = width.toFloat()
    paint.strokeWidth = strokeWidth
    paint.setShadowLayer(radius * 0.5f, 0f, 0f, Color.WHITE)
    canvas.drawCircle(left + circleCenterX + sqrt(radius * 0.5f), top + circleCenterY + sqrt(radius * 0.5f), radius * 1.13f + strokeWidth / 2, paint)
    paint.clearShadowLayer()

    canvas.restore()
  }

  private fun Canvas.drawStar(x: Float, y: Float, radius: Float) {
    val path = Path()
    val offset = radius * 0.2f
    path.moveTo(x, y - radius)
    path.quadTo(x + offset, y - offset, x + radius, y)
    path.quadTo(x + offset, y + offset, x, y + radius)
    path.quadTo(x - offset, y + offset, x - radius, y)
    path.quadTo(x - offset, y - offset, x, y - radius)
    drawPath(path, paint)
  }

  override fun onTouchEvent(event: MotionEvent): Boolean =
    when (event.action) {
      MotionEvent.ACTION_DOWN -> {
        downX = event.x
        downY = event.y
        isClick = true
        true
      }

      MotionEvent.ACTION_MOVE -> {
        if (abs(event.x - downX) > touchSlop || abs(event.y - downY) > touchSlop) {
          isClick = false
          fraction = if (isChecked) {
            1 - (downX - event.x) / (width - height)
          } else {
            (event.x - downX) / (width - height)
          }
        }
        true
      }

      MotionEvent.ACTION_UP -> {
        if (isClick) {
          performClick()
        } else {
          setChecked(fraction > 0.5f)
        }
        true
      }

      else -> false
    }

  override fun performClick(): Boolean {
    toggle()
    return super.performClick()
  }

  override fun setChecked(checked: Boolean) {
    if (isChecked == checked && (fraction == 0f || fraction == 1f)) {
      return
    }
    if (animator?.isRunning == true) {
      animator?.cancel()
      animator = null
    }
    animator = ObjectAnimator.ofFloat(this, "fraction", fraction, if (checked) 1f else 0f)
      .apply {
        duration = (animatorDuration * if (checked) 1f - fraction else fraction).toLong()
        addListener(object : AnimatorListenerAdapter() {
          private var isCancel = false

          override fun onAnimationCancel(animation: Animator) {
            isCancel = true
          }

          override fun onAnimationEnd(animation: Animator) {
            if (!isCancel) {
              onAnimatorEndListener?.onCheckedChanged(isChecked)
            }
            isCancel = false
          }
        })
        start()
      }
    if (isChecked == checked) return
    onCheckedChangeListener?.onCheckedChanged(checked)
    isChecked = checked
  }

  override fun isChecked(): Boolean = isChecked

  override fun toggle() = setChecked(!isChecked)

  fun toggleNightModeOnAnimatorStart(activity: ComponentActivity, block: ((Boolean) -> Unit)? = null) =
    toggleNightModeOnAnimatorStart(activity, activity, block)

  fun toggleNightModeOnAnimatorStart(fragment: Fragment, block: ((Boolean) -> Unit)? = null) =
    toggleNightModeOnAnimatorStart(fragment, fragment.viewLifecycleOwner, block)

  private fun toggleNightModeOnAnimatorStart(
    viewModelStoreOwner: ViewModelStoreOwner,
    lifecycleOwner: LifecycleOwner,
    block: ((Boolean) -> Unit)? = null
  ) {
    val viewModel = ViewModelProvider(viewModelStoreOwner)[NightModeViewModel::class.java]
    val liveData = viewModel.isNightMode
    val isNightMode = liveData.value
    if (isNightMode != null) {
      isDefaultNightMode = !isNightMode
      if (viewModel.fraction.value != null) {
        fraction = viewModel.fraction.value!!
      }
      setChecked(isNightMode)
    }
    setOnFractionChangedListener {
      viewModel.fraction.value = it
    }
    setOnCheckedChangeListener { isChecked ->
      liveData.value = isChecked
      DayNightManager.isNightMode = isChecked
      block?.invoke(isChecked)
    }
    cancelAnimatorOnDestroy(lifecycleOwner)
  }

  fun toggleNightModeOnAnimatorEnd(fragment: Fragment, block: ((Boolean) -> Unit)? = null) =
    toggleNightModeOnAnimatorEnd(fragment.viewLifecycleOwner, block)

  fun toggleNightModeOnAnimatorEnd(owner: LifecycleOwner, block: ((Boolean) -> Unit)? = null) {
    setOnAnimatorEndListener { isChecked ->
      DayNightManager.isNightMode = isChecked
    }
    setOnCheckedChangeListener { isChecked ->
      block?.invoke(isChecked)
    }
    cancelAnimatorOnDestroy(owner)
  }

  fun setOnCheckedChangeListener(listener: OnCheckedChangeListener?) {
    onCheckedChangeListener = listener
  }

  fun setOnAnimatorEndListener(listener: OnCheckedChangeListener?) {
    onAnimatorEndListener = listener
  }

  fun setOnFractionChangedListener(listener: OnFractionChangedListener?) {
    onFractionChangedListener = listener
  }

  private fun cancelAnimatorOnDestroy(owner: LifecycleOwner) {
    owner.lifecycle.addObserver(object : DefaultLifecycleObserver {
      override fun onDestroy(owner: LifecycleOwner) {
        animator?.cancel()
        animator = null
      }
    })
  }

  private val Int.dp get() = (this * resources.displayMetrics.density + 0.5f).toInt()

  class NightModeViewModel : ViewModel() {
    val isNightMode = MutableLiveData<Boolean>()
    val fraction = MutableLiveData<Float>()
  }

  fun interface OnCheckedChangeListener {
    fun onCheckedChanged(isChecked: Boolean)
  }

  fun interface OnFractionChangedListener {
    fun onFractionChanged(fraction: Float)
  }

  companion object {
    var isFollowSystem: Boolean by DayNightManager::isFollowSystem
  }
}