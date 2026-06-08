package com.example.suhaengpyeong.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.suhaengpyeong.data.Evaluation
import com.example.suhaengpyeong.data.EvaluationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

class EvaluationViewModel : ViewModel() {

    private val repository = EvaluationRepository()

    val allEvaluations: StateFlow<List<Evaluation>> = repository.evaluations
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _selectedClass = MutableStateFlow(1)
    val selectedClass: StateFlow<Int> = _selectedClass.asStateFlow()

    // evaluations filtered to the selected class
    val evaluations: StateFlow<List<Evaluation>> = combine(
        repository.evaluations, _selectedClass
    ) { evals, cls -> evals.filter { it.classNumber == cls } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _isAdminMode = MutableStateFlow(false)
    val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

    private val _selectedDate = MutableStateFlow<LocalDate>(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _currentMonth = MutableStateFlow<LocalDate>(LocalDate.now().withDayOfMonth(1))
    val currentMonth: StateFlow<LocalDate> = _currentMonth.asStateFlow()

    fun selectClass(classNumber: Int) {
        _selectedClass.value = classNumber
    }

    fun toggleAdminMode() {
        _isAdminMode.value = !_isAdminMode.value
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun previousMonth() {
        _currentMonth.value = _currentMonth.value.minusMonths(1)
    }

    fun nextMonth() {
        _currentMonth.value = _currentMonth.value.plusMonths(1)
    }

    fun addEvaluation(evaluation: Evaluation) {
        repository.addEvaluation(evaluation)
    }

    fun updateEvaluation(evaluation: Evaluation) {
        repository.updateEvaluation(evaluation)
    }

    fun deleteEvaluation(id: String) {
        repository.deleteEvaluation(id)
    }

    fun getEvaluationById(id: String): Evaluation? = repository.getById(id)

    fun getEvaluationsForDate(date: LocalDate): List<Evaluation> =
        evaluations.value.filter { it.date == date }

    fun getDatesWithEvaluations(month: LocalDate): Set<LocalDate> =
        evaluations.value
            .filter { it.date.year == month.year && it.date.month == month.month }
            .map { it.date }
            .toSet()
}
