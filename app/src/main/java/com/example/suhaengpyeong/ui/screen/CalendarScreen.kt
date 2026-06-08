package com.example.suhaengpyeong.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.suhaengpyeong.data.Evaluation
import com.example.suhaengpyeong.data.EvaluationType
import com.example.suhaengpyeong.ui.theme.subjectColor
import com.example.suhaengpyeong.viewmodel.EvaluationViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: EvaluationViewModel,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    val evaluations by viewModel.evaluations.collectAsState()
    val isAdminMode by viewModel.isAdminMode.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val currentMonth by viewModel.currentMonth.collectAsState()
    val selectedClass by viewModel.selectedClass.collectAsState()

    var classDropdownExpanded by remember { mutableStateOf(false) }

    val datesWithEvals = remember(evaluations, currentMonth) {
        viewModel.getDatesWithEvaluations(currentMonth)
    }
    val selectedDateEvals = remember(evaluations, selectedDate) {
        viewModel.getEvaluationsForDate(selectedDate)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("수행평가 관리", fontWeight = FontWeight.Bold)
                },
                actions = {
                    // 반 선택 드롭다운
                    Box {
                        TextButton(
                            onClick = { classDropdownExpanded = true },
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                        ) {
                            Text("${selectedClass}반", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "반 선택")
                        }
                        DropdownMenu(
                            expanded = classDropdownExpanded,
                            onDismissRequest = { classDropdownExpanded = false }
                        ) {
                            (1..10).forEach { cls ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "${cls}반",
                                            fontWeight = if (cls == selectedClass) FontWeight.Bold else FontWeight.Normal,
                                            color = if (cls == selectedClass) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        viewModel.selectClass(cls)
                                        classDropdownExpanded = false
                                    },
                                    trailingIcon = if (cls == selectedClass) {
                                        { Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                                    } else null
                                )
                            }
                        }
                    }
                    // 관리자 모드 토글
                    IconButton(onClick = { viewModel.toggleAdminMode() }) {
                        Icon(
                            imageVector = if (isAdminMode) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                            contentDescription = if (isAdminMode) "관리자 모드 해제" else "관리자 모드",
                            tint = if (isAdminMode) MaterialTheme.colorScheme.secondary else Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            AnimatedVisibility(visible = isAdminMode, enter = fadeIn(), exit = fadeOut()) {
                FloatingActionButton(
                    onClick = onNavigateToAdd,
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "추가", tint = Color.White)
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (isAdminMode) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.secondary)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("관리자 모드 — ${selectedClass}반 수행평가를 추가·수정·삭제할 수 있습니다", color = Color.White, fontSize = 12.sp)
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { viewModel.previousMonth() }) {
                                Icon(Icons.Default.ChevronLeft, contentDescription = "이전 달")
                            }
                            Text(
                                "${currentMonth.year}년 ${currentMonth.monthValue}월",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { viewModel.nextMonth() }) {
                                Icon(Icons.Default.ChevronRight, contentDescription = "다음 달")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            listOf("일", "월", "화", "수", "목", "금", "토").forEachIndexed { index, day ->
                                Text(
                                    day,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    color = when (index) {
                                        0 -> Color(0xFFE53935)
                                        6 -> Color(0xFF1E88E5)
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        CalendarGrid(
                            currentMonth = currentMonth,
                            selectedDate = selectedDate,
                            datesWithEvaluations = datesWithEvals,
                            onDateSelected = { viewModel.selectDate(it) }
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${selectedDate.monthValue}월 ${selectedDate.dayOfMonth}일",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "(${selectedDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        "${selectedDateEvals.size}개",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (selectedDateEvals.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.EventAvailable,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "이 날에는 수행평가가 없습니다",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            } else {
                items(selectedDateEvals) { eval ->
                    EvaluationListItem(
                        evaluation = eval,
                        isAdminMode = isAdminMode,
                        onClick = { onNavigateToDetail(eval.id) },
                        onEdit = { onNavigateToEdit(eval.id) },
                        onDelete = { viewModel.deleteEvaluation(eval.id) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun CalendarGrid(
    currentMonth: LocalDate,
    selectedDate: LocalDate,
    datesWithEvaluations: Map<LocalDate, List<Evaluation>>,
    onDateSelected: (LocalDate) -> Unit
) {
    val firstDay = currentMonth.withDayOfMonth(1)
    val lastDay = currentMonth.withDayOfMonth(currentMonth.lengthOfMonth())
    val startOffset = firstDay.dayOfWeek.value % 7
    val today = LocalDate.now()

    val weeks = mutableListOf<List<LocalDate?>>()
    var currentWeek = mutableListOf<LocalDate?>()
    repeat(startOffset) { currentWeek.add(null) }
    var day = firstDay
    while (!day.isAfter(lastDay)) {
        currentWeek.add(day)
        if (currentWeek.size == 7) { weeks.add(currentWeek); currentWeek = mutableListOf() }
        day = day.plusDays(1)
    }
    if (currentWeek.isNotEmpty()) {
        while (currentWeek.size < 7) currentWeek.add(null)
        weeks.add(currentWeek)
    }

    Column {
        weeks.forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { date ->
                    Box(
                        modifier = Modifier.weight(1f).aspectRatio(1f).padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (date != null) {
                            val isSelected = date == selectedDate
                            val isToday = date == today
                            val evalColors = datesWithEvaluations[date]
                                ?.map { subjectColor(it.subject) }
                                ?.distinct()
                                ?.take(3)
                                ?: emptyList()

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp))
                                    .then(
                                        if (isSelected) Modifier.background(MaterialTheme.colorScheme.primary)
                                        else if (isToday) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                                        else Modifier
                                    )
                                    .clickable { onDateSelected(date) }
                                    .padding(2.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = date.dayOfMonth.toString(),
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                    color = when {
                                        isSelected -> Color.White
                                        date.dayOfWeek == DayOfWeek.SUNDAY -> Color(0xFFE53935)
                                        date.dayOfWeek == DayOfWeek.SATURDAY -> Color(0xFF1E88E5)
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }
                                )
                                if (evalColors.isNotEmpty()) {
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.padding(top = 2.dp)
                                    ) {
                                        evalColors.forEach { color ->
                                            Box(
                                                modifier = Modifier
                                                    .size(5.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isSelected) Color.White.copy(alpha = 0.8f) else color)
                                            )
                                            Spacer(modifier = Modifier.width(1.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EvaluationListItem(
    evaluation: Evaluation,
    isAdminMode: Boolean,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("수행평가 삭제") },
            text = { Text("'${evaluation.title}'을(를) 삭제하시겠습니까?") },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteDialog = false }) {
                    Text("삭제", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("취소") }
            }
        )
    }

    val subjectClr = subjectColor(evaluation.subject)

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.width(6.dp).height(72.dp).background(subjectClr))

            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 12.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(4.dp), color = subjectClr.copy(alpha = 0.15f)) {
                        Text(
                            evaluation.subject,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = subjectClr
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    EvalTypeChip(type = evaluation.type)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(evaluation.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (evaluation.description.isNotBlank()) {
                    Text(evaluation.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }

            if (isAdminMode) {
                Column(modifier = Modifier.padding(end = 8.dp), verticalArrangement = Arrangement.Center) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "수정", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = { showDeleteDialog = true }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "삭제", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                    }
                }
            } else {
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f), modifier = Modifier.padding(end = 12.dp))
            }
        }
    }
}

@Composable
fun EvalTypeChip(type: EvaluationType) {
    val (bgColor, textColor, label) = when (type) {
        EvaluationType.WRITTEN -> Triple(Color(0xFFE8EAF6), Color(0xFF3949AB), "필기")
        EvaluationType.PRACTICAL -> Triple(Color(0xFFE0F2F1), Color(0xFF00796B), "실기")
        EvaluationType.PRESENTATION -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "발표")
        EvaluationType.SUBMISSION -> Triple(Color(0xFFFFF3E0), Color(0xFFE65100), "제출")
        EvaluationType.PROJECT -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "프로젝트")
        EvaluationType.ORAL -> Triple(Color(0xFFF3E5F5), Color(0xFF6A1B9A), "구술")
        EvaluationType.OTHER -> Triple(Color(0xFFECEFF1), Color(0xFF37474F), "기타")
    }
    Surface(shape = RoundedCornerShape(4.dp), color = bgColor) {
        Text(label, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = textColor)
    }
}
