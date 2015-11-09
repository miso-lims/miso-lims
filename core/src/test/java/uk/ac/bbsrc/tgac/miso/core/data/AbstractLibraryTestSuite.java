package uk.ac.bbsrc.tgac.miso.core.data;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

public class AbstractLibraryTestSuite {

   AbstractLibrary al;

   @Before
   public void setUp() throws Exception {
      al = new AbstractLibrary() {
      };
   }

   @Test
   public final void testInheritPermissions() {
      final SecurableByProfile parent = Mockito.mock(SecurableByProfile.class);
      final SecurityProfile mockSecurityProfile = Mockito.mock(SecurityProfile.class);
      final User mockUser = Mockito.mock(User.class);
      when(parent.getSecurityProfile()).thenReturn(mockSecurityProfile);
      when(mockSecurityProfile.getOwner()).thenReturn(mockUser);

      assertNull(al.getSecurityProfile());
      al.inheritPermissions(parent);
      assertNotNull(al.getSecurityProfile());
   }

   @Test(expected = SecurityException.class)
   public void testInheritPermissionsException() {
      final SecurableByProfile parent = Mockito.mock(SecurableByProfile.class);
      final SecurityProfile mockSecurityProfile = Mockito.mock(SecurityProfile.class);
      when(parent.getSecurityProfile()).thenReturn(mockSecurityProfile);
      al.inheritPermissions(parent);
   }

//   TODO - cs.  This exception is never thrown!  Remove it or add exception.
//   @Test(expected = MalformedLibraryException.class)
//   public final void testaddQc() {
//
//   }
}
