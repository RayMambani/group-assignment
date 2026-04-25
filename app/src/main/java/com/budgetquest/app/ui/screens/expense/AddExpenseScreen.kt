package com.budgetquest.app.ui.screens.expense

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.budgetquest.app.R
import com.budgetquest.app.data.local.entity.ExpenseEntity
import com.budgetquest.app.ui.theme.*
import com.budgetquest.app.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    onBack: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val categories by viewModel.categories.collectAsState()
    
    var amount by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<Long>(calendar.timeInMillis) }
    var startTime by remember { mutableStateOf("08:00") }
    var endTime by remember { mutableStateOf("09:00") }
    var description by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }
    var selectedCategoryName by remember { mutableStateOf("") }
    
    LaunchedEffect(categories) {
        if (selectedCategoryId == null && categories.isNotEmpty()) {
            selectedCategoryId = categories[0].id
            selectedCategoryName = categories[0].name
        }
    }
    
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success -> 
            if (success) {
                capturedImageUri = tempImageUri 
            } else {
                scope.launch { snackbarHostState.showSnackbar("Failed to capture image") }
            }
        }
    )
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                val file = File.createTempFile("receipt_", ".jpg", context.cacheDir)
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                tempImageUri = uri
                cameraLauncher.launch(uri)
            } else {
                scope.launch { snackbarHostState.showSnackbar("Camera permission is required to take photos") }
            }
        }
    )

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> 
            if (uri != null) {
                capturedImageUri = uri 
            }
        }
    )

    Scaffold(
        containerColor = CyberBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Add Expense", color = Color.White, fontWeight = FontWeight.Bold) },
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Expense Entry", color = CyberGold, fontWeight = FontWeight.Bold)

                    CyberInputField(
                        label = "Amount",
                        value = amount,
                        onValueChange = { amount = it },
                        placeholder = "0.00",
                        keyboardType = KeyboardType.Decimal
                    )

                    CyberInputField(
                        label = "Date",
                        value = dateFormat.format(Date(selectedDate)),
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = {
                                DatePickerDialog(context, { _, y, m, d ->
                                    val cal = Calendar.getInstance().apply { set(y, m, d) }
                                    selectedDate = cal.timeInMillis
                                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
                            }) {
                                Icon(Icons.Default.DateRange, null, tint = Color.White.copy(alpha = 0.5f))
                            }
                        }
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            CyberInputField(
                                label = "Start Time",
                                value = startTime,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(onClick = {
                                        TimePickerDialog(context, { _, h, m ->
                                            startTime = String.format("%02d:%02d", h, m)
                                        }, 8, 0, true).show()
                                    }) {
                                        Icon(painterResource(android.R.drawable.ic_menu_recent_history), null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
                                    }
                                }
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            CyberInputField(
                                label = "End Time",
                                value = endTime,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(onClick = {
                                        TimePickerDialog(context, { _, h, m ->
                                            endTime = String.format("%02d:%02d", h, m)
                                        }, 9, 0, true).show()
                                    }) {
                                        Icon(painterResource(android.R.drawable.ic_menu_recent_history), null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
                                    }
                                }
                            )
                        }
                    }

                    CyberInputField(
                        label = "Description",
                        value = description,
                        onValueChange = { description = it },
                        placeholder = "Add description..."
                    )

                    Text("Category", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        Surface(
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            color = CyberInputBackground,
                            shape = RoundedCornerShape(12.dp),
                            onClick = { expanded = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("\uD83D\uDED2 ${selectedCategoryName.ifEmpty { "Select Category" }}", color = Color.White, modifier = Modifier.weight(1f))
                                Text("▼", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                            }
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(CyberSurface)
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.name, color = Color.White) },
                                    onClick = {
                                        selectedCategoryId = category.id
                                        selectedCategoryName = category.name
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Text("Attach Receipt Photo", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ReceiptButton(
                            label = "Camera",
                            icon = android.R.drawable.ic_menu_camera,
                            onClick = {
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                    val file = File.createTempFile("receipt_", ".jpg", context.cacheDir)
                                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                                    tempImageUri = uri
                                    cameraLauncher.launch(uri)
                                } else {
                                    permissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                        ReceiptButton(
                            label = "Gallery",
                            icon = android.R.drawable.ic_menu_gallery,
                            onClick = { galleryLauncher.launch("image/*") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    if (capturedImageUri != null) {
                        Text(
                            text = "Receipt attached", 
                            color = NeonCyan, 
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Image(
                            painter = rememberAsyncImagePainter(capturedImageUri),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth().height(100.dp).background(Color.Black, RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Text("No file chosen", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                    }
                }
            }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            if (event == "EXPENSE_ADDED") {
                scope.launch {
                    snackbarHostState.showSnackbar("Expense Added Successfully!")
                    onBack()
                }
            }
        }
    }

    Button(
        onClick = {
            if (amount.isNotEmpty() && selectedCategoryId != null) {
                val expense = ExpenseEntity(
                    amount = amount.toDoubleOrNull() ?: 0.0,
                    date = selectedDate,
                    startTime = 0L,
                    endTime = 0L,
                    description = description,
                    categoryId = selectedCategoryId!!,
                    imagePath = capturedImageUri?.toString()
                )
                viewModel.addExpenseWithXP(expense)
            } else {
                scope.launch { snackbarHostState.showSnackbar("Please enter an amount and category") }
            }
        },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Add Expense", fontWeight = FontWeight.Black, color = Color.Black, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun CyberInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    readOnly: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.White.copy(alpha = 0.3f)) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = readOnly,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            trailingIcon = trailingIcon,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CyberInputBackground,
                unfocusedContainerColor = CyberInputBackground,
                focusedBorderColor = Color.White.copy(alpha = 0.1f),
                unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
    }
}

@Composable
fun ReceiptButton(
    label: String,
    icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        color = Color.Transparent,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberGold.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painterResource(icon), null, tint = CyberGold, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, color = CyberGold, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}
