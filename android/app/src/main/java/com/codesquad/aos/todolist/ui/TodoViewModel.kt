package com.codesquad.aos.todolist.ui

import android.util.Log.d
import android.util.Log.i
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codesquad.aos.todolist.data.model.Card
import com.codesquad.aos.todolist.data.model.CardData
import com.codesquad.aos.todolist.data.model.Log
import com.codesquad.aos.todolist.repository.CardItemRepository
import kotlinx.coroutines.launch

class TodoViewModel(private val repository: CardItemRepository): ViewModel() {

    private val _todoListLd = MutableLiveData<MutableList<Card>>(mutableListOf())
    private val todolist = mutableListOf<Card>()
    val todoListLd: LiveData<MutableList<Card>> get() = _todoListLd

    private val _progressListLd = MutableLiveData<MutableList<Card>>()
    private val progresslist = mutableListOf<Card>()
    val progressListLd: LiveData<MutableList<Card>> get() = _progressListLd

    private val _completeListLd = MutableLiveData<MutableList<Card>>()
    private val completelist = mutableListOf<Card>()
    val completeListLd: LiveData<MutableList<Card>> get() = _completeListLd

    private val _LogListLd = MutableLiveData<MutableList<Log>>()
    private var LogList = mutableListOf<Log>()
    val LogListLd: LiveData<MutableList<Log>> get() = _LogListLd

    private var todoId = 1
    private var progressId = 1
    private var completeId = 1
    private var cardId = 1

    // 해야할 일 추가
    fun addTodo(title: String, content: String){
        todolist.add(0, Card(cardId++, content, 0, "todo", title))
        _todoListLd.value = todolist
    }

    fun addProgress(title: String, content: String){
        progresslist.add(0, Card(cardId++, content, 0, "doing", title))
        _progressListLd.value = progresslist
    }

    fun addComplete(title: String, content: String){
        completelist.add(0, Card(cardId++, content, 0, "done", title))
        _completeListLd.value = completelist
    }

    // Card 객체를 바로 리스트 마지막에 추가
    fun addTodoCard(card: Card){
        todolist.add(card)
        _todoListLd.value = todolist
    }

    fun addProgressCard(card: Card){
        progresslist.add(card)
        _progressListLd.value = progresslist
    }

    fun addCompleteCard(card: Card){
        completelist.add(card)
        _completeListLd.value = completelist
    }

    // Card 객체를 바로 리스트 마지막에 추가
    fun addTodoCardAtInx(targetInx: Int, card: Card){
        todolist.add(targetInx, card)
        _todoListLd.value = todolist
    }

    fun addProgressCardAtInx(targetInx: Int, card: Card){
        progresslist.add(targetInx, card)
        _progressListLd.value = progresslist
    }

    fun addCompleteCardAtInx(targetInx: Int, card: Card){
        completelist.add(targetInx, card)
        _completeListLd.value = completelist
    }

    fun addLog(log: String, time: String){
        LogList.add(Log("@Han", log, time))
        _LogListLd.value = LogList
    }

    // 삭제
    fun deleteTodo(inx: Int){
        todolist.removeAt(inx)
        _todoListLd.value = todolist
    }

    fun deleteProgress(inx: Int){
        progresslist.removeAt(inx)
        _progressListLd.value = progresslist
    }

    fun deleteComplete(inx: Int) {
        completelist.removeAt(inx)
        _completeListLd.value = completelist
    }

    // 한 리사이클러뷰 내에서 순서 변경
    fun changeTodoOrder(fromPos: Int, toPos: Int){
        val fromTemp = Card(todolist[fromPos].cardId, todolist[fromPos].content, todolist[fromPos].order,
            todolist[fromPos].section, todolist[fromPos].title)
        val toTemp = Card(todolist[toPos].cardId, todolist[toPos].content, todolist[toPos].order,
            todolist[toPos].section, todolist[toPos].title)

        todolist[fromPos] = toTemp
        todolist[toPos] = fromTemp

        _todoListLd.value = todolist
    }

    fun changeProgressOrder(fromPos: Int, toPos: Int){
        val fromTemp = Card(progresslist[fromPos].cardId, progresslist[fromPos].content, progresslist[fromPos].order,
            progresslist[fromPos].section, progresslist[fromPos].title)
        val toTemp = Card(progresslist[toPos].cardId, progresslist[toPos].content, progresslist[toPos].order,
            progresslist[toPos].section, progresslist[toPos].title)

        progresslist[fromPos] = toTemp
        progresslist[toPos] = fromTemp
        _progressListLd.value = progresslist
    }

    fun changeCompleteOrder(fromPos: Int, toPos: Int){
        val fromTemp = Card(completelist[fromPos].cardId, completelist[fromPos].content, completelist[fromPos].order,
            completelist[fromPos].section, completelist[fromPos].title)
        val toTemp = Card(completelist[toPos].cardId, completelist[toPos].content, completelist[toPos].order,
            completelist[toPos].section, completelist[toPos].title)

        completelist[fromPos] = toTemp
        completelist[toPos] = fromTemp
        _completeListLd.value = completelist
    }

    ///////////////

    // 전체 카드 조회
    fun getCardItems(){
        viewModelScope.launch {
            val resopnse = repository.getCardItems()
            android.util.Log.d("AppTest", "response : ${resopnse.logs}")

            todolist.clear()
            todolist.addAll(resopnse.classifiedCardsDTO.classifiedCards.todo.toMutableList())
            _todoListLd.value = todolist

            progresslist.clear()
            progresslist.addAll(resopnse.classifiedCardsDTO.classifiedCards.doing.toMutableList())
            _progressListLd.value = progresslist

            completelist.clear()
            completelist.addAll(resopnse.classifiedCardsDTO.classifiedCards.done.toMutableList())
            _completeListLd.value = completelist
        }
    }

}