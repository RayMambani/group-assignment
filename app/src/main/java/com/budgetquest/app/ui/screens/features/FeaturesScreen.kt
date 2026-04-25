package com.budgetquest.app.ui.screens.features

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.budgetquest.app.ui.theme.*

data class FeatureItem(val name: String, val description: String, val icon: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeaturesScreen(onBack: () -> Unit) {
    val features = listOf(
        FeatureItem("XP Level System", "Earn XP when tracking expenses and staying within budget. Progress from Budget Novice to Financial Expert.", "🎖️"),
        FeatureItem("Visual Progress Rings", "Circular indicators show how close you are to each category spending limit.", "⭕"),
        FeatureItem("Receipt Photo Vault", "Attach photos of receipts to expense entries. Stored locally on your device.", "📷"),
        FeatureItem("Streak Tracker", "Track consecutive days of logging. Rewards at 7-day and 30-day milestones.", "🔥"),
        FeatureItem("Spending Health Indicator", "Green = Safe, Orange = Warning, Red = Overspending. Shown on every dashboard visit.", "💚"),
        FeatureItem("Age of Budget Score", "A computed score showing how many days your funds can last based on average spending.", "📅")
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    HeaderTitle("BudgetQuest Features") 
                },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack, 
                                contentDescription = "Back", 
                                tint = NeonGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Back", color = NeonGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = CyberBackground,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = CyberBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
        ) {
            items(features) { feature ->
                FeatureCard(feature)
            }
        }
    }
}

@Composable
fun FeatureCard(feature: FeatureItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color.White.copy(alpha = 0.05f)))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon / Emoji Circle
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.05f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(feature.icon, fontSize = 24.sp)
                }
            }
            
            Spacer(modifier = Modifier.width(20.dp))
            
            Column {
                Text(
                    text = feature.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CyberGold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = feature.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f),
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun HeaderTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Black,
        color = Color.White,
        letterSpacing = 0.5.sp
    )
}
