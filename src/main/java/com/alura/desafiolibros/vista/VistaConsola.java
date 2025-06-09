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


    private void listarLibrosRegistrado(){
        List<Libro> libros = repositorio.findAll();

        libros.stream()
                .sorted(Comparator.comparing(Libro::getLanguages))
                .forEach(System.out::println);
    }

    private void listarAutoresRegistrado() {

        List<Libro> libros = repositorio.findAllWithAuthors(); // Usa JOIN FETCH como antes

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
        System.out.println("Ingrese el año vivo de autor(es) que desee buscar:");
        int anio = teclado.nextInt();
        teclado.nextLine();

        List<Libro> libros = repositorio.findAllWithAuthors();

        Set<Autor> autoresVivos = libros.stream()
                .flatMap(libro -> libro.getAuthors().stream())
                .filter(autor -> estaVivoEnAnio(autor, anio))
                .collect(Collectors.toSet());

        if (autoresVivos.isEmpty()) {
            System.out.println("No hay autores vivos en el año " + anio);
        } else {
            System.out.println("=== AUTORES VIVOS EN " + anio + " ===");
            autoresVivos.forEach(autor -> {
                System.out.println("\n▂▂▂▂▂▂▂▂ AUTOR ▂▂▂▂▂▂▂▂");
                System.out.println("  ✍ " + autor.getName());
                System.out.println("  🎂 Nacimiento: " + autor.getBirth_year());
                System.out.println("  ✟ Fallecimiento: " + autor.getDeath_year());

                System.out.println("  📚 Libros:");
                libros.stream()
                        .filter(libro -> libro.getAuthors().contains(autor))
                        .forEach(libro -> System.out.println("    - " + libro.getTitle()));
            });
        }
    }

    private boolean estaVivoEnAnio(Autor autor, int anio) {
        return anio >= autor.getBirth_year() &&
                (autor.getDeath_year() == null || anio <= autor.getDeath_year());
    }

    private void listarLibrosPorIdioma() {

        System.out.println("""
        Ingrese el código del idioma para buscar libros:
        en - English
        es - Español
        fr - Français
        """);

        String codigoIdioma = teclado.nextLine().trim().toLowerCase();

        try {

            Lenguajes lenguaje = Lenguajes.fromString(codigoIdioma);

            List<Libro> libros = repositorio.findAll().stream()
                    .filter(libro -> libro.getLanguages() == lenguaje)
                    .sorted(Comparator.comparing(Libro::getTitle))
                    .toList();

            if (libros.isEmpty()) {
                System.out.println("\nNo hay libros en " + lenguaje.name());
            } else {
                System.out.println("\n=== LIBROS EN " + lenguaje.name() + " ===");
                libros.forEach(libro ->
                        System.out.printf("- %s (%d descargas)%n",
                                libro.getTitle(),
                                libro.getDownload_count())
                );
            }

        } catch (IllegalArgumentException e) {
            System.out.println("\n❌ Error: Código de idioma no válido. Use: en, es o fr");
        }
    }

}
