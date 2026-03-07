package com.example.meters.seed;

import com.example.meters.entity.Meter;
import com.example.meters.entity.MeterType;
import com.example.meters.entity.Reading;
import com.example.meters.entity.User;
import com.example.meters.repository.MeterRepository;
import com.example.meters.repository.ReadingRepository;
import com.example.meters.repository.UserRepository;
import io.minio.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeedService {

    private final UserRepository userRepository;
    private final MeterRepository meterRepository;
    private final ReadingRepository readingRepository;
    private final PasswordEncoder passwordEncoder;
    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;

    @PostConstruct
    public void seedData() {
        try {
            // Create bucket if it doesn't exist
            createBucketIfNotExists();
            
            // Check if demo user already exists
            if (userRepository.findByLogin("demo").isEmpty()) {
                createDemoData();
            }
        } catch (Exception e) {
            log.error("Error seeding data", e);
        }
    }

    private void createBucketIfNotExists() throws Exception {
        boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(bucketName)
                .build());
        
        if (!bucketExists) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
            log.info("Created MinIO bucket: {}", bucketName);
        }
    }

    private void createDemoData() {
        // Create demo user
        User demoUser = new User();
        demoUser.setLogin("demo");
        demoUser.setPasswordHash(passwordEncoder.encode("demo1234"));
        demoUser.setCreatedAt(LocalDateTime.now());
        demoUser = userRepository.save(demoUser);
        log.info("Created demo user with id: {}", demoUser.getId());

        // Create meters
        Meter kitchenMeter = new Meter();
        kitchenMeter.setName("Kitchen");
        kitchenMeter.setType(MeterType.HOT_WATER);
        kitchenMeter.setUser(demoUser);
        kitchenMeter.setCreatedAt(LocalDateTime.now());
        kitchenMeter = meterRepository.save(kitchenMeter);
        log.info("Created kitchen meter with id: {}", kitchenMeter.getId());

        Meter bathroomMeter = new Meter();
        bathroomMeter.setName("Bathroom");
        bathroomMeter.setType(MeterType.COLD_WATER);
        bathroomMeter.setUser(demoUser);
        bathroomMeter.setCreatedAt(LocalDateTime.now());
        bathroomMeter = meterRepository.save(bathroomMeter);
        log.info("Created bathroom meter with id: {}", bathroomMeter.getId());

        // Create readings
        createSampleReadings(kitchenMeter);
        createSampleReadings(bathroomMeter);
        
        log.info("Demo data seeding completed");
    }

    private void createSampleReadings(Meter meter) {
        Random random = new Random();
        LocalDateTime now = LocalDateTime.now();
        
        // Create 10 readings for the past 10 days
        int baseValue = random.nextInt(1000);
        for (int i = 0; i < 10; i++) {
            Reading reading = new Reading();
            reading.setMeter(meter);
            reading.setValue(baseValue + (i * random.nextInt(50)));
            reading.setPhotoUrl("/files/sample-" + meter.getId() + "-" + i + ".jpg");
            reading.setCreatedAt(now.minusDays(10 - i));
            readingRepository.save(reading);
        }
    }
}
