package com.alura.desafiolibros.model;

import jakarta.persistence.*;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "autores")
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer birth_year;
    private Integer death_year;

    @ManyToMany(mappedBy = "authors")
    private List<Libro> libros = new ArrayList<>();

    public Autor() {
    }

    public Autor(DatosAutor datos) {
        this.name = datos.name();
        this.birth_year = datos.birth_year();
        this.death_year = datos.death_year();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBirth_year() {
        return birth_year;
    }

    public void setBirth_year(Integer birth_year) {
        this.birth_year = birth_year;
    }

    public Integer getDeath_year() {
        return death_year;
    }

    public void setDeath_year(Integer death_year) {
        this.death_year = death_year;
    }

    public List<Libro> getLibros() {
        return libros;
    }

    public void setLibros(List<Libro> libros) {
        this.libros = libros;
    }

    @Override
    public String toString() {
        return "Autor: " + name + " (" + birth_year + "-" + death_year + ")";
    }

    public String toFormattedString() {
        String librosStr = (libros != null && !libros.isEmpty())
                ? libros.stream()
                .map(libro -> "    📌 " + libro.getTitle())
                .collect(Collectors.joining("\n"))
                : "    (Sin libros registrados)";

        return "\n▂▂▂▂▂▂▂▂AUTOR▂▂▂▂▂▂▂▂\n" +
                "  ✍ Nombre: " + name + "\n" +
                "  🎂 Nacimiento: " + birth_year + "\n" +
                "  ✟ Fallecimiento: " + death_year + "\n" +
                "  Libros:\n" + librosStr + "\n" +
                "▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂";
    }
}
