// model/Photo.java
package com.photobox.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "photos")
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String imageBase64;
    
    private String imagePath;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date captureTime;
    
    private boolean printed;
    
    // Constructors
    public Photo() {}
    
    public Photo(String imageBase64, String imagePath) {
        this.imageBase64 = imageBase64;
        this.imagePath = imagePath;
        this.captureTime = new Date();
        this.printed = false;
    }
    
    // Getters and Setters
    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }
    
    public String getImageBase64() { 
        return imageBase64; 
    }
    
    public void setImageBase64(String imageBase64) { 
        this.imageBase64 = imageBase64; 
    }
    
    public String getImagePath() { 
        return imagePath; 
    }
    
    public void setImagePath(String imagePath) { 
        this.imagePath = imagePath; 
    }
    
    public Date getCaptureTime() { 
        return captureTime; 
    }
    
    public void setCaptureTime(Date captureTime) { 
        this.captureTime = captureTime; 
    }
    
    public boolean isPrinted() { 
        return printed; 
    }
    
    public void setPrinted(boolean printed) { 
        this.printed = printed; 
    }
}