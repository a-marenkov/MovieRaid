package amarenkov.movieraid.utils.customs

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView

class SnappyRecyclerView : RecyclerView {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var onItemSelected: ((Int) -> Unit)? = null
    private val snapHelper = LinearSnapHelper()

    init {
        snapHelper.attachToRecyclerView(this)
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_DRAGGING -> onItemSelected?.invoke(NO_POSITION)

                    RecyclerView.SCROLL_STATE_IDLE -> {
                        layoutManager?.let { lm ->
                            snapHelper.findSnapView(lm)?.let { onItemSelected?.invoke(lm.getPosition(it)) }
                        }
                    }

                    else -> Unit
                }
            }
        })
    }

    fun smoothSnapTo(position: Int, doPreScroll: Boolean = true) {
        onItemSelected?.invoke(NO_POSITION)
        if (doPreScroll) smoothScrollToPosition(position)
        else onItemSelected?.invoke(position)
        layoutManager?.let { lm ->
            lm.findViewByPosition(position)?.let {
                snapHelper.calculateDistanceToFinalSnap(lm, it)?.apply {
                    smoothScrollBy(this[0], this[1])
                }
            }
        }
    }

    fun smoothSnapBy(dx: Int, dy: Int) {
        onItemSelected?.invoke(NO_POSITION)
        smoothScrollBy(dx, dy)
    }
}