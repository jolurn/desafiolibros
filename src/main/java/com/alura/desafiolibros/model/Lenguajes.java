package com.alura.desafiolibros.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.List;

public enum Lenguajes {
    ENGLISH("en","en"),
    SPANISH("es","es"),
    FRENCH("fr","fr"),
    UNKNOWN("unk", "unk");

    private String lenguagesOmdb;

    private String lenguagesEspanol;

    Lenguajes(String lenguagesOmdb, String lenguagesEspanol){
        this.lenguagesOmdb = lenguagesOmdb;
        this.lenguagesEspanol = lenguagesEspanol;
    }

    public static Lenguajes fromString(String text) {
        for (Lenguajes lenguaje : Lenguajes.values()) {
            if (lenguaje.lenguagesOmdb.equalsIgnoreCase(text)) {
                return lenguaje;
            }
        }
        throw new IllegalArgumentException("Ningun lenguaje encontrado: " + text);
    }

    public String getNombreEspanol() {
        return switch (this) {
            case ENGLISH -> "inglés";
            case SPANISH -> "español";
            case FRENCH -> "francés";
            case UNKNOWN -> "desconocido";
        };
    }

}
