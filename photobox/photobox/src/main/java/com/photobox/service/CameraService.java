// service/CameraService.java
package com.photobox.service;

import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Service
public class CameraService {
    
    public String savePhoto(String base64Image) throws Exception {
        try {
            // Hapus prefix data:image jika ada
            String base64Data = base64Image;
            if (base64Image.contains(",")) {
                base64Data = base64Image.split(",")[1];
            }
            
            // Decode base64 ke byte array
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);
            
            // Simpan ke file dengan timestamp
            String filename = "photo_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".jpg";
            String filepath = "captured/" + filename;
            
            // Buat direktori jika belum ada
            File directory = new File("captured");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            // Simpan file
            try (FileOutputStream fos = new FileOutputStream(filepath)) {
                fos.write(imageBytes);
            }
            
            return filepath;
        } catch (Exception e) {
            throw new Exception("Failed to save photo: " + e.getMessage());
        }
    }
}