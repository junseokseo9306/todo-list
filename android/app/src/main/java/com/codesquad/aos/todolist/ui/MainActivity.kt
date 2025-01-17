package com.codesquad.aos.todolist.ui

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codesquad.aos.todolist.R
import com.codesquad.aos.todolist.common.ViewModelFactory
import com.codesquad.aos.todolist.common.utils.VerticalItemDecorator
import com.codesquad.aos.todolist.data.model.Card
import com.codesquad.aos.todolist.databinding.ActivityMainBinding
import com.codesquad.aos.todolist.ui.adapter.LogCardListAdapter
import com.codesquad.aos.todolist.ui.adapter.TodoCardListAdapter
import com.codesquad.aos.todolist.ui.dialog.CompleteDialogFragment
import com.codesquad.aos.todolist.ui.dialog.ProgressDialogFragment
import com.codesquad.aos.todolist.ui.dialog.TodoDialogFragment
import kotlin.math.min

class MainActivity : AppCompatActivity(), DataChangeListener {
    private lateinit var binding: ActivityMainBinding

    private lateinit var todoCardListAdapter: TodoCardListAdapter
    private lateinit var progressCardListAdapter: TodoCardListAdapter
    private lateinit var completeCardListAdapter: TodoCardListAdapter
    private lateinit var logCardListAdapter: LogCardListAdapter

    private val viewModel: TodoViewModel by viewModels {ViewModelFactory(this)}
    private val dragListener = DragListener(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setMenuClick()
        setDrawerListener()
        setDrawerClose()
        setViewModel()

        setDialogFragmentView()
        setTodoRecyclerView()
        setProgressRecyclerView()
        setCompleteRecyclerView()
        setLogRecyclerView()

        //
        viewModel.getCardItems()
    }

    private fun setTodoRecyclerView() {
        todoCardListAdapter = TodoCardListAdapter(
            { deleteIndex ->
                viewModel.deleteTodo(deleteIndex)
            }, this
        )
        binding.rvTodo.adapter = todoCardListAdapter
        binding.rvTodo.layoutManager = LinearLayoutManager(this)
        binding.rvTodo.addItemDecoration(VerticalItemDecorator(15))

        val touchHelper = TodoTouchHelper(todoCardListAdapter, viewModel).apply {
            setClamp(170f)  // 170 이
        }

        binding.rvTodo.setOnDragListener(todoCardListAdapter.dragInstance)

        ItemTouchHelper(touchHelper).attachToRecyclerView(binding.rvTodo)

        binding.rvTodo.setOnTouchListener { _, _ ->
            touchHelper.removePreviousClamp(binding.rvTodo)
            false
        }

        // 더미데이터
        //viewModel.addTodo("rvTODO", "TAG 1")
        //viewModel.addTodo("rvTODO", "TAG 2")
    }

    private fun setProgressRecyclerView() {
        progressCardListAdapter = TodoCardListAdapter (
            { deleteIndex ->
                viewModel.deleteProgress(deleteIndex)
            }, this)
        binding.rvProgress.adapter = progressCardListAdapter
        binding.rvProgress.layoutManager = LinearLayoutManager(this)
        binding.rvProgress.addItemDecoration(VerticalItemDecorator(15))

        binding.rvProgress.setOnDragListener(progressCardListAdapter.dragInstance)

        val touchHelper = TodoTouchHelper(progressCardListAdapter, viewModel).apply {
            setClamp(170f)  // 170 이
        }

        ItemTouchHelper(touchHelper).attachToRecyclerView(binding.rvProgress)

        binding.rvProgress.setOnTouchListener { _, _ ->
            touchHelper.removePreviousClamp(binding.rvProgress)
            false
        }

        // 더미데이터
        //viewModel.addProgress("rvProgress", "TAG 1")
    }

    private fun setCompleteRecyclerView() {
        completeCardListAdapter = TodoCardListAdapter (
            { deleteIndex ->
            viewModel.deleteComplete(deleteIndex)
            }, this)
        binding.rvComplete.adapter = completeCardListAdapter
        binding.rvComplete.layoutManager = LinearLayoutManager(this)
        binding.rvComplete.addItemDecoration(VerticalItemDecorator(15))

        binding.rvComplete.setOnDragListener(completeCardListAdapter.dragInstance)

        val touchHelper = TodoTouchHelper(completeCardListAdapter, viewModel).apply {
            setClamp(170f)  // 170 이
        }

        ItemTouchHelper(touchHelper).attachToRecyclerView(binding.rvComplete)

        binding.rvComplete.setOnTouchListener { _, _ ->
            touchHelper.removePreviousClamp(binding.rvComplete)
            false
        }

        // 더미데이터
        //viewModel.addComplete("rvComplete", "TAG 1")
    }

    private fun setLogRecyclerView() {
        logCardListAdapter = LogCardListAdapter()
        binding.rvLog.adapter = logCardListAdapter
        binding.rvLog.layoutManager = LinearLayoutManager(this)

        viewModel.addLog("오늘 할일을 추가했습니다", "1분전")
        viewModel.addLog("오늘 할일을 추가했습니다", "2분전")
        viewModel.addLog("오늘 할일을 추가했습니다", "3분전")
    }


    private fun setViewModel() {
        viewModel.todoListLd.observe(this) {
            binding.tvTodoBadgeCount.text = it.size.toString()
            todoCardListAdapter.submitList(it.toList())
        }

        viewModel.progressListLd.observe(this) {
            binding.tvProgressBadgeCount.text = it.size.toString()
            progressCardListAdapter.submitList(it.toList())
        }

        viewModel.completeListLd.observe(this) {
            binding.tvCompleteBadgeCount.text = it.size.toString()
            completeCardListAdapter.submitList(it.toList())
        }

        viewModel.LogListLd.observe(this) {
            logCardListAdapter.submitList(it.toList())
        }
    }

    private fun setMenuClick() {
        binding.toolbarMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.appbar_main_menu -> {
                    Log.d("AppTest", "appbar-menu")
                    binding.drawerLayout.openDrawer(GravityCompat.END)

                    // binding.drawerLaytout?.open()
                    // 위 방식으로 하면 안드로이드 자체에서 gravity가 left 인 드로어를 필요로 하는 것으로 이해
                    // 그래서 앱이 죽는현상 발생
                    true
                }
                else -> false
            }
        }
    }

    private fun setDrawerListener() {
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                Log.d("AppTest", "onDrawerSlide called")
            }

            override fun onDrawerOpened(drawerView: View) {
                Log.d("AppTest", "onDrawerOpened called")

            }

            override fun onDrawerClosed(drawerView: View) {
            }

            override fun onDrawerStateChanged(newState: Int) {
            }
        })
    }

    private fun setDrawerClose() {
        binding.ivCloseDrawer.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.END)
            // 닫는 부분도 close()로만 하면 에러 발생
        }
    }

    private fun setDialogFragmentView() {
        binding.btnAddTodo.setOnClickListener {
            TodoDialogFragment().show(
                supportFragmentManager, "DialogFragment"
            )
        }

        binding.btnAddProgress.setOnClickListener {
            ProgressDialogFragment().show(
                supportFragmentManager, "DialogFragment"
            )
        }

        binding.btnAddComplete.setOnClickListener {
            CompleteDialogFragment().show(
                supportFragmentManager, "DialogFragment"
            )
        }
    }

    // implement DataChangeListener
    override fun swapData(rvType: Int, sourceIndex: Int, targetIndex: Int) {
        when(rvType){
            1 -> viewModel.changeTodoOrder(sourceIndex, targetIndex)
            2 -> viewModel.changeProgressOrder(sourceIndex, targetIndex)
            3 -> viewModel.changeCompleteOrder(sourceIndex, targetIndex)
        }
    }

    override fun addDataAtEnd(rvType: Int, card: Card) {
        when(rvType){
            1 -> viewModel.addTodoCard(card)
            2 -> viewModel.addProgressCard(card)
            3 -> viewModel.addCompleteCard(card)
        }
    }

    override fun addDataAtInx(rvType: Int, tartgetIndex: Int, card: Card) {
        when(rvType){
            1 -> viewModel.addTodoCardAtInx(tartgetIndex, card)
            2 -> viewModel.addProgressCardAtInx(tartgetIndex, card)
            3 -> viewModel.addCompleteCardAtInx(tartgetIndex, card)
        }
    }

    override fun deleteData(rvType: Int, targetIndex: Int) {
        when(rvType){
            1 -> viewModel.deleteTodo(targetIndex)
            2 -> viewModel.deleteProgress(targetIndex)
            3 -> viewModel.deleteComplete(targetIndex)
        }
    }

}