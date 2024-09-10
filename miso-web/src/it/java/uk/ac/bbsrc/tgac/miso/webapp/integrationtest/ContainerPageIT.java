package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.FormPageTestUtils.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
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
    login();
  }

  @Test
  public void testCreateContainerDialog() {
    // goal: ensure clicking to create new MiSeq container goes to the page with the
    // correct container model
    ListTabbedPage listContainers = ListTabbedPage.getTabbedListPage(getDriver(), getBaseUrl(), ListTarget.CONTAINERS);
    String containerName = PlatformType.get("Illumina").getContainerName();
    InstrumentModel miseq = (InstrumentModel) getSession().get(InstrumentModel.class, 2L);
    SequencingContainerModel model = (SequencingContainerModel) getSession().get(SequencingContainerModel.class, 3L);
    String newUrl = listContainers
        .clickButtonAndGetUrl(ButtonText.ADD + " " + containerName,
            Lists.newArrayList(miseq.getAlias(), model.getAlias()));

    assertTrue(newUrl.matches(".*/container/new/" + model.getId()));
  }

  @Test
  public void testSaveNewContainer() throws Exception {
    // goal: create new MiSeq container
    String model = "Generic 4-Lane Illumina Flow Cell";
    ContainerPage page1 = ContainerPage.getForCreate(getDriver(), getBaseUrl(), 1L);
    assertEquals(model, page1.getField(Field.MODEL));

    // default values
    Map<ContainerPage.Field, String> fields = Maps.newLinkedHashMap();
    fields.put(Field.ID, "Unsaved");
    fields.put(Field.SERIAL_NUMBER, null);
    fields.put(Field.MODEL, model);
    fields.put(Field.CLUSTERING_KIT, "None");
    fields.put(Field.MULTIPLEXING_KIT, "None");
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
    SequencerPartitionContainer savedContainer =
        (SequencerPartitionContainer) getSession().get(SequencerPartitionContainerImpl.class,
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
    fields.put(Field.MODEL, "Generic 4-Lane Illumina Flow Cell");
    fields.put(Field.CLUSTERING_KIT, "None");
    fields.put(Field.MULTIPLEXING_KIT, "None");
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
    SequencerPartitionContainer savedContainer =
        (SequencerPartitionContainer) getSession().get(SequencerPartitionContainerImpl.class,
            6001L);
    assertContainerAttributes(fields, savedContainer);
  }

  private void assertContainerAttributes(Map<ContainerPage.Field, String> expectedValues,
      SequencerPartitionContainer container) {
    assertAttribute(Field.ID, expectedValues, Long.toString(container.getId()));
    assertAttribute(Field.SERIAL_NUMBER, expectedValues, container.getIdentificationBarcode());
    assertAttribute(Field.MODEL, expectedValues, container.getModel().getAlias());
    assertAttribute(Field.CLUSTERING_KIT, expectedValues,
        nullOrGet(container.getClusteringKit(), KitDescriptor::getName));
    assertAttribute(Field.MULTIPLEXING_KIT, expectedValues,
        nullOrGet(container.getMultiplexingKit(), KitDescriptor::getName));
  }
}
