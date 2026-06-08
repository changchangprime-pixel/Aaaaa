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
                    Text(
                        text = "수행평가 관리",
                        fontWeight = FontWeight.Bold
                    )
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
                            tint = if (isAdminMode) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
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
            AnimatedVisibility(
                visible = isAdminMode,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                FloatingActionButton(
                    onClick = onNavigateToAdd,
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "수행평가 추가",
                        tint = Color.White
                    )
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
            // Admin mode banner
            if (isAdminMode) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.secondary)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "관리자 모드 활성화됨 - 수행평가를 추가, 수정, 삭제할 수 있습니다",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Calendar card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Month navigation header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { viewModel.previousMonth() }) {
                                Icon(Icons.Default.ChevronLeft, contentDescription = "이전 달")
                            }
                            Text(
                                text = "${currentMonth.year}년 ${currentMonth.monthValue}월",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { viewModel.nextMonth() }) {
                                Icon(Icons.Default.ChevronRight, contentDescription = "다음 달")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Day of week headers
                        val daysOfWeek = listOf("일", "월", "화", "수", "목", "금", "토")
                        Row(modifier = Modifier.fillMaxWidth()) {
                            daysOfWeek.forEachIndexed { index, day ->
                                Text(
                                    text = day,
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

                        // Calendar grid
                        CalendarGrid(
                            currentMonth = currentMonth,
                            selectedDate = selectedDate,
                            datesWithEvaluations = datesWithEvals,
                            evaluations = evaluations,
                            onDateSelected = { viewModel.selectDate(it) }
                        )
                    }
                }
            }

            // Selected date header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${selectedDate.monthValue}월 ${selectedDate.dayOfMonth}일",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    val dow = selectedDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)
                    Text(
                        text = "($dow)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "${selectedDateEvals.size}개",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Evaluations for selected date
            if (selectedDateEvals.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.EventAvailable,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "이 날에는 수행평가가 없습니다",
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

            // Spacer at bottom for FAB
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun CalendarGrid(
    currentMonth: LocalDate,
    selectedDate: LocalDate,
    datesWithEvaluations: Set<LocalDate>,
    evaluations: List<Evaluation>,
    onDateSelected: (LocalDate) -> Unit
) {
    val firstDay = currentMonth.withDayOfMonth(1)
    val lastDay = currentMonth.withDayOfMonth(currentMonth.lengthOfMonth())
    val startOffset = firstDay.dayOfWeek.value % 7  // Sunday = 0

    val today = LocalDate.now()
    val weeks = mutableListOf<List<LocalDate?>>()
    var currentWeek = mutableListOf<LocalDate?>()

    // Fill leading empty days
    repeat(startOffset) { currentWeek.add(null) }

    var day = firstDay
    while (!day.isAfter(lastDay)) {
        currentWeek.add(day)
        if (currentWeek.size == 7) {
            weeks.add(currentWeek)
            currentWeek = mutableListOf()
        }
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
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (date != null) {
                            val isSelected = date == selectedDate
                            val isToday = date == today
                            val hasEvals = date in datesWithEvaluations
                            val evalColors = if (hasEvals) {
                                evaluations
                                    .filter { it.date == date }
                                    .map { subjectColor(it.subject) }
                                    .distinct()
                                    .take(3)
                            } else emptyList()

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp))
                                    .then(
                                        if (isSelected) Modifier.background(MaterialTheme.colorScheme.primary)
                                        else if (isToday) Modifier.border(
                                            2.dp,
                                            MaterialTheme.colorScheme.primary,
                                            RoundedCornerShape(8.dp)
                                        )
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
                                                    .padding(horizontal = 1.dp)
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
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Subject color bar
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(subjectClr)
                    .defaultMinSize(minHeight = 60.dp)
            )
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(60.dp)
                    .background(subjectClr)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Subject chip
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = subjectClr.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = evaluation.subject,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = subjectClr
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    // Type chip
                    EvalTypeChip(type = evaluation.type)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = evaluation.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (evaluation.description.isNotBlank()) {
                    Text(
                        text = evaluation.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (isAdminMode) {
                Column(
                    modifier = Modifier.padding(end = 8.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "수정",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "삭제",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            } else {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier.padding(end = 12.dp)
                )
            }
        }
    }
}

@Composable
fun EvalTypeChip(type: EvaluationType) {
    val (bgColor, label) = when (type) {
        EvaluationType.WRITTEN -> Pair(Color(0xFFE8EAF6), "필기")
        EvaluationType.PRACTICAL -> Pair(Color(0xFFE0F2F1), "실기")
        EvaluationType.PRESENTATION -> Pair(Color(0xFFFFEBEE), "발표")
        EvaluationType.SUBMISSION -> Pair(Color(0xFFFFF3E0), "제출")
        EvaluationType.PROJECT -> Pair(Color(0xFFE8F5E9), "프로젝트")
        EvaluationType.ORAL -> Pair(Color(0xFFF3E5F5), "구술")
        EvaluationType.OTHER -> Pair(Color(0xFFECEFF1), "기타")
    }
    val textColor = when (type) {
        EvaluationType.WRITTEN -> Color(0xFF3949AB)
        EvaluationType.PRACTICAL -> Color(0xFF00796B)
        EvaluationType.PRESENTATION -> Color(0xFFC62828)
        EvaluationType.SUBMISSION -> Color(0xFFE65100)
        EvaluationType.PROJECT -> Color(0xFF2E7D32)
        EvaluationType.ORAL -> Color(0xFF6A1B9A)
        EvaluationType.OTHER -> Color(0xFF37474F)
    }
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = bgColor
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}
