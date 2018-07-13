package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.SpreadSheetFormat;
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

  @PostMapping("/spreadsheet")
  public HttpEntity<byte[]> downloadSpreadsheet(@RequestParam(name = "format", required = true) String format,
      @RequestBody SpreadsheetDataDto dto, HttpServletResponse response) {
    return MisoWebUtils.generateSpreadsheet(dto.getHeaders(), dto.getRows(), SpreadSheetFormat.valueOf(format), response);
  }

}
