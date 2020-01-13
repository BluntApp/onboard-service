package com.blunt.onboard.repository;

import com.blunt.onboard.entity.Blunt;
import com.blunt.onboard.repository.custom.BluntCustomRepository;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BluntRepository extends MongoRepository<Blunt, ObjectId>, BluntCustomRepository {

  Blunt findByMobile(String mobile);
  Blunt findByMobileAndPassword(String mobile, String password);
}
