package com.sydac.pdfexcel.app;

public enum EventHeadings
{
  NO(0, "NO."), 
  BUZZER(1, "Buzzer"), 
  ALARM_BELL(2, "Alarm bell"),
  UN_LOAD(3, "Un-load"),
  FORBID_EXCITATION(4, "Forbid excitation "),
  SAND(5, "Sand"), 
  NORMAL_BRAKE(6, "Normal brake"), 
  EMERGENCY_BRAKE(7, "Emergency brake"),
  STOP(8, "Stop"),
  RECORD(9, "Record"), 
  EVENT_NAME(10, "Event name"), 
  RESET_WAY(11, "Reset way"), 
  EVENT_CODE(12, "Event code"),
  PRIORITY(13, "Priority"),;

  String columnName;

  int columnIndex = 0;

  EventHeadings(Integer columnIndex, String columnName)
  {
    this.columnName = columnName;
    this.columnIndex = columnIndex;
  }

  public String getColumnName()
  {
    return columnName;
  }

  public int getColumnIndex()
  {
    return columnIndex;
  }

  public static String getColumnName(Integer columnIndex)
  {
    for (EventHeadings h : values())
    {
      if (h.getColumnIndex() == columnIndex)
      {
        return h.getColumnName();
      }
    }
    return null;
  }

}
