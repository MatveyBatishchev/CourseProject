package com.example.util;

import com.example.models.Patient;
import com.google.gson.*;

import java.lang.reflect.Type;

public class PatientSerializer implements JsonSerializer<Patient> {

    public JsonElement serialize(final Patient patient, final Type type, final JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("reviewerName", new JsonPrimitive(patient.getSurname() + " " + patient.getName()));
        return result;
    }

}
