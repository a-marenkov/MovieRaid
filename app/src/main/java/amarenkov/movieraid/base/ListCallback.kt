package amarenkov.movieraid.base

interface ListCallback<T> {
    fun onItemSelected(item: T)
    fun onItemRemoved(item: T)
    fun onListEmpty()
    fun onListShown()
}