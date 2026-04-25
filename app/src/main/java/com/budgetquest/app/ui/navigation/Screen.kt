package com.budgetquest.app.ui.navigation

import com.budgetquest.app.R

sealed class Screen(val route: String, val title: String = "", val iconResId: Int? = null) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    
    // Bottom Nav Tabs - Skeuomorphic 3D Icons
    object Dashboard : Screen("dashboard", "Dashboard", R.drawable.ic_skeuo_dashboard)
    object Expenses : Screen("expenses", "Expenses", R.drawable.ic_skeuo_expenses)
    object Categories : Screen("categories", "Categories", R.drawable.ic_skeuo_categories)
    object Goals : Screen("goals", "Goals", R.drawable.ic_skeuo_goals)
    object Badges : Screen("badges", "Badges", R.drawable.ic_skeuo_badges)
    
    // Sub-screens
    object AddExpense : Screen("add_expense")
    object BudgetGoals : Screen("budget_goals")
    object Features : Screen("features")
}
