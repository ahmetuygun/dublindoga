package com.hiking.dublindoga.domain;

import static com.hiking.dublindoga.domain.EventTestSamples.*;
import static com.hiking.dublindoga.domain.JoinerTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.hiking.dublindoga.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class JoinerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Joiner.class);
        Joiner joiner1 = getJoinerSample1();
        Joiner joiner2 = new Joiner();
        assertThat(joiner1).isNotEqualTo(joiner2);

        joiner2.setId(joiner1.getId());
        assertThat(joiner1).isEqualTo(joiner2);

        joiner2 = getJoinerSample2();
        assertThat(joiner1).isNotEqualTo(joiner2);
    }

    @Test
    void pendingEventsTest() {
        Joiner joiner = getJoinerRandomSampleGenerator();
        Event eventBack = getEventRandomSampleGenerator();

        joiner.addPendingEvents(eventBack);
        assertThat(joiner.getPendingEvents()).containsOnly(eventBack);
        assertThat(eventBack.getPendingJoiners()).containsOnly(joiner);

        joiner.removePendingEvents(eventBack);
        assertThat(joiner.getPendingEvents()).doesNotContain(eventBack);
        assertThat(eventBack.getPendingJoiners()).doesNotContain(joiner);

        joiner.pendingEvents(new HashSet<>(Set.of(eventBack)));
        assertThat(joiner.getPendingEvents()).containsOnly(eventBack);
        assertThat(eventBack.getPendingJoiners()).containsOnly(joiner);

        joiner.setPendingEvents(new HashSet<>());
        assertThat(joiner.getPendingEvents()).doesNotContain(eventBack);
        assertThat(eventBack.getPendingJoiners()).doesNotContain(joiner);
    }

    @Test
    void aprovedEventsTest() {
        Joiner joiner = getJoinerRandomSampleGenerator();
        Event eventBack = getEventRandomSampleGenerator();

        joiner.addAprovedEvents(eventBack);
        assertThat(joiner.getAprovedEvents()).containsOnly(eventBack);
        assertThat(eventBack.getApprovedJoiners()).containsOnly(joiner);

        joiner.removeAprovedEvents(eventBack);
        assertThat(joiner.getAprovedEvents()).doesNotContain(eventBack);
        assertThat(eventBack.getApprovedJoiners()).doesNotContain(joiner);

        joiner.aprovedEvents(new HashSet<>(Set.of(eventBack)));
        assertThat(joiner.getAprovedEvents()).containsOnly(eventBack);
        assertThat(eventBack.getApprovedJoiners()).containsOnly(joiner);

        joiner.setAprovedEvents(new HashSet<>());
        assertThat(joiner.getAprovedEvents()).doesNotContain(eventBack);
        assertThat(eventBack.getApprovedJoiners()).doesNotContain(joiner);
    }
}
