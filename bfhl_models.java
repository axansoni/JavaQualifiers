package com.bfhl.qualifier.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

// Webhook Generation Request
@Data
@AllArgsConstructor
@NoArgsConstructor
class WebhookRequest {
    private String name;
    private String regNo;
    private String email;
}

// Webhook Generation Response
@Data
@NoArgsConstructor
class WebhookResponse {
    private String webhook;
    private String accessToken;
}

// Solution Submission Request
@Data
@AllArgsConstructor
@NoArgsConstructor
class SolutionRequest {
    private String finalQuery;
}