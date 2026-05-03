import React, { useState, useEffect } from 'react';
import { Movie } from '../services/api';

interface MovieFormProps {
  movie?: Movie;
  onSubmit: (movie: Partial<Movie>) => Promise<void>;
  onCancel: () => void;
  isLoading?: boolean;
}

const MovieForm: React.FC<MovieFormProps> = ({ movie, onSubmit, onCancel, isLoading = false }) => {
  const [formData, setFormData] = useState<Partial<Movie>>({
    title: '',
    description: '',
    genre: '',
    year: new Date().getFullYear(),
    director: '',
    rating: 5.0,
  });

  const [error, setError] = useState<string>('');

  const genres = [
    'Action', 'Adventure', 'Animation', 'Comedy', 'Crime',
    'Documentary', 'Drama', 'Family', 'Fantasy', 'Horror',
    'Musical', 'Mystery', 'Romance', 'Science Fiction', 'Thriller',
    'War', 'Western', 'Sci-Fi'
  ];

  useEffect(() => {
    if (movie) {
      setFormData({
        title: movie.title,
        description: movie.description,
        genre: movie.genre,
        year: movie.year,
        director: movie.director,
        rating: movie.rating,
      });
    }
  }, [movie]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: name === 'year' ? parseInt(value) : name === 'rating' ? parseFloat(value) : value,
    }));
    setError('');
  };

  const validateForm = (): boolean => {
    if (!formData.title || !formData.title.trim()) {
      setError('Title is required');
      return false;
    }
    if (!formData.description || !formData.description.trim()) {
      setError('Description is required');
      return false;
    }
    if (!formData.genre) {
      setError('Genre is required');
      return false;
    }
    if (!formData.year || formData.year < 1800 || formData.year > 2100) {
      setError('Year must be between 1800 and 2100');
      return false;
    }
    if (!formData.director || !formData.director.trim()) {
      setError('Director is required');
      return false;
    }
    if (!formData.rating || formData.rating < 0 || formData.rating > 10) {
      setError('Rating must be between 0 and 10');
      return false;
    }
    return true;
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    try {
      await onSubmit(formData);
    } catch (err) {
      console.error('Form submission error:', err);
    }
  };

  return (
    <div className="modal-overlay" onClick={onCancel}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>{movie ? 'Edit Movie' : 'Add New Movie'}</h2>
          <button className="close-button" onClick={onCancel}>×</button>
        </div>

        <form onSubmit={handleSubmit} className="movie-form">
          {error && <div className="form-error">{error}</div>}

          <div className="form-group">
            <label htmlFor="title">Title *</label>
            <input
              type="text"
              id="title"
              name="title"
              value={formData.title || ''}
              onChange={handleChange}
              placeholder="Movie title"
              disabled={isLoading}
              maxLength={255}
            />
          </div>

          <div className="form-group">
            <label htmlFor="genre">Genre *</label>
            <select
              id="genre"
              name="genre"
              value={formData.genre || ''}
              onChange={handleChange}
              disabled={isLoading}
            >
              <option value="">Select a genre</option>
              {genres.map((g) => (
                <option key={g} value={g}>
                  {g}
                </option>
              ))}
            </select>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="year">Year *</label>
              <input
                type="number"
                id="year"
                name="year"
                value={formData.year || ''}
                onChange={handleChange}
                min="1800"
                max="2100"
                disabled={isLoading}
              />
            </div>

            <div className="form-group">
              <label htmlFor="rating">Rating (0-10) *</label>
              <input
                type="number"
                id="rating"
                name="rating"
                value={formData.rating || ''}
                onChange={handleChange}
                min="0"
                max="10"
                step="0.1"
                disabled={isLoading}
              />
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="director">Director *</label>
            <input
              type="text"
              id="director"
              name="director"
              value={formData.director || ''}
              onChange={handleChange}
              placeholder="Director name"
              disabled={isLoading}
              maxLength={255}
            />
          </div>

          <div className="form-group">
            <label htmlFor="description">Description *</label>
            <textarea
              id="description"
              name="description"
              value={formData.description || ''}
              onChange={handleChange}
              placeholder="Movie description"
              disabled={isLoading}
              rows={6}
              maxLength={2000}
            />
            <small className="char-count">
              {(formData.description || '').length}/2000
            </small>
          </div>

          <div className="form-actions">
            <button
              type="button"
              className="button-secondary"
              onClick={onCancel}
              disabled={isLoading}
            >
              Cancel
            </button>
            <button
              type="submit"
              className="button-primary"
              disabled={isLoading}
            >
              {isLoading ? 'Saving...' : movie ? 'Update Movie' : 'Add Movie'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default MovieForm;
