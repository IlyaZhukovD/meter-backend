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
        Integer monthConsumption = calculateMonthConsumption(readings);

        // Calculate average month consumption
        Integer averageMonthConsumption = calculateAverageMonthConsumption(readings);

        return new StatsResponse(monthConsumption, averageMonthConsumption);
    }

    private Integer calculateMonthConsumption(List<Reading> readings) {
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

        if (currentMonthReadings.size() < 2) {
            return 0;
        }

        // Get first and last reading of the month
        Reading firstReading = currentMonthReadings.get(0);
        Reading lastReading = currentMonthReadings.get(currentMonthReadings.size() - 1);

        return lastReading.getValue() - firstReading.getValue();
    }

    private Integer calculateAverageMonthConsumption(List<Reading> readings) {
        if (readings.size() < 2) {
            return 0;
        }

        // Group readings by month
        Map<YearMonth, List<Reading>> readingsByMonth = readings.stream()
                .collect(Collectors.groupingBy(
                        r -> YearMonth.from(r.getCreatedAt())));

        // Calculate consumption for each month
        List<Integer> monthlyConsumptions = readingsByMonth.values().stream()
                .filter(monthReadings -> monthReadings.size() >= 2)
                .map(monthReadings -> {
                    Reading first = monthReadings.get(0);
                    Reading last = monthReadings.get(monthReadings.size() - 1);
                    return last.getValue() - first.getValue();
                })
                .collect(Collectors.toList());

        if (monthlyConsumptions.isEmpty()) {
            return 0;
        }

        // Calculate average
        return (int) Math.round(monthlyConsumptions.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0));
    }
}
