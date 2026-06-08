package com.example.suhaengpyeong.data

import java.time.LocalDate
import java.util.UUID

enum class EvaluationType(val displayName: String) {
    WRITTEN("필기"),
    PRACTICAL("실기"),
    PRESENTATION("발표"),
    SUBMISSION("제출"),
    PROJECT("프로젝트"),
    ORAL("구술"),
    OTHER("기타")
}

data class Evaluation(
    val id: String = UUID.randomUUID().toString(),
    val classNumber: Int,
    val subject: String,
    val title: String,
    val date: LocalDate,
    val description: String,
    val type: EvaluationType
)
