package com.bitespace.admin.dto;

public class TicketCountsResponse {
    private long open;
    private long inProgress;
    private long resolved;

    public TicketCountsResponse(long open, long inProgress, long resolved) {
        this.open = open;
        this.inProgress = inProgress;
        this.resolved = resolved;
    }

    // Getters
    public long getOpen() {
        return open;
    }

    public long getInProgress() {
        return inProgress;
    }

    public long getResolved() {
        return resolved;
    }

    // Setters (if needed, though typically not for a response DTO)
    public void setOpen(long open) {
        this.open = open;
    }

    public void setInProgress(long inProgress) {
        this.inProgress = inProgress;
    }

    public void setResolved(long resolved) {
        this.resolved = resolved;
    }
}