package com.sydac.pdfvalidator.app.pdf.translator;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sydac.pdfvalidator.app.python.api.PythonApi;

@Component
public class ParagraphProcessor {

	@Autowired
	PythonApi pythonApi;

	public void replace(XWPFDocument document) {
		List<XWPFParagraph> paragraphs = document.getParagraphs();

		for (XWPFParagraph xwpfParagraph : paragraphs) {
			replace(xwpfParagraph, "");
		}
	}

	void replace(XWPFParagraph paragraph, String replacedText) {

		removeAllRuns(paragraph);

		insertReplacementRuns(paragraph, replacedText);
	}

	private void insertReplacementRuns(XWPFParagraph paragraph, String replacedText) {
		String[] replacementTextSplitOnCarriageReturn = StringUtils.split(replacedText, "\n");

		for (int j = 0; j < replacementTextSplitOnCarriageReturn.length; j++) {
			String part = replacementTextSplitOnCarriageReturn[j];

			XWPFRun newRun = paragraph.insertNewRun(j);
			newRun.setText(part);

			if (j + 1 < replacementTextSplitOnCarriageReturn.length) {
				newRun.addCarriageReturn();
			}
		}
	}

	private void removeAllRuns(XWPFParagraph paragraph) {
		int size = paragraph.getRuns().size();
		for (int i = 0; i < size; i++) {
			paragraph.removeRun(0);
		}
	}

	public void process(XWPFParagraph paragraph, String srcLanguage[], String destLanguage) throws Exception {
		paragraph.getCTP().addNewPPr().addNewKeepLines().setVal(STOnOff.OFF);
		paragraph.getCTP().getPPr().addNewKeepNext().setVal(STOnOff.OFF);
		paragraph.setSpacingBetween(1);
		String paragraphText = paragraph.getText();
		if (!paragraphText.trim().equals("") && !paragraphText.trim().equals("")) {
			for (XWPFRun run : paragraph.getRuns()) {
				run.setText(pythonApi.translateText(srcLanguage, destLanguage, run.text().trim()), 0);
			}
		} 
	}

}