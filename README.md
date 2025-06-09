<p align="center">
  <img src="https://firebasestorage.googleapis.com/v0/b/confecciones-5368b.appspot.com/o/Captura%20de%20pantalla%202025-06-08%20223121.jpg?alt=media&token=6456906d-7b34-45ba-895b-16863fe1307b"/>
</p>

<h1 align="center">Literalura width="30px" src="https://raw.githubusercontent.com/iampavangandhi/iampavangandhi/master/gifs/Hi.gif"></h1>
---
sidebar_position: 1
title: Catálogo de Libros - API Gutendex
description: Sistema de gestión de libros y autores con Spring Boot y Java
---

# 🏛️ Catálogo de Libros API

![Java](https://img.shields.io/badge/Java-17%2B-blue)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.1-green)
![Docusaurus](https://img.shields.io/badge/Documentación-Docusaurus-purple)

Aplicación de consola para búsqueda y gestión de libros usando la API [Gutendex](https://gutendex.com/).

## 🚀 Funcionalidades Clave

```mermaid
graph TD
    A[Búsqueda API] -->|Gutendex| B(Registro en BD)
    B --> C[Listado Completo]
    B --> D[Filtro por Idioma]
    B --> E[Autores Vivos]

📚 Core Features
- Búsqueda por título en Gutendex

- Persistencia en base de datos (H2/MySQL)

- Consultas avanzadas:
@Query("SELECT l FROM Libro l JOIN l.authors a WHERE a.birth_year <= :anio")
List<Libro> findLibrosAutoresVivos(@Param("anio") int anio);

Estadísticas por idioma

🛠️ Configuración Técnica
Estructura del Proyecto
src/
├── main/
│   ├── java/
│   │   └── com.alura.desafiolibros/
│   │       ├── model/
│   │       ├── repository/
│   │       ├── service/
│   │       └── DesafioApplication.java
├── resources/
│   ├── application.properties

Tecnologías Clave
Tecnología	Uso
Spring Data JPA	Derived queries
Jackson	Parseo JSON API
H2 Database	Almacenamiento local
Maven	Gestión de dependencias
📊 Diagrama de Clases

classDiagram
    class Libro {
        +String title
        +Lenguajes language
        +Set<Autor> authors
    }
    
    class Autor {
        +String name
        +Integer birth_year
    }
    
    Libro "1" *-- "n" Autor

🖥️ Cómo Ejecutar
1. Requisitos:
Java 17+
Maven 3.8+

2. Instalación:
git clone https://github.com/tu-usuario/catalogo-libros.git
cd catalogo-libros
mvn spring-boot:run

🌟 Ejemplo de Uso
=== MENÚ PRINCIPAL ===
1 - Buscar libro
2 - Listar libros
3 - Filtrar por idioma
4 - Autores vivos en año
> 1

Ingrese título: Dracula
✅ Libro registrado: Dracula (30,184 descargas)

📝 Documentación Adicional
Para la documentación completa con Docusaurus:

1. Instala dependencias:
npm install @docusaurus/core@latest

2. Crea la estructura:
npx create-docusaurus@latest docs classic

3. Añade páginas técnicas en docs/:
---
title: Arquitectura
---
## Diseño del Sistema

🔍 ¿Por qué Docusaurus?
Documentación interactiva: Soporta diagramas Mermaid y componentes React

SEO Optimizado: Genera metadata automáticamente

Versionado: Mantén documentación para cada versión del proyecto

Ejemplo de configuración en docusaurus.config.js:
module.exports = {
  presets: [
    [
      '@docusaurus/preset-classic',
      {
        docs: {
          routeBasePath: '/',
          sidebarPath: require.resolve('./sidebars.js'),
        },
      },
    ],
  ],
};



