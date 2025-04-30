package com.vsnt.asset_onboarding.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.vsnt.asset_onboarding.dtos.TranscodingJob;

import java.io.IOException;

public class Serializer extends JsonSerializer<TranscodingJob> {
    @Override
    public void serialize(TranscodingJob transcodingJob, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("jobId", transcodingJob.getJobId());
        jsonGenerator.writeStringField("key", transcodingJob.getKey());
        jsonGenerator.writeNumberField("size", transcodingJob.getSize());
        jsonGenerator.writeEndObject();
    }
}
