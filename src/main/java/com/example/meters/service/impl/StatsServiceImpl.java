package com.example.meters.service.impl;

import com.example.meters.dto.StatsResponse;
import com.example.meters.entity.Reading;
import com.example.meters.repository.MeterRepository;
import com.example.meters.repository.ReadingRepository;
import com.example.meters.service.MeterService;
import com.example.meters.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final ReadingRepository readingRepository;
    private final MeterService meterService;
    private final MeterRepository meterRepository;

    @Override
    public StatsResponse getMeterStats(Long meterId) {
        // Validate that the meter exists
        meterService.getMeterById(meterId);

        // Get all readings for this meter (not deleted)
        List<Reading> readings = readingRepository.findReadingsByMeterIdOrderByCreatedAtAsc(meterId);

        if (readings.isEmpty()) {
            return new StatsResponse(0, 0);
        }

        // Calculate month consumption (last reading of month - first reading of month)
        long monthConsumption = calculateMonthConsumption(readings);

        // Calculate average month consumption
        long averageMonthConsumption = calculateAverageMonthConsumption(readings);

        return new StatsResponse(monthConsumption, averageMonthConsumption);
    }

    private long calculateMonthConsumption(List<Reading> readings) {
        if (readings.size() < 2) {
            return 0;
        }

        // Group readings by month
        Map<YearMonth, List<Reading>> readingsByMonth = readings.stream()
                .collect(Collectors.groupingBy(
                        r -> YearMonth.from(r.getCreatedAt())));

        // Get current month readings
        YearMonth currentMonth = YearMonth.now();
        List<Reading> currentMonthReadings = readingsByMonth.getOrDefault(currentMonth, List.of());
        List<Reading> prevMonthReadings = readingsByMonth.getOrDefault(currentMonth.minusMonths(1), List.of());

        return Math.round(currentMonthReadings.stream().mapToInt(Reading::getValue).average().orElse(0.0) - prevMonthReadings.stream().mapToInt(Reading::getValue).average().orElse(0.0));
    }

    private long calculateAverageMonthConsumption(List<Reading> readings) {
        if (readings.size() < 2) {
            return 0;
        }

        // Group readings by month
        Map<YearMonth, List<Reading>> readingsByMonth = readings.stream()
                .collect(Collectors.groupingBy(
                        r -> YearMonth.from(r.getCreatedAt())));

        // Calculate consumption for each month
        List<Long> monthlyConsumptions = readingsByMonth.values().stream()
                .filter(monthReadings -> !monthReadings.isEmpty())
                .map(monthReadings -> {
                    return Math.round(monthReadings.stream().mapToInt(Reading::getValue).average().orElse(0.0));
                })
                .toList();

        if (monthlyConsumptions.isEmpty()) {
            return 0;
        }

        // Calculate average
        return Math.round(monthlyConsumptions.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0L));
    }
}
