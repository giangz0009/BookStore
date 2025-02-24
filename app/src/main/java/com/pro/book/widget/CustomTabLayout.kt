package com.pro.book.widget

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout

class CustomTabLayout : TabLayout {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewGroup = getChildAt(0) as ViewGroup
        val childCount = viewGroup.childCount
        if (childCount != 0) {
            val displayMetrics = context.resources.displayMetrics
            val tabMinWidth = displayMetrics.widthPixels / childCount
            for (i in 0 until childCount) {
                viewGroup.getChildAt(i).minimumWidth = tabMinWidth
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}
