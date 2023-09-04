package com.example.fileUpload.documentParser.module;

import com.example.fileUpload.unit.MimeType;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.Ole10Native;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Slf4j
@Component
public class XOfficeEntryHandler {

    public static final Pattern DiractoryPattern = Pattern.compile("([^/]+)\\.(\\w+)$");

    public static void getParseFile(List<PackagePart> picture, String fileOlePath) throws IOException {
        for(int i=0; i<picture.size(); i++) {
            if(picture.get(i).getContentType().equals(MimeType.OLEOBJECT.getValue())){

                POIFSFileSystem poifs = new POIFSFileSystem(picture.get(i).getInputStream());
                DirectoryEntry root = poifs.getRoot();
                DocumentEntry ole10NativeEntry = (DocumentEntry)root.getEntry(Ole10Native.OLE10_NATIVE);

                InputStream oleInputStream = poifs.createDocumentInputStream(ole10NativeEntry.getName());

                EmbeddedFileExtractor.parseOle10NativeEntry(oleInputStream, fileOlePath);

                poifs.close();
                continue;
            }

            Matcher matcher = DiractoryPattern.matcher(picture.get(i).getPartName().getName());

            if(matcher.find()){
                String fileNameAndExtension = matcher.group(0);


                try (FileOutputStream fileOutputStream = new FileOutputStream(fileOlePath +"\\"+ fileNameAndExtension)) {
                    fileOutputStream.write(picture.get(i).getInputStream().readAllBytes());
                } catch (IOException e) {
                    ExceptionUtils.getStackTrace(e);
                    log.error("파일 저장 실패");
                }

            }
        }
    }
}
