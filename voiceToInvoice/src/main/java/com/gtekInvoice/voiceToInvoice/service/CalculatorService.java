package com.gtekInvoice.voiceToInvoice.service;

import org.springframework.stereotype.Service;

import com.gtekInvoice.voiceToInvoice.entity.InvoiceItems;
import com.gtekInvoice.voiceToInvoice.entity.InvoiceParticularItems;

@Service
public class CalculatorService {
	
	
	public InvoiceItems calculateInvoice(InvoiceItems invoice){

        double subtotal = 0;

        for(InvoiceParticularItems item : invoice.getParticulars()){

            double amount = Integer.parseInt(item.getQty()) *  Integer.parseInt(item.getRate());

            item.setAmount(amount);

            subtotal += amount;
        }

        double sgst = subtotal * invoice.getSgstPercent() / 100;

        double cgst = subtotal * invoice.getCgstPercent() / 100;
        
        double igst = subtotal * invoice.getIgstPercent() / 100;

        double total = subtotal + sgst + cgst + igst + invoice.getCourierCharges();

        invoice.setSgstAmount(sgst);
        invoice.setCgstAmount(cgst);
        invoice.setIgstAmount(igst);
        invoice.setTotalAmount(total);

        return invoice;
    }


}
