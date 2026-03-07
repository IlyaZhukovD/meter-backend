package com.example.meters.repository;

import com.example.meters.entity.Reading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReadingRepository extends JpaRepository<Reading, Long> {
    List<Reading> findByMeterIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long meterId);
    
    List<Reading> findByMeterIdAndDeletedAtIsNull(Long meterId);
    
    List<Reading> findByMeterIdAndDeletedAtIsNullAndCreatedAtBetweenOrderByCreatedAtAsc(
        Long meterId, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT r FROM Reading r WHERE r.meter.id = :meterId AND r.deletedAt IS NULL ORDER BY r.createdAt ASC")
    List<Reading> findReadingsByMeterIdOrderByCreatedAtAsc(@Param("meterId") Long meterId);
    
    @Query("SELECT r FROM Reading r WHERE r.meter.id = :meterId AND r.deletedAt IS NULL AND r.createdAt >= :startDate ORDER BY r.createdAt ASC")
    List<Reading> findReadingsByMeterIdAndStartDateOrderByCreatedAtAsc(
        @Param("meterId") Long meterId, @Param("startDate") LocalDateTime startDate);
}
