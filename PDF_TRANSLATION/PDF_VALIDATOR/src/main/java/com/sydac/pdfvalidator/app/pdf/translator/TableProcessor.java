package com.sydac.pdfvalidator.app.pdf.translator;

import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sydac.pdfvalidator.app.python.api.PythonApi;

@Component
public class TableProcessor {
	@Autowired
	PythonApi pythonApi;

	@Autowired
	private ParagraphProcessor paragraphProcessor;

	public void process(XWPFTable table, String[] srcLanguage, String destLanguage) throws Exception {

		table.getRows().forEach(row -> {

			row.setCantSplitRow(false);// don't divide this row
			row.getTableCells().forEach(column -> {
				column.getParagraphs().forEach(paragraph -> {
					try {
						paragraphProcessor.process(paragraph, srcLanguage, destLanguage);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				});
			});
		});

	}

}