package com.alura.desafiolibros.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String title;
    @Enumerated(EnumType.STRING)
    private Lenguajes languages;
    private Integer download_count;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(
            name = "libro_autor",
            joinColumns = @JoinColumn(name = "libro_id"),
            inverseJoinColumns = @JoinColumn(name = "autor_id")
    )
    private List<Autor> authors = new ArrayList<>();

    public Libro() {
    }

    public Libro(DatosLibro datosLibro) {
        this.title = datosLibro.title();
        this.languages = Lenguajes.fromString(datosLibro.languages().split(",")[0].trim());
        this.download_count = datosLibro.download_count();

        // ▶ Toma solo el PRIMER autor (si existe)
        if (!datosLibro.authors().isEmpty()) {
            DatosAutor primerAutor = datosLibro.authors().get(0);
            this.authors = List.of(new Autor(primerAutor));
        } else {
            this.authors = new ArrayList<>();
        }

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Lenguajes getLanguages() {
        return languages;
    }

    public void setLanguages(Lenguajes languages) {
        this.languages = languages;
    }

    public Integer getDownload_count() {
        return download_count;
    }

    public void setDownload_count(Integer download_count) {
        this.download_count = download_count;
    }

    public List<Autor> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Autor> authors) {
        this.authors = authors;
    }

    @Override
    public String toString() {
        String autorStr = authors.stream()
                .map(autor -> "    └─ Autor: " + autor.getName() +
                        " (" + autor.getBirth_year() + "-" + autor.getDeath_year() + ")")
                .collect(Collectors.joining("\n"));

        return "═══════════════LIBRO════════════════\n" +
                "  📖 Título: " + title + "\n" +
                autorStr + "\n" +
                "  🌐 Idioma: " + languages + "\n" +
                "  ⬇ Descargas: " + download_count + "\n" +
                "════════════════════════════════════";
    }
}
