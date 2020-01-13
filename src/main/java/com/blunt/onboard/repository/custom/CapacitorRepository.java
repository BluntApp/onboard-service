package com.blunt.onboard.repository.custom;

import com.blunt.onboard.entity.Capacitor;
import com.blunt.onboard.type.Status;
import java.time.LocalDateTime;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CapacitorRepository extends MongoRepository<Capacitor, ObjectId> {

  Capacitor findByUserId(String userId);

  Capacitor findByMobile(String mobile);

  List<Capacitor> findByStatus(Status status);

  List<Capacitor> findByStatusAndTimeBefore(Status status, LocalDateTime time);

}
