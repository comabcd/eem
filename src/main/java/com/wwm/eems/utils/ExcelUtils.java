package com.wwm.eems.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.util.List;

public class ExcelUtils {
    
    public static <T> Workbook createWorkbook(String sheetName, String[] headers, List<T> dataList, DataWriter<T> dataWriter) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);
        
        // 创建标题行样式
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        
        // 写入标题行
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.autoSizeColumn(i);
        }
        
        // 写入数据行
        int rowNum = 1;
        for (T data : dataList) {
            Row row = sheet.createRow(rowNum++);
            dataWriter.write(row, data);
        }
        
        // 调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        return workbook;
    }
    
    @FunctionalInterface
    public interface DataWriter<T> {
        void write(Row row, T data);
    }
} 