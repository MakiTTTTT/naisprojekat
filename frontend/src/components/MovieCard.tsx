import React from 'react';
import { SearchResult } from '../services/api';

interface MovieCardProps {
  movie: SearchResult;
  onClick?: (movie: SearchResult) => void;
  onEdit?: (movie: SearchResult) => void;
  onDelete?: (movieId: string) => void;
  isDeleting?: boolean;
}

const MovieCard: React.FC<MovieCardProps> = ({ movie, onClick, onEdit, onDelete, isDeleting = false }) => {
  const handleClick = () => {
    if (onClick) {
      onClick(movie);
    }
  };

  const handleEdit = (e: React.MouseEvent) => {
    e.stopPropagation();
    if (onEdit) {
      onEdit(movie);
    }
  };

  const handleDelete = (e: React.MouseEvent) => {
    e.stopPropagation();
    if (onDelete && window.confirm(`Are you sure you want to delete "${movie.title}"?`)) {
      onDelete(movie.id);
    }
  };

  const similarityPercent = movie.similarity ? (movie.similarity * 100).toFixed(1) : '0';

  return (
    <div className="movie-card" onClick={handleClick}>
      <div className="movie-header">
        <h3 className="movie-title">{movie.title}</h3>
        {movie.similarity !== undefined && (
          <span className="similarity-badge">
            {similarityPercent}% match
          </span>
        )}
      </div>

      <div className="movie-meta">
        <span className="movie-year">{movie.year}</span>
        <span className="movie-genre">{movie.genre}</span>
        {movie.rating && (
          <span className="movie-rating">★ {movie.rating.toFixed(1)}</span>
        )}
      </div>

      <p className="movie-description">
        {movie.description.substring(0, 150)}
        {movie.description.length > 150 ? '...' : ''}
      </p>

      {movie.director && (
        <p className="movie-director">
          <strong>Director:</strong> {movie.director}
        </p>
      )}

      {(onEdit || onDelete) && (
        <div className="movie-actions">
          {onEdit && (
            <button
              className="action-button edit-button"
              onClick={handleEdit}
              title="Edit movie"
              disabled={isDeleting}
            >
              ✏️ Edit
            </button>
          )}
          {onDelete && (
            <button
              className="action-button delete-button"
              onClick={handleDelete}
              title="Delete movie"
              disabled={isDeleting}
            >
              {isDeleting ? '⏳' : '🗑️'} Delete
            </button>
          )}
        </div>
      )}
    </div>
  );
};

export default MovieCard;
