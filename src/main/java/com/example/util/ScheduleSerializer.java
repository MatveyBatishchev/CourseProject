package com.example.util;

import com.example.models.Schedule;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class ScheduleSerializer extends JsonSerializer<Schedule> {

    @Override
    public void serialize(Schedule value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        jgen.writeObjectField("id", value.getId());
        jgen.writeObjectField("date", new SimpleDateFormat("dd-MM-yyyy").format(value.getDate()));
        jgen.writeEndObject();
    }
}