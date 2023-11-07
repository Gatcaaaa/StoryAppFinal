package com.submisson.aleggappstory.view.customview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.submisson.aleggappstory.R

class MyEditText: AppCompatEditText {
    private lateinit var iconEdit: Drawable
    private var isPasswordVisible = false

    constructor(context: Context): super(context){
        init()
    }
    constructor(context: Context, attrs: AttributeSet): super(context, attrs){
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr){
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init(){
        iconEdit = ContextCompat.getDrawable(context, R.drawable.ic_baseline_lock_24) as Drawable
        compoundDrawablePadding = 12
        setIcons(iconEdit)
//        setHint(R.string.hint_password)

        setOnTouchListener{ _, event ->
            val drawableInRight = 2
            if (event.action == MotionEvent.ACTION_UP){
                if (event.rawX >= (right - compoundDrawables[drawableInRight].bounds.width())){
                    togglePasswordVisibility()
                    return@setOnTouchListener true
                }
            }
            false
        }

        addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().length < 8 ){
                    setError(context.getString(R.string.not_invalid_password),null )
                }else {
                        error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        inputType = if (isPasswordVisible){
            InputType.TYPE_CLASS_TEXT
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        }
        setSelection(text!!.length)
    }

    private fun setIcons(
        startOfTheText: Drawable? =  null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }
}