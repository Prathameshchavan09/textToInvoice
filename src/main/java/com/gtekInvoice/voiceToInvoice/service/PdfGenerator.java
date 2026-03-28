package com.gtekInvoice.voiceToInvoice.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import org.springframework.stereotype.Service;
import com.gtekInvoice.voiceToInvoice.entity.InvoiceItems;
import com.gtekInvoice.voiceToInvoice.entity.InvoiceParticularItems;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
public class PdfGenerator {

	private final SpringTemplateEngine templateEngine;

	public PdfGenerator(SpringTemplateEngine templateEngine) {
		super();
		this.templateEngine = templateEngine;
	}

	public byte[] generatePdf(InvoiceItems invoice) throws Exception {
		Context context = new Context();
		context.setVariable("invoice", invoice);
		String formattedAddress = formatAddress(invoice.getAddress());
		context.setVariable("formattedAddress", formattedAddress);

		String html = templateEngine.process("invoiceBill", context);

		// Generate PDF
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PdfRendererBuilder builder = new PdfRendererBuilder();
		builder.withHtmlContent(html, new File("src/main/resources/templates/").toURI().toString());
		builder.toStream(output);
		builder.run();

		return output.toByteArray();
	}
	
	
	public InvoiceItems calculateTotalAmount(InvoiceItems amountToCalculate) {

	    if (amountToCalculate == null || amountToCalculate.getParticulars() == null) {
	        return amountToCalculate;
	    }
	    double itemTotal = 0;

	    for (InvoiceParticularItems item : amountToCalculate.getParticulars()) {

	        double qty = parseDoubleSafe(item.getQty());
	        double rate = parseDoubleSafe(item.getRate());
	        double amount = qty * rate;
	        amount = Math.round(amount * 100.0) / 100.0;
	        item.setAmount(amount);
	        itemTotal += amount;
	    }

	    double courier = parseDoubleSafe(amountToCalculate.getCourierCharges());
	    double baseAmount = itemTotal + courier;

	    if (amountToCalculate.getSgstAmount() == 0 && amountToCalculate.getSgstPercent() > 0) {
	        double sgst = baseAmount * amountToCalculate.getSgstPercent() / 100;
	        amountToCalculate.setSgstAmount(Math.round(sgst * 100.0) / 100.0);
	    }
	    if (amountToCalculate.getCgstAmount() == 0 && amountToCalculate.getCgstPercent() > 0) {
	        double cgst = baseAmount * amountToCalculate.getCgstPercent() / 100;
	        amountToCalculate.setCgstAmount(Math.round(cgst * 100.0) / 100.0);
	    }
	    if (amountToCalculate.getIgstAmount() == 0 && amountToCalculate.getIgstPercent() > 0) {
	        double igst = baseAmount * amountToCalculate.getIgstPercent() / 100;
	        amountToCalculate.setIgstAmount(Math.round(igst * 100.0) / 100.0);
	    }

	    double total = baseAmount
	            + amountToCalculate.getSgstAmount()
	            + amountToCalculate.getCgstAmount()
	            + amountToCalculate.getIgstAmount();
	    

	    total = Math.round(total * 100.0) / 100.0;

	    amountToCalculate.setTotalAmount(total);
	    String words = NumberToWords.convert(total);
	    amountToCalculate.setTotalAmountInWords(words);

	    return amountToCalculate;
	}
	
	private double parseDoubleSafe(Object value) {
	    try {
	        if (value == null) return 0;
	        return Double.parseDouble(value.toString().replaceAll("[^0-9.]", ""));
	    } catch (Exception e) {
	        return 0;
	    }
	}
	
	public class NumberToWords {

	    private static final String[] units = {
	            "", "One", "Two", "Three", "Four", "Five", "Six",
	            "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve",
	            "Thirteen", "Fourteen", "Fifteen", "Sixteen",
	            "Seventeen", "Eighteen", "Nineteen"
	    };

	    private static final String[] tens = {
	            "", "", "Twenty", "Thirty", "Forty", "Fifty",
	            "Sixty", "Seventy", "Eighty", "Ninety"
	    };

	    public static String convert(double amount) {

	        long rupees = (long) amount;
	        int paise = (int) Math.round((amount - rupees) * 100);

	        if (rupees == 0 && paise == 0) {
	            return "Zero Only";
	        }

	        StringBuilder result = new StringBuilder();

	        if (rupees > 0) {
	            result.append(convertNumber(rupees)).append(" Rupees");
	        }

	        if (paise > 0) {
	            if (rupees > 0) result.append(" and ");
	            result.append(convertNumber(paise)).append(" Paise");
	        }

	        result.append(" Only");

	        return result.toString().trim();
	    }

	    private static String convertNumber(long number) {

	        StringBuilder words = new StringBuilder();

	        if (number >= 10000000) {
	            words.append(convertNumber(number / 10000000)).append(" Crore ");
	            number %= 10000000;
	        }

	        if (number >= 100000) {
	            words.append(convertNumber(number / 100000)).append(" Lakh ");
	            number %= 100000;
	        }

	        if (number >= 1000) {
	            words.append(convertNumber(number / 1000)).append(" Thousand ");
	            number %= 1000;
	        }

	        if (number >= 100) {
	            words.append(convertNumber(number / 100)).append(" Hundred ");
	            number %= 100;
	        }

	        if (number > 0) {
	            if (number < 20) {
	                words.append(units[(int) number]).append(" ");
	            } else {
	                words.append(tens[(int) number / 10]).append(" ");
	                if (number % 10 > 0) {
	                    words.append(units[(int) number % 10]).append(" ");
	                }
	            }
	        }

	        return words.toString().trim();
	    }
	}
	
	public String formatAddress(String address) {
	    if (address == null || address.isEmpty()) return "";

	    String[] words = address.trim().split("\\s+");
	    StringBuilder result = new StringBuilder();
	    for (int i = 0; i < words.length; i++) {
	        result.append(words[i]).append(" ");
	        if ((i + 1) % 6 == 0) {
	            result.append("<br/>");
	        }
	    }

	    return result.toString().trim();
	}

}
