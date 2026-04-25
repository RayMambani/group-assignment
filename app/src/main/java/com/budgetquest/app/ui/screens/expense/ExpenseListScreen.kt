package com.budgetquest.app.ui.screens.expense

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.budgetquest.app.data.local.entity.ExpenseEntity
import com.budgetquest.app.ui.theme.*
import com.budgetquest.app.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseListScreen(
    onBack: () -> Unit,
    onAddExpense: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    val expenses by viewModel.filteredExpenses.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val total by viewModel.filteredTotal.collectAsState()
    val startDate by viewModel.startDateFilter.collectAsState()
    val endDate by viewModel.endDateFilter.collectAsState()
    
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    Scaffold(
        containerColor = CyberBackground,
        topBar = {
            TopAppBar(
                title = { Text("Quest Log", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = NeonGreen)
                            Text("Back", color = NeonGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberBackground)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddExpense,
                containerColor = NeonGreen,
                contentColor = Color.Black,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Select Date Range", color = CyberGold, fontWeight = FontWeight.Bold)
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        DateDisplayBox(
                            label = "From Date",
                            value = dateFormat.format(Date(startDate)),
                            onClick = {
                                val cal = Calendar.getInstance().apply { timeInMillis = startDate }
                                DatePickerDialog(context, { _, y, m, d ->
                                    val newStart = Calendar.getInstance().apply { set(y, m, d) }.timeInMillis
                                    viewModel.setDateFilters(newStart, endDate)
                                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                            },
                            modifier = Modifier.weight(1f)
                        )
                        DateDisplayBox(
                            label = "To Date",
                            value = dateFormat.format(Date(endDate)),
                            onClick = {
                                val cal = Calendar.getInstance().apply { timeInMillis = endDate }
                                DatePickerDialog(context, { _, y, m, d ->
                                    val newEnd = Calendar.getInstance().apply { set(y, m, d) }.timeInMillis
                                    viewModel.setDateFilters(startDate, newEnd)
                                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Button(
                        onClick = { 
                            scope.launch {
                                snackbarHostState.showSnackbar("Expenses updated for selected range")
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("View Expenses", fontWeight = FontWeight.Black, color = Color.Black)
                    }
                    
                    Text(
                        text = "Total: R ${String.format("%,.2f", total)} (${expenses.size} expenses)",
                        color = NeonGreen,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
            ) {
                items(expenses) { expense ->
                    val category = categories.find { it.id == expense.categoryId }
                    DetailedExpenseItem(expense, category)
                }
            }
        }
    }
}

@Composable
fun DateDisplayBox(
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
        Surface(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            color = CyberInputBackground,
            shape = RoundedCornerShape(10.dp)
        ) {
            Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.padding(horizontal = 12.dp)) {
                Text(value, color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun DetailedExpenseItem(expense: ExpenseEntity, category: com.budgetquest.app.data.local.entity.CategoryEntity?) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    val stripeColor = when (category?.name?.lowercase()) {
        "groceries" -> NeonCyan
        "transport" -> NeonPurple
        "bills" -> NeonPink
        "entertainment" -> NeonGreen
        "savings" -> NeonCyan
        else -> CyberGold
    }
    
    val icon = when (category?.name?.lowercase()) {
        "groceries" -> "\uD83D\uDED2"
        "transport" -> "\uD83D\uDE97"
        "bills" -> "\uD83D\uDCA1"
        "entertainment" -> "\uD83C\uDFAC"
        "savings" -> "\uD83D\uDCB0"
        else -> "❓"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .background(CyberBackground)
            .padding(vertical = 4.dp)
    ) {
        HorizontalDivider(
            modifier = Modifier.align(Alignment.BottomCenter).padding(horizontal = 20.dp),
            color = Color.White.copy(alpha = 0.05f)
        )
        
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight(0.6f)
                    .background(stripeColor, RoundedCornerShape(2.dp))
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.description,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(icon, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${category?.name ?: "Pending"} • ${dateFormat.format(Date(expense.date))}",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
            
            Text(
                text = "R ${expense.amount.toInt()}",
                color = CyberGold,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}
