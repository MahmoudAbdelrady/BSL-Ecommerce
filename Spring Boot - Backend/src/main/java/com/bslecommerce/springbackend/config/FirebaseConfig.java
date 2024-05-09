package com.bslecommerce.springbackend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Configuration
public class FirebaseConfig {
    @Value("${firebase.configPath}")
    private String firebaseConfigPath;
    @PostConstruct
    public void initFirebase() {
        try {
            FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath);
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
}
