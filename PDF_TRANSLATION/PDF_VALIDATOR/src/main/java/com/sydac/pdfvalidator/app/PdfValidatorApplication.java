/*
 * ==================================================================
 * 
 * (C) Copyright 2020 Sydac Pty Ltd., all rights reserved. This is unpublished
 * proprietary source code of Sydac. The copyright notice above does not
 * evidence any actual or intended publication of such source code.
 * 
 * ==================================================================
 */
package com.sydac.pdfvalidator.app;

import java.awt.EventQueue;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.sydac.pdfvalidator.app.panel.PDFTraslationPanel;
import com.sydac.pdfvalidator.app.panel.PDFValidatorPanel;
import com.sydac.pdfvalidator.app.pdf.translator.DocumentTranslator;

@SpringBootApplication
/**
 * @author mulukg
 * 
 *         The class PdfValidatorApplication is startup class for validating PDF
 *         documents
 */
public class PdfValidatorApplication extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	DocumentTranslator documentTranslator;
	
	public static final Logger LOGGER = LogManager.getLogger(PdfValidatorApplication.class);
	
	@PostConstruct
	private void initUI() throws IOException {

		JTabbedPane tabs = new JTabbedPane();
		tabs.add("PDF Validation", new PDFValidatorPanel());
		tabs.add("Document Translation", new PDFTraslationPanel(documentTranslator));

		add(tabs);

		tabs.setSelectedIndex(1);

		setTitle("PDF Export Validator");
		setSize(640, 250);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
	}

	public static void main(String[] args) {

		ApplicationContext ctx = new SpringApplicationBuilder(PdfValidatorApplication.class).headless(false).run(args);

		EventQueue.invokeLater(() -> {

			PdfValidatorApplication ex = ctx.getBean(PdfValidatorApplication.class);
			ex.setVisible(true);
		});
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
