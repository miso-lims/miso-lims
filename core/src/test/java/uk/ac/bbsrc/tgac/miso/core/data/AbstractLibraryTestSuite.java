package uk.ac.bbsrc.tgac.miso.core.data;

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.codehaus.jackson.map.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAdditionalInfoImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueOriginImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueTypeImpl;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

public class AbstractLibraryTestSuite {

  AbstractLibrary al;

  protected ObjectMapper mapper;

  @Before
  public void setUp() throws Exception {
    al = new AbstractLibrary() {
    };
    mapper = new ObjectMapper();
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

  @Test
  public final void testLibrarySerialization()  throws Exception {
    Sample sample = new SampleImpl();
    Library library = new LibraryImpl();
    LibraryAdditionalInfo lai = new LibraryAdditionalInfoImpl();
    library.setSample(sample);
    lai.setTissueOrigin(new TissueOriginImpl());
    lai.setTissueType(new TissueTypeImpl());
    lai.setLibrary(library);
    library.setLibraryAdditionalInfo(lai);
    library.setAlias("TestLib");
    String mappedLib = mapper.writer().writeValueAsString(library);
    assertThat(mappedLib, containsString("TestLib"));
  }
}
