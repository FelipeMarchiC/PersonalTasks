package bes.mobile.personaltasks.view

interface OnTaskLongClickListener {
    fun onDetailsTaskMenuItemClick(position: Int)
    fun onEditTaskMenuItemClick(position: Int)
    fun onRemoveTaskMenuItemClick(position: Int)
}