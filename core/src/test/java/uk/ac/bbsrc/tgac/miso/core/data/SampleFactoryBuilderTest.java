package uk.ac.bbsrc.tgac.miso.core.data;

import static org.mockito.Mockito.mock;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractSample.SampleFactoryBuilder;

public class SampleFactoryBuilderTest {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test(expected = NullPointerException.class)
  public void test_user_must_be_provided() throws Exception {
    new SampleFactoryBuilder().build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void test_description_must_be_provided() throws Exception {
    User mockedUser = mock(User.class);
    Project mockedProject = mock(Project.class);
    new SampleFactoryBuilder().user(mockedUser).project(mockedProject).build();
  }

}
