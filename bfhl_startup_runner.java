package com.bfhl.qualifier.runner;

import com.bfhl.qualifier.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class StartupRunner implements CommandLineRunner {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    // URLs
    private static final String WEBHOOK_GENERATE_URL = 
        "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
    private static final String WEBHOOK_SUBMIT_URL = 
        "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";
    
    public StartupRunner() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== Starting BFHL Qualifier Task ===");
        
        // Step 1: Generate Webhook
        WebhookResponse webhookResponse = generateWebhook();
        
        if (webhookResponse == null) {
            System.err.println("Failed to generate webhook");
            return;
        }
        
        System.out.println("Webhook URL: " + webhookResponse.getWebhook());
        System.out.println("Access Token: " + webhookResponse.getAccessToken());
        
        // Step 2: Determine which question based on regNo
        String regNo = "REG12347"; // Change this to your actual regNo
        String finalQuery = determineSQLQuery(regNo);
        
        System.out.println("Final Query: " + finalQuery);
        
        // Step 3: Submit Solution
        boolean success = submitSolution(webhookResponse.getAccessToken(), finalQuery);
        
        if (success) {
            System.out.println("=== Solution submitted successfully! ===");
        } else {
            System.err.println("=== Failed to submit solution ===");
        }
    }
    
    private WebhookResponse generateWebhook() {
        try {
            // Create request body
            WebhookRequest request = new WebhookRequest(
                "John Doe",           // Change to your name
                "REG12347",           // Change to your registration number
                "john@example.com"    // Change to your email
            );
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<WebhookRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<WebhookResponse> response = restTemplate.exchange(
                WEBHOOK_GENERATE_URL,
                HttpMethod.POST,
                entity,
                WebhookResponse.class
            );
            
            return response.getBody();
            
        } catch (Exception e) {
            System.err.println("Error generating webhook: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    private String determineSQLQuery(String regNo) {
        // Extract last two digits
        String lastTwoDigits = regNo.replaceAll("[^0-9]", "");
        if (lastTwoDigits.length() >= 2) {
            lastTwoDigits = lastTwoDigits.substring(lastTwoDigits.length() - 2);
        }
        
        int lastDigits = Integer.parseInt(lastTwoDigits);
        boolean isOdd = lastDigits % 2 != 0;
        
        System.out.println("Last two digits: " + lastDigits);
        System.out.println("Question type: " + (isOdd ? "ODD - Question 1" : "EVEN - Question 2"));
        
        if (isOdd) {
            return getQuestion1Solution();
        } else {
            return getQuestion2Solution();
        }
    }
    
    private String getQuestion1Solution() {
        // Replace this with the actual SQL query for Question 1
        // Download the PDF from the odd link and solve the problem
        return "SELECT * FROM table_name WHERE condition = 'value'";
    }
    
    private String getQuestion2Solution() {
        // Replace this with the actual SQL query for Question 2
        // Download the PDF from the even link and solve the problem
        return "SELECT * FROM table_name WHERE condition = 'value'";
    }
    
    private boolean submitSolution(String accessToken, String finalQuery) {
        try {
            SolutionRequest request = new SolutionRequest(finalQuery);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", accessToken);
            
            HttpEntity<SolutionRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                WEBHOOK_SUBMIT_URL,
                HttpMethod.POST,
                entity,
                String.class
            );
            
            System.out.println("Response: " + response.getBody());
            return response.getStatusCode().is2xxSuccessful();
            
        } catch (Exception e) {
            System.err.println("Error submitting solution: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}