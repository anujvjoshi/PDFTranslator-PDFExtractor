/*
 * ==================================================================
 * 
 * (C) Copyright 2020 Sydac Pty Ltd., all rights reserved. This is unpublished
 * proprietary source code of Sydac. The copyright notice above does not
 * evidence any actual or intended publication of such source code.
 * 
 * ==================================================================
 */
package com.sydac.pdfvalidator.app.pdf.validator;

import java.io.IOException;
import java.util.Optional;

import org.apache.pdfbox.preflight.ValidationResult;
import org.apache.pdfbox.preflight.ValidationResult.ValidationError;

/**
 * @author mulukg The class PdfValidatonPage is used for validating PDF
 *         documents
 */

public class PdfValidatonPage {

	public static void main(String args[]) throws IOException {
		String fileName = "C:\\WORK_DATA\\DOCUMENTS\\extraction\\47_PDFsam_1_PDFsam_Operation and Drive manual.pdf";

		if (PDFTextStripperUtil.isValidPDF(fileName)) {
			System.out.println("The file " + fileName + " is a valid PDF file");
		} else {
			System.out.println("Not a valid PDF file");
			Optional<ValidationResult> validationResult = PDFTextStripperUtil.getValidationResult(fileName);
			if (!validationResult.isPresent()) {
				return;
			}
			ValidationResult result = validationResult.get();
			for (ValidationError error : result.getErrorsList()) {
				System.out.println(error.getErrorCode() + " : " + error.getDetails());
			}
		}
	}

}