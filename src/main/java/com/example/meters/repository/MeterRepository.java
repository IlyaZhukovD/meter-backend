package com.example.meters.repository;

import com.example.meters.entity.Meter;
import com.example.meters.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MeterRepository extends JpaRepository<Meter, Long> {
    List<Meter> findByUserId(Long userId);
}
