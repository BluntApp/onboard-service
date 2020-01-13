package com.blunt.onboard.entity;

import com.blunt.onboard.type.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Capacitor {

  @Id
  private ObjectId id;

  private String otp;
  private String userId;
  private String mobile;
  private Status status;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  @LastModifiedDate
  private LocalDateTime time;

}
