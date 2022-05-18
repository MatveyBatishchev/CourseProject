package com.example.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.io.IOException;

public class BindingResultSerializer extends JsonSerializer<BindingResult> {

    @Override
    public void serialize(BindingResult value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        for (FieldError error : value.getFieldErrors()) {
            jgen.writeObjectField(error.getField(), error.getDefaultMessage());
        }
        jgen.writeEndObject();
    }
}
