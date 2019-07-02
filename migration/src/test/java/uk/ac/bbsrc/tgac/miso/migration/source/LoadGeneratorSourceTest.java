package uk.ac.bbsrc.tgac.miso.migration.source;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.migration.MigrationData;
import uk.ac.bbsrc.tgac.miso.migration.MigrationProperties;

public class LoadGeneratorSourceTest {

  private static final String testProperties = "target/test-classes/LoadGeneratorTest.properties";
  
  private LoadGeneratorSource sut;
  
  @Before
  public void setUp() throws FileNotFoundException, IOException {
    sut = new LoadGeneratorSource(new MigrationProperties(testProperties));
  }
  
  @Test
  public void testGetMigrationData() {
    MigrationData data = sut.getMigrationData();
    assertNotNull(data.getProjects());
    assertEquals(5, data.getProjects().size());

    assertNotNull(data.getSamples());
    assertEquals(100, data.getSamples().size());

    assertNotNull(data.getLibraries());
    assertEquals(100, data.getLibraries().size());

    assertNotNull(data.getLibraryAliquots());
    assertEquals(100, data.getLibraryAliquots().size());

    assertNotNull(data.getPools());
    assertEquals(100, data.getPools().size());
    Pool p = data.getPools().iterator().next();
    assertNotNull(p.getPoolContents());
    assertEquals(5, p.getPoolContents().size());

    assertNotNull(data.getRuns());
    assertEquals(100, data.getRuns().size());
    Run r = data.getRuns().iterator().next();
    assertNotNull(r.getSequencerPartitionContainers());
    assertEquals(1, r.getSequencerPartitionContainers().size());
    SequencerPartitionContainer spc = r.getSequencerPartitionContainers().get(0);
    assertNotNull(spc.getPartitions());
    assertEquals(8, spc.getPartitions().size());
  }

}
