package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.Response.Status;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.DelimitedSpreadsheetWrapper;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.ExcelSpreadsheetWrapper;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.OpenDocumentSpreadsheetWrapper;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.SpreadSheetFormat;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.SpreadsheetWrapper;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;

@Controller
@RequestMapping("/rest/hot")
public class HotRestController extends RestController {

  public static class SpreadsheetDataDto {

    List<String> headers;
    List<List<String>> rows;

    public List<String> getHeaders() {
      return headers;
    }

    public void setHeaders(List<String> headers) {
      this.headers = headers;
    }

    public List<List<String>> getRows() {
      return rows;
    }

    public void setRows(List<List<String>> rows) {
      this.rows = rows;
    }

  }

  @Value("${miso.detailed.sample.enabled}")
  private Boolean detailedSample;

  @PostMapping(value = "/spreadsheet", produces = "application/octet-stream")
  public HttpEntity<byte[]> downloadSpreadsheet(@RequestParam(name = "format", required = true) String format,
      @RequestBody SpreadsheetDataDto dto, HttpServletResponse response) {
    return MisoWebUtils.generateSpreadsheet(dto.getHeaders(), dto.getRows(), detailedSample,
        SpreadSheetFormat.valueOf(format), response);
  }

  public static class ColumnDataDto {

    private String heading;

    private List<String> data = new ArrayList<>();

    public ColumnDataDto(String heading) {
      this.heading = heading;
    }

    public String getHeading() {
      return heading;
    }

    public void setHeading(String heading) {
      this.heading = heading;
    }

    public List<String> getData() {
      return data;
    }

    public void setData(List<String> data) {
      this.data = data;
    }

  }

  @PostMapping("/import")
  public @ResponseBody List<ColumnDataDto> importSpreadsheet(@RequestParam("file") MultipartFile file)
      throws IOException {
    String extension =
        file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.') + 1).toLowerCase();
    SpreadsheetWrapper sheet = null;
    switch (extension) {
      case "xlsx":
        sheet = new ExcelSpreadsheetWrapper(file.getInputStream());
        break;
      case "ods":
        sheet = new OpenDocumentSpreadsheetWrapper(file.getInputStream());
        break;
      case "csv":
        sheet = new DelimitedSpreadsheetWrapper(file.getInputStream());
        break;
      default:
        throw new RestException("Unknown file extension: " + extension + ". Only xlsx, ods, and csv files are accepted",
            Status.BAD_REQUEST);
    }

    int rows = sheet.getRowCount();
    if (rows < 2) {
      throw new RestException("Spreadsheet contains no data to import", Status.BAD_REQUEST);
    }
    List<ColumnDataDto> columns = new ArrayList<>();
    for (int i = 0; i < sheet.getColumnCount(); i++) {
      try {
        final String header = sheet.getCellValue(0, i);
        if (header.isEmpty()) {
          break;
        }
        columns.add(new ColumnDataDto(header));
      } catch (IllegalArgumentException e) {
        throw new RestException(String.format("Failed to parse heading cell at row 0, column %d", i),
            Status.BAD_REQUEST);
      }
    }
    for (int rowNum = 1; rowNum < sheet.getRowCount(); rowNum++) {
      List<String> values = new ArrayList<>();
      for (int colNum = 0; colNum < columns.size(); colNum++) {
        try {
          values.add(sheet.getCellValue(rowNum, colNum));
        } catch (IllegalArgumentException e) {
          throw new RestException(String.format("Failed to parse value of cell at row %d, column %d", rowNum, colNum),
              Status.BAD_REQUEST);
        }
      }
      if (values.stream().allMatch(""::equals)) {
        break;
      }
      for (int colNum = 0; colNum < columns.size(); colNum++) {
        columns.get(colNum).getData().add(values.get(colNum));
      }
    }
    return columns;
  }

}
