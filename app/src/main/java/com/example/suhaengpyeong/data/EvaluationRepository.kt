package com.example.suhaengpyeong.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

class EvaluationRepository {

    private val _evaluations = MutableStateFlow<List<Evaluation>>(sampleData())
    val evaluations: StateFlow<List<Evaluation>> = _evaluations.asStateFlow()

    fun addEvaluation(evaluation: Evaluation) {
        _evaluations.value = _evaluations.value + evaluation
    }

    fun updateEvaluation(updated: Evaluation) {
        _evaluations.value = _evaluations.value.map {
            if (it.id == updated.id) updated else it
        }
    }

    fun deleteEvaluation(id: String) {
        _evaluations.value = _evaluations.value.filter { it.id != id }
    }

    fun getById(id: String): Evaluation? = _evaluations.value.find { it.id == id }

    private fun sampleData(): List<Evaluation> {
        val today = LocalDate.now()
        return listOf(
            Evaluation(
                subject = "수학",
                title = "중간고사 필기평가",
                date = today.plusDays(3),
                description = "2단원 ~ 4단원 범위, 서술형 포함",
                type = EvaluationType.WRITTEN
            ),
            Evaluation(
                subject = "체육",
                title = "배드민턴 실기평가",
                date = today.plusDays(5),
                description = "서브, 스매시, 경기 능력 평가",
                type = EvaluationType.PRACTICAL
            ),
            Evaluation(
                subject = "영어",
                title = "말하기 발표",
                date = today.plusDays(7),
                description = "자기소개 및 관심사 영어 발표 (3분 이내)",
                type = EvaluationType.PRESENTATION
            ),
            Evaluation(
                subject = "국어",
                title = "독서감상문 제출",
                date = today.plusDays(10),
                description = "A4 2장 분량, 지정 도서 중 선택",
                type = EvaluationType.SUBMISSION
            ),
            Evaluation(
                subject = "과학",
                title = "실험 보고서 제출",
                date = today.plusDays(2),
                description = "광합성 실험 보고서 작성 및 제출",
                type = EvaluationType.PROJECT
            ),
            Evaluation(
                subject = "사회",
                title = "구술 면접 평가",
                date = today.plusDays(14),
                description = "사회문화 현상에 대한 구술 답변",
                type = EvaluationType.ORAL
            ),
            Evaluation(
                subject = "수학",
                title = "단원평가",
                date = today.plusDays(1),
                description = "5단원 함수 범위",
                type = EvaluationType.WRITTEN
            )
        )
    }
}
