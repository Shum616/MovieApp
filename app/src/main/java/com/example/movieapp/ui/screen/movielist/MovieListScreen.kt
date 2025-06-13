package com.example.movieapp.ui.screen.movielist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.movieapp.data.model.Movie

@Composable
fun MovieListScreen(
    onMovieClick: (Int) -> Unit
) {
    val viewModel: MoviesViewModel = hiltViewModel()
    val movies = viewModel.movies
    val isLoading = viewModel.isLoading
    val hasError = viewModel.hasError
    val errorMessage = viewModel.errorMessage

    Box(modifier = Modifier.fillMaxSize()) {
        if (hasError) {
            Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(errorMessage)
                Button(onClick = { viewModel.loadMovies() }) { Text("Повторити") }
            }
        } else {
            LazyColumn {
                itemsIndexed(movies) { index, movie ->
                    MovieItem(movie = movie, onClick = { onMovieClick(movie.id) })
                    if (index == movies.size - 1) viewModel.loadMovies()
                }
                if (isLoading) {
                    item {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            }
        }
    }
}
