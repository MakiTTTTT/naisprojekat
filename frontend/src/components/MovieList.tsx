import React from 'react';
import MovieCard from './MovieCard';
import { SearchResult } from '../services/api';

interface MovieListProps {
  movies: SearchResult[];
  isLoading?: boolean;
  onMovieClick?: (movie: SearchResult) => void;
  onEditMovie?: (movie: SearchResult) => void;
  onDeleteMovie?: (movieId: string) => void;
  isDeletingId?: string;
  emptyMessage?: string;
}

const MovieList: React.FC<MovieListProps> = ({
  movies,
  isLoading = false,
  onMovieClick,
  onEditMovie,
  onDeleteMovie,
  isDeletingId,
  emptyMessage = 'No movies found. Try a different search query.',
}) => {
  if (isLoading) {
    return (
      <div className="movie-list loading">
        <p>Searching for movies...</p>
      </div>
    );
  }

  if (movies.length === 0) {
    return (
      <div className="movie-list empty">
        <p>{emptyMessage}</p>
      </div>
    );
  }

  return (
    <div className="movie-list">
      <p className="results-count">Found {movies.length} movie(s)</p>
      <div className="movie-grid">
        {movies.map((movie) => (
          <MovieCard
            key={movie.id}
            movie={movie}
            onClick={onMovieClick}
            onEdit={onEditMovie}
            onDelete={onDeleteMovie}
            isDeleting={isDeletingId === movie.id}
          />
        ))}
      </div>
    </div>
  );
};

export default MovieList;
