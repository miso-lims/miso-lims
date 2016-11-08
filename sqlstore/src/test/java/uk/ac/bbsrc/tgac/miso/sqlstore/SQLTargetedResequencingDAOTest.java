package uk.ac.bbsrc.tgac.miso.sqlstore;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedResequencing;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.store.KitStore;

public class SQLTargetedResequencingDAOTest extends AbstractDAOTest {

  @Autowired
  private JdbcTemplate template;

  @Autowired
  private DataObjectFactory dataObjectFactory;

  @Mock
  private Authentication authenticationMock;

  @Mock
  private SQLSecurityDAO securityDAO;

  @Mock
  private KitStore kitDao;

  @InjectMocks
  private SQLTargetedResequencingDAO dao;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    dao.setJdbcTemplate(template);
    dao.setDataObjectFactory(dataObjectFactory);
  }

  @Test
  public void testListAllCountIsAtLeastThree() throws Exception {
    Collection<TargetedResequencing> targetedResequencingList = dao.listAll();
    assertThat("count of all targeted resequencing items", targetedResequencingList.size(), is(greaterThanOrEqualTo(3)));
  }

  @Test
  public void testGetByIdOneAliasIsHalo() throws Exception {
    Long idOne = 1L;
    TargetedResequencing actual = dao.get(idOne);
    assertThat("alias for targeted resequencing with id 1", actual.getAlias(), is("HALO_IBP"));
  }

  @Test
  public void testCountIsAtLeastThree() throws Exception {
    int actual = dao.count();
    assertThat("count of targeted resequencing entries", actual, is(greaterThanOrEqualTo(3)));
  }
}
