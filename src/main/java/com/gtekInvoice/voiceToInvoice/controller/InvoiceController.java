package com.gtekInvoice.voiceToInvoice.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gtekInvoice.voiceToInvoice.dto.GroqAiResponse;
import com.gtekInvoice.voiceToInvoice.dto.VoiceRequest;
import com.gtekInvoice.voiceToInvoice.entity.InvoiceItems;
import com.gtekInvoice.voiceToInvoice.service.AiService;
import com.gtekInvoice.voiceToInvoice.service.PdfGenerator;

@CrossOrigin(origins = "*")
@RestController
public class InvoiceController {

	private final AiService aiService;
	private final PdfGenerator pdfGen;

	public InvoiceController(AiService aiService, PdfGenerator pdfGen) {
		super();
		this.aiService = aiService;
		this.pdfGen = pdfGen;
	}
	
	@GetMapping("/checkApi")
	public String demo(){
		return "Working....";
	}
	
	
	@PostMapping("/generate")
	public ResponseEntity<byte[]> generateInvoice(@RequestBody VoiceRequest request) {

		InvoiceItems invoice = null;
		try {

			String result = aiService.extractInvoiceJSON(request.getVoiceText());
			
			ObjectMapper mapper = new ObjectMapper();
			GroqAiResponse groqResponse = mapper.readValue(result, GroqAiResponse.class);

			String content = groqResponse.getChoices().get(0).getMessage().getContent();
			invoice = mapper.readValue(content, InvoiceItems.class);
			InvoiceItems jsonToMapHtml = mapper.readValue(content, InvoiceItems.class);

			System.out.println("Response JSON from Groq AI " + jsonToMapHtml);
			InvoiceItems caculatedAmountWithResponse = pdfGen.calculateTotalAmount(invoice);

			byte[] pdfBytes = pdfGen.generatePdf(caculatedAmountWithResponse);

			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice.pdf")
					.contentType(MediaType.APPLICATION_PDF).body(pdfBytes);

		}

		catch (Exception e) {
			e.printStackTrace();
			System.out.print(e.getMessage());
		}
		return null;

	}

}
