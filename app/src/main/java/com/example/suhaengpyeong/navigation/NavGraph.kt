package com.example.suhaengpyeong.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.suhaengpyeong.ui.screen.AddEditEvaluationScreen
import com.example.suhaengpyeong.ui.screen.CalendarScreen
import com.example.suhaengpyeong.ui.screen.EvaluationDetailScreen
import com.example.suhaengpyeong.viewmodel.EvaluationViewModel

sealed class Screen(val route: String) {
    object Calendar : Screen("calendar")
    object Detail : Screen("detail/{evaluationId}") {
        fun createRoute(id: String) = "detail/$id"
    }
    object Add : Screen("add")
    object Edit : Screen("edit/{evaluationId}") {
        fun createRoute(id: String) = "edit/$id"
    }
}

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    viewModel: EvaluationViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Calendar.route
    ) {
        composable(Screen.Calendar.route) {
            CalendarScreen(
                viewModel = viewModel,
                onNavigateToDetail = { id ->
                    navController.navigate(Screen.Detail.createRoute(id))
                },
                onNavigateToAdd = {
                    navController.navigate(Screen.Add.route)
                },
                onNavigateToEdit = { id ->
                    navController.navigate(Screen.Edit.createRoute(id))
                }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("evaluationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val evaluationId = backStackEntry.arguments?.getString("evaluationId") ?: return@composable
            EvaluationDetailScreen(
                evaluationId = evaluationId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id ->
                    navController.navigate(Screen.Edit.createRoute(id))
                }
            )
        }

        composable(Screen.Add.route) {
            AddEditEvaluationScreen(
                evaluationId = null,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Edit.route,
            arguments = listOf(navArgument("evaluationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val evaluationId = backStackEntry.arguments?.getString("evaluationId") ?: return@composable
            AddEditEvaluationScreen(
                evaluationId = evaluationId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
