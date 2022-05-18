package com.example.util;

import com.example.models.TimeTable;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.temporal.ChronoUnit;

public class TimeTableSerializer extends JsonSerializer<TimeTable> {

    @Override
    public void serialize(TimeTable timeTable, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        jgen.writeObjectField("start", timeTable.getDate() + "T" + timeTable.getStartTime() + ":00");
        jgen.writeObjectField("end", timeTable.getDate() + "T" + timeTable.getStartTime().plus(timeTable.getSchedule().getConsultingTime(), ChronoUnit.MINUTES) + ":00");
        if (timeTable.isAvailable())
            jgen.writeObjectField("color", "#1f5624d1");
        else jgen.writeObjectField("color", "gray");
        jgen.writeEndObject();
    }

}
