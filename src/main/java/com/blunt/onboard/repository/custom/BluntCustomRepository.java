package com.blunt.onboard.repository.custom;

import com.blunt.onboard.entity.Blunt;
import java.util.List;

public interface BluntCustomRepository {

  List<Blunt> findBluntByKey(String key);
}
