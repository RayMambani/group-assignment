package com.budgetquest.app.ui.screens.goals

import android.app.DatePickerDialog
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.budgetquest.app.data.local.dao.CategorySpending
import com.budgetquest.app.ui.theme.*
import com.budgetquest.app.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetGoalsScreen(
    onBack: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val user by viewModel.currentUser.collectAsState()
    val categorySpending by viewModel.categorySpending.collectAsState()
    val totalSpent by viewModel.filteredTotal.collectAsState()
    val startDate by viewModel.startDateFilter.collectAsState()
    val endDate by viewModel.endDateFilter.collectAsState()

    var minGoalInput by remember(user) { mutableStateOf(user?.minMonthlyGoal?.toInt()?.toString() ?: "1000") }
    var maxGoalInput by remember(user) { mutableStateOf(user?.maxMonthlyGoal?.toInt()?.toString() ?: "5000") }

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = CyberBackground,
        topBar = {
            TopAppBar(
                title = { Text("BUDGET GOALS", color = CyberGold, fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = CyberGold)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val cal = Calendar.getInstance().apply { timeInMillis = startDate }
                        DatePickerDialog(context, { _, y, m, d ->
                            val newStart = Calendar.getInstance().apply { set(y, m, d) }.timeInMillis
                            viewModel.setDateFilters(newStart, endDate)
                        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                    }) {
                        Icon(Icons.Default.DateRange, "Filter", tint = CyberGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberBackground)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Budget Distribution",
                        color = CyberGold,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(160.dp)) {
                        BudgetDonutChart(categorySpending)
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        LegendItem("Groceries", NeonCyan)
                        LegendItem("Transport", NeonPurple)
                        LegendItem("Entertainment", NeonGreen)
                        LegendItem("Bills", NeonPink)
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Set Monthly Budget Goals", color = CyberGold, fontWeight = FontWeight.Bold)
                    
                    CustomGoalInput(
                        label = "Min Monthly Spending Target",
                        value = minGoalInput,
                        onValueChange = { minGoalInput = it }
                    )
                    
                    CustomGoalInput(
                        label = "Max Monthly Spending Target",
                        value = maxGoalInput,
                        onValueChange = { maxGoalInput = it }
                    )
                    
                    val maxGoal = maxGoalInput.toDoubleOrNull() ?: 5000.0
                    val progress = (totalSpent / maxGoal).toFloat().coerceIn(0f, 1f)
                    val isExceeding = totalSpent > maxGoal
                    
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Spending Progress", color = Color.White, fontSize = 14.sp)
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth().height(12.dp),
                            color = if (isExceeding) ThresholdMax else NeonGreen,
                            trackColor = Color.White.copy(alpha = 0.1f),
                            strokeCap = StrokeCap.Round
                        )
                        Text(
                            text = "R ${String.format("%,.0f", totalSpent)} spent of R ${String.format("%,.0f", maxGoal)} max",
                            color = if (isExceeding) ThresholdMax else Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.updateUserGoals(
                                minGoalInput.toDoubleOrNull() ?: 0.0,
                                maxGoalInput.toDoubleOrNull() ?: 5000.0
                            )
                            scope.launch {
                                snackbarHostState.showSnackbar("Budget goals updated successfully!")
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Save Goals", fontWeight = FontWeight.Black, color = Color.Black, fontSize = 18.sp)
                    }
                }
            }
            
            Text(
                text = "Viewing: ${dateFormat.format(Date(startDate))} to ${dateFormat.format(Date(endDate))}",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun BudgetDonutChart(spending: List<CategorySpending>) {
    val colors = listOf(NeonCyan, NeonPurple, NeonGreen, NeonPink, CyberGold)
    val total = spending.sumOf { it.total }
    
    if (total == 0.0) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                color = Color.White.copy(alpha = 0.05f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 24.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        return
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        var startAngle = -90f
        spending.forEachIndexed { index, item ->
            val sweepAngle = (item.total / total * 360f).toFloat()
            drawArc(
                color = colors[index % colors.size],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 24.dp.toPx(), cap = StrokeCap.Round)
            )
            startAngle += sweepAngle
        }
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(modifier = Modifier.size(8.dp).background(color, RoundedCornerShape(2.dp)))
        Text(label, color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
    }
}

@Composable
fun CustomGoalInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            prefix = { Text("R ", color = Color.White.copy(alpha = 0.5f)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CyberInputBackground,
                unfocusedContainerColor = CyberInputBackground,
                focusedBorderColor = NeonCyan.copy(alpha = 0.5f),
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
    }
}
