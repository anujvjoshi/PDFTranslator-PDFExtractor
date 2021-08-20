package com.sydac.pdfvalidator.app.lang;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Language
{

  private String code;

  private String name;

  public Language(String code, String name)
  {
    this.code = code;
    this.name = name;
  }

  @Override
  public String toString()
  {
    return name;
  }

  public String getCode()
  {
    return code;
  }

  public String getName()
  {
    return name;
  }
  
  

}
