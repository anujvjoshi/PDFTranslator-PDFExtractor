package com.sydac.pdfexcel.app;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.list.TreeList;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadExcel
{

  public static void main(String[] args)
  {
    try
    {
      File inputFile = new File(
          "C:\\WORK_DAT\\Projects\\DOCUMENTS\\extraction\\47_PDFsam_1_PDFsam_Operation_and_Drive_manual.xlsx");
      FileInputStream file = new FileInputStream(inputFile);
      XSSFWorkbook workbook = new XSSFWorkbook(file);
      System.out.println("ReadExcel.main() --> "+inputFile.getAbsolutePath() + " processing started..!");
      List<EventStrategy> events = new ArrayList<>();
      int sheetIndex = 0;
      for (Sheet sheet : workbook)
      {

        TreeList<Cell> columns = new TreeList<>();
        for (Row row : sheet)
        {

          if (sheetIndex == 0 && row.getRowNum() == 5 || sheetIndex > 0 && row.getRowNum() == 2)
          {
            for (Cell c : row)
            {
              columns.add(c);
            }
          }

          if (sheetIndex == 0 && row.getRowNum() > 5 || sheetIndex > 0 && row.getRowNum() > 2)
          {

            EventStrategy event = new EventStrategy();

            for (Cell cell : columns)
            {

              StringBuffer value = new StringBuffer();

              String cellColumnName = getCellValue(cell);

              String cellValue = getCellValue(row.getCell(cell.getColumnIndex()));
              if (cellValue != null && cellColumnName != null)
              {
                cellColumnName = cellColumnName.replaceAll("\n", " ").trim();
                value.append(cellValue);
                value.trimToSize();

                if (EventHeadings.NO.getColumnName().equalsIgnoreCase(cellColumnName))
                {
                  event.setNo(value.toString());
                }
                else if (EventHeadings.BUZZER.getColumnName().equalsIgnoreCase(cellColumnName))
                {
                  event.setBuzzer(value.toString());
                }
                else if (EventHeadings.ALARM_BELL.getColumnName().equalsIgnoreCase(cellColumnName))
                {
                  event.setAlarmBell(value.toString());
                }
                else if (EventHeadings.UN_LOAD.getColumnName().equalsIgnoreCase(cellColumnName))
                {
                  event.setUnLoad(value.toString());
                }
                else if (EventHeadings.FORBID_EXCITATION.getColumnName().equalsIgnoreCase(cellColumnName))
                {
                  event.setForbidExcitation(value.toString());
                }
                else if (EventHeadings.SAND.getColumnName().equalsIgnoreCase(cellColumnName))
                {
                  event.setSand(value.toString());
                }
                else if (EventHeadings.NORMAL_BRAKE.getColumnName().equalsIgnoreCase(cellColumnName))
                {
                  event.setNormalBrake(value.toString());
                }
                else if (EventHeadings.EMERGENCY_BRAKE.getColumnName().equalsIgnoreCase(cellColumnName))
                {
                  event.setEmergencyBrake(value.toString());
                }
                else if (EventHeadings.STOP.getColumnName().equalsIgnoreCase(cellColumnName))
                {
                  event.setStop(value.toString());
                }
                else if (EventHeadings.RECORD.getColumnName().equalsIgnoreCase(cellColumnName))
                {
                  event.setRecord(value.toString());
                }
                else if (EventHeadings.EVENT_NAME.getColumnName().equalsIgnoreCase(cellColumnName))
                {
                  event.setEventName(value.toString());
                }
                else if (EventHeadings.RESET_WAY.getColumnName().equalsIgnoreCase(cellColumnName))
                {
                  event.setResetWay(value.toString());
                }
                else if (EventHeadings.EVENT_CODE.getColumnName().equalsIgnoreCase(cellColumnName))
                {
                  if (value.toString().equalsIgnoreCase("Auto")
                      || value.toString().equalsIgnoreCase("Auto delay")
                      || value.toString().equalsIgnoreCase("Auto zero"))
                  {
                    event.setEventName(event.getEventName() + " " + event.getResetWay());
                    event.setResetWay(value.toString());
                    event.setEventCode(getCellValue(row.getCell(cell.getColumnIndex() + 1)));
                  }
                  else
                  {
                    event.setEventCode(value.toString());
                  }
                }
              }
            }

            if (event.getEventCode() != null && !event.getEventCode().trim().equals(""))
            {

              if ("●".equals(event.getAlarmBell().trim()))
              {
                event.setPriority("1");
              }
              else if ("●".equals(event.getBuzzer().trim()))
              {
                event.setPriority("2");
              }
              else
              {
                event.setPriority("3");
              }

              events.add(event);
            }
          }

        }

        sheetIndex++;

      }
      WriteExcel.createTable(events,inputFile);
      
      

    }
    catch (Exception e)
    {
      System.out.println("ReadExcel.main() " + e.toString());
    }
  }

  public static String getCellValue(Cell cell)
  {
    String cellValue = "";
    if (cell != null)
    {
      switch (cell.getCellTypeEnum())
      {
      case STRING:
        cellValue = cell.getStringCellValue();
        break;
      case NUMERIC:
        cellValue = String.valueOf(cell.getNumericCellValue());
        break;
      case BOOLEAN:
        cellValue = String.valueOf(cell.getBooleanCellValue());
        break;
      case FORMULA:
        cellValue = cell.getCellFormula();
        break;
      default:
      }
    }
    else
    {
      cellValue = null;
    }
    return cellValue;
  }

  public static Integer getColumsInRow(Map<Integer, List<Object>> data, int i, Row row)
  {
    for (Cell cell : row)
    {
      data.get(i).add(cell);
    }
    return data.size();
  }

}
