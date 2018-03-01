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

public class ExperimentTest {
  private Experiment ae;

  @Before
  public void setUp() throws Exception {
    ae = new Experiment();
  }

  @Test
  public final void testGetKitsByKitType() {
    final Collection<Kit> expectedLibraryKits = getNMockKits(3, KitType.LIBRARY);
    final Collection<Kit> expectedSequencingKits = getNMockKits(2, KitType.SEQUENCING);
    final Collection<Kit> expectedClusteringKits = getNMockKits(5, KitType.CLUSTERING);
    final Collection<Kit> expectedMultiplexingKits = getNMockKits(6, KitType.MULTIPLEXING);

    final Collection<Kit> libraryKits = ae.getKitsByKitType(KitType.LIBRARY);
    final Collection<Kit> sequencingKits = ae.getKitsByKitType(KitType.SEQUENCING);
    final Collection<Kit> clusteringKits = ae.getKitsByKitType(KitType.CLUSTERING);
    final Collection<Kit> multiplexingKits = ae.getKitsByKitType(KitType.MULTIPLEXING);

    assertKitListEqual(expectedLibraryKits, libraryKits);
    assertKitListEqual(expectedSequencingKits, sequencingKits);
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

    ae.inheritPermissions(parent);
    assertNotNull(ae.getSecurityProfile());
  }

  // Utility methods.
  private static final void assertKitListEqual(Collection<Kit> expected, Collection<Kit> actual) {
    assertEquals(expected.size(), actual.size());
    int found = 0;
    for (final Kit eKit : expected) {
      for (final Kit aKit : actual) {
        if (eKit.getName().equals(aKit.getName())) {
          found++;
          break;
        }
      }
    }
    assertEquals(found, actual.size());
  }

  private final List<Kit> getNMockKits(int n, KitType kitType) {
    final List<Kit> rtn = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      final Kit k = Mockito.mock(Kit.class);
      setUpKit(k, kitType, "" + kitType + i);
      rtn.add(k);
    }
    return rtn;
  }

  private final void setUpKit(Kit kit, KitType type, String identifier) {
    final KitDescriptor kitDescriptor = new KitDescriptor();
    kitDescriptor.setKitType(type);
    when(kit.getKitDescriptor()).thenReturn(kitDescriptor);
    when(kit.getName()).thenReturn(identifier);
    ae.addKit(kit);
  }

}
