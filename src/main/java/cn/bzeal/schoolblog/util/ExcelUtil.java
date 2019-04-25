package cn.bzeal.schoolblog.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 解析Excel表格工具类
 * Created by Godbobo on 2019/4/21.
 */
@Component
public class ExcelUtil {

    // 处理上传文件
    public Map<Integer, Map<String, Object>> getExcelContent(MultipartFile file) throws Exception {
        Map<Integer, Map<String, Object>> res = new HashMap<>();
        Workbook wb = getWorkbook(file);
        if (wb == null || wb.getNumberOfSheets() <= 0 || wb.getSheetAt(0).getPhysicalNumberOfRows() <= 0) {
            throw new Exception("读取表格数据失败");
        }
        Sheet sheet = wb.getSheetAt(0);
        // 获取总行数
        int rowNum = sheet.getLastRowNum();
        Row header  = sheet.getRow(0);
        int colNum = header.getPhysicalNumberOfCells();
        Row row;
        for (int i = 1;i<=rowNum;i++){
            row = sheet.getRow(i);
            int j = 0;
            Map<String, Object> cellValue = new HashMap<>();
            while (j < colNum) {
                cellValue.put(String.valueOf(getCellAfterFormat(header.getCell(j))), getCellAfterFormat(row.getCell(j++)));
            }
            res.put(i, cellValue);
        }
        return res;
    }

    public Workbook getWorkbook(MultipartFile file) {
        String filepath = file.getOriginalFilename();
        String ext = filepath.substring(filepath.lastIndexOf("."));
        Workbook wb = null;
        try {
            InputStream is = file.getInputStream();
            if (".xls".equals(ext)) {
                wb = new HSSFWorkbook(is);
            } else if (".xlsx".equals(ext)) {
                wb = new XSSFWorkbook(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wb;
    }

    // 根据Cell类型设置数据
    public Object getCellAfterFormat(Cell cell){
        Object res = "";
        if (cell != null) {
            switch (cell.getCellTypeEnum()){
                case NUMERIC:
                case FORMULA:
                    if(DateUtil.isCellDateFormatted(cell)) {
                        res = cell.getDateCellValue();
                    } else{
                        Double d = cell.getNumericCellValue();
                        res = String.valueOf(d.intValue());
                    }
                    break;
                case STRING:
                    res = cell.getRichStringCellValue().getString();
                    break;
            }
        }
        return res;
    }

}
