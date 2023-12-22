package com.example.fileUpload.documentParser.ExtractEngine;

import org.apache.poi.hpsf.ClassID;
import org.apache.poi.poifs.crypt.EncryptionInfo;

import java.util.HashMap;
import java.util.Map;

public enum MakeClassIDPredefined {

    PAINT("{0003000A-0000-0000-C000-000000000046}", ".bmp", "image/bmp"),
    HWP_V5("{965829DB-438E-4D31-B4FA-F1F8819A35FD}", ".hwp", "application/x-hwp-v5"),
    ODP("{C282417B-2662-44B8-8A94-3BFF61C50900}", ".odp", "application/vnd.oasis.opendocument.presentation"),
    ODS("{EABCECDB-CC1C-4A6F-B4E3-7F888A5ADFC8}", ".ods", "application/vnd.oasis.opendocument.spreadsheet"),
    ODT("{1B261B22-AC6A-4E68-A870-AB5080E8687B}", ".odt","application/vnd.oasis.opendocument.text");

    private static final Map<String, MakeClassIDPredefined> LOOKUP = new HashMap<String, MakeClassIDPredefined>();
    private final String externalForm;
    private ClassID classId;
    private final String fileExtension;
    private final String contentType;

    private MakeClassIDPredefined(String externalForm, String fileExtension, String contentType) {
        this.externalForm = externalForm;
        this.fileExtension = fileExtension;
        this.contentType = contentType;
    }

    public ClassID getClassID() {
        synchronized(this) {
            if (this.classId == null) {
                this.classId = new ClassID(this.externalForm);
            }
        }

        return this.classId;
    }

    public String getFileExtension() {
        return this.fileExtension;
    }

    public String getContentType() {
        return this.contentType;
    }

    public static MakeClassIDPredefined lookup(String externalForm) {
        return LOOKUP.get(externalForm);
    }

    public static MakeClassIDPredefined lookup(ClassID classID) {
        return classID == null ? null : LOOKUP.get(classID.toString());
    }

    public boolean equals(ClassID classID) {
        return this.getClassID().equals(classID);
    }

    static {
        MakeClassIDPredefined[] var0 = values();
        int var1 = var0.length;

        for(int var2 = 0; var2 < var1; ++var2) {
            MakeClassIDPredefined p = var0[var2];
            LOOKUP.put(p.externalForm, p);
        }

    }
}
