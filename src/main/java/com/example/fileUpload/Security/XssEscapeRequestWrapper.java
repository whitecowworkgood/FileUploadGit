package com.example.fileUpload.Security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class XssEscapeRequestWrapper extends HttpServletRequestWrapper {
    private static final Pattern[] scriptPatterns = {
            Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
            Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
            Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("onmouseover(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("onerror(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("onfocus(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("on\\w+\\s*=(.*?)\\s*", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("import\\s*\\(.*?\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("iframe\\s*src=.*", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("<a\\s+href[\\s]*=[\\s]*\"(?!javascript:)([^\"]+)\"[^>]*>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\\\[xX][0-9a-fA-F]{2}|\\\\[0-7]{3}|\\\\t", Pattern.CASE_INSENSITIVE),
            Pattern.compile("&(#[0-9]+|#x[0-9a-fA-F]+|[a-zA-Z]+);", Pattern.CASE_INSENSITIVE),
            Pattern.compile("<a\\shref=", Pattern.CASE_INSENSITIVE),
            Pattern.compile("</a>", Pattern.CASE_INSENSITIVE)

    };
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
            boolean hasMatched = false;

            do {
                hasMatched = false;
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
