package com.hiking.dublindoga.domain;


public class JoinStatus {
    boolean approved;
    boolean pending;

    public JoinStatus(boolean approved, boolean pending) {
        this.approved = approved;
        this.pending = pending;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }
}
