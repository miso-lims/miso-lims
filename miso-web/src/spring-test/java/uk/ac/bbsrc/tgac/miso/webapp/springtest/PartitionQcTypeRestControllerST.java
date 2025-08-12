package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

import org.springframework.security.test.context.support.WithMockUser;
import static org.junit.Assert.*;
import java.util.List;
import java.util.ArrayList;

import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.core.data.PartitionQCType;
import uk.ac.bbsrc.tgac.miso.dto.PartitionQCTypeDto;

public class PartitionQcTypeRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/partitionqctypes";
  private static final Class<PartitionQCType> entityClass = PartitionQCType.class;

  private List<PartitionQCTypeDto> makeCreateDtos() {

    List<PartitionQCTypeDto> dtos = new ArrayList<PartitionQCTypeDto>();
    PartitionQCTypeDto one = new PartitionQCTypeDto();
    one.setAnalysisSkipped(false);
    one.setNoteRequired(false);
    one.setOrderFulfilled(false);
    one.setDescription("one");


    PartitionQCTypeDto two = new PartitionQCTypeDto();
    two.setDescription("two");
    two.setNoteRequired(false);
    two.setOrderFulfilled(true);
    two.setAnalysisSkipped(true);

    dtos.add(one);
    dtos.add(two);

    return dtos;
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkCreateAsync() throws Exception {
    List<PartitionQCType> qcTypes = baseTestBulkCreateAsync(CONTROLLER_BASE, entityClass, makeCreateDtos());
    assertEquals("one", qcTypes.get(0).getDescription());
    assertEquals(false, qcTypes.get(0).isNoteRequired());
    assertEquals(false, qcTypes.get(0).isOrderFulfilled());
    assertEquals(false, qcTypes.get(0).isAnalysisSkipped());

    assertEquals("two", qcTypes.get(1).getDescription());
    assertEquals(false, qcTypes.get(1).isNoteRequired());
    assertEquals(true, qcTypes.get(1).isOrderFulfilled());
    assertEquals(true, qcTypes.get(1).isAnalysisSkipped());
  }

  @Test
  public void testBulkCreateFail() throws Exception {
    // PartitionQcType creation is for admin only, so this test is expecting failure due to
    // insufficent permission
    testBulkCreateAsyncUnauthorized(CONTROLLER_BASE, entityClass, makeCreateDtos());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkUpdateAsync() throws Exception {
    // the admin user made these PartitionQCTypes so only admin can update them
    PartitionQCTypeDto pqt1 = Dtos.asDto(currentSession().get(PartitionQCType.class, 1));
    PartitionQCTypeDto pqt2 = Dtos.asDto(currentSession().get(PartitionQCType.class, 2));

    pqt1.setDescription("pqt1");
    pqt2.setDescription("pqt2");

    List<PartitionQCTypeDto> dtos = new ArrayList<PartitionQCTypeDto>();
    dtos.add(pqt1);
    dtos.add(pqt2);

    List<PartitionQCType> partitionQcTypes = baseTestBulkUpdateAsync(CONTROLLER_BASE, entityClass, dtos,
        PartitionQCTypeDto::getId);
    assertEquals("pqt1", partitionQcTypes.get(0).getDescription());
    assertEquals("pqt2", partitionQcTypes.get(1).getDescription());
  }

  @Test
  public void testBulkUpdateAsyncFail() throws Exception {
    // the admin user made these PartitionQcTypes so only admin can update them
    PartitionQCTypeDto pqt1 = Dtos.asDto(currentSession().get(PartitionQCType.class, 1));
    PartitionQCTypeDto pqt2 = Dtos.asDto(currentSession().get(PartitionQCType.class, 2));

    pqt1.setDescription("pqt1");
    pqt2.setDescription("pqt2");

    List<PartitionQCTypeDto> dtos = new ArrayList<PartitionQCTypeDto>();
    dtos.add(pqt1);
    dtos.add(pqt2);

    testBulkUpdateAsyncUnauthorized(CONTROLLER_BASE, entityClass, dtos);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDeletePartitionQCType() throws Exception {
    testBulkDelete(entityClass, 4, CONTROLLER_BASE);
  }

  @Test
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(entityClass, 4, CONTROLLER_BASE);
  }
}
