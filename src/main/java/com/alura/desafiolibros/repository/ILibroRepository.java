package com.alura.desafiolibros.repository;

import com.alura.desafiolibros.model.Autor;
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
}
