package com.middara.helper.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.middara.helper.ui.screens.HeroHandScreen
import com.middara.helper.ui.screens.HeroListScreen

object Routes {
    const val HERO_LIST = "heroes"
    const val HERO_HAND = "hero/{heroId}"
    fun heroHand(heroId: String) = "hero/$heroId"
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HERO_LIST
    ) {
        composable(Routes.HERO_LIST) {
            HeroListScreen(
                onHeroClick = { heroId ->
                    navController.navigate(Routes.heroHand(heroId))
                }
            )
        }

        composable(
            route = Routes.HERO_HAND,
            arguments = listOf(navArgument("heroId") { type = NavType.StringType })
        ) {
            HeroHandScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
