package com.manoj.baseproject.core.common.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.manoj.baseproject.core.utils.extension.Drw
import kotlin.math.abs

class RecyclerItemTouchHelper(
    private val adapter: BaseAdapter<*, *>?
) : ItemTouchHelper.Callback() {
    private val paint = Paint()
    private val swipeThreshold = 0.9f

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        val swipeFlags = ItemTouchHelper.LEFT
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val fromPosition = viewHolder.bindingAdapterPosition
        val toPosition = target.bindingAdapterPosition
        adapter?.moveItem(fromPosition, toPosition)
        return true
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && dX < 0) {
            val itemView: View = viewHolder.itemView
            val height = itemView.bottom.toFloat() - itemView.top.toFloat()
            val width = height / 3

            val swipeThreshold = itemView.width * this.swipeThreshold

            paint.color = Color.GREEN
            val background = RectF(
                itemView.right.toFloat() + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
            c.drawRect(background, paint)

            val icon: Bitmap? =
                BitmapFactory.decodeResource(viewHolder.itemView.context.resources, Drw.delete)
            val iconDest = RectF(
                itemView.right.toFloat() - 2 * width,
                itemView.top.toFloat() + width,
                itemView.right.toFloat() - width,
                itemView.bottom.toFloat() - width
            )
            if (icon != null) {
                c.drawBitmap(icon, null, iconDest, paint)
            }

            if (abs(dX) > swipeThreshold) {
                paint.color = Color.GREEN
                val bg = RectF(
                    itemView.right.toFloat() + dX,
                    itemView.top.toFloat(),
                    itemView.right.toFloat(),
                    itemView.bottom.toFloat()
                )
                c.drawRect(bg, paint)
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (direction == ItemTouchHelper.LEFT) {
            val itemView = viewHolder.itemView
            val swipeThreshold = itemView.width * this.swipeThreshold
            if (abs(viewHolder.itemView.translationX) > swipeThreshold) {
                adapter?.removeItem(viewHolder.bindingAdapterPosition)
            } else {
                adapter?.notifyItemChanged(viewHolder.bindingAdapterPosition)
            }
        }
    }
}
