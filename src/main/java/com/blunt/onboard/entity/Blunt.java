package com.blunt.onboard.entity;

import com.blunt.onboard.serializer.ObjectIdSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.LocalDateTime;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class Blunt {

  @Id
  @JsonSerialize(using = ObjectIdSerializer.class)
  private ObjectId id;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  @LastModifiedDate
  private LocalDateTime createdOn;

  private String firstName;
  private String lastName;
  private String mobile;
  private String email;
  private String dob;
  private String street;
  private String city;
  private String state;
  private String country;
  private String zipCode;
  private String userId;
  private String password;
}
