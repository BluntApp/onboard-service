package com.blunt.onboard.mapper;

import com.blunt.onboard.dto.BluntDto;
import com.blunt.onboard.entity.Blunt;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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
