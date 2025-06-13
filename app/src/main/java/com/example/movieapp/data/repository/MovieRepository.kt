package com.example.movieapp.data.repository

import android.util.Log
import com.example.movieapp.data.model.MovieResponse
import com.example.movieapp.data.remote.MovieApi
import javax.inject.Inject
import javax.inject.Named

private const val TAG = "MovieRepository"

class MovieRepository @Inject constructor(
    private val api: MovieApi,
    @Named("apiKey") private val apiKey: String
) {
    init {
        Log.d(TAG, "Initializing MovieRepository")
    }

    suspend fun getPopularMovies(page: Int): Result<MovieResponse> = try {
        val response = api.getPopularMovies(apiKey, page)
        response.results.take(3).forEachIndexed { index, movie ->
            Log.d(TAG, "Movie ${index + 1}: ${movie.title} (ID: ${movie.id})")
        }
        
        Result.success(response)
    } catch (e: Exception) {
        Log.e(TAG, "API request failed for page $page", e)
        Log.e(TAG, "Error message: ${e.message}")
        Log.e(TAG, "Error cause: ${e.cause}")
        Result.failure(e)
    }
}