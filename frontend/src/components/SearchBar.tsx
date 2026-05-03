import React, { useState } from 'react';
import { SearchRequest } from '../services/api';

interface SearchBarProps {
  onSearch: (query: string, filters?: SearchRequest) => void;
  isLoading?: boolean;
}

const SearchBar: React.FC<SearchBarProps> = ({ onSearch, isLoading = false }) => {
  const [query, setQuery] = useState('');
  const [showFilters, setShowFilters] = useState(false);
  const [filters, setFilters] = useState({
    minYear: '',
    maxYear: '',
    minRating: '',
    maxRating: '',
    genre: '',
  });

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (query.trim()) {
      const searchFilters: SearchRequest = { query };
      
      if (filters.minYear) searchFilters.minYear = parseInt(filters.minYear);
      if (filters.maxYear) searchFilters.maxYear = parseInt(filters.maxYear);
      if (filters.minRating) searchFilters.minRating = parseFloat(filters.minRating);
      if (filters.maxRating) searchFilters.maxRating = parseFloat(filters.maxRating);
      if (filters.genre) searchFilters.genre = filters.genre;
      
      onSearch(query, searchFilters);
    }
  };

  const handleFilterChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFilters((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const clearFilters = () => {
    setFilters({
      minYear: '',
      maxYear: '',
      minRating: '',
      maxRating: '',
      genre: '',
    });
  };

  const hasActiveFilters = Object.values(filters).some(v => v !== '');

  const genres = [
    'Action', 'Adventure', 'Comedy', 'Drama', 'Fantasy',
    'Horror', 'Mystery', 'Romance', 'Science Fiction', 'Thriller'
  ];

  return (
    <form onSubmit={handleSubmit} className="search-bar">
      <div className="search-input-wrapper">
        <input
          type="text"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Describe a movie (e.g., 'dark sci-fi about space exploration')..."
          className="search-input"
          disabled={isLoading}
        />
        <button
          type="button"
          className="filter-toggle-button"
          onClick={() => setShowFilters(!showFilters)}
          title="Toggle filters"
        >
          ⚙️ Filters
        </button>
        <button
          type="submit"
          className="search-button"
          disabled={isLoading || !query.trim()}
        >
          {isLoading ? 'Searching...' : 'Search'}
        </button>
      </div>

      {showFilters && (
        <div className="filters-panel">
          <div className="filters-header">
            <h4>Advanced Filters</h4>
            {hasActiveFilters && (
              <button
                type="button"
                className="clear-filters-button"
                onClick={clearFilters}
              >
                Clear All
              </button>
            )}
          </div>

          <div className="filters-grid">
            <div className="filter-group">
              <label htmlFor="genre">Genre</label>
              <select
                id="genre"
                name="genre"
                value={filters.genre}
                onChange={handleFilterChange}
              >
                <option value="">All Genres</option>
                {genres.map((g) => (
                  <option key={g} value={g}>
                    {g}
                  </option>
                ))}
              </select>
            </div>

            <div className="filter-group">
              <label htmlFor="minYear">Year From</label>
              <input
                id="minYear"
                type="number"
                name="minYear"
                value={filters.minYear}
                onChange={handleFilterChange}
                placeholder="1900"
                min="1800"
                max="2100"
              />
            </div>

            <div className="filter-group">
              <label htmlFor="maxYear">Year To</label>
              <input
                id="maxYear"
                type="number"
                name="maxYear"
                value={filters.maxYear}
                onChange={handleFilterChange}
                placeholder="2100"
                min="1800"
                max="2100"
              />
            </div>

            <div className="filter-group">
              <label htmlFor="minRating">Min Rating</label>
              <input
                id="minRating"
                type="number"
                name="minRating"
                value={filters.minRating}
                onChange={handleFilterChange}
                placeholder="0.0"
                min="0"
                max="10"
                step="0.5"
              />
            </div>

            <div className="filter-group">
              <label htmlFor="maxRating">Max Rating</label>
              <input
                id="maxRating"
                type="number"
                name="maxRating"
                value={filters.maxRating}
                onChange={handleFilterChange}
                placeholder="10.0"
                min="0"
                max="10"
                step="0.5"
              />
            </div>
          </div>
        </div>
      )}
    </form>
  );
};

export default SearchBar;
