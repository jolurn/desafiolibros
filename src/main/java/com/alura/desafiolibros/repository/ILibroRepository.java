package com.alura.desafiolibros.repository;

import com.alura.desafiolibros.model.Autor;
import com.alura.desafiolibros.model.Lenguajes;
import com.alura.desafiolibros.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ILibroRepository extends JpaRepository<Libro, Long> {
    Optional<Libro> findByTitleContainsIgnoreCase(String nombreLibro);

    Optional<Libro> findByTitleIgnoreCase(String title);

    @Query("SELECT DISTINCT l FROM Libro l LEFT JOIN FETCH l.authors")
    List<Libro> findAllWithAuthors();

    @Query("SELECT DISTINCT l FROM Libro l JOIN FETCH l.authors a " +
            "WHERE a.birth_year <= :anio AND " +
            "(a.death_year IS NULL OR a.death_year >= :anio)")
    List<Libro> findLibrosConAutoresVivosEnAnio(@Param("anio") int anio);

    List<Libro> findByLanguages(Lenguajes lenguaje);

    @Query("SELECT COUNT(l), SUM(l.download_count) FROM Libro l WHERE l.languages = :lenguaje")
    Object[] getEstadisticasIdioma(@Param("lenguaje") Lenguajes lenguaje);
}
