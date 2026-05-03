import axios, { AxiosInstance } from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export interface Movie {
  id: string;
  title: string;
  description: string;
  genre: string;
  year: number;
  director: string;
  rating: number;
  releaseDate: string;
}

export interface SearchResult extends Movie {
  similarity: number;
}

export interface SearchRequest {
  query?: string;
  topK?: number;
  genre?: string;
  minYear?: number;
  maxYear?: number;
  minRating?: number;
  maxRating?: number;
  offset?: number;
  limit?: number;
}

export interface PaginatedResponse {
  results: SearchResult[];
  total: number;
  offset: number;
  limit: number;
  hasMore: boolean;
}

// CRUD Operations
export const movieAPI = {
  // Create
  createMovie: (movie: Partial<Movie>) => apiClient.post('/movies', movie),

  // Read
  getMovieById: (id: string) => apiClient.get(`/movies/${id}`),
  getAllMovies: () => apiClient.get('/movies'),

  // Update
  updateMovie: (id: string, movie: Partial<Movie>) => apiClient.put(`/movies/${id}`, movie),

  // Delete
  deleteMovie: (id: string) => apiClient.delete(`/movies/${id}`),

  // Search
  searchByGenre: (genre: string) => apiClient.get(`/movies/search/by-genre/${genre}`),
  getGenres: () => apiClient.get('/genres'),
};

// Search Operations
export const searchAPI = {
  // Vector search
  vectorSearch: (request: SearchRequest) => apiClient.post('/search/vector', request),

  // Filter search
  filterSearch: (request: SearchRequest) => apiClient.post('/search/filter', request),

  // Hybrid search
  hybridSearch: (request: SearchRequest) => apiClient.post('/search/hybrid', request),

  // Paginated search
  paginatedSearch: (query: string, offset: number = 0, limit: number = 20) =>
    apiClient.get('/search/paginated', {
      params: { query, offset, limit },
    }),
};

// Health check
export const healthAPI = {
  check: () => apiClient.get('/health'),
};

export default apiClient;
