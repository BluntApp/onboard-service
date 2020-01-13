package com.blunt.onboard.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.util.CollectionUtils;

public class ObjectIdListSerializer extends JsonSerializer<List<ObjectId>> {

  @Override
  public void serialize(List<ObjectId> objectIds, JsonGenerator jsonGenerator,
      SerializerProvider serializerProvider) throws IOException {
    if (CollectionUtils.isEmpty(objectIds)) {
      jsonGenerator.writeNull();
    } else {
      jsonGenerator.writeStartArray();
      for (ObjectId objectId : objectIds) {
        jsonGenerator.writeString(objectId.toString());
      }
      jsonGenerator.writeEndArray();
    }
  }
}
