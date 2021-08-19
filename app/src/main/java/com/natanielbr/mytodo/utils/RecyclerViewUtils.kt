package com.natanielbr.mytodo.utils

import android.view.GestureDetector
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

object RecyclerViewUtils {

    /**
     * Metodo para facilitar o click no item
     */
    fun RecyclerView.setOnItemClickListener(
        action: (recyclerView: RecyclerView, position: Int, isLongTouch: Boolean) -> Unit
    ) {
        addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
                override fun onLongPress(e: MotionEvent) {
                    val rv = this@setOnItemClickListener
                    action.invoke(
                        rv,
                        rv.getChildAdapterPosition(rv.findChildViewUnder(e.x, e.y)!!),
                        true
                    )
                }

                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    val rv = this@setOnItemClickListener
                    action.invoke(
                        rv,
                        rv.getChildAdapterPosition(rv.findChildViewUnder(e.x, e.y)!!),
                        false
                    )
                    return true
                }
            }

            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                rv.findChildViewUnder(e.x, e.y) ?: return false // checa e deixa o listener nullsafe
                val gestureDetector = GestureDetector(rv.context, gestureListener)

                return gestureDetector.onTouchEvent(e)
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

        })
    }
}