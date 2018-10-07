package amarenkov.movieraid.base

interface ListCallback<T> {
    fun onItemSelected(item: T)
    fun onItemRemove(item: T)
    fun onListEmpty()
    fun onListShown()
}