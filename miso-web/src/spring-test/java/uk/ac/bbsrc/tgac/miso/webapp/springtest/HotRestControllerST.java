package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

import org.springframework.web.servlet.*;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import javax.ws.rs.core.MediaType;

import org.checkerframework.checker.units.qual.Temperature;
import org.junit.Before;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import org.springframework.test.web.servlet.ResultActions;
import com.jayway.jsonpath.JsonPath;

import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.springframework.test.web.servlet.MvcResult;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.View;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.security.test.context.support.WithMockUser;
import uk.ac.bbsrc.tgac.miso.core.data.type.StatusType;
import static org.junit.Assert.*;
import java.util.Collections;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.HotRestController.SpreadsheetDataDto;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import org.springframework.test.web.servlet.MockMvc;
import java.util.Date;
import java.io.BufferedReader;
import java.io.FileReader;
import org.springframework.mock.web.MockHttpServletResponse;


public class HotRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/hot";

  @Test
  public void testDownloadSpreadsheet() throws Exception {
    SpreadsheetDataDto sheet = new SpreadsheetDataDto();

    List<String> headers = Arrays.asList("1", "2", "3", "4", "5");
    List<String> firstrow = Arrays.asList("one", "two", "three", "four", "five");
    List<String> secondrow = Arrays.asList("un", "deux", "trois", "quatre", "cinq");
    List<List<String>> rows = Arrays.asList(firstrow, secondrow);

    sheet.setHeaders(headers);
    sheet.setRows(rows);

    MockHttpServletResponse response = getMockMvc()
        .perform(post(CONTROLLER_BASE + "/spreadsheet").param("format", "CSV").content(makeJson(sheet))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType("text/csv"))
        .andExpect(header().longValue("Content-Length", 83)).andReturn().getResponse();

    String filename = response.getHeader("Content-Disposition").split("=")[1];
    List<List<String>> records = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
      String line;
      int row = 0;
      while ((line = br.readLine()) != null) {
        String[] values = line.split(",");
        records.add(Arrays.asList(values));
        if (row == 0) {
          checkArray(values, headers);
        } else {
          checkArray(values, rows.get(row - 1));
        }
        row++;
      }
    } catch (Exception e) {
    }
  }

  private void checkArray(String[] values, List<String> expected) {
    for (int i = 0; i < expected.size(); i++) {
      assertEquals(values[i], expected.get(i));
    }
  }

  @Test
  public void testImportSpreadsheet() throws Exception {
    String csvContent = "id,name,email,age\n" +
        "1,John Doe,john@email.com,25\n" +
        "2,Jane Smith,jane@email.com,30\n";

    MockMultipartFile file = new MockMultipartFile("file", "sheet.csv", "text/csv", csvContent.getBytes());

    getMockMvc().perform(multipart(CONTROLLER_BASE + "/import").file(file))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(4)))
        .andExpect(jsonPath("$[0].heading").value("id"))
        .andExpect(jsonPath("$[1].heading").value("name"))
        .andExpect(jsonPath("$[0].data.*", hasSize(2)))
        .andExpect(jsonPath("$[1].data[0]").value("John Doe"))
        .andExpect(jsonPath("$[1].data[1]").value("Jane Smith"));
  }

}
