import React, { useState, useEffect } from 'react';
import SearchBar from '../components/SearchBar';
import MovieList from '../components/MovieList';
import MovieForm from '../components/MovieForm';
import MovieDetail from '../components/MovieDetail';
import { searchAPI, movieAPI, SearchResult, Movie, SearchRequest } from '../services/api';

type ViewMode = 'search' | 'browse';

const HomePage: React.FC = () => {
  const [results, setResults] = useState<SearchResult[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string>('');
  const [lastQuery, setLastQuery] = useState<string>('');
  const [viewMode, setViewMode] = useState<ViewMode>('search');
  const [isDeletingId, setIsDeletingId] = useState<string>('');

  // Modal states
  const [showMovieForm, setShowMovieForm] = useState(false);
  const [editingMovie, setEditingMovie] = useState<Movie | undefined>();
  const [selectedMovie, setSelectedMovie] = useState<SearchResult | undefined>();

  // Load all movies on component mount for browse mode
  useEffect(() => {
    if (viewMode === 'browse') {
      loadAllMovies();
    }
  }, [viewMode]);

  const loadAllMovies = async () => {
    setIsLoading(true);
    setError('');
    try {
      const response = await movieAPI.getAllMovies();
      // Convert Movie[] to SearchResult[] by adding similarity field
      const moviesWithSimilarity: SearchResult[] = response.data.map((movie: Movie) => ({
        ...movie,
        similarity: 0, // No similarity score for browse view
      }));
      setResults(moviesWithSimilarity);
      setLastQuery('');
    } catch (err) {
      setError('Failed to load movies. Please try again.');
      console.error(err);
      setResults([]);
    } finally {
      setIsLoading(false);
    }
  };

  const handleSearch = async (query: string, filters?: SearchRequest) => {
    setIsLoading(true);
    setError('');
    setLastQuery(query);

    try {
      const searchRequest: SearchRequest = {
        query,
        topK: 20,
        ...filters,
      };
      const response = await searchAPI.hybridSearch(searchRequest);
      setResults(response.data);
    } catch (err) {
      setError('Failed to search movies. Please try again.');
      console.error(err);
      setResults([]);
    } finally {
      setIsLoading(false);
    }
  };

  const handleCreateMovie = async (movieData: Partial<Movie>) => {
    setIsLoading(true);
    try {
      await movieAPI.createMovie(movieData);
      setShowMovieForm(false);
      setError('');
      
      // Refresh the current view
      if (viewMode === 'browse') {
        await loadAllMovies();
      }
      
      // Show success message
      alert('Movie created successfully!');
    } catch (err) {
      setError('Failed to create movie. Please try again.');
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleUpdateMovie = async (movieData: Partial<Movie>) => {
    if (!editingMovie) return;

    setIsLoading(true);
    try {
      await movieAPI.updateMovie(editingMovie.id, movieData);
      setShowMovieForm(false);
      setEditingMovie(undefined);
      setError('');

      // Refresh the current view
      if (viewMode === 'browse') {
        await loadAllMovies();
      } else if (viewMode === 'search' && lastQuery) {
        await handleSearch(lastQuery);
      }

      // Update selected movie if it's open
      if (selectedMovie?.id === editingMovie.id) {
        setSelectedMovie(undefined);
      }

      alert('Movie updated successfully!');
    } catch (err) {
      setError('Failed to update movie. Please try again.');
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleDeleteMovie = async (movieId: string) => {
    setIsDeletingId(movieId);
    try {
      await movieAPI.deleteMovie(movieId);
      setError('');

      // Remove from results
      setResults(results.filter((m: { id: string; }) => m.id !== movieId));

      // Close detail view if deleting selected movie
      if (selectedMovie?.id === movieId) {
        setSelectedMovie(undefined);
      }

      alert('Movie deleted successfully!');
    } catch (err) {
      setError('Failed to delete movie. Please try again.');
      console.error(err);
    } finally {
      setIsDeletingId('');
    }
  };

  const openCreateForm = () => {
    setEditingMovie(undefined);
    setShowMovieForm(true);
  };

  const openEditForm = async (movie: SearchResult) => {
    try {
      const response = await movieAPI.getMovieById(movie.id);
      setEditingMovie(response.data);
      setShowMovieForm(true);
    } catch (err) {
      setError('Failed to load movie details for editing.');
      console.error(err);
    }
  };

  const handleCloseForm = () => {
    setShowMovieForm(false);
    setEditingMovie(undefined);
  };

  return (
    <div className="home-page">
      <header className="header">
        <h1>🎬 Semantic Movie Search</h1>
        <p>Describe a movie in natural language and find it instantly</p>
        
        <div className="header-actions">
          <button
            className={`view-toggle-button ${viewMode === 'search' ? 'active' : ''}`}
            onClick={() => setViewMode('search')}
          >
            🔍 Search
          </button>
          <button
            className={`view-toggle-button ${viewMode === 'browse' ? 'active' : ''}`}
            onClick={() => setViewMode('browse')}
          >
            📚 Browse
          </button>
          <button className="add-movie-button" onClick={openCreateForm}>
            ➕ Add Movie
          </button>
        </div>
      </header>

      <main className="main-content">
        {viewMode === 'search' && (
          <SearchBar onSearch={handleSearch} isLoading={isLoading} />
        )}

        {error && <div className="error-message">{error}</div>}

        {lastQuery && viewMode === 'search' && (
          <div className="search-info">
            <p>Results for: <strong>"{lastQuery}"</strong></p>
          </div>
        )}

        <MovieList
          movies={results}
          isLoading={isLoading}
          onMovieClick={setSelectedMovie}
          onEditMovie={openEditForm}
          onDeleteMovie={handleDeleteMovie}
          isDeletingId={isDeletingId}
          emptyMessage={
            viewMode === 'search' && lastQuery
              ? 'No movies found matching your query. Try a different description.'
              : viewMode === 'browse'
              ? 'No movies available. Click "Add Movie" to create one!'
              : 'Enter a movie description above to get started.'
          }
        />
      </main>

      {/* Movie Form Modal */}
      {showMovieForm && (
        <MovieForm
          movie={editingMovie}
          onSubmit={editingMovie ? handleUpdateMovie : handleCreateMovie}
          onCancel={handleCloseForm}
          isLoading={isLoading}
        />
      )}

      {/* Movie Detail Modal */}
      {selectedMovie && (
        <MovieDetail
          movie={selectedMovie}
          onClose={() => setSelectedMovie(undefined)}
        />
      )}

      <footer className="footer">
        <p>Powered by semantic vector search 🔍</p>
      </footer>
    </div>
  );
};

export default HomePage;
