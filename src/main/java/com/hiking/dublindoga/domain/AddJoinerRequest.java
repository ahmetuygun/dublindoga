package com.hiking.dublindoga.domain;

public class AddJoinerRequest {

    private Long eventId;
    private Long joinerId;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getJoinerId() {
        return joinerId;
    }

    public void setJoinerId(Long joinerId) {
        this.joinerId = joinerId;
    }
}
