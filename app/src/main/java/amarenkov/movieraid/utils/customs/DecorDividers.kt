package amarenkov.movieraid.utils.customs

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView

class VerticalItemDecoration(private val defaultSpacing: Int,
                             private val bottomSpacing: Int,
                             context: Context?,
                             orientation: Int,
                             private val isRevesed: Boolean = false) : DividerItemDecoration(context, orientation) {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (isRevesed) when (parent.getChildAdapterPosition(view)) {
            0 -> {
                outRect.top = defaultSpacing
                outRect.bottom = bottomSpacing
            }
            (parent.adapter?.itemCount ?: 0) - 1 -> {
                outRect.top = defaultSpacing * 2
                outRect.bottom = defaultSpacing
            }
            else -> {
                outRect.top = defaultSpacing
                outRect.bottom = defaultSpacing
            }
        } else when (parent.getChildAdapterPosition(view)) {
            0 -> {
                outRect.top = defaultSpacing * 2
                outRect.bottom = defaultSpacing
            }
            (parent.adapter?.itemCount ?: 0) - 1 -> {
                outRect.top = defaultSpacing
                outRect.bottom = bottomSpacing
            }
            else -> {
                outRect.top = defaultSpacing
                outRect.bottom = defaultSpacing
            }
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {}
}

class HorizontalItemDecoration(private val defaultSpacing: Int,
                               private val edgeSpacing: Int,
                               context: Context?,
                               orientation: Int,
                               private val isRevesed: Boolean = false) : DividerItemDecoration(context, orientation) {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (isRevesed) when (parent.getChildAdapterPosition(view)) {
            0 -> {
                outRect.left = defaultSpacing
                outRect.right = edgeSpacing
            }
            (parent.adapter?.itemCount ?: 0) - 1 -> {
                outRect.left = defaultSpacing * 2
                outRect.right = defaultSpacing
            }
            else -> {
                outRect.left = edgeSpacing
                outRect.right = defaultSpacing
            }
        } else when (parent.getChildAdapterPosition(view)) {
            0 -> {
                outRect.left = edgeSpacing
                outRect.right = defaultSpacing
            }
            (parent.adapter?.itemCount ?: 0) - 1 -> {
                outRect.left = defaultSpacing
                outRect.right = edgeSpacing
            }
            else -> {
                outRect.left = defaultSpacing
                outRect.right = defaultSpacing
            }
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {}
}