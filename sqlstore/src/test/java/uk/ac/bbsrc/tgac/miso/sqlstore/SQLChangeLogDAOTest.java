package uk.ac.bbsrc.tgac.miso.sqlstore;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;

import org.joda.time.format.ISODateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSecurityDao;

public class SQLChangeLogDAOTest extends AbstractDAOTest {

  @Autowired
  private JdbcTemplate jdbcTemplate;
  @Mock
  private HibernateSecurityDao securityDAO;

  private SQLChangeLogDAO sut;

  User user = new UserImpl();

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);
    sut = new SQLChangeLogDAO();
    sut.setJdbcTemplate(jdbcTemplate);
    sut.setSecurityDAO(securityDAO);
    user.setUserId(1L);
    when(securityDAO.getUserById(anyLong())).thenReturn(user);
  }

  @Test
  public void testListAll() throws Exception {
    List<ChangeLog> list = sut.listAll("sample");
    assertNotNull(list);
    assertEquals(18, list.size());
  }

  @Test
  public void testListAllById() throws Exception {
    List<ChangeLog> list = sut.listAllById("sample", 1L);
    assertNotNull(list);
    assertEquals(2, list.size());
  }

  @Test
  public void testDeleteAllById() throws Exception {
    assertEquals(2, sut.listAllById("sample", 1L).size());
    sut.deleteAllById("sample", 1L);
    List<ChangeLog> list = sut.listAllById("sample", 1L);
    assertNotNull(list);
    assertEquals(0, list.size());
  }

  @Test
  public void testCreate() throws Exception {
    List<ChangeLog> list = sut.listAllById("sample", 1L);
    assertNotNull(list);
    assertEquals(2, list.size());

    ChangeLog cl = new ChangeLog();
    cl.setColumnsChanged("cols");
    cl.setSummary("things changed");
    cl.setUser(user);
    cl.setTime(new Date());

    sut.create("sample", 1L, cl);
    List<ChangeLog> newList = sut.listAllById("sample", 1L);
    assertNotNull(newList);
    assertEquals(3, newList.size());
  }

  @Test
  public void testMapping() throws Exception {
    // (7, 'qcPassed', 1, 'false -> true', '2016-07-07 13:31:01')
    List<ChangeLog> list = sut.listAllById("sample", 7L);
    assertNotNull(list);
    assertEquals(1, list.size());
    ChangeLog cl = list.get(0);
    assertEquals("qcPassed", cl.getColumnsChanged());
    assertEquals(Long.valueOf(1L), cl.getUser().getUserId());
    assertEquals("false -> true", cl.getSummary());
    Date date = new Date(ISODateTimeFormat.dateTimeParser().parseDateTime("2016-07-07T13:31:01").getMillis());
    assertEquals(date, cl.getTime());
  }

}
