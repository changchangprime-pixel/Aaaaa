package com.example.suhaengpyeong.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.suhaengpyeong.data.Evaluation
import com.example.suhaengpyeong.data.EvaluationType
import com.example.suhaengpyeong.ui.theme.subjectColor
import com.example.suhaengpyeong.viewmodel.EvaluationViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditEvaluationScreen(
    evaluationId: String?,
    viewModel: EvaluationViewModel,
    onNavigateBack: () -> Unit
) {
    val isEditing = evaluationId != null
    val existing = if (isEditing) viewModel.getEvaluationById(evaluationId!!) else null
    val currentClass by viewModel.selectedClass.collectAsState()

    var selectedClass by remember { mutableStateOf(existing?.classNumber ?: currentClass) }
    var subject by remember { mutableStateOf(existing?.subject ?: "") }
    var title by remember { mutableStateOf(existing?.title ?: "") }
    var description by remember { mutableStateOf(existing?.description ?: "") }
    var selectedDate by remember { mutableStateOf(existing?.date ?: LocalDate.now()) }
    var selectedType by remember { mutableStateOf(existing?.type ?: EvaluationType.WRITTEN) }

    var subjectError by remember { mutableStateOf(false) }
    var titleError by remember { mutableStateOf(false) }

    // Dropdown states
    var classDropdownExpanded by remember { mutableStateOf(false) }
    var subjectDropdownExpanded by remember { mutableStateOf(false) }
    var typeDropdownExpanded by remember { mutableStateOf(false) }

    // Date picker dialog state
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate
            .atStartOfDay()
            .toInstant(java.time.ZoneOffset.UTC)
            .toEpochMilli()
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate = java.time.Instant.ofEpochMilli(millis)
                                .atZone(java.time.ZoneId.of("UTC"))
                                .toLocalDate()
                        }
                        showDatePicker = false
                    }
                ) { Text("확인") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("취소") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    val predefinedSubjects = listOf("수학", "국어", "영어", "과학", "사회", "역사", "도덕", "체육", "음악", "미술", "기술·가정", "정보", "한문", "기타")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "수행평가 수정" else "수행평가 추가",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Class selector
            Column {
                Text(
                    text = "반 *",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                ExposedDropdownMenuBox(
                    expanded = classDropdownExpanded,
                    onExpandedChange = { classDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = "${selectedClass}반",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = classDropdownExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = classDropdownExpanded,
                        onDismissRequest = { classDropdownExpanded = false }
                    ) {
                        (1..10).forEach { cls ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "${cls}반",
                                        fontWeight = if (cls == selectedClass) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    selectedClass = cls
                                    classDropdownExpanded = false
                                },
                                leadingIcon = if (cls == selectedClass) {
                                    { Icon(Icons.Default.Check, contentDescription = null) }
                                } else null
                            )
                        }
                    }
                }
            }

            // Subject field with dropdown
            Column {
                Text(
                    text = "과목 *",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                ExposedDropdownMenuBox(
                    expanded = subjectDropdownExpanded,
                    onExpandedChange = { subjectDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = subject,
                        onValueChange = {
                            subject = it
                            subjectError = false
                        },
                        placeholder = { Text("과목명 입력 또는 선택") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = subjectDropdownExpanded)
                        },
                        isError = subjectError,
                        supportingText = if (subjectError) {
                            { Text("과목을 입력해주세요") }
                        } else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        singleLine = true,
                        leadingIcon = if (subject.isNotBlank()) {
                            {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(
                                            subjectColor(subject),
                                            shape = androidx.compose.foundation.shape.CircleShape
                                        )
                                )
                            }
                        } else null
                    )
                    ExposedDropdownMenu(
                        expanded = subjectDropdownExpanded,
                        onDismissRequest = { subjectDropdownExpanded = false }
                    ) {
                        predefinedSubjects.forEach { s ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .background(
                                                    subjectColor(s),
                                                    shape = androidx.compose.foundation.shape.CircleShape
                                                )
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(s)
                                    }
                                },
                                onClick = {
                                    subject = s
                                    subjectError = false
                                    subjectDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Title field
            Column {
                Text(
                    text = "제목 *",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        titleError = false
                    },
                    placeholder = { Text("수행평가 제목 입력") },
                    isError = titleError,
                    supportingText = if (titleError) {
                        { Text("제목을 입력해주세요") }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            // Date field
            Column {
                Text(
                    text = "날짜 *",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = "${selectedDate.year}년 ${selectedDate.monthValue}월 ${selectedDate.dayOfMonth}일",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "날짜 선택")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                )
            }

            // Evaluation type dropdown
            Column {
                Text(
                    text = "평가 유형 *",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                ExposedDropdownMenuBox(
                    expanded = typeDropdownExpanded,
                    onExpandedChange = { typeDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedType.displayName,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeDropdownExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = typeDropdownExpanded,
                        onDismissRequest = { typeDropdownExpanded = false }
                    ) {
                        EvaluationType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.displayName) },
                                onClick = {
                                    selectedType = type
                                    typeDropdownExpanded = false
                                },
                                leadingIcon = {
                                    if (type == selectedType) {
                                        Icon(Icons.Default.Check, contentDescription = null)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Description field
            Column {
                Text(
                    text = "설명",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("수행평가에 대한 설명 (범위, 유의사항 등)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Save button
            Button(
                onClick = {
                    var hasError = false
                    if (subject.isBlank()) { subjectError = true; hasError = true }
                    if (title.isBlank()) { titleError = true; hasError = true }

                    if (!hasError) {
                        if (isEditing && existing != null) {
                            viewModel.updateEvaluation(
                                existing.copy(
                                    classNumber = selectedClass,
                                    subject = subject.trim(),
                                    title = title.trim(),
                                    date = selectedDate,
                                    description = description.trim(),
                                    type = selectedType
                                )
                            )
                        } else {
                            viewModel.addEvaluation(
                                Evaluation(
                                    classNumber = selectedClass,
                                    subject = subject.trim(),
                                    title = title.trim(),
                                    date = selectedDate,
                                    description = description.trim(),
                                    type = selectedType
                                )
                            )
                        }
                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = if (isEditing) Icons.Default.Save else Icons.Default.Add,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isEditing) "수정 완료" else "추가하기",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Preview card
            if (subject.isNotBlank() || title.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "미리보기",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontWeight = FontWeight.SemiBold
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .height(72.dp)
                                .background(if (subject.isNotBlank()) subjectColor(subject) else Color.Gray)
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 12.dp, vertical = 12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (subject.isNotBlank()) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = subjectColor(subject).copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = subject,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = subjectColor(subject)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                EvalTypeChip(type = selectedType)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = title.ifBlank { "제목 없음" },
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${selectedDate.year}년 ${selectedDate.monthValue}월 ${selectedDate.dayOfMonth}일",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
