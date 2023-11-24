package com.example.fileUpload.Security.Wrapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.fileUpload.util.scriptRegex.scriptPatterns;

@Slf4j
public class XssEscapeRequestWrapper extends HttpServletRequestWrapper {

    public XssEscapeRequestWrapper(HttpServletRequest request) {
        super(request);
    }
    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (values == null) {
            return null;
        }

        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = cleanXSS(values[i]);
        }

        return encodedValues;
    }

    @Override
    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);
        return cleanXSS(value);
    }

    private String cleanXSS(String value) {

        if (value != null) {
            boolean hasMatched;

            do {
                hasMatched = false;
                //hasMatched = false;
                for (Pattern pattern : scriptPatterns) {
                    Matcher matcher = pattern.matcher(value);
                    if (matcher.find()) {
                        value = matcher.replaceAll("");
                        hasMatched = true;
                    }
                }
            } while (hasMatched);
        }
        return value;
    }
}
