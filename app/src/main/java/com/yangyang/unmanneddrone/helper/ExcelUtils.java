package com.yangyang.unmanneddrone.helper;

import android.text.TextUtils;
import android.util.Log;

import com.yangyang.unmanneddrone.body.SelectionBody;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @description: Excel 工具类
 * 读取excel
 * @author: ODM
 * @date: 2021/3/24
 */
public class ExcelUtils {

    /**
     * 读取Excel文件
     *
     * @param file
     * @throws FileNotFoundException
     */
    public static List<SelectionBody> readExcel(File file, List<SelectionBody> list) throws FileNotFoundException {
        if (file == null) {
            Log.e("NullFile", "读取Excel出错，文件为空文件");
            return null;
        }
        InputStream stream = new FileInputStream(file);
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(stream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int rowsCount = sheet.getPhysicalNumberOfRows();
            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
            for (int r = 0; r < rowsCount; r++) {
                Row row = sheet.getRow(r);
                int cellsCount = row.getPhysicalNumberOfCells();
                //每次读取一行的内容
                for (int c = 0; c < cellsCount; c++) {
                    //将每一格子的内容转换为字符串形式
                    String value = getCellAsString(row, c, formulaEvaluator);
                    String cellInfo = "r:" + r + "; c:" + c + "; v:" + value;
                    // 定义对象去承载这个excel书记

                    Log.e("TAG", "===========cellInfo==========" + cellInfo);
                }
            }
        } catch (Exception e) {
            /* proper exception handling to be here */
            Log.e("EasyAndroid", "===========e==========" + e);
        }
        return list;
    }

    public static List<SelectionBody> readExcel(InputStream inputStream, List<SelectionBody> list) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int rowsCount = sheet.getPhysicalNumberOfRows();
            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
            for (int r = 0; r < rowsCount; r++) {
                Row row = sheet.getRow(r);
                int cellsCount = row.getPhysicalNumberOfCells();
                //每次读取一行的内容
                SelectionBody selectionBody = new SelectionBody();
                for (int c = 0; c < cellsCount; c++) {
                    //将每一格子的内容转换为字符串形式
                    String value = getCellAsString(row, c, formulaEvaluator);
                    String cellInfo = "r:" + r + "; c:" + c + "; v:" + value;
                    Log.e("TAG", "===========cellInfo==========" + cellInfo);
                    //
                    if (r > 2 && c == 0) {
                        selectionBody.setSerialNum(value);
                    } else if (r > 2 && c == 1) {
                        selectionBody.setAbscissa(value);
                    } else if (r > 2 && c == 2) {
                        selectionBody.setOrdinate(value);
                    }
                }
                if (!TextUtils.isEmpty(selectionBody.getAbscissa())
                        && !TextUtils.isEmpty(selectionBody.getOrdinate())
                        && !TextUtils.isEmpty(selectionBody.getSerialNum())) {
                    list.add(selectionBody);
                }
            }
        } catch (Exception e) {
            /* proper exception handling to be here */
            Log.e("EasyAndroid", "===========e==========" + e);
        }
        return list;
    }

    /**
     * 读取excel文件中每一行的内容
     *
     * @param row
     * @param c
     * @param formulaEvaluator
     * @return
     */
    private static String getCellAsString(Row row, int c, FormulaEvaluator formulaEvaluator) {
        String value = "";
        try {
            Cell cell = row.getCell(c);
            CellValue cellValue = formulaEvaluator.evaluate(cell);
            switch (cellValue.getCellType()) {
                case Cell.CELL_TYPE_BOOLEAN:
                    value = "" + cellValue.getBooleanValue();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    double numericValue = cellValue.getNumberValue();
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        double date = cellValue.getNumberValue();
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
                        value = formatter.format(HSSFDateUtil.getJavaDate(date));
                    } else {
                        value = "" + numericValue;
                    }
                    break;
                case Cell.CELL_TYPE_STRING:
                    value = "" + cellValue.getStringValue();
                    break;
                default:
                    break;
            }
        } catch (NullPointerException e) {
            /* proper error handling should be here */
            Log.e("TAG", "===========e==========" + e);
        }
        return value;
    }

    /**
     * 根据类型后缀名简单判断是否Excel文件
     *
     * @param file 文件
     * @return 是否Excel文件
     */
    public static boolean checkIfExcelFile(File file) {
        if (file == null) {
            return false;
        }
        String name = file.getName();
        //”.“ 需要转义字符
        String[] list = name.split("\\.");
        //划分后的小于2个元素说明不可获取类型名
        if (list.length < 2) {
            return false;
        }
        String typeName = list[list.length - 1];
        //满足xls或者xlsx才可以
        return "xls".equals(typeName) || "xlsx".equals(typeName);
    }
}