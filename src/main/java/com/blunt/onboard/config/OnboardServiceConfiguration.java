package com.blunt.onboard.config;

import brave.sampler.Sampler;
import com.blunt.onboard.mapper.BluntMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class OnboardServiceConfiguration {

  @Bean
  public Sampler defaultSampler() {
    return Sampler.ALWAYS_SAMPLE;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public BluntMapper bluntMapper(PasswordEncoder passwordEncoder){
    BluntMapper mapper = Mappers.getMapper(BluntMapper.class);
    mapper.setPasswordEncoder(passwordEncoder);
    return mapper;
  }
}
