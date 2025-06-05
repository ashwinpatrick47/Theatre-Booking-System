package db;

import model.Event;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EventDAOTest {

    @BeforeEach
    public void setup() {
        EventDAO.clearTable();
    }

    @Test
    public void testAddEventAndExists() {
        EventDAO.addEvent("Test Event", "Main Hall", "Mon", 50.0, 100);
        boolean exists = EventDAO.eventExists("Test Event", "Main Hall", "Mon");

        assertTrue(exists, "Event should exist after being added.");
    }

    @Test
    public void testDuplicateEventDetection() {
        EventDAO.addEvent("Duplicate Event", "Theatre", "Tue", 40.0, 60);
        boolean firstAdd = EventDAO.eventExists("Duplicate Event", "Theatre", "Tue");
        EventDAO.addEvent("Different Event", "Hall", "Wed", 20.0, 80);
        boolean notExists = EventDAO.eventExists("Duplicate Event", "Hall", "Wed");

        assertTrue(firstAdd, "Original event should exist.");
        assertFalse(notExists, "Should not find event with different venue or day.");
    }

    @Test
    public void testClearTable() {
        EventDAO.addEvent("Temporary Event", "Room A", "Fri", 30.0, 20);
        EventDAO.clearTable();
        List<Event> events = EventDAO.getAllEventsAdmin();

        assertTrue(events.isEmpty(), "Event table should be empty after clear.");
    }

    @Test
    public void testDuplicateEventDetection_shouldFail() {
        EventDAO.addEvent("Art Show", "Gallery", "Sun", 30.0, 50);
        boolean wronglyExpectDuplicate = EventDAO.eventExists("Art Show", "Different Venue", "Sun");
        assertFalse(wronglyExpectDuplicate, "This test should fail because venue is different.");
    }
}
