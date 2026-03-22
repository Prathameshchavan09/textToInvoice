package com.gtekInvoice.voiceToInvoice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AiService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${groq-api-key}")
    private String apiKey;

    public AiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String extractInvoiceJSON(String voiceText) {

        try {

        	String prompt = """
        			You are an AI that extracts invoice data from text.

        			Read the text and extract invoice information.

        			Rules:
        			- Return ONLY valid JSON.
        			- Do not add explanation.
        			- Do not add markdown.
        			- If a value is missing return empty string "" or 0.
        			- If quantity is written in words convert it to number.
        			- Extract all items. Each item must be a separate object in "particulars" with particular, qty, rate, and amount.
					- itemTotal = sum of (qty × rate) for all particulars
					

        			JSON structure:

        			{
        			  "to": "",
        			  "address":"",
        			  "gstin": "",
        			  "piNo": "",
        			  "date": "",
        			  "orderNo": "",
        			  "orderDate": "",
        			  "dispatch": "",
        			  "courierCharges": 0,
        			  "sgstPercent": 0,
        			  "cgstPercent": 0,
        			  "igstPercent": 0,
        			  "sgstAmount": 0,
        			  "cgstAmount": 0,
        			  "igstAmount": 0,
        			  "totalAmount": 0,
        			  "totalAmountInWords":"",
        			  "mobileNo":"",
        			  "particulars": [
        			    {
        			      "particular": "",
        			      "qty": 0,
        			      "rate": "",
        			      "amount": 0
        			    }
        			  ]
        			}
        			

        			Extract the information from this text:

        			""" + voiceText;


            String requestBody = """
                    {
                      "model": "llama-3.1-8b-instant",
                      "messages": [
                        {
                          "role": "user",
                          "content": %s
                        }
                      ]
                    }
                    """.formatted(objectMapper.writeValueAsString(prompt));


            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);


            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            String url = "https://api.groq.com/openai/v1/chat/completions";


            ResponseEntity<String> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity, String.class);


            return response.getBody();

        } catch (Exception e) {
            throw new RuntimeException("Error calling AI service", e);
        }
    }
    
}