package amarenkov.movieraid.utils.customs

import androidx.recyclerview.widget.RecyclerView

abstract class SnappyAdapter<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {
    protected var mScroller: Scroller? = null

    abstract fun onItemCentered(position: Int)

    fun setScroller(scroller: Scroller) {
        mScroller = scroller
    }

    interface Scroller {
        fun smoothSnapToPosition(position: Int)
        fun smoothSnapBy(dx: Int, dy: Int)
        fun snapToPosition(position: Int)
        fun snapBy(dx: Int, dy: Int)
    }
}