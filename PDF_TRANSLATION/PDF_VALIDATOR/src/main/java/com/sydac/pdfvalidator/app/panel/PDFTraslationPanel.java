/*
 * ==================================================================
 * 
 * (C) Copyright 2020 Sydac Pty Ltd., all rights reserved. This is unpublished
 * proprietary source code of Sydac. The copyright notice above does not
 * evidence any actual or intended publication of such source code.
 * 
 * ==================================================================
 */
package com.sydac.pdfvalidator.app.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.aspose.pdf.DocSaveOptions;
import com.aspose.pdf.Document;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.sydac.pdfvalidator.app.PdfValidatorApplication;
import com.sydac.pdfvalidator.app.lang.Language;
import com.sydac.pdfvalidator.app.lang.LanguageUtility;
import com.sydac.pdfvalidator.app.pdf.translator.DocumentTranslator;
import com.sydac.pdfvalidator.app.pdf.validator.PDFTextStripperUtil;

import net.miginfocom.swing.MigLayout;

/**
 * @author mulukg
 * 
 *         The class PDFValidatorPanel gives interface to select and validate
 *         PDF files
 */
public class PDFTraslationPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JButton translateButton;

	private JButton browseButton;

	private CSVPrinter csvPrinter;

	private JLabel progressLabel;

	private JCheckBox pdfToWordCheckBox;

	private JFileChooser fc;

	private File selectedDirectory;

	private JList<Language> srcLanguages;

	private JComboBox<Language> destLanguage;

	private DocumentTranslator documentTranslator;

	private List<Language> languageList;

	public PDFTraslationPanel(DocumentTranslator documentTranslator)
			throws JsonMappingException, JsonProcessingException {

		this.documentTranslator = documentTranslator;

		this.languageList = LanguageUtility.getLanguages();

		initFileChooser();

		createUI();

	}

	private void initFileChooser() {
		selectedDirectory = null;
		fc = new JFileChooser();
		fc.setDialogTitle("Select PDF export directory");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	}

	private void createUI() {
		setPreferredSize(new Dimension(640, 250));
		setLayout(new MigLayout("", "[]2%[grow][]", "[]2%[grow][]"));
		setBorder(BorderFactory.createLineBorder(Color.black));

		JTextField selectedDirectoryText = new JTextField();
		selectedDirectoryText.setEditable(false);
		selectedDirectoryText.setPreferredSize(new Dimension(300, 18));
		selectedDirectoryText.setToolTipText("Selected PDF directory");

		browseButton = new JButton("Browse");
		browseButton.setToolTipText("Browse PDF directory");
		browseButton.setPreferredSize(new Dimension(130, 18));
		browseButton.addActionListener((ActionEvent event) -> {
			int returnVal = fc.showOpenDialog(this);
			progressLabel.setText("");
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				selectedDirectory = fc.getSelectedFile();
				if (selectedDirectory.isDirectory()) {
					enableDisableComponents(true);
					selectedDirectoryText.setText(selectedDirectory.getAbsolutePath());
				} else {
					enableDisableComponents(false);
					selectedDirectoryText.setText("");
					JOptionPane.showMessageDialog(this, "Invalid Selected Directory..!", "Invalid Selected Directory",
							JOptionPane.ERROR_MESSAGE);
				}
			}

		});

		translateButton = new JButton("Translate");
		translateButton.setToolTipText("Translate PDF directory");
		translateButton.setPreferredSize(new Dimension(100, 18));
		translateButton.setEnabled(false);

		translateButton.addActionListener(translateAction);

		pdfToWordCheckBox = new JCheckBox("Convert PDF to Word");
		pdfToWordCheckBox.setEnabled(true);
		pdfToWordCheckBox.setVisible(true);

		JLabel srcLanguageLabel = new JLabel("Source Language:");
		srcLanguageLabel.setMaximumSize(new Dimension(130, 18));

		DefaultListModel<Language> listModel = new DefaultListModel<Language>();
		srcLanguages = new JList<Language>(listModel);
		srcLanguages.setEnabled(false);
		srcLanguages.setMaximumSize(new Dimension(140, 100));
		srcLanguages.setMinimumSize(new Dimension(140, 100));
		srcLanguages.setVisibleRowCount(5);
		srcLanguages.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(srcLanguages);
		srcLanguages.setLayoutOrientation(JList.VERTICAL);

		JLabel destLanguageLabel = new JLabel("Destination Language:");
		destLanguageLabel.setMaximumSize(new Dimension(130, 18));
		destLanguage = new JComboBox<Language>();
		destLanguage.setEnabled(false);
		destLanguage.setMaximumSize(new Dimension(100, 18));

		languageList.forEach(language -> {
			listModel.addElement(language);
			if (!language.getCode().equals("-1")) {
				destLanguage.addItem(language);
			}
		});

		srcLanguages.setSelectedIndex(0);
		destLanguage.setSelectedItem(languageList.stream().filter(l -> l.getCode().equals("en")).findFirst().get());
		progressLabel = new JLabel();
		progressLabel.setMinimumSize(new Dimension(480, 18));
		progressLabel.setMaximumSize(new Dimension(480, 18));

		JButton exitButton = new JButton("Exit");
		exitButton.setToolTipText("Exit");
		exitButton.setPreferredSize(new Dimension(100, 18));
		exitButton.addActionListener((ActionEvent) -> System.exit(0));

		add(browseButton, "growx");
		add(selectedDirectoryText, "spanx 3, growx");
		add(pdfToWordCheckBox, "wrap");

		add(srcLanguageLabel, "growx");
		add(scrollPane, "growx");
		add(destLanguageLabel, "growx");
		add(destLanguage, "growx");

		add(translateButton, "wrap");

		add(progressLabel, "spanx 4,growx");

		add(exitButton, "wrap");

	}

	ActionListener translateAction = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent action) {

			progressLabel.setText("We are processing inputs. Please Wait...!");

			browseButton.setEnabled(false);
			enableDisableComponents(false);

			new Thread(() -> {

				String resultFilePath = selectedDirectory + File.separator + "invalidPDF.csv";

				File invalidPdf = new File(resultFilePath);

				if (invalidPdf.exists()) {
					invalidPdf.delete();
				}

				Writer writer;
				try {
					writer = Files.newBufferedWriter(Paths.get(resultFilePath));
					csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("File Name"));

					if (pdfToWordCheckBox.isSelected()) {
						savePdfToDocxDirectory(selectedDirectory);
					}

					String[] srcLanguageCodeArr = Arrays.asList(srcLanguages.getSelectedValue()).stream()
							.map(language -> ((Language) language).getCode()).collect(Collectors.toList()).stream()
							.toArray(String[]::new);

					String destLanguageCode = ((Language) destLanguage.getSelectedItem()).getCode();

					translateWordDirectory(selectedDirectory, srcLanguageCodeArr, destLanguageCode);

					JOptionPane.showMessageDialog(null,
							"Document Translation Completed..!\n Please view result at " + resultFilePath, "Success..!",
							JOptionPane.INFORMATION_MESSAGE);
					csvPrinter.flush();

					csvPrinter.close();

				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, e.getMessage(), "Error..!", JOptionPane.ERROR_MESSAGE);
					throw new RuntimeException(e);
				} finally {
					progressLabel.setText("");
					enableDisableComponents(true);
					browseButton.setEnabled(true);
				}

			}).start();
		}

	};

	private void enableDisableComponents(boolean isEnabled) {
		translateButton.setEnabled(isEnabled);
		translateButton.setEnabled(isEnabled);
		pdfToWordCheckBox.setEnabled(isEnabled);
		srcLanguages.setEnabled(isEnabled);
		destLanguage.setEnabled(isEnabled);
	}

	private void savePdfToDocxDirectory(File pdfRootDirectory) {
		if (pdfRootDirectory.isDirectory()) {
			Arrays.asList(pdfRootDirectory.listFiles()).forEach(file -> {
				if (file.isDirectory()) {
					savePdfToDocxDirectory(file);
				} else {
					validatePdf(file);
				}
			});
		}
	}

	private void validatePdf(File file) {

		if (file.getName().toLowerCase().endsWith(".pdf")) {

			if (!PDFTextStripperUtil.getValidationResult(file.getAbsolutePath()).isPresent()) {
				try {
					csvPrinter.printRecord(file.getAbsolutePath());
				} catch (Exception e) {
					PdfValidatorApplication.LOGGER.error("Exception",e);
				}
			} else {

				savePdfAsDocx(file);
			}

		}

	}

	private void translateWordDirectory(File wordDirectory, String[] srcLanguageCodeArr, String destLanguageCode) {
		String fileNameSuffix = "_" + destLanguageCode.toUpperCase() + ".docx";
		Arrays.asList(wordDirectory.listFiles()).forEach(file -> {
			if (file.isDirectory()) {
				translateWordDirectory(file, srcLanguageCodeArr, destLanguageCode);
			} else {
				if (file.getName().toLowerCase().endsWith(".docx")
						&& !file.getName().toLowerCase().endsWith(fileNameSuffix.toLowerCase())) {
					translateWordFile(file, srcLanguageCodeArr, destLanguageCode, fileNameSuffix);
				}
			}
		});
	}

	private void translateWordFile(File wordFile, String[] srcLanguageCodeArr, String destLanguageCode,
			String fileNameSuffix) {

		String inputDoc = wordFile.getAbsolutePath();

		String outputDoc = inputDoc.substring(0, inputDoc.length() - 5) + fileNameSuffix;

		PdfValidatorApplication.LOGGER.info("PDFValidatorPanel.saveAsDocx() started:  " + inputDoc);

		documentTranslator.translateDoc(inputDoc, outputDoc, srcLanguageCodeArr, destLanguageCode);

		PdfValidatorApplication.LOGGER.info("PDFValidatorPanel.saveAsDocx() completed:  " + inputDoc);

	}

	private void savePdfAsDocx(File file) {

		String fileLocation = file.getAbsolutePath();
		// Load source PDF file

		Document doc = new Document(fileLocation);

		// Instantiate DocSaveOptions instance
		DocSaveOptions saveOptions = new DocSaveOptions();

		// Set output format
		saveOptions.setFormat(DocSaveOptions.DocFormat.DocX);

		// Set the recognition mode as Flow
		saveOptions.setMode(DocSaveOptions.RecognitionMode.Flow);

		// Set the horizontal proximity as 2.5
		saveOptions.setRelativeHorizontalProximity(2.5f);

		// Enable bullets recognition during conversion process
		saveOptions.setRecognizeBullets(true);

		String fileName = fileLocation.substring(0, fileLocation.length() - 4) + ".docx";

		// Save resultant DOCX file
		doc.save(fileName, saveOptions);

		doc.close();

	}

}
