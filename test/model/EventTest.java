package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EventTest {

    @Test
    public void testRemainingTicketsCalculation() {
        Event event = new Event(1, "Rock Concert", "Arena", "Sat", 70.0, 25, 100, true);
        assertEquals(75, event.getRemainingTickets(), "Remaining tickets should be total - sold.");
    }

    @Test
    public void testRemainingTicketsZero() {
        Event event = new Event(2, "Movie Night", "Cinema", "Sun", 20.0, 100, 100, true);
        assertEquals(0, event.getRemainingTickets(), "Should show 0 when ticketsSold equals totalTickets.");
    }

    @Test
    public void testRemainingTicketsCalculation1() {
        Event event = new Event(3, "Jazz Night", "Club", "Fri", 45.0, 20, 80, true);

        assertTrue(event.getRemainingTickets() == 60, "This test should fail: expected value is incorrect.");

    }

    @Test
    public void testRemainingNegative_shouldFail() {
        Event event = new Event(4, "Movie Night", "Cinema", "Sun", 20.0, -100, 100, true);


        assertTrue(event.getRemainingTickets() == 200, "This should fail because ticketsSold is negative and remaining should not be 200.");
    }




}
