// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

package handWavey;

import handWavey.Gesture;
import handWavey.HandWaveyConfig;
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
        HandWaveyConfig handWaveyConfig = new HandWaveyConfig("handWaveyUnitTest");
        handWaveyConfig.defineGeneralConfig();
        this.primary = new HandStateEvents(true);
        this.secondary = new HandStateEvents(false);
    }

    @AfterEach
    void destroy() {
        this.primary = null;
        this.secondary = null;
        System.gc();
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
        this.primary.deriveEvents();
        List<String> exitEvents = this.primary.getExitEvents();
        List<String> anyChangeEvents = this.primary.getAnyChangeEvents();
        List<String> enterEvents = this.primary.getEnterEvents();
        assertEquals(0, exitEvents.size());
        assertEquals(0, anyChangeEvents.size());
        assertEquals(0, enterEvents.size());

        // Change something.
        this.primary.setZone("active");
        this.primary.deriveEvents();
        exitEvents = this.primary.getExitEvents();
        anyChangeEvents = this.primary.getAnyChangeEvents();
        enterEvents = this.primary.getEnterEvents();

        System.out.println(exitEvents);
        System.out.println(anyChangeEvents);
        System.out.println(enterEvents);

        assertThat(exitEvents, contains(
            "individual-pOOB0Absent-exit",
            "general-zone-pOOB-exit"));
        assertThat(anyChangeEvents, contains(
            "general-zone-pAnyChange"));
        assertThat(enterEvents, contains(
            "individual-pActive0Absent-enter",
            "individual-pNonOOB0Absent-enter",
            "general-zone-pActive-enter"));


        // Change something.
        this.primary.setZone("OOB");
        this.primary.deriveEvents();
        exitEvents = this.primary.getExitEvents();
        anyChangeEvents = this.primary.getAnyChangeEvents();
        enterEvents = this.primary.getEnterEvents();

        System.out.println(exitEvents);
        System.out.println(anyChangeEvents);
        System.out.println(enterEvents);

        assertThat(exitEvents, contains(
            "individual-pActive0Absent-exit",
            "individual-pNonOOB0Absent-exit",
            "general-zone-pActive-exit"));
        assertThat(anyChangeEvents, contains(
            "general-zone-pAnyChange"));
        assertThat(enterEvents, contains(
            "individual-pOOB0Absent-enter",
            "general-zone-pOOB-enter"));

        // Set the same thing again. The change state should be cleared.
        this.primary.setZone("OOB");
        this.primary.deriveEvents();
        exitEvents = this.primary.getExitEvents();
        anyChangeEvents = this.primary.getAnyChangeEvents();
        enterEvents = this.primary.getEnterEvents();
        assertEquals(0, exitEvents.size());
        assertEquals(0, anyChangeEvents.size());
        assertEquals(0, enterEvents.size());
    }

    @Test
    public void testOOBStateEvents() {
        // Nothing should have changed yet.
        this.primary.deriveEvents();
        List<String> exitEvents = this.primary.getExitEvents();
        List<String> anyChangeEvents = this.primary.getAnyChangeEvents();
        List<String> enterEvents = this.primary.getEnterEvents();
        assertEquals(0, exitEvents.size());
        assertEquals(0, anyChangeEvents.size());
        assertEquals(0, enterEvents.size());

        // Change something.
        this.primary.setZone("active");
        this.primary.deriveEvents();
        exitEvents = this.primary.getExitEvents();
        anyChangeEvents = this.primary.getAnyChangeEvents();
        enterEvents = this.primary.getEnterEvents();

        System.out.println("Change OOB.");
        System.out.println(exitEvents);
        System.out.println(anyChangeEvents);
        System.out.println(enterEvents);

        assertThat(exitEvents, contains(
            "individual-pOOB0Absent-exit",
            "general-zone-pOOB-exit"));
        assertThat(anyChangeEvents, contains(
            "general-zone-pAnyChange"));
        assertThat(enterEvents, contains(
            "individual-pActive0Absent-enter",
            "individual-pNonOOB0Absent-enter",
            "general-zone-pActive-enter"));


        // Change something that should not change OOB state.
        this.primary.setZone("none");
        this.primary.deriveEvents();
        exitEvents = this.primary.getExitEvents();
        anyChangeEvents = this.primary.getAnyChangeEvents();
        enterEvents = this.primary.getEnterEvents();

        System.out.println("Not change OOB.");
        System.out.println(exitEvents);
        System.out.println(anyChangeEvents);
        System.out.println(enterEvents);

        assertThat(exitEvents, contains(
            "individual-pActive0Absent-exit",
            "general-zone-pActive-exit"));
        assertThat(anyChangeEvents, contains(
            "general-zone-pAnyChange"));
        assertThat(enterEvents, contains(
            "individual-pNone0Absent-enter",
            "general-zone-pNone-enter"));


        // Change something that should change OOB state.
        this.primary.setZone("none");
        this.primary.setSegment(1);
        this.primary.deriveEvents();
        exitEvents = this.primary.getExitEvents();
        anyChangeEvents = this.primary.getAnyChangeEvents();
        enterEvents = this.primary.getEnterEvents();

        System.out.println("Change OOB.");
        System.out.println(exitEvents);
        System.out.println(anyChangeEvents);
        System.out.println(enterEvents);

        assertThat(exitEvents, contains(
            "individual-pNone0Absent-exit",
            "individual-pNonOOB0Absent-exit",
            "general-segment-p0-exit"));
        assertThat(anyChangeEvents, contains(
            "general-segment-pAnyChange"));
        assertThat(enterEvents, contains(
            "individual-pNone1Absent-enter",
            "individual-pNonOOB1Absent-enter",
            "general-segment-p1-enter"));
    }
}
