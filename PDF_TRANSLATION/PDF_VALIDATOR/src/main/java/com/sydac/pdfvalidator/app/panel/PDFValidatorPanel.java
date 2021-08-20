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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.sydac.pdfvalidator.app.PdfValidatorApplication;
import com.sydac.pdfvalidator.app.pdf.validator.PDFTextStripperUtil;

import net.miginfocom.swing.MigLayout;

/**
 * @author mulukg
 * 
 *         The class PDFValidatorPanel gives interface to select and validate
 *         PDF files
 */
public class PDFValidatorPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JButton validateButton;

	private JButton browseButton;

	private CSVPrinter csvPrinter;

	private JLabel progressLabel;

	private JFileChooser fc;

	private File selectedDirectory;
	
	public PDFValidatorPanel() {

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
		setLayout(new MigLayout("", "[]2%[][]", "[]2%[][]"));
		setBorder(BorderFactory.createLineBorder(Color.black));

		JTextField selectedDirectoryText = new JTextField();
		selectedDirectoryText.setEditable(false);
		selectedDirectoryText.setPreferredSize(new Dimension(300, 18));
		selectedDirectoryText.setToolTipText("Selected PDF directory");

		browseButton = new JButton("Browse");
		browseButton.setToolTipText("Browse PDF directory");
		browseButton.setPreferredSize(new Dimension(100, 18));
		browseButton.addActionListener((ActionEvent event) -> {
			int returnVal = fc.showOpenDialog(this);
			progressLabel.setText("");
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				selectedDirectory = fc.getSelectedFile();
				if (selectedDirectory.isDirectory()) {
					validateButton.setEnabled(true);
					selectedDirectoryText.setText(selectedDirectory.getAbsolutePath());
				} else {
					validateButton.setEnabled(false);
					selectedDirectoryText.setText("");
					JOptionPane.showMessageDialog(this, "Invalid Selected Directory..!", "Invalid Selected Directory",
							JOptionPane.ERROR_MESSAGE);
				}
			}

		});

		validateButton = new JButton("Validate");
		validateButton.setToolTipText("Validate PDF directory");
		validateButton.setPreferredSize(new Dimension(100, 18));
		validateButton.setEnabled(false);

		validateButton.addActionListener(validateAction);

		progressLabel = new JLabel();
		progressLabel.setPreferredSize(new Dimension(280, 18));

		JButton exitButton = new JButton("Exit");
		exitButton.setToolTipText("Exit");
		exitButton.setPreferredSize(new Dimension(100, 18));
		exitButton.addActionListener((ActionEvent) -> System.exit(0));

		add(browseButton, "gaptop 15, split 2");
		add(selectedDirectoryText);
		add(validateButton, "wrap,gapbottom 15");

		add(progressLabel, "cell 0 1,width 1%");

		add(exitButton, "wrap");

	}

	ActionListener validateAction = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent action) {

			progressLabel.setText("We are processing inputs. Please Wait...!");

			browseButton.setEnabled(false);

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

					validatePdfDirectory(selectedDirectory);

					JOptionPane.showMessageDialog(null,
							"PDF Validation Completed..!\n Please view result at " + resultFilePath, "Success..!",
							JOptionPane.INFORMATION_MESSAGE);
					csvPrinter.flush();

					csvPrinter.close();

				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, e.getMessage(), "Error..!", JOptionPane.ERROR_MESSAGE);
					throw new RuntimeException(e);
				} finally {
					progressLabel.setText("");
					browseButton.setEnabled(true);
				}

			}).start();
		}
	};

	private void validatePdfDirectory(File pdfRootDirectory) {
		if (pdfRootDirectory.isDirectory()) {
			Arrays.asList(pdfRootDirectory.listFiles()).forEach(file -> {
				if (file.isDirectory()) {
					validatePdfDirectory(file);
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
			}
		}

	}

}
