package com.budgetquest.app.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import com.budgetquest.app.ui.screens.auth.LoginScreen
import com.budgetquest.app.ui.screens.auth.RegisterScreen
import com.budgetquest.app.ui.screens.categories.CategoriesScreen
import com.budgetquest.app.ui.screens.dashboard.DashboardScreen
import com.budgetquest.app.ui.screens.badges.BadgesScreen
import com.budgetquest.app.ui.screens.features.FeaturesScreen
import com.budgetquest.app.ui.screens.expense.AddExpenseScreen
import com.budgetquest.app.ui.screens.expense.ExpenseListScreen
import com.budgetquest.app.ui.screens.goals.BudgetGoalsScreen
import com.budgetquest.app.ui.screens.splash.SplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.budgetquest.app.ui.viewmodel.MainViewModel

@Composable
fun BudgetQuestNavHost(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    val mainViewModel: MainViewModel = hiltViewModel()
    
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        enterTransition = { fadeIn(animationSpec = tween(500)) },
        exitTransition = { fadeOut(animationSpec = tween(500)) },
        popEnterTransition = { fadeIn(animationSpec = tween(500)) },
        popExitTransition = { fadeOut(animationSpec = tween(500)) }
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(onAnimationFinished = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onViewFeatures = { navController.navigate(Screen.Features.route) },
                onViewCategories = { navController.navigate(Screen.Categories.route) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                viewModel = mainViewModel
            )
        }
        composable(Screen.Expenses.route) {
            ExpenseListScreen(
                onBack = { navController.popBackStack() },
                onAddExpense = { navController.navigate(Screen.AddExpense.route) },
                viewModel = mainViewModel
            )
        }
        composable(Screen.Categories.route) {
            CategoriesScreen(viewModel = mainViewModel)
        }
        composable(Screen.Goals.route) {
            BudgetGoalsScreen(onBack = { navController.popBackStack() }, viewModel = mainViewModel)
        }
        composable(Screen.Badges.route) {
            BadgesScreen()
        }
        composable(Screen.Features.route) {
            FeaturesScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.AddExpense.route) {
            AddExpenseScreen(onBack = { navController.popBackStack() }, viewModel = mainViewModel)
        }
        composable(Screen.BudgetGoals.route) {
            BudgetGoalsScreen(onBack = { navController.popBackStack() }, viewModel = mainViewModel)
        }
    }
}
