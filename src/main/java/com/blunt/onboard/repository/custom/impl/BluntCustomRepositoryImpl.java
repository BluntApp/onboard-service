package com.blunt.onboard.repository.custom.impl;

import static com.blunt.onboard.util.BluntConstant.CASE_INSENSITIVE;

import com.blunt.onboard.entity.Blunt;
import com.blunt.onboard.repository.custom.BluntCustomRepository;
import io.micrometer.core.instrument.util.StringUtils;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BluntCustomRepositoryImpl implements BluntCustomRepository {

  private final MongoTemplate mongoTemplate;

  @Override
  public List<Blunt> findBluntByKey(String key) {
    if (StringUtils.isBlank(key)) {
      return Collections.EMPTY_LIST;
    }
    String value = Pattern.quote(key);
    Criteria criteria = new Criteria();
    criteria.orOperator(
        Criteria.where("firstName").regex(value, CASE_INSENSITIVE),
        Criteria.where("lastName").regex(value, CASE_INSENSITIVE),
        Criteria.where("mobile").regex(value, CASE_INSENSITIVE),
        Criteria.where("email").regex(value, CASE_INSENSITIVE),
        Criteria.where("city").regex(value, CASE_INSENSITIVE),
        Criteria.where("zipCode").regex(value, CASE_INSENSITIVE)
    );
    Query query = new Query(criteria);
    addProjections(query);
    return mongoTemplate.find(query, Blunt.class);
  }

  private void addProjections(Query query) {
    query.fields()
        .include("_id")
        .include("firstName")
        .include("lastName")
        .include("mobile")
        .include("email")
        .include("city")
        .include("zipCode");
  }
}
