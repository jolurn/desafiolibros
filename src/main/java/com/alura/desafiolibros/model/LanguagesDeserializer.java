package com.alura.desafiolibros.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.List;

public class LanguagesDeserializer extends JsonDeserializer<String> {
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

        if (p.getCurrentToken() == JsonToken.VALUE_NULL) {
            return "unk";
        }

        List<String> langs = p.readValueAs(List.class);
        return (langs != null && !langs.isEmpty())
                ? String.join(",", langs)
                : "unk";
    }
}
