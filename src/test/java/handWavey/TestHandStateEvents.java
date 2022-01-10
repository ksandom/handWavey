package handWavey;

import handWavey.Gesture;
import handWavey.HandStateEvents;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.Matchers.hasItemInArray;


class TestHandState {
    private HandStateEvents primary;
    private HandStateEvents secondary;

    @BeforeEach
    void setUp() {
        this.primary = new HandStateEvents(true);
        this.secondary = new HandStateEvents(false);
    }

    @AfterEach
    void destroy() {
        this.primary = null;
        this.secondary = null;
    }

    @Test
    public void testSomethingChanged() {
        // Nothing should have changed yet.
        assertEquals(false, this.primary.somethingChanged());
        assertEquals(false, this.secondary.somethingChanged());
        
        // Change something.
        this.primary.setZone("active");
        this.secondary.setSegment(1);
        assertEquals(true, this.primary.somethingChanged());
        assertEquals(true, this.secondary.somethingChanged());
        
        // Set the same thing again. The change state should be cleared.
        this.primary.setZone("active");
        this.secondary.setSegment(1);
        assertEquals(false, this.primary.somethingChanged());
        assertEquals(false, this.secondary.somethingChanged());
        
        // One last change to capture the last remaining input.
        this.primary.setState(Gesture.closed);
        assertEquals(true, this.primary.somethingChanged());
    }
    
    @Test
    public void testGeneratedEvents() {
        // Nothing should have changed yet.
        List<String> events = this.primary.getEvents();
        assertEquals(0, events.size());
        
        // Change something.
        this.primary.setZone("active");
        assertThat(this.primary.getEvents(), contains(
            "individual-pOOB0Absent-exit",
            "individual-pActive0Absent-enter",
            "general-zone-pAnyChange",
            "general-zone-pOOB-exit",
            "general-zone-pActive-enter"));
        
        // Set the same thing again. The change state should be cleared.
        this.primary.setZone("active");
        events = this.primary.getEvents();
        assertEquals(0, events.size());
    }
}
