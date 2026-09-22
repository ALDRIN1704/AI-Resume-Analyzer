package com.project.resume_analyzer.service;

import org.springframework.stereotype.Service;

@Service
public class TextCleaningService {

    public String cleanText(String text) {

        if (text == null || text.isBlank()) {
            return "";
        }

        return text
                .replace("\r\n", "\n")
                .replace("\r", "\n")
                .replace('\u00A0', ' ')
                .replaceAll("[\\t ]+", " ")
                .replaceAll("(?m)^ +| +$", "")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }
}