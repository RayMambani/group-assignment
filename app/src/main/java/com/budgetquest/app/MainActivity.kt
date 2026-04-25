package com.budgetquest.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.budgetquest.app.ui.navigation.BudgetQuestNavHost
import com.budgetquest.app.ui.navigation.Screen
import com.budgetquest.app.ui.theme.BudgetQuestTheme
import com.budgetquest.app.ui.theme.CyberGold
import com.budgetquest.app.ui.theme.CyberBackground
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            
            val screens = listOf(
                Screen.Dashboard,
                Screen.Expenses,
                Screen.Categories,
                Screen.Goals,
                Screen.Badges
            )

            BudgetQuestTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = CyberBackground,
                    bottomBar = {
                        if (screens.any { it.route == currentDestination?.route }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 24.dp),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Surface(
                                    color = CyberBackground,
                                    shape = RoundedCornerShape(32.dp),
                                    tonalElevation = 0.dp,
                                    shadowElevation = 8.dp,
                                    modifier = Modifier.height(90.dp)
                                ) {
                                    NavigationBar(
                                        containerColor = Color.Transparent, 
                                        contentColor = CyberGold,
                                        tonalElevation = 0.dp,
                                    ) {
                                        screens.forEach { screen ->
                                            NavigationBarItem(
                                                icon = { 
                                                    Icon(
                                                        painter = painterResource(id = screen.iconResId!!), 
                                                        contentDescription = screen.title,
                                                        tint = Color.Unspecified, 
                                                        modifier = Modifier.size(36.dp)
                                                    ) 
                                                },
                                                label = { 
                                                    Text(
                                                        text = screen.title,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Black,
                                                        color = CyberGold
                                                    ) 
                                                },
                                                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                                onClick = {
                                                    navController.navigate(screen.route) {
                                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                                        launchSingleTop = true
                                                        restoreState = true
                                                    }
                                                },
                                                colors = NavigationBarItemDefaults.colors(
                                                    indicatorColor = Color.White.copy(alpha = 0.05f)
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                ) { padding ->
                    Surface(
                        modifier = Modifier.padding(padding),
                        color = Color.Transparent
                    ) {
                        BudgetQuestNavHost(navController = navController)
                    }
                }
            }
        }
    }
}
