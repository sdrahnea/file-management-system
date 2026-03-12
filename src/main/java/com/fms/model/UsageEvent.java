package com.fms.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Records every upload and download operation per tenant.
 * Used for usage metering, quota enforcement, and billing.
 */
@Entity
@Table(name = "usage_event")
public class UsageEvent {

    public enum EventType { UPLOAD, DOWNLOAD }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "tenant", nullable = false)
    private String tenant;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    @Column(name = "file_id", nullable = false)
    private String fileId;

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @Column(name = "event_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date eventDate;

    public UsageEvent() {
        this.eventDate = new Date();
    }

    public UsageEvent(String tenant, EventType eventType, String fileId, Long fileSizeBytes) {
        this.tenant = tenant;
        this.eventType = eventType;
        this.fileId = fileId;
        this.fileSizeBytes = fileSizeBytes;
        this.eventDate = new Date();
    }

    public long getId() { return id; }

    public String getTenant() { return tenant; }
    public void setTenant(String tenant) { this.tenant = tenant; }

    public EventType getEventType() { return eventType; }
    public void setEventType(EventType eventType) { this.eventType = eventType; }

    public String getFileId() { return fileId; }
    public void setFileId(String fileId) { this.fileId = fileId; }

    public Long getFileSizeBytes() { return fileSizeBytes; }
    public void setFileSizeBytes(Long fileSizeBytes) { this.fileSizeBytes = fileSizeBytes; }

    public Date getEventDate() { return eventDate; }
    public void setEventDate(Date eventDate) { this.eventDate = eventDate; }
}

