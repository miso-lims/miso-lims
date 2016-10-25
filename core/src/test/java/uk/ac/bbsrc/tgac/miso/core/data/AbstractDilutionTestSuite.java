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

import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

/**
 * @author saltc
 * 
 */
public class AbstractDilutionTestSuite {

  /**
   * @throws java.lang.Exception
   */
  AbstractDilution ad;

  @Before
  public void setUp() throws Exception {
    ad = new AbstractDilution() {
      @Override
      public String getUnits() {
        return null;
      }

      @Override
      public Library getLibrary() {
        return null;
      }
    };
    ad.setName("MyAbstractDilution");
  }

  @Test
  public final void testInheritPermissions() {
    final SecurableByProfile parent = Mockito.mock(SecurableByProfile.class);
    final SecurityProfile mockSecurityProfile = Mockito.mock(SecurityProfile.class);
    final User mockUser = Mockito.mock(User.class);
    when(parent.getSecurityProfile()).thenReturn(mockSecurityProfile);
    when(mockSecurityProfile.getOwner()).thenReturn(mockUser);

    assertNull(ad.getSecurityProfile());
    ad.inheritPermissions(parent);
    assertNotNull(ad.getSecurityProfile());
  }

  @Test(expected = SecurityException.class)
  public void testInheritPermissionsException() {
    final SecurableByProfile parent = Mockito.mock(SecurableByProfile.class);
    final SecurityProfile mockSecurityProfile = Mockito.mock(SecurityProfile.class);
    when(parent.getSecurityProfile()).thenReturn(mockSecurityProfile);
    ad.inheritPermissions(parent);
  }

}
