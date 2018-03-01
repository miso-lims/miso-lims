/**
 *
 */
package uk.ac.bbsrc.tgac.miso.core.data;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

/**
 * @author saltc
 * 
 */
public class LibraryDilutionTest {

  /**
   * @throws java.lang.Exception
   */
  LibraryDilution ld;

  @Before
  public void setUp() throws Exception {
    ld = new LibraryDilution() {
      private static final long serialVersionUID = 1L;

      @Override
      public String getUnits() {
        return null;
      }

      @Override
      public Library getLibrary() {
        return null;
      }
    };
    ld.setName("MyLibraryDilution");
  }

  @Test
  public final void testInheritPermissions() {
    final SecurableByProfile parent = Mockito.mock(SecurableByProfile.class);
    final SecurityProfile mockSecurityProfile = Mockito.mock(SecurityProfile.class);
    final User mockUser = Mockito.mock(User.class);
    when(parent.getSecurityProfile()).thenReturn(mockSecurityProfile);
    when(mockSecurityProfile.getOwner()).thenReturn(mockUser);

    assertNotEquals(mockSecurityProfile, ld.getSecurityProfile());
    ld.inheritPermissions(parent);
    assertEquals(mockSecurityProfile, ld.getSecurityProfile());
  }

}
