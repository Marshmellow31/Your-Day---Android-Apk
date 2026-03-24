package com.yourday.app.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.yourday.app.ui.screens.auth.ForgotPasswordScreen
import com.yourday.app.ui.screens.auth.LoginScreen
import com.yourday.app.ui.screens.auth.RegisterScreen
import com.yourday.app.ui.screens.main.AnalyticsScreen
import com.yourday.app.ui.screens.main.CreateEditTaskScreen
import com.yourday.app.ui.screens.main.GoalsScreen
import com.yourday.app.ui.screens.main.HomeScreen
import com.yourday.app.ui.screens.main.NotificationsScreen
import com.yourday.app.ui.screens.main.ProfileScreen
import com.yourday.app.ui.screens.main.SchedulerScreen
import com.yourday.app.ui.screens.main.SettingsScreen
import com.yourday.app.ui.screens.main.SubjectsScreen
import com.yourday.app.ui.screens.main.TasksScreen
import com.yourday.app.ui.screens.main.TopicsScreen
import com.yourday.app.ui.screens.onboarding.OnboardingScreen
import com.yourday.app.ui.screens.splash.SplashScreen
import com.yourday.app.ui.viewmodel.AuthState

private const val ANIM_DURATION = 300

@Composable
fun NavGraph(
    navController: NavHostController,
    authState: AuthState
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(ANIM_DURATION)) +
                fadeIn(tween(ANIM_DURATION))
        },
        exitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(ANIM_DURATION)) +
                fadeOut(tween(ANIM_DURATION))
        },
        popEnterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(ANIM_DURATION)) +
                fadeIn(tween(ANIM_DURATION))
        },
        popExitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(ANIM_DURATION)) +
                fadeOut(tween(ANIM_DURATION))
        }
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                authState = authState,
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Auth Flow
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // Main Screens
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToTasks = { navController.navigate(Screen.Tasks.route) },
                onNavigateToSubjects = { navController.navigate(Screen.Subjects.route) },
                onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToAnalytics = { navController.navigate(Screen.Analytics.route) },
                onNavigateToScheduler = { navController.navigate(Screen.Scheduler.route) },
                onNavigateToGoals = { navController.navigate(Screen.Goals.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onCreateTask = { navController.navigate(Screen.CreateEditTask.createRoute()) }
            )
        }

        composable(Screen.Tasks.route) {
            TasksScreen(
                onNavigateBack = { navController.popBackStack() },
                onCreateTask = { navController.navigate(Screen.CreateEditTask.createRoute()) },
                onEditTask = { taskId -> navController.navigate(Screen.CreateEditTask.createRoute(taskId)) }
            )
        }

        composable(Screen.Subjects.route) {
            SubjectsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToTopics = { id, name ->
                    navController.navigate(Screen.Topics.createRoute(id, name))
                }
            )
        }

        composable(
            route = Screen.Topics.route,
            arguments = listOf(
                navArgument("subjectId") { type = NavType.StringType },
                navArgument("subjectName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getString("subjectId") ?: ""
            val subjectName = java.net.URLDecoder.decode(
                backStackEntry.arguments?.getString("subjectName") ?: "", "UTF-8"
            )
            TopicsScreen(
                subjectId = subjectId,
                subjectName = subjectName,
                onNavigateBack = { navController.popBackStack() },
                onCreateTask = { navController.navigate(Screen.CreateEditTask.createRoute()) }
            )
        }

        composable(
            route = Screen.CreateEditTask.route,
            arguments = listOf(
                navArgument("taskId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")
            CreateEditTaskScreen(
                taskId = taskId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Scheduler.route) {
            SchedulerScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Goals.route) {
            GoalsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Notifications.route) {
            NotificationsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Analytics.route) {
            AnalyticsScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
