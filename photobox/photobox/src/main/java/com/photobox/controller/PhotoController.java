package com.photobox.controller;

import com.photobox.model.Session;
import com.photobox.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;  // <-- TAMBAHKAN INI
import java.util.Base64;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

@Controller
public class PhotoController {
    
    @Autowired
    private SessionRepository sessionRepository;
    
    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }
    
    // Start session
    @PostMapping("/api/start-session")
    @ResponseBody
    public ResponseEntity<Map<String, String>> startSession() {
        Session session = new Session();
        session.setPaymentStatus("PAID");
        session = sessionRepository.save(session);
        
        Map<String, String> response = new HashMap<>();
        response.put("sessionId", session.getId());
        response.put("status", "success");
        
        System.out.println("✅ Session started: " + session.getId());
        return ResponseEntity.ok(response);
    }
    
    // Get all sessions
    @GetMapping("/api/sessions")
    @ResponseBody
    public ResponseEntity<List<Session>> getAllSessions() {
        List<Session> sessions = sessionRepository.findAll();
        return ResponseEntity.ok(sessions);
    }
    
    // Get session by ID
    @GetMapping("/api/session/{sessionId}")
    @ResponseBody
    public ResponseEntity<Session> getSession(@PathVariable String sessionId) {
        Session session = sessionRepository.findById(sessionId).orElse(null);
        if (session != null) {
            return ResponseEntity.ok(session);
        }
        return ResponseEntity.notFound().build();
    }
    
    // Delete all sessions
    @DeleteMapping("/api/sessions")
    @ResponseBody
    public ResponseEntity<String> deleteAllSessions() {
        sessionRepository.deleteAll();
        return ResponseEntity.ok("All sessions deleted");
    }
    
    // Save selected frame
    @PostMapping("/api/save-frame/{sessionId}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> saveFrame(@PathVariable String sessionId, 
                                                          @RequestBody Map<String, String> request) {
        Session session = sessionRepository.findById(sessionId).orElse(null);
        Map<String, String> response = new HashMap<>();
        
        if (session != null) {
            session.setSelectedFrame(request.get("frame"));
            sessionRepository.save(session);
            response.put("status", "success");
            System.out.println("✅ Frame saved for session: " + sessionId);
            return ResponseEntity.ok(response);
        }
        
        response.put("status", "failed");
        return ResponseEntity.badRequest().body(response);
    }
    
    // Capture photo
    @PostMapping("/api/capture-photo/{sessionId}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> capturePhoto(@PathVariable String sessionId,
                                                             @RequestBody Map<String, String> request) {
        System.out.println("📸 Capture photo called for session: " + sessionId);
        
        Map<String, String> response = new HashMap<>();
        
        Session session = sessionRepository.findById(sessionId).orElse(null);
        if (session == null) {
            response.put("status", "failed");
            response.put("message", "Session not found");
            return ResponseEntity.badRequest().body(response);
        }
        
        String base64Image = request.get("image");
        String filter = request.getOrDefault("filter", "none");
        
        if (base64Image == null || base64Image.isEmpty()) {
            response.put("status", "failed");
            response.put("message", "No image data");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            File folder = new File("captured");
            if (!folder.exists()) {
                folder.mkdirs();
            }
            
            String fileName = "photo_" + System.currentTimeMillis() + ".jpg";
            String filePath = "captured/" + fileName;
            
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            Files.write(Paths.get(filePath), imageBytes);
            
            session.setPhotoPath(filePath);
            session.setSelectedFilter(filter);
            sessionRepository.save(session);
            
            System.out.println("✅ Photo saved: " + filePath);
            
            response.put("status", "success");
            response.put("photoPath", filePath);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Error saving photo: " + e.getMessage());
            response.put("status", "failed");
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    // Print and generate QR
    @PostMapping("/api/print-and-generate-qr/{sessionId}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> printAndGenerateQR(@PathVariable String sessionId) {
        System.out.println("🖨️ Print and generate QR for session: " + sessionId);
        
        Map<String, String> response = new HashMap<>();
        
        Session session = sessionRepository.findById(sessionId).orElse(null);
        if (session == null || session.getPhotoPath() == null) {
            response.put("status", "failed");
            response.put("message", "No photo found");
            return ResponseEntity.badRequest().body(response);
        }
        
        String downloadUrl = "http://localhost:8080/api/download/" + session.getId();
        String qrCode = "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=" + downloadUrl;
        
        response.put("status", "success");
        response.put("qrCode", qrCode);
        response.put("downloadUrl", downloadUrl);
        
        System.out.println("✅ QR generated: " + qrCode);
        return ResponseEntity.ok(response);
    }
    
    // Download photo
    @GetMapping("/api/download/{sessionId}")
    @ResponseBody
    public ResponseEntity<byte[]> downloadPhoto(@PathVariable String sessionId) {
        System.out.println("📥 Download photo for session: " + sessionId);
        
        Session session = sessionRepository.findById(sessionId).orElse(null);
        
        if (session != null && session.getPhotoPath() != null) {
            try {
                File photoFile = new File(session.getPhotoPath());
                if (photoFile.exists()) {
                    byte[] imageBytes = Files.readAllBytes(photoFile.toPath());
                    System.out.println("✅ Photo downloaded: " + session.getPhotoPath());
                    return ResponseEntity.ok()
                            .header("Content-Type", "image/jpeg")
                            .header("Content-Disposition", "attachment; filename=\"" + photoFile.getName() + "\"")
                            .body(imageBytes);
                }
            } catch (Exception e) {
                System.err.println("❌ Error downloading: " + e.getMessage());
            }
        }
        
        return ResponseEntity.notFound().build();
    }
    
    // Confirm payment
    @PostMapping("/api/confirm-payment/{sessionId}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> confirmPayment(@PathVariable String sessionId) {
        Session session = sessionRepository.findById(sessionId).orElse(null);
        Map<String, String> response = new HashMap<>();
        
        if (session != null) {
            session.setPaymentStatus("PAID");
            sessionRepository.save(session);
            response.put("status", "success");
            response.put("remainingTime", String.valueOf(session.getRemainingSeconds()));
            System.out.println("✅ Payment confirmed for session: " + sessionId);
            return ResponseEntity.ok(response);
        }
        
        response.put("status", "failed");
        return ResponseEntity.badRequest().body(response);
    }
}