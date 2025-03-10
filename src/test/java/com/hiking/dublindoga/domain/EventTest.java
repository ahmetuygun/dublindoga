package com.hiking.dublindoga.domain;

import static com.hiking.dublindoga.domain.EventTestSamples.*;
import static com.hiking.dublindoga.domain.JoinerTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.hiking.dublindoga.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class EventTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Event.class);
        Event event1 = getEventSample1();
        Event event2 = new Event();
        assertThat(event1).isNotEqualTo(event2);

        event2.setId(event1.getId());
        assertThat(event1).isEqualTo(event2);

        event2 = getEventSample2();
        assertThat(event1).isNotEqualTo(event2);
    }

    @Test
    void pendingJoinerTest() {
        Event event = getEventRandomSampleGenerator();
        Joiner joinerBack = getJoinerRandomSampleGenerator();

        event.addPendingJoiner(joinerBack);
        assertThat(event.getPendingJoiners()).containsOnly(joinerBack);

        event.removePendingJoiner(joinerBack);
        assertThat(event.getPendingJoiners()).doesNotContain(joinerBack);

        event.pendingJoiners(new HashSet<>(Set.of(joinerBack)));
        assertThat(event.getPendingJoiners()).containsOnly(joinerBack);

        event.setPendingJoiners(new HashSet<>());
        assertThat(event.getPendingJoiners()).doesNotContain(joinerBack);
    }

    @Test
    void approvedJoinerTest() {
        Event event = getEventRandomSampleGenerator();
        Joiner joinerBack = getJoinerRandomSampleGenerator();

        event.addApprovedJoiner(joinerBack);
        assertThat(event.getApprovedJoiners()).containsOnly(joinerBack);

        event.removeApprovedJoiner(joinerBack);
        assertThat(event.getApprovedJoiners()).doesNotContain(joinerBack);

        event.approvedJoiners(new HashSet<>(Set.of(joinerBack)));
        assertThat(event.getApprovedJoiners()).containsOnly(joinerBack);

        event.setApprovedJoiners(new HashSet<>());
        assertThat(event.getApprovedJoiners()).doesNotContain(joinerBack);
    }
}
