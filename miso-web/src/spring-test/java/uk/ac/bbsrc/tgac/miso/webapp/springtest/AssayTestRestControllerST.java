package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.core.data.impl.AssayTest;
import uk.ac.bbsrc.tgac.miso.dto.AssayTestDto;
import uk.ac.bbsrc.tgac.miso.core.data.impl.AssayTest.PermittedSamples;
import org.springframework.security.test.context.support.WithMockUser;
import static org.junit.Assert.*;
import java.util.List;
import java.util.ArrayList;


public class AssayTestRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/assaytests";
  private static final Class<AssayTest> entityClass = AssayTest.class;

  private List<AssayTestDto> makeCreateDtos() {
    AssayTest atest1 = new AssayTest();
    atest1.setLibraryQualificationMethod(AssayTest.LibraryQualificationMethod.LOW_DEPTH_SEQUENCING);
    atest1.setPermittedSamples(AssayTest.PermittedSamples.ALL);

    AssayTestDto test1 = AssayTestDto.from(atest1);
    test1.setAlias("first");
    test1.setTissueTypeId(1L);
    test1.setExtractionClassId(11L);
    test1.setLibraryDesignCodeId(2L);


    AssayTest atest2 = new AssayTest();
    atest2.setLibraryQualificationMethod(AssayTest.LibraryQualificationMethod.NONE);
    atest2.setPermittedSamples(AssayTest.PermittedSamples.REQUISITIONED);

    AssayTestDto test2 = AssayTestDto.from(atest2);
    test2.setAlias("second");
    test2.setTissueTypeId(2L);
    test2.setExtractionClassId(12L);
    test2.setLibraryDesignCodeId(3L);


    List<AssayTestDto> dtos = new ArrayList<AssayTestDto>();
    dtos.add(test1);
    dtos.add(test2);

    return dtos;
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkCreateAsync() throws Exception {
    List<AssayTest> tests = baseTestBulkCreateAsync(CONTROLLER_BASE, entityClass, makeCreateDtos());

    assertEquals("first", tests.get(0).getAlias());
    assertEquals(1L, tests.get(0).getTissueType().getId());
    assertEquals(11L, tests.get(0).getExtractionClass().getId());
    assertEquals(2L, tests.get(0).getLibraryDesignCode().getId());
    assertEquals(AssayTest.LibraryQualificationMethod.LOW_DEPTH_SEQUENCING,
        tests.get(0).getLibraryQualificationMethod());
    assertEquals(AssayTest.PermittedSamples.ALL, tests.get(0).getPermittedSamples());

    assertEquals("second", tests.get(1).getAlias());
    assertEquals(2L, tests.get(1).getTissueType().getId());
    assertEquals(12L, tests.get(1).getExtractionClass().getId());
    assertEquals(3L, tests.get(1).getLibraryDesignCode().getId());
    assertEquals(AssayTest.LibraryQualificationMethod.NONE,
        tests.get(1).getLibraryQualificationMethod());
    assertEquals(AssayTest.PermittedSamples.REQUISITIONED, tests.get(1).getPermittedSamples());
  }

  @Test
  public void testBulkCreateFail() throws Exception {
    testBulkCreateAsyncUnauthorized(CONTROLLER_BASE, entityClass, makeCreateDtos());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkUpdateAsync() throws Exception {
    // admin perms needed to update
    AssayTestDto one = AssayTestDto.from(currentSession().get(entityClass, 1));
    AssayTestDto two = AssayTestDto.from(currentSession().get(entityClass, 2));

    one.setAlias("one");
    two.setAlias("two");

    List<AssayTestDto> dtos = new ArrayList<AssayTestDto>();
    dtos.add(one);
    dtos.add(two);

    List<AssayTest> assayTests =
        (List<AssayTest>) baseTestBulkUpdateAsync(CONTROLLER_BASE, entityClass, dtos, AssayTestDto::getId);

    assertEquals("one", assayTests.get(0).getAlias());
    assertEquals("two", assayTests.get(1).getAlias());
  }

  @Test
  public void testBulkUpdateAsyncFail() throws Exception {
    AssayTestDto one = AssayTestDto.from(currentSession().get(entityClass, 1));
    AssayTestDto two = AssayTestDto.from(currentSession().get(entityClass, 2));

    one.setAlias("one");
    two.setAlias("two");

    List<AssayTestDto> dtos = new ArrayList<AssayTestDto>();
    dtos.add(one);
    dtos.add(two);

    testBulkUpdateAsyncUnauthorized(CONTROLLER_BASE, entityClass, dtos);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDeleteAssayTest() throws Exception {
    testBulkDelete(entityClass, 4, CONTROLLER_BASE);
  }


  @Test
  @WithMockUser(username = "hhenderson", roles = {"INTERNAL"})
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(entityClass, 4, CONTROLLER_BASE);
  }
}
