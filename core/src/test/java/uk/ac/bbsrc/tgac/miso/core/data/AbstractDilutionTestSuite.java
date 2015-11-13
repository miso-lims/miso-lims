/**
 *
 */
package uk.ac.bbsrc.tgac.miso.core.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collection;

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

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.data.AbstractDilution#getInternalPoolableElements()}.
   */
  @Test
  public final void testGetInternalPoolableElements() {
    final Collection<Dilution> dil = ad.getInternalPoolableElements();
    assertEquals(1, dil.size());
    assertTrue(dil.contains(ad));
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
