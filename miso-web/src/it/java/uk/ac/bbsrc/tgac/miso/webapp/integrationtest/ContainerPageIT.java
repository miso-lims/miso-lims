package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.assertEquals;
import static uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.FormPageTestUtils.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.AbstractListPage.ButtonText;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.AbstractListPage.ListTarget;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.ContainerPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.ContainerPage.Field;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.ListTabbedPage;

public class ContainerPageIT extends AbstractIT {

  private static final Map<String, Long> idForPlatform;
  static {
    idForPlatform = new HashMap<>();
    idForPlatform.put("Illumina HiSeq 2500", 1L);
    idForPlatform.put("Illumina MiSeq", 2L);
    idForPlatform.put("PacBio RS II", 3L);
  }

  @Before
  public void setup() {
    loginAdmin();
  }

  @Test
  public void testCreateContainerDialog() {
    // goal: ensure clicking to create new MiSeq container goes to the page with the
    // correct platform and number of partitions
    ListTabbedPage listContainers = ListTabbedPage.getTabbedListPage(getDriver(), getBaseUrl(), ListTarget.CONTAINERS);
    String partitionName = PlatformType.get("Illumina").getPartitionName();
    String containerName = PlatformType.get("Illumina").getContainerName();
    String miSeq = "Illumina MiSeq";
    String platformId = idForPlatform.get(miSeq).toString();
    Integer numPartitions = 1;
    String partitionText = numPartitions + " " + partitionName + (numPartitions == 1 ? "" : "s");
    String newUrl = listContainers
        .clickButtonAndGetUrl(ButtonText.ADD + " " + containerName, Lists.newArrayList(miSeq, partitionText));

    String foundPartitions = newUrl.split("count=")[1];
    assertEquals("same number of partitions", numPartitions.toString(), foundPartitions);
    String foundSequencerId = newUrl.split("new/")[1].split("\\?count=")[0];
    assertEquals("same sequencer ID", platformId, foundSequencerId);
  }

  @Test
  public void testSaveNewContainer() throws Exception {
    // goal: create new MiSeq container
    String platform = "Illumina";
    String model = "Illumina MiSeq";
    ContainerPage page1 = ContainerPage.getForCreate(getDriver(), getBaseUrl(), idForPlatform.get(model), 1);
    assertEquals("Illumina MiSeq", page1.getField(Field.MODEL));

    // default values
    Map<ContainerPage.Field, String> fields = Maps.newLinkedHashMap();
    fields.put(Field.ID, "Unsaved");
    fields.put(Field.SERIAL_NUMBER, null);
    fields.put(Field.PLATFORM, platform);
    fields.put(Field.MODEL, model);
    fields.put(Field.CLUSTERING_KIT, "(None)");
    fields.put(Field.MULTIPLEXING_KIT, "(None)");
    assertFieldValues("default values", fields, page1);

    // enter container info
    Map<ContainerPage.Field, String> changes = Maps.newLinkedHashMap();
    changes.put(Field.SERIAL_NUMBER, "CEREAL");
    changes.put(Field.CLUSTERING_KIT, "Test Kit Three");
    changes.put(Field.MULTIPLEXING_KIT, "Test Kit Four");
    page1.setFields(changes);
    fields.putAll(changes);
    assertFieldValues("pre-save", fields, page1);

    ContainerPage page2 = page1.save();
    fields.remove(Field.ID);
    assertFieldValues("post-save", fields, page2);
    long savedId = Long.parseLong(page2.getField(Field.ID));
    SequencerPartitionContainer savedContainer = (SequencerPartitionContainer) getSession().get(SequencerPartitionContainerImpl.class,
        savedId);
    assertContainerAttributes(fields, savedContainer);
  }

  @Test
  public void testChangeValues() throws Exception {
    // goal: change all changeable values
    ContainerPage page1 = ContainerPage.getForEdit(getDriver(), getBaseUrl(), 6001L);

    // initial values
    Map<ContainerPage.Field, String> fields = Maps.newLinkedHashMap();
    fields.put(Field.ID, "6001");
    fields.put(Field.SERIAL_NUMBER, "CHANGEABLE");
    fields.put(Field.PLATFORM, "Illumina");
    fields.put(Field.MODEL, "Illumina MiSeq");
    fields.put(Field.CLUSTERING_KIT, "(None)");
    fields.put(Field.MULTIPLEXING_KIT, "(None)");
    assertFieldValues("initial values", fields, page1);

    Map<ContainerPage.Field, String> changes = Maps.newLinkedHashMap();
    changes.put(Field.SERIAL_NUMBER, "CHANGED");
    changes.put(Field.CLUSTERING_KIT, "Test Kit Three");
    changes.put(Field.MULTIPLEXING_KIT, "Test Kit Four");
    page1.setFields(changes);

    fields.putAll(changes);
    assertFieldValues("changes pre-save", fields, page1);

    ContainerPage page2 = page1.save();
    assertFieldValues("post-save", fields, page2);
    SequencerPartitionContainer savedContainer = (SequencerPartitionContainer) getSession().get(SequencerPartitionContainerImpl.class,
        6001L);
    assertContainerAttributes(fields, savedContainer);
  }

  private void assertContainerAttributes(Map<ContainerPage.Field, String> expectedValues, SequencerPartitionContainer container) {
    assertAttribute(Field.ID, expectedValues, Long.toString(container.getId()));
    assertAttribute(Field.SERIAL_NUMBER, expectedValues, container.getIdentificationBarcode());
    assertAttribute(Field.PLATFORM, expectedValues, container.getPlatform().getPlatformType().getKey());
    assertAttribute(Field.MODEL, expectedValues, container.getPlatform().getInstrumentModel());
    assertAttribute(Field.CLUSTERING_KIT, expectedValues, nullOrGet(container.getClusteringKit(), KitDescriptor::getName));
    assertAttribute(Field.MULTIPLEXING_KIT, expectedValues, nullOrGet(container.getMultiplexingKit(), KitDescriptor::getName));
  }
}
