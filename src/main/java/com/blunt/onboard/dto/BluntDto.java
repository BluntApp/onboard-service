package com.blunt.onboard.dto;

import com.blunt.onboard.serializer.ObjectIdSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.Setter;
import org.bson.types.ObjectId;

@Setter
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BluntDto {

  @JsonSerialize(using = ObjectIdSerializer.class)
  private ObjectId id;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createdOn;

  @NotNull(message = "Blunt First Name is mandatory")
  private String firstName;

  @NotNull(message = "Blunt Last Name is mandatory")
  private String lastName;

  @NotNull(message = "Blunt Mobile Number is mandatory")
  private String mobile;

  @NotNull(message = "Blunt Email id is mandatory")
  private String email;
  private String userId;
  private String dob;
  private String street;
  private String city;
  private String state;
  private String country;
  private String zipCode;

  private String password;
  private String otp;
  private String photo;
}
