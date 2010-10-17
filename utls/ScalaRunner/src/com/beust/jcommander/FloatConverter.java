/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.beust.jcommander;

import com.beust.jcommander.converters.BaseConverter;

/**
 *
 * @author nickl
 */
public class FloatConverter extends BaseConverter<Float> {

  public FloatConverter(String optionName) {
    super(optionName);
  }

  @Override
  public Float convert(String value) {
    try {
      return Float.parseFloat(value);
    } catch(NumberFormatException ex) {
      throw new ParameterException(getErrorString(value, "a float"));
    }
  }

}

class FloatConverterFactory implements IStringConverterFactory {
  public Class<? extends IStringConverter<?>> getConverter(Class forType) {
    if (forType.equals(float.class)) return FloatConverter.class;
    if (forType.equals(Float.class)) return FloatConverter.class;
    else return null;
  }
}

