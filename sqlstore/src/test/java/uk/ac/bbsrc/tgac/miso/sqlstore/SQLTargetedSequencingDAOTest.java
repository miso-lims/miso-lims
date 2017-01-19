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
import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.store.KitDescriptorStore;

public class SQLTargetedSequencingDAOTest extends AbstractDAOTest {

  @Autowired
  private JdbcTemplate template;

  @Autowired
  private DataObjectFactory dataObjectFactory;

  @Mock
  private Authentication authenticationMock;

  @Mock
  private SQLSecurityDAO securityDAO;

  @Mock
  private KitDescriptorStore kitDao;

  @InjectMocks
  private SQLTargetedSequencingDAO dao;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    dao.setJdbcTemplate(template);
    dao.setDataObjectFactory(dataObjectFactory);
  }

  @Test
  public void testListAllCountIsAtLeastThree() throws Exception {
    Collection<TargetedSequencing> targetedSequencingList = dao.listAll();
    assertThat("count of all targeted sequencing items", targetedSequencingList.size(), is(greaterThanOrEqualTo(3)));
  }

  @Test
  public void testGetByIdOneAliasIsHalo() throws Exception {
    Long idOne = 1L;
    TargetedSequencing actual = dao.get(idOne);
    assertThat("alias for targeted sequencing with id 1", actual.getAlias(), is("HALO_IBP"));
  }

  @Test
  public void testCountIsAtLeastThree() throws Exception {
    int actual = dao.count();
    assertThat("count of targeted sequencing entries", actual, is(greaterThanOrEqualTo(3)));
  }
}
