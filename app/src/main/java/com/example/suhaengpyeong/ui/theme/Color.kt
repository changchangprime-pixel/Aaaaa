package com.example.suhaengpyeong.ui.theme

import androidx.compose.ui.graphics.Color

val Primary = Color(0xFF3F51B5)
val PrimaryVariant = Color(0xFF303F9F)
val Secondary = Color(0xFFFF5722)
val SecondaryVariant = Color(0xFFE64A19)
val Background = Color(0xFFF5F5F5)
val Surface = Color(0xFFFFFFFF)
val OnPrimary = Color(0xFFFFFFFF)
val OnSecondary = Color(0xFFFFFFFF)
val OnBackground = Color(0xFF212121)
val OnSurface = Color(0xFF212121)

// Subject colors for calendar highlights
val SubjectMath = Color(0xFF5C6BC0)       // 수학 - 인디고
val SubjectScience = Color(0xFF26A69A)    // 과학 - 청록
val SubjectKorean = Color(0xFFEF5350)     // 국어 - 빨강
val SubjectEnglish = Color(0xFF66BB6A)    // 영어 - 초록
val SubjectPE = Color(0xFFFFA726)         // 체육 - 주황
val SubjectSocial = Color(0xFFAB47BC)     // 사회 - 보라
val SubjectArt = Color(0xFFEC407A)        // 미술 - 핑크
val SubjectMusic = Color(0xFF42A5F5)      // 음악 - 파랑
val SubjectDefault = Color(0xFF78909C)    // 기타 - 회색

fun subjectColor(subject: String): Color = when (subject) {
    "수학" -> SubjectMath
    "과학" -> SubjectScience
    "국어" -> SubjectKorean
    "영어" -> SubjectEnglish
    "체육" -> SubjectPE
    "사회" -> SubjectSocial
    "미술" -> SubjectArt
    "음악" -> SubjectMusic
    else -> SubjectDefault
}

// Evaluation type badge colors
val TypeWritten = Color(0xFF5C6BC0)
val TypePractical = Color(0xFF26A69A)
val TypePresentation = Color(0xFFEF5350)
val TypeSubmission = Color(0xFFFFA726)
val TypeProject = Color(0xFF66BB6A)
val TypeOral = Color(0xFFAB47BC)
val TypeOther = Color(0xFF78909C)
