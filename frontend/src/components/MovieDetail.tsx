import React from 'react';
import { SearchResult } from '../services/api';

interface MovieDetailProps {
  movie: SearchResult;
  onClose?: () => void;
}

const MovieDetail: React.FC<MovieDetailProps> = ({ movie, onClose }) => {
  return (
    <div className="movie-detail-overlay">
      <div className="movie-detail-modal">
        <button className="close-button" onClick={onClose}>×</button>

        <div className="detail-header">
          <h1 className="detail-title">{movie.title}</h1>
          <div className="detail-meta">
            <span className="year">{movie.year}</span>
            <span className="genre">{movie.genre}</span>
            {movie.rating && <span className="rating">★ {movie.rating.toFixed(1)}/10</span>}
          </div>
        </div>

        {movie.director && (
          <div className="detail-section">
            <h3>Director</h3>
            <p>{movie.director}</p>
          </div>
        )}

        <div className="detail-section">
          <h3>Summary</h3>
          <p className="description">{movie.description}</p>
        </div>

        {movie.similarity !== undefined && (
          <div className="detail-section">
            <h3>Match Quality</h3>
            <div className="similarity-meter">
              <div
                className="similarity-bar"
                style={{ width: `${(movie.similarity * 100).toFixed(1)}%` }}
              />
              <span className="similarity-text">
                {(movie.similarity * 100).toFixed(1)}% similar to your search
              </span>
            </div>
          </div>
        )}

        {movie.releaseDate && (
          <div className="detail-section">
            <h3>Release Date</h3>
            <p>{new Date(movie.releaseDate).toLocaleDateString()}</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default MovieDetail;
