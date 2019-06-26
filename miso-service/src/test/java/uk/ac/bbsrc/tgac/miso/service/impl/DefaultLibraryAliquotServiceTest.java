package uk.ac.bbsrc.tgac.miso.service.impl;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;

public class DefaultLibraryAliquotServiceTest {
  private DefaultLibraryAliquotService service;
  private DetailedLibrary library;
  private final Long id = 42L;
  private final String alias = "test alias";
  private final String description = "test description";
  private final boolean archived = true;

  @Before
  public void setUp() {
    service = new DefaultLibraryAliquotService();
    library = new DetailedLibraryImpl();
    KitDescriptor libraryKd = new KitDescriptor();
    library.setKitDescriptor(libraryKd);
  }

  @Test
  public void testEmptyTargetedSequencingCompatibility() {
    TargetedSequencing ts = new TargetedSequencing();
    addTargetedSequencing(ts, library);

    assertTrue(service.isTargetedSequencingCompatible(ts, library));
  }

  @Test
  public void testDistinctEmptyTargetedSequencingCompatibility() {
    TargetedSequencing ts1 = new TargetedSequencing();
    TargetedSequencing ts2 = new TargetedSequencing();
    addTargetedSequencing(ts2, library);

    assertTrue(service.isTargetedSequencingCompatible(ts1, library));
  }

  @Test
  public void testDistinctEqualTargetedSequencingCompatibility() {
    TargetedSequencing ts1 = defaultTargetedSequencing();
    TargetedSequencing ts2 = defaultTargetedSequencing();
    addTargetedSequencing(ts2, library);

    assertTrue(service.isTargetedSequencingCompatible(ts1, library));
  }

  @Test
  public void testUnequalTargetedSequencingIncompatibility() {
    TargetedSequencing ts1 = defaultTargetedSequencing();

    TargetedSequencing ts2 = defaultTargetedSequencing();
    // Now distinct from ts1
    ts2.setId(ts1.getId() + 1);
    addTargetedSequencing(ts2, library);

    assertFalse(service.isTargetedSequencingCompatible(ts1, library));
  }


  private TargetedSequencing defaultTargetedSequencing() {
    TargetedSequencing ts = new TargetedSequencing();

    ts.setId(id);
    ts.setAlias(alias);
    ts.setDescription(description);
    ts.setArchived(archived);

    return ts;
  }

  private void addTargetedSequencing(TargetedSequencing ts, Library library) {
    library.getKitDescriptor().addTargetedSequencing(ts);
  }
}
