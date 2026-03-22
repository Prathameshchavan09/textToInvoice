package com.gtekInvoice.voiceToInvoice.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gtekInvoice.voiceToInvoice.entity.Choice;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroqAiResponse {

	private List<Choice> choices;
}

