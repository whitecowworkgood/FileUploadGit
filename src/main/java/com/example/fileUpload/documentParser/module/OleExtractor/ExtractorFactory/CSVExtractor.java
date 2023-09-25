package com.example.fileUpload.documentParser.module.OleExtractor.ExtractorFactory;

import com.example.fileUpload.documentParser.module.OleExtractor.abstracts.OleExtractor;
import com.example.fileUpload.model.FileDto;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;

import static com.example.fileUpload.util.ExternalFileMap.addUniqueFileNameMapping;

public class CSVExtractor extends OleExtractor {
    private final String oleSavePath;
    private final PackagePart packagePart;

    OPCPackage docPackage = null;
    Workbook workbook = null;
    BufferedWriter csvWriter = null;
    Sheet sheet = null;

    private void doExtract(){

        try {
            openCSV();

        } catch (Exception e) {
            catchException(e);

        } finally {
            closeResources();
        }

    }

    private void openCSV() throws Exception {
        docPackage = OPCPackage.open(packagePart.getInputStream());
        workbook = new XSSFWorkbook(docPackage);

        // 시트 선택 (시트 인덱스 또는 이름 사용 가능)
        sheet = workbook.getSheetAt(0); // 첫 번째 시트 선택 (인덱스 0부터 시작)

        // CSV 파일 경로 및 파일명 설정
        stringBuffer.append(sheet.getSheetName()).append(".csv");
        String uuid = addUniqueFileNameMapping(stringBuffer.toString());
        stringBuffer.delete(0, stringBuffer.length());

        stringBuffer.append(oleSavePath).append(uuid);

        csvWriter = new BufferedWriter(new FileWriter(stringBuffer.toString(), Charset.forName("EUC-KR")));

        writeCSVFile();
    }
    private void writeCSVFile() throws IOException {
        // 각 행을 반복하여 CSV로 쓰기
        for (Row row : sheet) {
            for (Cell cell : row) {
                String cellValue = switch (cell.getCellType()) {
                    case STRING -> cell.getStringCellValue();
                    case NUMERIC -> String.valueOf(cell.getNumericCellValue());
                    case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
                    case BLANK -> "";
                    default -> "";
                };
                // CSV 파일에 쓰기
                csvWriter.write(cellValue);

                // 다음 셀에 데이터가 없으면 줄바꿈
                if (cell.getColumnIndex() < row.getLastCellNum() - 1) {
                    csvWriter.write(",");
                } else {
                    csvWriter.newLine();
                }
            }
        }
    }
    @Override
    protected void closeResources() {
        stringBuffer.delete(0, stringBuffer.length());
        IOUtils.closeQuietly(docPackage);
        IOUtils.closeQuietly(workbook);
        IOUtils.closeQuietly(csvWriter);
    }

    public CSVExtractor(PackagePart pPart, FileDto fileDto) {
        this.oleSavePath = fileDto.getFileOlePath();
        this.packagePart = pPart;

        doExtract();
    }
}
