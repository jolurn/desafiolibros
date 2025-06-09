package com.alura.desafiolibros.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosLibro(
        @JsonAlias("title") String title,
        @JsonAlias("authors") List<DatosAutor> authors,
        @JsonAlias("languages") @JsonDeserialize(using = LanguagesDeserializer.class) String languages,
        @JsonAlias("download_count") Integer download_count

) {
}
