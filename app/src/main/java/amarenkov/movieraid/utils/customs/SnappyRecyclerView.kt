package amarenkov.movieraid.utils.customs

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView

class SnappyRecyclerView : RecyclerView, SnappyAdapter.Scroller {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val snapHelper = LinearSnapHelper()
    private var adapter: SnappyAdapter<*>? = null
    private var isPaddingSet = false
    private var isPaddingApplied = false
    private var itemSize = 0
    private var edgeMargin = 0
    private var startPosition = 0

    init {
        clipToPadding = false
        clipChildren = false
        snapHelper.attachToRecyclerView(this)
        addOnScrollListener(object : RecyclerView.OnScrollListener() {

            private var dragging = false

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> onItemCentered()

                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        dragging = true
                    }
                    else -> {
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dragging) {
                    dragging = false
                    adapter?.onItemCentered(NO_POSITION)
                } else if (!canScrollHorizontally(1) || !canScrollHorizontally(-1)) {
                    if (!(layoutManager?.isSmoothScrolling ?: true)) {
                        onItemCentered()
                        dragging = true
                    }
                }
            }

            private fun onItemCentered() {
                dragging = false
                layoutManager?.let { lm ->
                    snapHelper.findSnapView(lm)?.let { adapter?.onItemCentered(lm.getPosition(it)) }
                }
            }
        })
    }

    fun set(size: Int, edgeMargin: Int, startPosition: Int, reset: Boolean = false) {
        this.itemSize = size
        this.edgeMargin = edgeMargin
        this.startPosition = startPosition
        this.isPaddingSet = true
        if (reset) {
            isPaddingApplied = false
            requestLayout()
        }
    }

    fun set(target: View, startPosition: Int, reset: Boolean = false) {
        target.post {
            when ((layoutManager as? LinearLayoutManager)?.orientation) {
                LinearLayoutManager.HORIZONTAL -> {
                    this.itemSize = target.width
                    this.edgeMargin = (target.layoutParams as? FrameLayout.LayoutParams)?.leftMargin ?: 0
                }
                LinearLayoutManager.VERTICAL -> {
                    this.itemSize = target.height
                    this.edgeMargin = (target.layoutParams as? FrameLayout.LayoutParams)?.topMargin ?: 0
                }
                else -> {
                }
            }
            this.startPosition = startPosition
            this.isPaddingSet = true
            if (reset) {
                isPaddingApplied = false
                requestLayout()
            }
            target.visibility = GONE
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        setPadding()
    }

    private fun setPadding() {
        if (!isPaddingApplied && isPaddingSet) {
            isPaddingApplied = true
            val edgeSpacing = (width - itemSize) / 2 - edgeMargin
            when ((layoutManager as? LinearLayoutManager)?.orientation) {
                LinearLayoutManager.HORIZONTAL -> setPadding(edgeSpacing, 0, edgeSpacing, 0)
                LinearLayoutManager.VERTICAL -> setPadding(0, edgeSpacing, 0, edgeSpacing)
                else -> {
                }
            }
            snapToPosition(startPosition)
        }
    }

    fun isPaddingApplied() = isPaddingApplied

    override fun setAdapter(adapter: Adapter<*>?) {
        this.adapter = (adapter as? SnappyAdapter<*>)?.apply { setScroller(this@SnappyRecyclerView) }
        super.setAdapter(this.adapter)
    }

    override fun smoothSnapToPosition(position: Int) {
        smoothScrollToPosition(position)
        layoutManager?.let { lm ->
            lm.findViewByPosition(position)?.let {
                snapHelper.calculateDistanceToFinalSnap(lm, it)?.apply {
                    smoothSnapBy(this[0], this[1])
                }
            }
        }
    }

    override fun smoothSnapBy(dx: Int, dy: Int) {
        adapter?.onItemCentered(NO_POSITION)
        smoothScrollBy(dx, dy)
    }

    override fun snapToPosition(position: Int) {
        scrollToPosition(position)
        layoutManager?.let { lm ->
            lm.findViewByPosition(position)?.let {
                snapHelper.calculateDistanceToFinalSnap(lm, it)?.apply {
                    smoothSnapBy(this[0], this[1])
                }
            }
        }
    }

    override fun snapBy(dx: Int, dy: Int) {
        adapter?.onItemCentered(NO_POSITION)
        scrollBy(dx, dy)
    }
}