package com.example.meters.chart;

import com.example.meters.chart.Period;
import com.example.meters.dto.ChartPoint;
import com.example.meters.dto.ChartResponse;
import com.example.meters.entity.Reading;
import com.example.meters.exception.ResourceNotFoundException;
import com.example.meters.repository.MeterRepository;
import com.example.meters.repository.ReadingRepository;
import com.example.meters.service.MeterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChartServiceImpl implements ChartService {

    private final ReadingRepository readingRepository;
    private final MeterService meterService;
    private final MeterRepository meterRepository;

    @Override
    public ChartResponse getReadingChart(Long meterId, Period period) {
        // Validate that the meter exists
        meterService.getMeterById(meterId);

        List<Reading> readings = getReadingsForPeriod(meterId, period);
        
        if (readings.isEmpty()) {
            return new ChartResponse(new ArrayList<>());
        }

        List<ChartPoint> points;
        
        switch (period) {
            case WEEK:
                // No aggregation for week
                points = readings.stream()
                        .map(r -> new ChartPoint(r.getCreatedAt().toLocalDate(), r.getValue()))
                        .collect(Collectors.toList());
                break;
                
            case MONTH:
                // Aggregate by day
                points = aggregateByDay(readings);
                break;
                
            case YEAR:
            case ALL:
                // Aggregate by month
                points = aggregateByMonth(readings);
                break;
                
            default:
                points = new ArrayList<>();
        }

        // Limit to 100 points
        if (points.size() > 100) {
            points = points.subList(0, 100);
        }

        return new ChartResponse(points);
    }

    @Override
    public ChartResponse getConsumptionChart(Long meterId, Period period) {
        // Validate that the meter exists
        meterService.getMeterById(meterId);

        List<Reading> readings = getReadingsForPeriod(meterId, period);
        
        if (readings.size() < 2) {
            return new ChartResponse(new ArrayList<>());
        }

        // Sort readings by date
        readings.sort(Comparator.comparing(Reading::getCreatedAt));

        List<ChartPoint> points = new ArrayList<>();
        
        // Calculate consumption (difference between consecutive readings)
        for (int i = 1; i < readings.size(); i++) {
            Reading current = readings.get(i);
            Reading previous = readings.get(i - 1);
            
            int consumption = current.getValue() - previous.getValue();
            points.add(new ChartPoint(current.getCreatedAt().toLocalDate(), consumption));
        }

        // Apply aggregation based on period
        List<ChartPoint> aggregatedPoints;
        switch (period) {
            case WEEK:
                // No aggregation for week
                aggregatedPoints = points;
                break;
                
            case MONTH:
                // Aggregate by day
                aggregatedPoints = aggregateConsumptionByDay(points);
                break;
                
            case YEAR:
            case ALL:
                // Aggregate by month
                aggregatedPoints = aggregateConsumptionByMonth(points);
                break;
                
            default:
                aggregatedPoints = new ArrayList<>();
        }

        // Limit to 100 points
        if (aggregatedPoints.size() > 100) {
            aggregatedPoints = aggregatedPoints.subList(0, 100);
        }

        return new ChartResponse(aggregatedPoints);
    }

    private List<Reading> getReadingsForPeriod(Long meterId, Period period) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = calculateStartDate(now, period);
        
        if (startDate != null) {
            return readingRepository.findByMeterIdAndDeletedAtIsNullAndCreatedAtBetweenOrderByCreatedAtAsc(
                    meterId, startDate, now);
        } else {
            return readingRepository.findReadingsByMeterIdOrderByCreatedAtAsc(meterId);
        }
    }

    private LocalDateTime calculateStartDate(LocalDateTime now, Period period) {
        switch (period) {
            case WEEK:
                return now.minusDays(7);
            case MONTH:
                return now.minusMonths(1);
            case YEAR:
                return now.minusYears(1);
            case ALL:
                return null; // No start date limit
            default:
                return null;
        }
    }

    private List<ChartPoint> aggregateByDay(List<Reading> readings) {
        Map<LocalDate, Integer> dailyMaxValues = readings.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getCreatedAt().toLocalDate(),
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparing(Reading::getValue)),
                                opt -> opt.map(Reading::getValue).orElse(0)
                        )
                ));

        return dailyMaxValues.entrySet().stream()
                .map(entry -> new ChartPoint(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(ChartPoint::getDate))
                .collect(Collectors.toList());
    }

    private List<ChartPoint> aggregateByMonth(List<Reading> readings) {
        Map<LocalDate, Integer> monthlyMaxValues = readings.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getCreatedAt().with(TemporalAdjusters.firstDayOfMonth()).toLocalDate(),
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparing(Reading::getValue)),
                                opt -> opt.map(Reading::getValue).orElse(0)
                        )
                ));

        return monthlyMaxValues.entrySet().stream()
                .map(entry -> new ChartPoint(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(ChartPoint::getDate))
                .collect(Collectors.toList());
    }

    private List<ChartPoint> aggregateConsumptionByDay(List<ChartPoint> points) {
        Map<LocalDate, Integer> dailyValues = points.stream()
                .collect(Collectors.groupingBy(
                        ChartPoint::getDate,
                        Collectors.summingInt(ChartPoint::getValue)
                ));

        return dailyValues.entrySet().stream()
                .map(entry -> new ChartPoint(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(ChartPoint::getDate))
                .collect(Collectors.toList());
    }

    private List<ChartPoint> aggregateConsumptionByMonth(List<ChartPoint> points) {
        Map<LocalDate, Integer> monthlyValues = points.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getDate().with(TemporalAdjusters.firstDayOfMonth()),
                        Collectors.summingInt(ChartPoint::getValue)
                ));

        return monthlyValues.entrySet().stream()
                .map(entry -> new ChartPoint(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(ChartPoint::getDate))
                .collect(Collectors.toList());
    }
}
