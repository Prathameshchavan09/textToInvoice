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

//        	String prompt = """
//        	        You are an AI that extracts invoice data from text.
//
//        	        Read the text and extract invoice information.
//
//        	        Rules:
//        	        - Return ONLY valid JSON.
//        	        - Do not add explanation.
//        	        - Do not add markdown.
//        	        - If a value is missing return empty string or 0.
//        	        - If quantity is written in words convert it to number.
//        	        - Extract all items. Each item must be a separate object in particulars with particular, qty, rate, and amount.
//        	        - itemTotal = sum of (qty * rate) for all particulars
//
//        	        JSON structure:
//
//        	        {
//        	          "to": "",
//        	          "address":"",
//        	          "gstin": "",
//        	          "piNo": "",
//        	          "date": "",
//        	          "orderNo": "",
//        	          "orderDate": "",
//        	          "dispatch": "",
//        	          "courierCharges": 0,
//        	          "sgstPercent": 0,
//        	          "cgstPercent": 0,
//        	          "igstPercent": 0,
//        	          "sgstAmount": 0,
//        	          "cgstAmount": 0,
//        	          "igstAmount": 0,
//        	          "totalAmount": 0,
//        	          "totalAmountInWords":"",
//        	          "mobileNo":"",
//        	          "particulars": [
//        	            {
//        	              "particular": "",
//        	              "qty": 0,
//        	              "rate": "",
//        	              "amount": 0
//        	            }
//        	          ]
//        	        }
//
//        	        Extract the information from this text:
//
//        	        """ + voiceText;
        	
        	String prompt =
        	        "You are an AI that extracts invoice data from text.\n\n" +
        	        "Read the text and extract invoice information.\n\n" +

        	        "Rules:\n" +
        	        "- Return ONLY valid JSON.\n" +
        	        "- Do not add explanation.\n" +
        	        "- Do not add markdown.\n" +
        	        "- If a value is missing return empty string or 0.\n" +
        	        "- If quantity is written in words convert it to number.\n" +
        	        "- Extract all items. Each item must be a separate object in particulars with particular, qty, rate, and amount.\n" +
        	        "- itemTotal = sum of (qty * rate) for all particulars\n\n" +

        	        "JSON structure:\n\n" +

        	        "{\n" +
        	        "  \"to\": \"\",\n" +
        	        "  \"address\":\"\",\n" +
        	        "  \"gstin\": \"\",\n" +
        	        "  \"piNo\": \"\",\n" +
        	        "  \"date\": \"\",\n" +
        	        "  \"orderNo\": \"\",\n" +
        	        "  \"orderDate\": \"\",\n" +
        	        "  \"dispatch\": \"\",\n" +
        	        "  \"courierCharges\": 0,\n" +
        	        "  \"sgstPercent\": 0,\n" +
        	        "  \"cgstPercent\": 0,\n" +
        	        "  \"igstPercent\": 0,\n" +
        	        "  \"sgstAmount\": 0,\n" +
        	        "  \"cgstAmount\": 0,\n" +
        	        "  \"igstAmount\": 0,\n" +
        	        "  \"totalAmount\": 0,\n" +
        	        "  \"totalAmountInWords\":\"\",\n" +
        	        "  \"mobileNo\":\"\",\n" +
        	        "  \"particulars\": [\n" +
        	        "    {\n" +
        	        "      \"particular\": \"\",\n" +
        	        "      \"qty\": 0,\n" +
        	        "      \"rate\": \"\",\n" +
        	        "      \"amount\": 0\n" +
        	        "    }\n" +
        	        "  ]\n" +
        	        "}\n\n" +

        	        "Extract the information from this text:\n\n" +
        	        voiceText;


        	String requestBody =
        	        "{\n" +
        	        "  \"model\": \"llama-3.1-8b-instant\",\n" +
        	        "  \"messages\": [\n" +
        	        "    {\n" +
        	        "      \"role\": \"user\",\n" +
        	        "      \"content\": " + objectMapper.writeValueAsString(prompt) + "\n" +
        	        "    }\n" +
        	        "  ]\n" +
        	        "}";

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