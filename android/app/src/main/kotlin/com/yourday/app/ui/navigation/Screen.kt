package com.yourday.app.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Tasks : Screen("tasks")
    object Subjects : Screen("subjects")
    object Topics : Screen("topics/{subjectId}/{subjectName}") {
        fun createRoute(subjectId: String, subjectName: String) =
            "topics/$subjectId/${subjectName.encodeUrl()}"
    }
    object CreateEditTask : Screen("create_edit_task?taskId={taskId}") {
        fun createRoute(taskId: String? = null) =
            if (taskId != null) "create_edit_task?taskId=$taskId" else "create_edit_task"
    }
    object Scheduler : Screen("scheduler")
    object Goals : Screen("goals")
    object Notifications : Screen("notifications")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object Analytics : Screen("analytics")
}

fun String.encodeUrl(): String = java.net.URLEncoder.encode(this, "UTF-8")
