package com.blunt.onboard.scheduler;

import com.blunt.onboard.entity.Capacitor;
import com.blunt.onboard.repository.custom.CapacitorRepository;
import com.blunt.onboard.type.Status;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OnboardScheduler {

  private final CapacitorRepository capacitorRepository;

  @Scheduled(cron = "0/60 * * * * ?")
  public void resetOtpStatus(){
    List<Capacitor> capacitorList = capacitorRepository.findByStatus(Status.ACTIVE);
    List<Capacitor> filteredCapacitor = capacitorList.stream()
        .filter(capacitor -> {
          log.info(capacitor.getTime().plusMinutes(3).toString());
          log.info(Integer.toString(capacitor.getTime().plusMinutes(3).compareTo(LocalDateTime.now())));
          return capacitor.getTime().plusMinutes(3).compareTo(LocalDateTime.now()) <= 0;
        })
        .map(capacitor -> {
          capacitor.setStatus(Status.EXPIRED);
          return capacitor;
        }).collect(Collectors.toList());
    capacitorRepository.saveAll(filteredCapacitor);
  }

  @Scheduled(cron="0 0 0 * * ?")
  public void resetCapacitor(){
    List<Capacitor> capacitorList = capacitorRepository.findByStatusAndTimeBefore(Status.EXPIRED, LocalDateTime.now());
    capacitorRepository.deleteAll(capacitorList);
  }

}
