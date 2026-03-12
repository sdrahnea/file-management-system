package com.fms.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Stores API keys issued per tenant.
 * Each tenant may have multiple keys; only active keys are accepted.
 */
@Entity
@Table(name = "api_key")
public class ApiKeyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "key_value", nullable = false, unique = true)
    private String keyValue;

    @Column(name = "tenant", nullable = false)
    private String tenant;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    public ApiKeyEntity() {
        this.createdDate = new Date();
    }

    public ApiKeyEntity(String keyValue, String tenant) {
        this.keyValue = keyValue;
        this.tenant = tenant;
        this.active = true;
        this.createdDate = new Date();
    }

    public long getId() { return id; }

    public String getKeyValue() { return keyValue; }
    public void setKeyValue(String keyValue) { this.keyValue = keyValue; }

    public String getTenant() { return tenant; }
    public void setTenant(String tenant) { this.tenant = tenant; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
}

