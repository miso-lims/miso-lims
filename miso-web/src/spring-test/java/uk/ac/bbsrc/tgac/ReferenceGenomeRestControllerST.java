package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;
import org.springframework.web.servlet.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import uk.ac.bbsrc.tgac.miso.core.data.ReferenceGenome;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ReferenceGenomeImpl;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.ReferenceGenomeDto;
import static org.hamcrest.Matchers.*;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.Assert.*;
import org.springframework.test.web.servlet.MockMvc;


public class ReferenceGenomeRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/referencegenomes";
  private static final Class<ReferenceGenomeImpl> entityClass = ReferenceGenomeImpl.class;

  private List<ReferenceGenomeDto> makeCreateDtos() {
    ReferenceGenomeDto dto1 = new ReferenceGenomeDto();
    dto1.setAlias("newOne");
    dto1.setDefaultScientificNameId(1L);


    ReferenceGenomeDto dto2 = new ReferenceGenomeDto();
    dto2.setAlias("newTwo");
    dto2.setDefaultScientificNameId(2L);

    return Arrays.asList(dto1, dto2);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkCreateAsync() throws Exception {
    // only admin can create
    List<ReferenceGenomeImpl> genes = baseTestBulkCreateAsync(CONTROLLER_BASE, entityClass, makeCreateDtos());
    assertEquals("newOne", genes.get(0).getAlias());
    assertEquals(1L, genes.get(0).getDefaultScientificName().getId());

    assertEquals("newTwo", genes.get(1).getAlias());
    assertEquals(2L, genes.get(1).getDefaultScientificName().getId());
  }

  @Test
  public void testCreateFail() throws Exception {
    testBulkCreateAsyncUnauthorized(CONTROLLER_BASE, entityClass, makeCreateDtos());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkUpdateAsync() throws Exception {
    // only admin can update
    ReferenceGenomeDto geneOne = Dtos.asDto(currentSession().get(entityClass, 1));
    ReferenceGenomeDto geneTwo = Dtos.asDto(currentSession().get(entityClass, 2));
    geneOne.setAlias("one");
    geneTwo.setAlias("two");

    List<ReferenceGenomeImpl> genes =
        (List<ReferenceGenomeImpl>) baseTestBulkUpdateAsync(CONTROLLER_BASE, entityClass,
            Arrays.asList(geneOne, geneTwo), ReferenceGenomeDto::getId);
    assertEquals(geneOne.getAlias(), genes.get(0).getAlias());
    assertEquals(geneTwo.getAlias(), genes.get(1).getAlias());

  }

  @Test
  public void testUpdateFail() throws Exception {
    ReferenceGenomeDto geneOne = Dtos.asDto(currentSession().get(entityClass, 1));
    ReferenceGenomeDto geneTwo = Dtos.asDto(currentSession().get(entityClass, 2));
    geneOne.setAlias("one");
    geneTwo.setAlias("two");
    testBulkUpdateAsyncUnauthorized(CONTROLLER_BASE, entityClass, Arrays.asList(geneOne, geneTwo));
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDelete() throws Exception {
    // only admin can delete
    testBulkDelete(entityClass, 4, CONTROLLER_BASE);
  }

  @Test
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(entityClass, 4, CONTROLLER_BASE);
  }


}
