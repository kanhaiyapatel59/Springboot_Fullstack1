package com.example.trainbookingsystem.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.trainbookingsystem.entity.Ticket;
import com.example.trainbookingsystem.entity.Train;
import com.example.trainbookingsystem.entity.User;
import com.example.trainbookingsystem.repository.TicketRepository;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TrainService trainService;

    // ✅ Get all tickets
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    // ✅ Get ticket by ID
    public Optional<Ticket> getTicketById(Long id) {
        return ticketRepository.findById(id);
    }

    // ✅ Create a new ticket (fixed)
    public void createTicket(Long userId, Long trainId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        Train train = trainService.getTrainById(trainId)
                .orElseThrow(() -> new RuntimeException("Train not found with ID: " + trainId));

        Ticket ticket = new Ticket();
        ticket.setUser(user);
        ticket.setTrain(train);
        ticket.setBookingDate(LocalDateTime.now());
        ticket.setFinalPrice(calculateTicketPrice(train.getBasePrice(), train.getDiscountPercentage()));

        ticketRepository.save(ticket);
    }

    // ✅ Update existing ticket
    public Ticket updateTicket(Long id, Ticket ticketDetails) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + id));

        if (ticketDetails.getUser() != null) {
            ticket.setUser(ticketDetails.getUser());
        }
        if (ticketDetails.getTrain() != null) {
            ticket.setTrain(ticketDetails.getTrain());
            ticket.setFinalPrice(calculateTicketPrice(ticketDetails.getTrain().getBasePrice(),
                    ticketDetails.getTrain().getDiscountPercentage()));
        }

        ticket.setBookingDate(LocalDateTime.now()); // update booking date
        return ticketRepository.save(ticket);
    }

    // ✅ Delete ticket
    public void deleteTicket(Long id) {
        ticketRepository.deleteById(id);
    }

    // ✅ Calculate final price
    public double calculateTicketPrice(double basePrice, double discountPercentage) {
        return basePrice - (basePrice * discountPercentage / 100);
    }
}