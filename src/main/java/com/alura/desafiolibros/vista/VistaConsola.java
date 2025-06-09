package com.alura.desafiolibros.vista;

import com.alura.desafiolibros.model.*;
import com.alura.desafiolibros.repository.ILibroRepository;
import com.alura.desafiolibros.service.ConsumoAPI;
import com.alura.desafiolibros.service.ConvierteDatos;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.dao.DataIntegrityViolationException;

import java.util.*;
import java.util.stream.Collectors;


public class VistaConsola {

    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://gutendex.com/books";
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<Libro> libro;
    private Optional<Libro> libroBuscada;
    private List<DatosLibro> datosLibros = new ArrayList<>();

    private ILibroRepository repositorio;
    public VistaConsola(ILibroRepository repository) {
        this.repositorio = repository;
    }

    public void mostrarMenu() {
        var opcion = -1;
        while (opcion != 0) {
            System.out.println("\n=== MENÚ PRINCIPAL ===");
            System.out.println("1 - Buscar libro por título");
            System.out.println("2 - Listar libros registrados");
            System.out.println("3 - Listar autores registrados");
            System.out.println("4 - Listar autores vivos en un determinado año");
            System.out.println("5 - Listar libros por idioma");
            System.out.println("0 - Salir");
            System.out.print("Selecciona una opción: ");

            try {
                opcion = teclado.nextInt();
                teclado.nextLine();

                switch (opcion) {
                    case 1:
                        buscarLibrosPorTitulo();
                        break;
                    case 2:
                        listarLibrosRegistrado();
                        break;
                    case 3:
                        listarAutoresRegistrado();
                         break;
                    case 4:
                        listarAutoresVivosEnAnio();
                        break;
                    case 5:
                        listarLibrosPorIdioma();
                        break;
                    case 0:
                        System.out.println("👋 ¡Hasta pronto!");
                        break;
                    default:
                        System.out.println("⚠️ Opción inválida. Intenta nuevamente.");
                }
            } catch (InputMismatchException e) {
                System.out.println("❌ Error: Debes ingresar un número.");
                teclado.nextLine();
            }
        }
    }

    private DatosLibro getDatosLibros() {
        System.out.println("Escribe el nombre del Libro que deseas buscar");
        var tituloBusqueda = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + "/?search=" + tituloBusqueda.replace(" ", "+"));

        ObjectMapper mapper = new ObjectMapper(); // Jackson

        try {

            JsonNode rootNode = mapper.readTree(json);
            JsonNode resultsNode = rootNode.get("results");

            if (resultsNode == null || resultsNode.isEmpty()) {
                return null;
            }

            JsonNode primerLibroNode = resultsNode.get(0);

            DatosLibro datos = mapper.treeToValue(primerLibroNode, DatosLibro.class);

            return datos;

        } catch (Exception e) {
            throw new RuntimeException("Error al parsear JSON: " + e.getMessage(), e);
        }
    }

    private void buscarLibrosPorTitulo() {
        try {
            System.out.println("\n=== BUSCAR LIBRO ===");
            DatosLibro datos = getDatosLibros();

            if (datos == null) {
                System.out.println("❌ No se encontró el libro o hubo un error en la API");
                return;
            }

            Optional<Libro> existente = repositorio.findByTitleIgnoreCase(datos.title());
            if (existente.isPresent()) {
                System.out.println("⚠️ El libro ya está registrado:\n" + existente.get());
                return;
            }

            Libro libro = new Libro(datos);
            repositorio.save(libro);
            System.out.println("✅ Libro registrado exitosamente:\n" + datos);

        } catch (DataIntegrityViolationException e) {
            System.out.println("❌ Error: El libro ya existe en la base de datos");
        } catch (Exception e) {
            System.out.println("❌ Error inesperado: " + e.getMessage());
        }
    }

    private void listarLibrosRegistrado() {
        System.out.println("\n=== CATÁLOGO COMPLETO ===");

        List<Libro> libros = repositorio.findAllWithAuthors();

        libros.stream()
                .sorted(Comparator.comparing(Libro::getLanguages)
                        .thenComparing(Libro::getTitle))
                .forEach(libro -> {
                    System.out.println("\n▂▂▂▂▂▂▂▂ LIBRO ▂▂▂▂▂▂▂▂");
                    System.out.println("  📖 " + libro.getTitle());
                    System.out.println("  🌐 " + libro.getLanguages().getNombreEspanol());
                    System.out.println("  ⬇ " + libro.getDownload_count() + " descargas");
                    System.out.println("  ✍ Autores: " +
                            libro.getAuthors().stream()
                                    .map(Autor::getName)
                                    .collect(Collectors.joining(", ")));
                });

        System.out.println("\nTotal: " + libros.size() + " libros registrados");
    }

    private void listarAutoresRegistrado() {

        List<Libro> libros = repositorio.findAllWithAuthors();

        Map<Autor, List<Libro>> autoresConLibros = libros.stream()
                .flatMap(libro -> libro.getAuthors().stream()
                        .map(autor -> new AbstractMap.SimpleEntry<>(autor, libro)))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));

        autoresConLibros.forEach((autor, librosAutor) -> {
            System.out.println("\n▂▂▂▂▂▂▂▂ AUTOR ▂▂▂▂▂▂▂▂");
            System.out.println("  ✍ " + autor.getName());
            System.out.println("  🎂 Nacimiento: " + autor.getBirth_year());
            System.out.println("  ✟ Fallecimiento: " + autor.getDeath_year());
            System.out.println("  📚 Libros:");

            librosAutor.forEach(libro ->
                    System.out.println("    - " + libro.getTitle() + " (" + libro.getLanguages() + ")")
            );

            System.out.println("▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂");
        });
    }

    private void listarAutoresVivosEnAnio() {
        try {
            System.out.println("\nIngrese el año (ej: 1980):");
            int anio = teclado.nextInt();
            teclado.nextLine();

            if (anio <= 0) throw new IllegalArgumentException();

            List<Libro> libros = repositorio.findLibrosConAutoresVivosEnAnio(anio);

            Set<Autor> autoresVivos = libros.stream()
                    .flatMap(libro -> libro.getAuthors().stream())
                    .collect(Collectors.toSet());

            if (autoresVivos.isEmpty()) {
                System.out.println("No hay autores vivos en " + anio);
            } else {
                System.out.println("\n=== AUTORES VIVOS EN " + anio + " ===");
                autoresVivos.forEach(autor -> {
                    System.out.println("\n▂▂▂▂▂▂▂▂ AUTOR ▂▂▂▂▂▂▂▂");
                    System.out.println("  ✍ " + autor.getName());
                    System.out.println("  🎂 " + autor.getBirth_year());
                    System.out.println("  ✟ " + autor.getDeath_year());

                    System.out.println("  📚 Libros:");
                    libros.stream()
                            .filter(libro -> libro.getAuthors().contains(autor))
                            .forEach(libro -> System.out.println("    - " + libro.getTitle()));
                });
            }
        } catch (Exception e) {
            System.out.println("❌ Año inválido. Ingrese un número positivo.");
            teclado.nextLine();
        }
    }

    private void listarLibrosPorIdioma() {

        System.out.println("""
        \n=== ESTADÍSTICAS POR IDIOMA ===
        Ingrese el código del idioma:
        en - English
        es - Español
        fr - Français
        """);

        String codigoIdioma = teclado.nextLine().trim().toLowerCase();

        try {
            Lenguajes lenguaje = Lenguajes.fromString(codigoIdioma);

            List<Libro> libros = repositorio.findByLanguages(lenguaje);

            long totalLibros = libros.size();
            long totalDescargas = libros.stream()
                    .mapToLong(Libro::getDownload_count)
                    .sum();
            double promedioDescargas = totalLibros > 0 ?
                    (double) totalDescargas / totalLibros : 0;

            if (libros.isEmpty()) {
                System.out.printf("\nNo hay libros en %s (%s)\n",
                        lenguaje.getNombreEspanol(), codigoIdioma);
            } else {
                System.out.printf("\n=== ESTADÍSTICAS %s (%d libros) ===",
                        lenguaje.name(), totalLibros);
                System.out.printf("\n📊 Total descargas: %,d", totalDescargas);
                System.out.printf("\n📈 Promedio descargas: %,.1f\n", promedioDescargas);

                libros.stream()
                        .sorted(Comparator.comparing(Libro::getDownload_count).reversed())
                        .limit(3)
                        .forEach(libro -> System.out.printf(
                                "\n⭐ %s (%,d descargas)",
                                libro.getTitle(),
                                libro.getDownload_count())
                        );
            }

        } catch (IllegalArgumentException e) {
            System.out.println("\n❌ Error: Código no válido. Use en, es o fr");
        }
    }

}
