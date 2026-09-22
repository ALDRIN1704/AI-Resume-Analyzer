package com.project.resume_analyzer.service;

import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ResumeParserService {

    public String extractText(MultipartFile file) throws IOException {

        validateFile(file);

        String fileName = file.getOriginalFilename();

        if (fileName == null) {
            throw new IllegalArgumentException("Invalid file name.");
        }

        String lowerFileName = fileName.toLowerCase();

        if (lowerFileName.endsWith(".pdf")) {
            return extractPdfText(file);
        }

        if (lowerFileName.endsWith(".docx")) {
            return extractDocxText(file);
        }

        throw new IllegalArgumentException(
                "Only PDF and DOCX files are supported."
        );
    }

    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Resume file cannot be empty."
            );
        }

        String fileName = file.getOriginalFilename();

        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException(
                    "Invalid file name."
            );
        }

        String lowerFileName = fileName.toLowerCase();

        if (!lowerFileName.endsWith(".pdf")
                && !lowerFileName.endsWith(".docx")) {

            throw new IllegalArgumentException(
                    "Only PDF and DOCX files are supported."
            );
        }
    }

    private String extractPdfText(MultipartFile file)
            throws IOException {

        try (PDDocument document =
                     Loader.loadPDF(file.getBytes())) {

            PDFTextStripper stripper =
                    new PDFTextStripper();

            String text = stripper.getText(document);

            if (text == null || text.isBlank()) {
                throw new IllegalArgumentException(
                        "No readable text found. "
                        + "The PDF may be scanned or image-based."
                );
            }

            return text;
        }
    }

    private String extractDocxText(MultipartFile file)
            throws IOException {

        StringBuilder text = new StringBuilder();

        try (InputStream inputStream = file.getInputStream();
             XWPFDocument document = new XWPFDocument(inputStream)) {

            document.getParagraphs().forEach(paragraph -> {

                String paragraphText = paragraph.getText();

                if (paragraphText != null
                        && !paragraphText.isBlank()) {

                    text.append(paragraphText)
                            .append("\n");
                }
            });

            for (XWPFTable table : document.getTables()) {

                for (XWPFTableRow row : table.getRows()) {

                    for (XWPFTableCell cell :
                            row.getTableCells()) {

                        String cellText = cell.getText();

                        if (cellText != null
                                && !cellText.isBlank()) {

                            text.append(cellText)
                                    .append("\n");
                        }
                    }
                }
            }
        }

        String extractedText = text.toString();

        if (extractedText.isBlank()) {
            throw new IllegalArgumentException(
                    "No readable text found in the DOCX file."
            );
        }

        return extractedText;
    }
}