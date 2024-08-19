package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.eaglegenomics.simlims.core.Group;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.TransferType;

public class HibernateListTransferViewDaoIT extends AbstractDAOTest {

  private HibernateListTransferViewDao sut;

  @Before
  public void setup() {
    sut = new HibernateListTransferViewDao();
    sut.setEntityManager(getEntityManager());
  }

  @Test
  public void testSearchByPending() throws Exception {
    testSearch(PaginationFilter.pending());
  }

  @Test
  public void testSearchByRecipientGroups() throws Exception {
    @SuppressWarnings("unchecked")
    QueryBuilder<Group, Group> builder = new QueryBuilder<>(currentSession(), Group.class, Group.class);
    List<Group> groups = builder.getResultList();
    testSearch(PaginationFilter.recipientGroups(groups));
  }

  @Test
  public void testSearchByTransferType() throws Exception {
    testSearch(PaginationFilter.transferType(TransferType.RECEIPT));
    testSearch(PaginationFilter.transferType(TransferType.INTERNAL));
    testSearch(PaginationFilter.transferType(TransferType.DISTRIBUTION));
  }

  /**
   * Verifies Hibernate mappings by ensuring that no exception is thrown by a search
   * 
   * @param filter the search filter
   * @throws IOException
   */
  private void testSearch(PaginationFilter filter) throws IOException {
    // verify Hibernate mappings by ensuring that no exception is thrown
    assertNotNull(sut.list(err -> {
      throw new RuntimeException(err);
    }, 0, 10, true, "transferId", filter));
  }

}
