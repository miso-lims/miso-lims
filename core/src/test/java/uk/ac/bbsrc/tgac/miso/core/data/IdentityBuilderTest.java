package uk.ac.bbsrc.tgac.miso.core.data;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIdentityImpl.IdentityBuilder;

public class IdentityBuilderTest {

  @BeforeAll
  public static void setUpBeforeClass() throws Exception {}

  @AfterAll
  public static void tearDownAfterClass() throws Exception {}

  @BeforeEach
  public void setUp() throws Exception {}

  @AfterEach
  public void tearDown() throws Exception {}

  @Test
  public void test_user_must_be_provided() throws Exception {
    assertThrows(IllegalArgumentException.class, () -> new IdentityBuilder().build());
  }

  @Test
  public void test_description_must_be_provided() throws Exception {
    Project mockedProject = mock(Project.class);
    assertThrows(IllegalArgumentException.class, () -> new IdentityBuilder().project(mockedProject).build());
  }

}
