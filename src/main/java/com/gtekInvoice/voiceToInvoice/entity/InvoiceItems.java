package com.gtekInvoice.voiceToInvoice.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InvoiceItems {
	
	private String to;
	private String address;
    private String gstin;
    private String piNo;
    private String date;
    private String orderNo;
    private String orderDate;
    private String dispatch;
    private double courierCharges;
    private double sgstPercent;
    private double cgstPercent;
    private double igstPercent;
    private double sgstAmount;
    private double cgstAmount;
    private double igstAmount;
    private double totalAmount;
    private List<InvoiceParticularItems> particulars;
    private String totalAmountInWords;
    private long mobileNo;
    
    
    
    
    
    

}
