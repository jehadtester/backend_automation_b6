package apache_poi_excel;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ReadingFromExcel {
    public static void main(String[] args) throws IOException {

        String excelFilePath = "test_data/ReadData.xlsx";


        FileInputStream fileInputStream = new FileInputStream(excelFilePath);

        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);

        XSSFSheet sheet = workbook.getSheet("Sheet1");


        String dept = sheet.getRow(2).getCell(2).getStringCellValue();
        System.out.println(dept);

        int lastRow = sheet.getLastRowNum();

        int lastCell = sheet.getRow(1).getLastCellNum();

    }
}
