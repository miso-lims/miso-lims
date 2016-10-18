package uk.ac.bbsrc.tgac.miso.core.data;

import static org.mockito.Mockito.mock;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.impl.IdentityImpl.IdentityBuilder;

public class IdentityBuilderTest {

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

  @Test(expected = IllegalArgumentException.class)
  public void test_user_must_be_provided() throws Exception {
    new IdentityBuilder().build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void test_description_must_be_provided() throws Exception {
    Project mockedProject = mock(Project.class);
    new IdentityBuilder().project(mockedProject).build();
  }

}
