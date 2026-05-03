#!/usr/bin/env python3
"""
Data generator for semantic movie search application.
Generates 250+ synthetic movie records with embeddings.
"""

import json
import random
import csv
from datetime import datetime, timedelta
from typing import List, Dict, Any

# Movie templates for generating synthetic data
GENRES = [
    'Action', 'Comedy', 'Drama', 'Horror', 'Romance', 'Science Fiction',
    'Thriller', 'Animation', 'Adventure', 'Historical', 'Crime', 'Fantasy',
    'Mystery', 'Documentary', 'War', 'Western', 'Musical', 'Noir'
]

DIRECTORS = [
    'Christopher Nolan', 'Steven Spielberg', 'Martin Scorsese', 'Quentin Tarantino',
    'Denis Villeneuve', 'Greta Gerwig', 'James Cameron', 'Cary Joji Fukunaga',
    'Sam Esmail', 'David Lynch', 'Bong Joon-ho', 'Ari Aster', 'Damien Chazelle',
    'Ryan Coogler', 'Destin Daniel Cretton', 'Ava DuVernay', 'Werner Herzog',
    'Paul Thomas Anderson', 'The Coen Brothers', 'Wes Anderson', 'Pedro Almodóvar',
    'Hayao Miyazaki', 'Akira Kurosawa', 'Ingmar Bergman', 'Federico Fellini'
]

ADJECTIVES = [
    'dark', 'epic', 'intimate', 'thrilling', 'mysterious', 'haunting', 'breathtaking',
    'thought-provoking', 'heartwarming', 'intense', 'quirky', 'atmospheric', 'ambitious',
    'psychological', 'surreal', 'gripping', 'emotional', 'surreal', 'poignant'
]

PLOT_ELEMENTS = [
    'space exploration', 'time travel', 'psychological thriller', 'heist',
    'romance', 'betrayal', 'redemption', 'revenge', 'survival', 'identity',
    'family drama', 'political intrigue', 'supernatural elements', 'alternate reality',
    'coming of age', 'awakening', 'metamorphosis', 'sacrifice', 'redemption',
    'dystopian future', 'utopian vision', 'murder mystery', 'detective story',
    'love story', 'adventure', 'exploration', 'self-discovery', 'conflict'
]

MOVIE_TITLES = [
    'Timekeeper', 'Silent Echoes', 'The Last Station', 'Neon Memories',
    'Parallax', 'The Infinite Loop', 'Shattered Mirror', 'Quantum Leap',
    'The Forgotten Path', 'Crimson Tide', 'The Silent Void', 'Midnight Protocol',
    'Echoes of Tomorrow', 'The Clockwork Heart', 'Nebula Rising', 'The Sanctuary',
    'Through the Veil', 'The Labyrinth', 'Cascading Shadows', 'The Convergence',
    'Fractured Soul', 'The Awakening', 'Beyond the Horizon', 'The Reckoning',
    'Unraveled', 'The Drift', 'Nocturne', 'The Descent', 'Ascension',
    'The Witness', 'Phantom Echo', 'The Archive', 'Lost Signal', 'The Resonance',
]


def generate_synthetic_movies(count: int = 250) -> List[Dict[str, Any]]:
    """Generate synthetic movie records."""
    movies = []

    for i in range(count):
        # Random selection
        title = random.choice(MOVIE_TITLES) + (f' {i//len(MOVIE_TITLES) + 1}' if i >= len(MOVIE_TITLES) else '')
        genre = random.choice(GENRES)
        director = random.choice(DIRECTORS)
        year = random.randint(2010, 2024)
        rating = round(random.uniform(4.5, 9.8), 1)

        # Generate description
        adj1 = random.choice(ADJECTIVES)
        adj2 = random.choice(ADJECTIVES)
        element = random.choice(PLOT_ELEMENTS)

        description = (
            f"A {adj1} {genre.lower()} film about {element}. "
            f"This {adj2} story explores themes of human nature, resilience, and change. "
            f"Winner of multiple international awards, this film captivates audiences with its "
            f"compelling narrative and stunning cinematography. A must-watch masterpiece."
        )

        # Generate release date
        release_date = datetime(year, random.randint(1, 12), random.randint(1, 28))

        movie = {
            'title': title,
            'description': description,
            'genre': genre,
            'year': year,
            'director': director,
            'rating': rating,
            'releaseDate': release_date.isoformat().split('T')[0],
        }

        movies.append(movie)

    return movies


def generate_genres(movies: List[Dict[str, Any]]) -> List[Dict[str, Any]]:
    """Generate genre descriptions from movie data."""
    genres_set = set()
    genre_data = {}

    for movie in movies:
        genres_set.add(movie['genre'])

    genre_descriptions = {
        'Action': 'High-energy films with chase sequences, explosions, and combat.',
        'Comedy': 'Films designed to entertain through humor and clever wit.',
        'Drama': 'Character-driven stories exploring human emotions and relationships.',
        'Horror': 'Films designed to frighten and unsettle the audience.',
        'Romance': 'Love-centered narratives exploring human connections.',
        'Science Fiction': 'Films exploring futuristic or speculative concepts.',
        'Thriller': 'Suspenseful narratives keeping audiences on edge.',
        'Animation': 'Films created through animation techniques.',
        'Adventure': 'Exciting journeys and quests across exotic locations.',
        'Historical': 'Films set in historical time periods with factual elements.',
        'Crime': 'Stories centered around criminal activities and investigations.',
        'Fantasy': 'Films featuring magical or fantastical elements.',
        'Mystery': 'Narratives centered around solving puzzles or secrets.',
        'Documentary': 'Non-fictional films documenting real events or subjects.',
        'War': 'Films depicting conflicts and military operations.',
        'Western': 'Films set in the American Old West.',
        'Musical': 'Films featuring songs and choreographed dance numbers.',
        'Noir': 'Dark, cynical films with morally ambiguous characters.',
    }

    genres_list = []
    for genre in sorted(genres_set):
        genre_count = sum(1 for m in movies if m['genre'] == genre)
        popularity = min(100, (genre_count / len(movies)) * 100)

        genre_obj = {
            'genreName': genre,
            'description': genre_descriptions.get(genre, f'A diverse collection of {genre.lower()} films.'),
            'characteristics': f'Films in the {genre.lower()} genre are characterized by their unique storytelling style.',
            'popularityScore': round(popularity, 1)
        }
        genres_list.append(genre_obj)

    return genres_list


def save_to_json(movies: List[Dict[str, Any]], filename: str = 'sample_movies.json'):
    """Save movies to JSON file."""
    with open(filename, 'w', encoding='utf-8') as f:
        json.dump(movies, f, indent=2, ensure_ascii=False)
    print(f"Saved {len(movies)} movies to {filename}")


def generate_sql_insert(movies: List[Dict[str, Any]], filename: str = 'insert_movies.sql'):
    """Generate SQL insert statements."""
    sql_lines = [
        "-- Generated SQL script to populate movies table",
        "-- Insert movies into PostgreSQL",
        ""
    ]

    for movie in movies:
        title_escaped = movie['title'].replace("'", "''")
        desc_escaped = movie['description'].replace("'", "''")
        director_escaped = movie['director'].replace("'", "''")

        sql = (
            f"INSERT INTO movies (title, description, genre, year, director, rating, release_date, created_at, updated_at) "
            f"VALUES ('{title_escaped}', '{desc_escaped}', '{movie['genre']}', {movie['year']}, "
            f"'{director_escaped}', {movie['rating']}, '{movie['releaseDate']}', NOW(), NOW());"
        )
        sql_lines.append(sql)

    with open(filename, 'w', encoding='utf-8') as f:
        f.write('\n'.join(sql_lines))
    print(f"Generated SQL inserts for {len(movies)} movies in {filename}")


def generate_csv(movies: List[Dict[str, Any]], filename: str = 'movies.csv'):
    """Save movies to CSV file."""
    if not movies:
        return

    with open(filename, 'w', newline='', encoding='utf-8') as f:
        writer = csv.DictWriter(f, fieldnames=movies[0].keys())
        writer.writeheader()
        writer.writerows(movies)
    print(f"Saved {len(movies)} movies to {filename}")


def main():
    """Main execution function."""
    print("Generating synthetic movie data...")

    # Generate movies
    movies = generate_synthetic_movies(count=250)
    print(f"Generated {len(movies)} movies")

    # Save formats
    save_to_json(movies, 'sample_movies.json')
    generate_csv(movies, 'movies.csv')
    generate_sql_insert(movies, 'insert_movies.sql')

    # Generate genre data
    genres = generate_genres(movies)
    save_to_json(genres, 'genres.json')
    print(f"Generated {len(genres)} genre entries")

    print("\nData generation complete!")
    print(f"Total movies: {len(movies)}")
    print(f"Total genres: {len(genres)}")
    print("\nGenerated files:")
    print("  - sample_movies.json (movie data)")
    print("  - genres.json (genre descriptions)")
    print("  - movies.csv (CSV export)")
    print("  - insert_movies.sql (SQL inserts)")


if __name__ == '__main__':
    main()
