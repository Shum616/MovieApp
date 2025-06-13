package com.example.movieapp.ui.screen.movielist

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.data.model.Movie
import com.example.movieapp.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

private const val TAG = "MoviesViewModel"

@HiltViewModel
class MoviesViewModel @Inject constructor(private val repository: MovieRepository) : ViewModel() {
    var movies by mutableStateOf<List<Movie>>(emptyList())
    var isLoading by mutableStateOf(false)
    var currentPage by mutableStateOf(1)
    var hasError by mutableStateOf(false)
    var errorMessage by mutableStateOf("")
    var totalPages by mutableStateOf(Int.MAX_VALUE)

    init {
        Log.d(TAG, "Initializing MoviesViewModel")
        viewModelScope.launch {
            Log.d(TAG, "Starting initial movie load")
            loadMovies()
        }
    }

    fun loadMovies() {
        if (currentPage > totalPages || isLoading) {
            Log.d(TAG, "Skipping loadMovies: currentPage=$currentPage, totalPages=$totalPages, isLoading=$isLoading")
            return
        }
        
        Log.d(TAG, "Starting to load movies for page $currentPage")
        isLoading = true
        hasError = false
        
        viewModelScope.launch {
            try {
                Log.d(TAG, "Requesting movies from repository")
                val result = repository.getPopularMovies(currentPage)
                
                result.getOrNull()?.let { response ->
                    Log.d(TAG, "Successfully received ${response.results.size} movies")
                    val newMovies = movies + response.results
                    Log.d(TAG, "Total movies after update: ${newMovies.size}")
                    movies = newMovies
                    totalPages = response.total_pages
                    currentPage++
                    Log.d(TAG, "Updated page info: currentPage=$currentPage, totalPages=$totalPages")
                } ?: run {
                    Log.e(TAG, "Failed to load movies: Result was null")
                    hasError = true
                    errorMessage = "Failed to load movies"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading movies", e)
                hasError = true
                errorMessage = e.message ?: "Unknown error occurred"
            } finally {
                isLoading = false
                Log.d(TAG, "Finished loading attempt. isLoading=$isLoading, hasError=$hasError")
            }
        }
    }
}
