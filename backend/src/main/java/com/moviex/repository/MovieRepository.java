package com.moviex.repository;

import com.moviex.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, String> {

    List<Movie> findByGenre(String genre);

    List<Movie> findByYearBetween(Integer minYear, Integer maxYear);

    List<Movie> findByRatingGreaterThanEqual(Double rating);

    @Query("SELECT m FROM Movie m WHERE m.genre = :genre AND m.year >= :minYear AND m.year <= :maxYear")
    List<Movie> findByGenreAndYearRange(@Param("genre") String genre,
                                         @Param("minYear") Integer minYear,
                                         @Param("maxYear") Integer maxYear);

    @Query("SELECT m FROM Movie m WHERE m.genre = :genre AND m.rating >= :minRating")
    List<Movie> findByGenreAndMinRating(@Param("genre") String genre,
                                        @Param("minRating") Double minRating);

    @Query("SELECT DISTINCT m.genre FROM Movie m ORDER BY m.genre")
    List<String> findAllGenres();
}
