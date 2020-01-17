package com.blunt.onboard.mapper;

import com.blunt.onboard.dto.BluntDto;
import com.blunt.onboard.entity.Blunt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper
public abstract class BluntMapper {

  private PasswordEncoder passwordEncoder;

  @Mapping(source = "password", target = "password", qualifiedByName = "encryptPassword")
  public abstract BluntDto bluntToBluntDto(Blunt blunt);

  @Mapping(source = "password", target = "password", qualifiedByName = "encryptPassword")
  public abstract Blunt bluntDtoToBlunt(BluntDto bluntDto);

  @Named("encryptPassword")
  String encryptPassword(String password){
    return passwordEncoder.encode(password);
  }

  public void setPasswordEncoder(
      PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }
}
