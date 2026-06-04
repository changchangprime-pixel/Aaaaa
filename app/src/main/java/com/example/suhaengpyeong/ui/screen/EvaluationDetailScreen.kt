package com.example.suhaengpyeong.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.suhaengpyeong.data.Evaluation
import com.example.suhaengpyeong.data.EvaluationType
import com.example.suhaengpyeong.ui.theme.subjectColor
import com.example.suhaengpyeong.viewmodel.EvaluationViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvaluationDetailScreen(
    evaluationId: String,
    viewModel: EvaluationViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    val isAdminMode by viewModel.isAdminMode.collectAsState()
    val evaluations by viewModel.evaluations.collectAsState()
    val evaluation = evaluations.find { it.id == evaluationId }

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (evaluation == null) {
        LaunchedEffect(Unit) { onNavigateBack() }
        return
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("수행평가 삭제") },
            text = { Text("'${evaluation.title}'을(를) 삭제하시겠습니까?\n이 작업은 되돌릴 수 없습니다.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteEvaluation(evaluation.id)
                        showDeleteDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("삭제", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("취소")
                }
            }
        )
    }

    val subjectClr = subjectColor(evaluation.subject)
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 (E)", Locale.KOREAN)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("수행평가 상세") },
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
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "삭제",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
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
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Subject header banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(subjectClr)
                    .padding(24.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color.White.copy(alpha = 0.25f)
                        ) {
                            Text(
                                text = evaluation.subject,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        EvalTypeChip(type = evaluation.type)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = evaluation.title,
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = evaluation.date.format(dateFormatter),
                            color = Color.White.copy(alpha = 0.95f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Details card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    DetailRow(
                        icon = Icons.Default.School,
                        label = "과목",
                        value = evaluation.subject,
                        valueColor = subjectClr
                    )
                    Divider(modifier = Modifier.padding(vertical = 12.dp))
                    DetailRow(
                        icon = Icons.Default.Assignment,
                        label = "평가 유형",
                        value = evaluation.type.displayName
                    )
                    Divider(modifier = Modifier.padding(vertical = 12.dp))
                    DetailRow(
                        icon = Icons.Default.CalendarMonth,
                        label = "날짜",
                        value = evaluation.date.format(dateFormatter)
                    )
                    if (evaluation.description.isNotBlank()) {
                        Divider(modifier = Modifier.padding(vertical = 12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Description,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "설명",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = evaluation.description,
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = 22.sp
                            )
                        }
                    }
                }
            }

            // Days until notice
            val today = java.time.LocalDate.now()
            val daysUntil = java.time.temporal.ChronoUnit.DAYS.between(today, evaluation.date)
            if (daysUntil >= 0) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            daysUntil == 0L -> MaterialTheme.colorScheme.errorContainer
                            daysUntil <= 3L -> Color(0xFFFFF3E0)
                            daysUntil <= 7L -> Color(0xFFFFFDE7)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when {
                                daysUntil == 0L -> Icons.Default.Warning
                                daysUntil <= 3L -> Icons.Default.AccessTime
                                else -> Icons.Default.Info
                            },
                            contentDescription = null,
                            tint = when {
                                daysUntil == 0L -> MaterialTheme.colorScheme.error
                                daysUntil <= 3L -> Color(0xFFE65100)
                                daysUntil <= 7L -> Color(0xFFF57F17)
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = when (daysUntil) {
                                0L -> "오늘 수행평가가 있습니다!"
                                1L -> "내일 수행평가가 있습니다!"
                                else -> "${daysUntil}일 후에 수행평가가 있습니다"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (daysUntil <= 1L) FontWeight.Bold else FontWeight.Normal,
                            color = when {
                                daysUntil == 0L -> MaterialTheme.colorScheme.error
                                daysUntil <= 3L -> Color(0xFFE65100)
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = valueColor
            )
        }
    }
}
