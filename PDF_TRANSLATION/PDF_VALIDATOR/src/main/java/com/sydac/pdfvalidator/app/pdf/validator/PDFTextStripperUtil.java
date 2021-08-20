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
import java.util.Objects;
import java.util.Optional;

import org.apache.pdfbox.preflight.PreflightDocument;
import org.apache.pdfbox.preflight.ValidationResult;
import org.apache.pdfbox.preflight.parser.PreflightParser;

/**
 * Utility class to work with PDF files
 * 
 * @author mulukg
 *
 */
public class PDFTextStripperUtil {
	public static Optional<ValidationResult> getValidationResult(String fileName) {
		if (Objects.isNull(fileName)) {
			throw new NullPointerException("fileName shouldn't be null");
		}

		try {
			PreflightParser parser = new PreflightParser(fileName);

			parser.parse();

			try (PreflightDocument document = parser.getPreflightDocument()) {
				document.validate();
				ValidationResult result = document.getResult();
				return Optional.of(result);
			}

		} catch (IOException e) {
			return Optional.empty();
		}

	}

	/**
	 * Return true if file is a valid PDF/A-1b file
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean isValidPDF(String fileName) {
		Optional<ValidationResult> validationResult = getValidationResult(fileName);

		if (!validationResult.isPresent()) {
			return false;
		}

		ValidationResult result = validationResult.get();
		if (result.isValid()) {
			return true;
		}

		return false;
	}
}
