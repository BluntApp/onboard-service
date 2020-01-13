package com.blunt.onboard.repository;

import com.blunt.onboard.entity.Follow;
import com.blunt.onboard.type.Status;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends MongoRepository<Follow, ObjectId> {

}
