package com.example.suhaengpyeong.ui.theme

import androidx.compose.ui.graphics.Color

val Primary = Color(0xFF3F51B5)
val PrimaryDark = Color(0xFF303F9F)
val Secondary = Color(0xFFFF6F00)

val SubjectMath = Color(0xFF3949AB)
val SubjectScience = Color(0xFF00897B)
val SubjectKorean = Color(0xFFE53935)
val SubjectEnglish = Color(0xFF2E7D32)
val SubjectPE = Color(0xFFEF6C00)
val SubjectSocial = Color(0xFF8E24AA)
val SubjectHistory = Color(0xFF6D4C41)
val SubjectMoral = Color(0xFF00838F)
val SubjectMusic = Color(0xFFD81B60)
val SubjectArt = Color(0xFF039BE5)
val SubjectDefault = Color(0xFF546E7A)

fun subjectColor(subject: String): Color = when (subject) {
    "수학" -> SubjectMath
    "과학" -> SubjectScience
    "국어" -> SubjectKorean
    "영어" -> SubjectEnglish
    "체육" -> SubjectPE
    "사회" -> SubjectSocial
    "역사" -> SubjectHistory
    "도덕" -> SubjectMoral
    "음악" -> SubjectMusic
    "미술" -> SubjectArt
    else -> SubjectDefault
}
