package uk.ac.bbsrc.tgac.miso.service.impl;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;

public class DefaultLibraryAliquotServiceTest {
  private DefaultLibraryAliquotService service;
  private DetailedLibraryAliquot aliquot;
  private final Long id = 42L;
  private final String alias = "test alias";
  private final String description = "test description";
  private final boolean archived = true;

  @Before
  public void setUp() {
    service = new DefaultLibraryAliquotService();
    aliquot = new DetailedLibraryAliquot();
    KitDescriptor libraryKd = new KitDescriptor();
    aliquot.setKitDescriptor(libraryKd);
  }

  @Test
  public void testEmptyTargetedSequencingCompatibility() {
    TargetedSequencing ts = new TargetedSequencing();
    addTargetedSequencing(ts, aliquot);

    assertTrue(service.isTargetedSequencingCompatible(ts, aliquot));
  }

  @Test
  public void testDistinctEmptyTargetedSequencingCompatibility() {
    TargetedSequencing ts1 = new TargetedSequencing();
    TargetedSequencing ts2 = new TargetedSequencing();
    addTargetedSequencing(ts2, aliquot);

    assertTrue(service.isTargetedSequencingCompatible(ts1, aliquot));
  }

  @Test
  public void testDistinctEqualTargetedSequencingCompatibility() {
    TargetedSequencing ts1 = defaultTargetedSequencing();
    TargetedSequencing ts2 = defaultTargetedSequencing();
    addTargetedSequencing(ts2, aliquot);

    assertTrue(service.isTargetedSequencingCompatible(ts1, aliquot));
  }

  @Test
  public void testUnequalTargetedSequencingIncompatibility() {
    TargetedSequencing ts1 = defaultTargetedSequencing();

    TargetedSequencing ts2 = defaultTargetedSequencing();
    // Now distinct from ts1
    ts2.setId(ts1.getId() + 1);
    addTargetedSequencing(ts2, aliquot);

    assertFalse(service.isTargetedSequencingCompatible(ts1, aliquot));
  }


  private TargetedSequencing defaultTargetedSequencing() {
    TargetedSequencing ts = new TargetedSequencing();

    ts.setId(id);
    ts.setAlias(alias);
    ts.setDescription(description);
    ts.setArchived(archived);

    return ts;
  }

  private void addTargetedSequencing(TargetedSequencing ts, LibraryAliquot aliquot) {
    aliquot.getKitDescriptor().addTargetedSequencing(ts);
  }
}
