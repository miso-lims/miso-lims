package uk.ac.bbsrc.tgac.miso.core.data.spreadsheet;

import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.odftoolkit.odfdom.doc.table.OdfTableCell;

public abstract class Column<T> {

  public static <T> Column<T> forDate(String name, Function<T, Date> transform) {
    return new Column<T>(name) {

      @Override
      void appendCsv(StringBuilder builder, T value) {
        Date date = transform.apply(value);
        if (date != null) {
          builder.append(DateTimeFormatter.ISO_LOCAL_DATE.format(date.toInstant()));
        }
      }

      @Override
      void setExcel(XSSFCell cell, T value) {
        cell.setCellValue(transform.apply(value));
      }

      @Override
      void setODF(OdfTableCell cell, T value) {
        Date date = transform.apply(value);
        if (date != null) {
          Calendar c = Calendar.getInstance();
          c.setTime(date);
          cell.setTimeValue(c);
        }
      }
    };
  }

  public static <T> Column<T> forDouble(String name, Function<T, Double> transform) {
    return new Column<T>(name) {

      @Override
      void appendCsv(StringBuilder builder, T value) {
        Double result = transform.apply(value);
        if (result != null) {
          builder.append(result.doubleValue());
        }
      }

      @Override
      void setExcel(XSSFCell cell, T value) {
        Double result = transform.apply(value);
        if (result != null) {
          cell.setCellValue(result);
        }
      }

      @Override
      void setODF(OdfTableCell cell, T value) {
        cell.setDoubleValue(transform.apply(value));
      }
    };
  }

  public static <T> Column<T> forString(String name, Function<T, String> transform) {
    return new Column<T>(name) {

      @Override
      void appendCsv(StringBuilder builder, T value) {
        String result = transform.apply(value);
        if (result != null) {
          builder.append('"');
          result.codePoints().forEach(codepoint -> {
            switch (codepoint) {
            case '"':
              builder.append("\"\"");
              break;
            case '\r':
            case '\n':
              break;
            default:
              builder.appendCodePoint(codepoint);
            }
          });
          builder.append('"');
        }
      }

      @Override
      void setExcel(XSSFCell cell, T value) {
        cell.setCellValue(transform.apply(value));
      }

      @Override
      void setODF(OdfTableCell cell, T value) {
        cell.setStringValue(transform.apply(value));
      }
    };
  }

  private final String name;

  public Column(String name) {
    super();
    this.name = name;
  }

  abstract void appendCsv(StringBuilder builder, T value);

  public String name() {
    return name;
  }

  abstract void setExcel(XSSFCell cell, T value);

  abstract void setODF(OdfTableCell cell, T value);

}
