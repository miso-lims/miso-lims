package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import javax.print.attribute.standard.Media;
import javax.ws.rs.core.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.checkerframework.checker.units.qual.Temperature;
import org.junit.Before;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateDeletionDao;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.beans.factory.annotation.Value;

import com.jayway.jsonpath.JsonPath;
import java.io.BufferedReader;
import java.io.FileReader;

import jakarta.transaction.Transactional;
import javassist.bytecode.ExceptionTable;

import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.springframework.test.web.servlet.MvcResult;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;

import uk.ac.bbsrc.tgac.miso.core.data.IlluminaRun;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.impl.runimpl;
import uk.ac.bbsrc.tgac.miso.dto.run.IlluminaRunDto;
import org.springframework.security.test.context.support.WithMockUser;


import static org.junit.Assert.*;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.springframework.test.web.servlet.MockMvc;
import java.util.Date;

public class RunRestControllerST {
  private static final String CONTROLLER_BASE = "/rest/runs";
  private static final Class<IlluminaRun> entityClass = IlluminaRun.class;

  // all Run classes are basically the same besides Illumina (and Oxford Nanopore to some extent),
  // which have some additional fields

  // since the run rest controller is generalized for all Run types, it should be valid to just test
  // the controller with one specific run class. Illumina Run is used here as an arbitrary choice

  @Test
  public void testGetById() throws Exception {

  }

  @Test
  public void testGetParents() throws Exception {
    // parents should be pools I think
  }

  @Test
  public void testGetContainersByRunId() throws Exception {

  }

  @Test
  public void testGetByAlias() throws Exception {

  }


  @Test
  public void testDatatable() throws Exception {

  }

  @Test
  public void testDatatableByProject() throws Exception {

  }

  @Test
  public void testDatatableByPlatform() throws Exception {

  }

  @Test
  public void testDatatableBySequencer() throws Exception {

  }

  @Test
  public void testAddContainerByBarcode() throws Exception {

  }

  @Test
  public void testRemoveContainer() throws Exception {

  }

  @Test
  public void testSetQC() throws Exception {

  }

  @Test
  public void testSetPartitionPurposes() throws Exception {

  }

  @Test
  public void testSaveAliquots() throws Exception {

  }

  @Test
  public void testGetPotentialExperiments() throws Exception {

  }

  @Test
  public void testGetPotentialExperimentsExpansions() throws Exception {

  }



  @Test
  public void testSearch() throws Exception {

  }

  @Test
  public void testGetRecent() throws Exception {

  }

  @Test
  public void testCreate() throws Exception {

  }

  @Test
  public void testUpdate() throws Exception {

  }

  @Test
  public void testDelete() throws Exception {

  }

  @Test
  public void testDeleteFail() throws Exception {

  }

  @Test
  public void testGetSpreadsheet() throws Exception {

  }

  @Test
  public void testGetSampleSheetForRun() throws Exception {

  }

  @Test
  public void testGetSampleSheetForRunByAlias() throws Exception {

  }



}
