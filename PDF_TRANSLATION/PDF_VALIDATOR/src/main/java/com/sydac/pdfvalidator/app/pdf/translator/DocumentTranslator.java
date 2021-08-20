package com.sydac.pdfvalidator.app.pdf.translator;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sydac.pdfvalidator.app.PdfValidatorApplication;

@Component
public class DocumentTranslator {

	@Autowired
	private ParagraphProcessor paragraphProcessor;

	@Autowired
	private TableProcessor tableProcessor;

	public void translateDoc(String inputDoc, String outputDoc, String[] srcLanguage, String destLanguage) {

		try (XWPFDocument doc = new XWPFDocument(OPCPackage.open(new FileInputStream(inputDoc)))) {

			doc.getParagraphs().parallelStream().forEach(paragraph -> {
				try {
					paragraphProcessor.process(paragraph, srcLanguage, destLanguage);
				} catch (Exception e) {
					PdfValidatorApplication.LOGGER.error("Exception", e);
				}
			});

			doc.getTables().parallelStream().forEach(table -> {
				try {
					tableProcessor.process(table, srcLanguage, destLanguage);
				} catch (Exception e) {
					PdfValidatorApplication.LOGGER.error("Exception", e);
				}
			});

			doc.getHeaderList().parallelStream().forEach(header -> {
				header.getParagraphs().forEach(paragraph -> {
					try {
						paragraphProcessor.process(paragraph, srcLanguage, destLanguage);
					} catch (Exception e) {
						PdfValidatorApplication.LOGGER.error("Exception", e);
					}
				});
			});

			doc.getFooterList().parallelStream().forEach(footer -> {

				footer.getParagraphs().forEach(paragraph -> {
					try {
						paragraphProcessor.process(paragraph, srcLanguage, destLanguage);
					} catch (Exception e) {
						PdfValidatorApplication.LOGGER.error("Exception", e);
					}
				});

			});

			doc.getFootnotes().parallelStream().forEach(footNote -> {
				footNote.getParagraphs().forEach(paragraph -> {
					try {
						paragraphProcessor.process(paragraph, srcLanguage, destLanguage);
					} catch (Exception e) {
						PdfValidatorApplication.LOGGER.error("Exception", e);
					}
				});
			});
			

			try (FileOutputStream fout = new FileOutputStream(outputDoc)) {
				doc.write(fout);
			}
		} catch (Exception e) {
			PdfValidatorApplication.LOGGER.error("Exception", e);
		}

	}

}
