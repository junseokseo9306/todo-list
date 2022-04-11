package com.codesquad.aos.todolist.ui

import android.util.Log
import android.view.DragEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.codesquad.aos.todolist.R
import com.codesquad.aos.todolist.data.model.Card
import com.codesquad.aos.todolist.ui.adapter.TodoCardListAdapter

class DragListener : View.OnDragListener {
    private var isDropped = false
    override fun onDrag(view: View, event: DragEvent): Boolean {
        when (event.action) {
            DragEvent.ACTION_DROP -> {
                isDropped = true
                var positionTarget = -1
                val viewSource = event.localState as View?
                val viewId = view.id
                val cardItem = R.id.mcItemView
                val rvTodo = R.id.rvTodo
                val rvProgress = R.id.rvProgress
                val rvComplete = R.id.rvComplete
                when (viewId) {
                    cardItem, rvTodo, rvProgress, rvComplete -> {
                        val target: RecyclerView
                        when (viewId) {
                            rvTodo -> target = view.rootView.findViewById(rvTodo) as RecyclerView
                            rvProgress -> target =
                                view.rootView.findViewById(rvProgress) as RecyclerView
                            rvComplete -> target =
                                view.rootView.findViewById(rvComplete) as RecyclerView
                            else -> {
                                target = view.parent as RecyclerView
                                positionTarget = view.tag as Int
                            }
                        }
                        if (viewSource != null) {
                            val source = viewSource.parent as RecyclerView
                            val adapterSource = source.adapter as TodoCardListAdapter
                            val positionSource = source.getChildAdapterPosition(viewSource)
                            val list = adapterSource.currentList[positionSource]
                            val listSource = adapterSource.currentList.toMutableList().apply {
                                removeAt(positionSource)
                            }
                            listSource.let { adapterSource.submitList(it) }
                            val adapterTarget = target.adapter as TodoCardListAdapter
                            val targetList = adapterTarget.currentList.toMutableList()
                            list.let { targetList.add(it) }
                            targetList.let { adapterTarget.submitList(it) }
                        }
                        Log.d("DragListener", "Dropped")
                    }
                }
            }

        }
        if (!isDropped && event.localState != null) {
            Log.d("DragListener", "NotDropped")
            val shadowCard = event.localState as View
            shadowCard.visibility = View.VISIBLE
            shadowCard.alpha = 0.5f
        }
        return true
    }

}