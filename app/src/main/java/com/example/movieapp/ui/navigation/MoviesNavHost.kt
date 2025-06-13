package com.example.movieapp.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.movieapp.ui.screen.moviedetail.MovieDetailScreen
import com.example.movieapp.ui.screen.movielist.MovieListScreen
import com.example.movieapp.ui.screen.movielist.MoviesViewModel

private const val TAG = "MoviesNavHost"

sealed class Screen(val route: String) {
    object MovieList : Screen("movie_list")
    data class MovieDetail(val movieId: Int) : Screen("movie_detail/{movieId}") {
        fun createRoute(movieId: Int) = "movie_detail/$movieId"
    }
}

@Composable
fun MoviesNavHost(navController: NavHostController, moviesViewModel: MoviesViewModel) {
    Log.d(TAG, "Initializing MoviesNavHost")
    
    NavHost(navController, startDestination = Screen.MovieList.route) {
        composable(Screen.MovieList.route) {
            Log.d(TAG, "Navigating to MovieList screen")
            MovieListScreen(
                onMovieClick = { movieId -> 
                    Log.d(TAG, "Movie clicked, navigating to detail screen for movie ID: $movieId")
                    navController.navigate(Screen.MovieDetail(movieId).createRoute(movieId)) 
                }
            )
        }

        
        composable(
            route = "movie_detail/{movieId}",
            arguments = listOf(navArgument("movieId") { type = NavType.IntType })
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: run {
                Log.e(TAG, "Movie ID not found in arguments")
                return@composable
            }
            Log.d(TAG, "Navigating to MovieDetail screen for movie ID: $movieId")
            
            val movie = moviesViewModel.movies.find { it.id == movieId }
            if (movie != null) {
                MovieDetailScreen(
                    movie = movie,
                    onBackClick = {
                        Log.d(TAG, "Back button clicked, navigating back to list")
                        navController.navigateUp()
                    }
                )
            } else {
                Log.e(TAG, "Movie not found for ID: $movieId")
            }
        }
    }
} 