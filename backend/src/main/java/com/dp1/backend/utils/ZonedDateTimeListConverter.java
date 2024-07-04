package com.dp1.backend.utils;

import java.time.ZonedDateTime;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ZonedDateTimeListConverter implements AttributeConverter<ArrayList<ZonedDateTime>, String> {
    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public String convertToDatabaseColumn(ArrayList<ZonedDateTime> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting list of ZonedDateTime to JSON", e);
        }
    }

    @Override
    public ArrayList<ZonedDateTime> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(dbData, objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, ZonedDateTime.class));
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON to list of ZonedDateTime", e);
        }
    }
}
