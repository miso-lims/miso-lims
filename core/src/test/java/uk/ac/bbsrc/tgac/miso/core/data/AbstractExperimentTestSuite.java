package uk.ac.bbsrc.tgac.miso.core.data;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

public class AbstractExperimentTestSuite {
  private AbstractExperiment ae;

  @Before
  public void setUp() throws Exception {
    ae = new AbstractExperiment() {

      @Override
      public void buildSubmission() {
        // Not implemented.
      }
    };
  }

  @Test
  public final void testGetKitsByKitType() {
    final Collection<KitComponent> expectedLibraryKits = getNMockKits(3, KitType.LIBRARY);
    final Collection<KitComponent> expectedSequencingKits = getNMockKits(2, KitType.SEQUENCING);
    final Collection<KitComponent> expectedEmpcrKits = getNMockKits(4, KitType.EMPCR);
    final Collection<KitComponent> expectedClusteringKits = getNMockKits(5, KitType.CLUSTERING);
    final Collection<KitComponent> expectedMultiplexingKits = getNMockKits(6, KitType.MULTIPLEXING);

    final Collection<KitComponent> libraryKits = ae.getKitsByKitType(KitType.LIBRARY);
    final Collection<KitComponent> sequencingKits = ae.getKitsByKitType(KitType.SEQUENCING);
    final Collection<KitComponent> empcrKits = ae.getKitsByKitType(KitType.EMPCR);
    final Collection<KitComponent> clusteringKits = ae.getKitsByKitType(KitType.CLUSTERING);
    final Collection<KitComponent> multiplexingKits = ae.getKitsByKitType(KitType.MULTIPLEXING);

    assertKitListEqual(expectedLibraryKits, libraryKits);
    assertKitListEqual(expectedSequencingKits, sequencingKits);
    assertKitListEqual(expectedEmpcrKits, empcrKits);
    assertKitListEqual(expectedClusteringKits, clusteringKits);
    assertKitListEqual(expectedMultiplexingKits, multiplexingKits);
  }

  @Test
  public final void testInheritPermissions() {
    final SecurableByProfile parent = Mockito.mock(SecurableByProfile.class);
    final SecurityProfile mockSecurityProfile = Mockito.mock(SecurityProfile.class);
    final User mockUser = Mockito.mock(User.class);
    when(parent.getSecurityProfile()).thenReturn(mockSecurityProfile);
    when(mockSecurityProfile.getOwner()).thenReturn(mockUser);

    assertNull(ae.getSecurityProfile());
    ae.inheritPermissions(parent);
    assertNotNull(ae.getSecurityProfile());
  }

  @Test(expected = SecurityException.class)
  public void testInheritPermissionsException() {
    final SecurableByProfile parent = Mockito.mock(SecurableByProfile.class);
    final SecurityProfile mockSecurityProfile = Mockito.mock(SecurityProfile.class);
    when(parent.getSecurityProfile()).thenReturn(mockSecurityProfile);
    ae.inheritPermissions(parent);
  }

  // Utility methods.
  private static final void assertKitListEqual(Collection<KitComponent> expected, Collection<KitComponent> actual) {
    assertEquals(expected.size(), actual.size());
    int found = 0;
    for (final KitComponent eKit : expected) {
      for (final KitComponent aKit : actual) {
        if (eKit.getName().equals(aKit.getName())) {
          found++;
          break;
        }
      }
    }
    assertEquals(found, actual.size());
  }

  private final List<KitComponent> getNMockKits(int n, KitType kitType) {
    final List<KitComponent> rtn = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      final KitComponent k = Mockito.mock(KitComponent.class);
      setUpKit(k, kitType, "" + kitType + i);
      rtn.add(k);
    }
    return rtn;
  }

  private final void setUpKit(KitComponent kit, KitType type, String identifier) {
    final KitDescriptor kitDescriptor = new KitDescriptor();
    final KitComponentDescriptor kitComponentDescriptor = Mockito.mock(KitComponentDescriptor.class);
    kitDescriptor.setKitType(type);

    when(kit.getKitComponentDescriptor()).thenReturn(kitComponentDescriptor);
    when(kitComponentDescriptor.getKitDescriptor()).thenReturn(kitDescriptor);
    when(kit.getName()).thenReturn(identifier);
    ae.addKitComponent(kit);
  }

}
