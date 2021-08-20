package com.sydac.pdfexcel.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFTableStyleInfo;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Demonstrates how to create a simple table using Apache POI.
 */
public class WriteExcel
{

  public static void createTable(List<EventStrategy> events, File inputFile) throws IOException
  {

    try (Workbook wb = new XSSFWorkbook())
    {
      XSSFSheet sheet = (XSSFSheet)wb.createSheet();

      // Set which area the table should be placed in
      AreaReference reference = wb.getCreationHelper().createAreaReference(new CellReference(0, 0),
          new CellReference(2, 2));

      // Create
      XSSFTable table = sheet.createTable(reference); // creates a table having
                                                      // 3 columns as of area
                                                      // reference
      // but all of those have id 1, so we need repairing
      table.getCTTable().getTableColumns().getTableColumnArray(1).setId(2);
      table.getCTTable().getTableColumns().getTableColumnArray(2).setId(3);

      // For now, create the initial style in a low-level way
      table.getCTTable().addNewTableStyleInfo();
      // table.getCTTable().getTableStyleInfo().setName("TableStyleMedium2");

      // Style the table
      XSSFTableStyleInfo style = (XSSFTableStyleInfo)table.getStyle();
      // style.setName("TableStyleMedium2");
      /*
       * style.setShowColumnStripes(false); style.setShowRowStripes(true);
       * style.setFirstColumn(false); style.setLastColumn(false);
       * style.setShowRowStripes(true); style.setShowColumnStripes(true);
       */

      // Set the values for the table
      XSSFRow row;
      XSSFCell cell;

      // Create row
      row = sheet.createRow(0);

      for (int j = 0; j < EventHeadings.values().length; j++)
      {
        // Create cell
        cell = row.createCell(j);
        cell.setCellValue(EventHeadings.getColumnName(j));

      }

      for (int i = 1; i <= events.size(); i++)
      {
        // Create row
        row = sheet.createRow(i);
        EventStrategy event = events.get(i - 1);
        for (int j = 0; j < EventHeadings.values().length; j++)
        {
          // Create cell
          cell = row.createCell(j);
          String cellValue = "";

          if (j == EventHeadings.NO.getColumnIndex())
          {
            cellValue = event.getNo();
          }
          else if (j == EventHeadings.BUZZER.getColumnIndex())
          {
            cellValue = event.getBuzzer();
          }
          else if (j == EventHeadings.ALARM_BELL.getColumnIndex())
          {
            cellValue = event.getAlarmBell();
          }
          else if (j == EventHeadings.UN_LOAD.getColumnIndex())
          {
            cellValue = event.getUnLoad();
          }
          else if (j == EventHeadings.FORBID_EXCITATION.getColumnIndex())
          {
            cellValue = event.getForbidExcitation();
          }
          else if (j == EventHeadings.SAND.getColumnIndex())
          {
            cellValue = event.getSand();
          }
          else if (j == EventHeadings.NORMAL_BRAKE.getColumnIndex())
          {
            cellValue = event.getNormalBrake();
          }
          else if (j == EventHeadings.EMERGENCY_BRAKE.getColumnIndex())
          {
            cellValue = event.getEmergencyBrake();
          }
          else if (j == EventHeadings.STOP.getColumnIndex())
          {
            cellValue = event.getStop();
          }
          else if (j == EventHeadings.RECORD.getColumnIndex())
          {
            cellValue = event.getRecord();
          }
          else if (j == EventHeadings.EVENT_NAME.getColumnIndex())
          {
            cellValue = event.getEventName();
          }
          else if (j == EventHeadings.RESET_WAY.getColumnIndex())
          {
            cellValue = event.getResetWay();
          }
          else if (j == EventHeadings.EVENT_CODE.getColumnIndex())
          {
            cellValue = event.getEventCode();
          }
          else if (j == EventHeadings.PRIORITY.getColumnIndex())
          {
            cellValue = event.getPriority();
          }

          cell.setCellValue(cellValue);
        }
      }

      int lastIndexOfDot = inputFile.getName().lastIndexOf(".");

      // Save
      String outputFilePath = inputFile.getParent() + File.separator + inputFile.getName().substring(0, lastIndexOfDot) + "_new"
          + inputFile.getName().substring(lastIndexOfDot, inputFile.getName().length());
      try (FileOutputStream fileOut = new FileOutputStream(
          outputFilePath))
      {
        wb.write(fileOut);
        
        System.out.println("WriteExcel.createTable() --> "+outputFilePath + " processing completed..!");
      }
    }
  }
}
