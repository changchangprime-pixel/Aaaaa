package com.example.suhaengpyeong.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.suhaengpyeong.ui.theme.subjectColor
import com.example.suhaengpyeong.viewmodel.EvaluationViewModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvaluationDetailScreen(
    evaluationId: String,
    viewModel: EvaluationViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    val isAdminMode by viewModel.isAdminMode.collectAsState()
    val evaluation = viewModel.getEvaluationById(evaluationId) ?: run {
        onNavigateBack(); return
    }
    val subjectClr = subjectColor(evaluation.subject)
    val today = LocalDate.now()
    val daysUntil = ChronoUnit.DAYS.between(today, evaluation.date)
    val dDayText = when {
        daysUntil < 0 -> "지남"
        daysUntil == 0L -> "오늘"
        daysUntil == 1L -> "내일"
        else -> "${daysUntil}일 후"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("수행평가 상세", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                actions = {
                    if (isAdminMode) {
                        IconButton(onClick = { onNavigateToEdit(evaluationId) }) {
                            Icon(Icons.Default.Edit, contentDescription = "수정")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = subjectClr,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Box(
                modifier = Modifier.fillMaxWidth().background(subjectClr).padding(24.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = RoundedCornerShape(6.dp), color = Color.White.copy(alpha = 0.25f)) {
                            Text(
                                "${evaluation.classNumber}반",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(shape = RoundedCornerShape(6.dp), color = Color.White.copy(alpha = 0.25f)) {
                            Text(
                                evaluation.subject,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(shape = RoundedCornerShape(6.dp), color = Color.White.copy(alpha = 0.25f)) {
                            Text(
                                evaluation.type.displayName,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = Color.White, fontSize = 13.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(evaluation.title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "${evaluation.date.year}년 ${evaluation.date.monthValue}월 ${evaluation.date.dayOfMonth}일",
                            color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Surface(shape = RoundedCornerShape(20.dp), color = Color.White) {
                            Text(
                                dDayText,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                color = subjectClr, fontWeight = FontWeight.Bold, fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // Description
            if (evaluation.description.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("설명", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(evaluation.description, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
