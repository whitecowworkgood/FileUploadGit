package com.example.fileUpload.documentParser.module.OleExtractor.ExtractorFactory;

import com.example.fileUpload.documentParser.module.OleExtractor.abstracts.OleExtractor;
import com.example.fileUpload.model.File.FileDto;
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

    private final PackagePart packagePart;

    private OPCPackage docPackage = null;
    private Workbook workbook = null;
    private BufferedWriter csvWriter = null;
    private Sheet sheet = null;

    public void doExtract(){

        try {
            openCSV();

        } catch (Exception e) {
            catchException(e);

        } finally {
            closeResources();
        }

    }

    private void openCSV() throws Exception {
        this.docPackage = OPCPackage.open(this.packagePart.getInputStream());
        this.workbook = new XSSFWorkbook(this.docPackage);

        // 시트 선택 (시트 인덱스 또는 이름 사용 가능)
        this.sheet = this.workbook.getSheetAt(0); // 첫 번째 시트 선택 (인덱스 0부터 시작)

        // CSV 파일 경로 및 파일명 설정
        super.stringBuffer.append(this.sheet.getSheetName()).append(".csv");
        String uuid = addUniqueFileNameMapping(super.stringBuffer.toString());
        super.stringBuffer.delete(0, super.stringBuffer.length());

        super.stringBuffer.append(this.oleSavePath).append(uuid);

        this.csvWriter = new BufferedWriter(new FileWriter(super.stringBuffer.toString(), Charset.forName("EUC-KR")));

        this.writeCSVFile();
    }
    private void writeCSVFile() throws IOException {
        // 각 행을 반복하여 CSV로 쓰기
        for (Row row : this.sheet) {
            for (Cell cell : row) {
                String cellValue = switch (cell.getCellType()) {
                    case STRING -> cell.getStringCellValue();
                    case NUMERIC -> String.valueOf(cell.getNumericCellValue());
                    case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
                    case BLANK -> "";
                    default -> "";
                };
                // CSV 파일에 쓰기
                this.csvWriter.write(cellValue);

                // 다음 셀에 데이터가 없으면 줄바꿈
                if (cell.getColumnIndex() < row.getLastCellNum() - 1) {
                    this.csvWriter.write(",");
                } else {
                    this.csvWriter.newLine();
                }
            }
        }
    }
    @Override
    protected void closeResources() {
        super.stringBuffer.delete(0, super.stringBuffer.length());
        IOUtils.closeQuietly(this.docPackage);
        IOUtils.closeQuietly(this.workbook);
        IOUtils.closeQuietly(this.csvWriter);
    }

    public CSVExtractor(PackagePart pPart, FileDto fileDto) {
        super.oleSavePath = fileDto.getFileOlePath();
        this.packagePart = pPart;

        //doExtract();
    }
}
