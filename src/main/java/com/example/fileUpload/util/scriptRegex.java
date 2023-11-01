package com.example.fileUpload.util;

import java.util.regex.Pattern;

public class scriptRegex {
    public static final Pattern[] scriptPatterns = {
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
            Pattern.compile("</a>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("<div(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("<img(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile(".*\\.\\..*|.*[.]+/.*", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
    };
}
