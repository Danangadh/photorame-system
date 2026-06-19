package com.photobox.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    private String paymentStatus;
    private Date startTime;
    private Date endTime;
    private String selectedFrame;
    private String photoPath;
    private String selectedFilter;
    
    public Session() {
        this.paymentStatus = "PENDING";
        this.startTime = new Date();
        this.endTime = new Date(System.currentTimeMillis() + (8 * 60 * 1000));
    }
    
    public boolean isActive() {
        return "PAID".equals(paymentStatus) && new Date().before(endTime);
    }
    
    public long getRemainingSeconds() {
        if (!isActive()) return 0;
        return (endTime.getTime() - System.currentTimeMillis()) / 1000;
    }
    
    // Getters
    public String getId() { return id; }
    public String getPaymentStatus() { return paymentStatus; }
    public Date getStartTime() { return startTime; }
    public Date getEndTime() { return endTime; }
    public String getSelectedFrame() { return selectedFrame; }
    public String getPhotoPath() { return photoPath; }
    public String getSelectedFilter() { return selectedFilter; }
    
    // Setters
    public void setId(String id) { this.id = id; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
    public void setSelectedFrame(String selectedFrame) { this.selectedFrame = selectedFrame; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }
    public void setSelectedFilter(String selectedFilter) { this.selectedFilter = selectedFilter; }
}