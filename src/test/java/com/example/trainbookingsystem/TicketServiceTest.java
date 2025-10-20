package com.example.trainbookingsystem;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.trainbookingsystem.entity.Train;
import com.example.trainbookingsystem.repository.TrainRepository;
import com.example.trainbookingsystem.service.TrainService;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest{

    @Mock
    private TrainRepository trainRepository;

    @InjectMocks
    private TrainService trainService;

    private Train train;

    @BeforeEach
    void setUp() {
        train = buildTrain(1L, "Express 101", "CityA", "CityB", 100, 10);
    }

    // Helper
    private Train buildTrain(Long id, String name, String source, String dest, double price, double discount) {
        Train t = new Train();
        t.setId(id); t.setName(name); t.setSource(source); t.setDestination(dest);
        t.setBasePrice(price); t.setDiscountPercentage(discount);
        return t;
    }

    @Test
    void getAllTrains_ShouldReturnList() {
        when(trainRepository.findAll()).thenReturn(Arrays.asList(train));
        List<Train> result = trainService.getAllTrains();
        assertEquals(1, result.size());
        assertEquals("Express 101", result.get(0).getName());
    }

    @Test
    void getTrainById_ShouldReturnTrainOrEmpty() {
        when(trainRepository.findById(1L)).thenReturn(Optional.of(train));
        Optional<Train> r1 = trainService.getTrainById(1L);
        assertTrue(r1.isPresent());

        when(trainRepository.findById(2L)).thenReturn(Optional.empty());
        Optional<Train> r2 = trainService.getTrainById(2L);
        assertFalse(r2.isPresent());
    }

    @Test
    void createTrain_ShouldSaveTrain() {
        when(trainRepository.save(any(Train.class))).thenReturn(train);
        Train r = trainService.createTrain(train);
        assertEquals("Express 101", r.getName());
        verify(trainRepository).save(train);
    }

    @Test
    void updateTrain_ShouldUpdateFields() {
        Train newTrain = buildTrain(2L, "Super Express", "CityX", "CityY", 200, 20);
        when(trainRepository.findById(1L)).thenReturn(Optional.of(train));
        when(trainRepository.save(any(Train.class))).thenAnswer(i -> i.getArguments()[0]);

        Train r = trainService.updateTrain(1L, newTrain);

        assertEquals("Super Express", r.getName());
        assertEquals("CityX", r.getSource());
        assertEquals(200, r.getBasePrice());
        assertEquals(20, r.getDiscountPercentage());

        verify(trainRepository).findById(1L);
        verify(trainRepository).save(train);
    }

    @Test
    void deleteTrain_ShouldCallRepository() {
        doNothing().when(trainRepository).deleteById(1L);
        trainService.deleteTrain(1L);
        verify(trainRepository).deleteById(1L);
    }
}
