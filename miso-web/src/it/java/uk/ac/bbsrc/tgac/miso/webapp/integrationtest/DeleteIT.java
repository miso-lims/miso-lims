package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.eaglegenomics.simlims.core.Group;

import uk.ac.bbsrc.tgac.miso.core.data.Array;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayModel;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRun;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.data.LibrarySpikeIn;
import uk.ac.bbsrc.tgac.miso.core.data.PartitionQCType;
import uk.ac.bbsrc.tgac.miso.core.data.Printer;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunLibraryQcStatus;
import uk.ac.bbsrc.tgac.miso.core.data.SampleType;
import uk.ac.bbsrc.tgac.miso.core.data.ScientificName;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingControlType;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.Stain;
import uk.ac.bbsrc.tgac.miso.core.data.StainCategory;
import uk.ac.bbsrc.tgac.miso.core.data.StudyType;
import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.data.Workstation;
import uk.ac.bbsrc.tgac.miso.core.data.impl.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.Transfer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.Workset;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetCategory;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetStage;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.data.type.TissuePieceType;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.AbstractListPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.AbstractListPage.Columns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.AbstractListPage.ListTarget;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.ListPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.ListTabbedPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.ListTabbedPage.Tabs;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.DataTable;

public class DeleteIT extends AbstractIT {

  @Test
  public void testDeleteAssayTest() {
    testAdminDelete(ListTarget.ASSAY_TESTS, null, "Delete Me", Columns.ALIAS, AssayTest.class, 4L);
  }

  @Test
  public void testDeleteProject() {
    testDelete(ListTarget.PROJECTS, null, "GSLE", Columns.CODE, ProjectImpl.class, 7L);
  }

  @Test
  public void testDeleteSample() {
    testDelete(ListTarget.SAMPLES, null, "TEST_0001_Bn_R_nn_1-1_R_1_SM_1", Columns.ALIAS, SampleImpl.class, 12L);
  }

  @Test
  public void testDeleteLibrary() {
    testDelete(ListTarget.LIBRARIES, null, "LIB100001", Columns.NAME, LibraryImpl.class, 100001L);
  }

  @Test
  public void testDeleteLibraryAliquot() {
    testDelete(ListTarget.LIBRARY_ALIQUOTS, null, "LDI304", Columns.NAME, LibraryAliquot.class, 304L);
  }

  @Test
  public void testDeletePoolOrder() {
    testDelete(ListTarget.POOL_ORDERS, null, "Pool Order Three", Columns.ALIAS, PoolOrder.class, 3L);
  }

  @Test
  public void testDeletePool() {
    testDelete(ListTarget.POOLS, null, "IPO701", Columns.NAME, PoolImpl.class, 701L);
  }

  @Test
  public void testDeleteWorkset() {
    testDelete(ListTarget.WORKSETS, null, "Workset Two", Columns.ALIAS, Workset.class, 2L);
  }

  @Test
  public void testDeleteBox() {
    testDelete(ListTarget.BOXES, null, "BOX501", Columns.NAME, BoxImpl.class, 501L);
  }

  @Test
  public void testDeleteTransfer() {
    testDelete(ListTarget.TRANSFERS, Tabs.RECEIPT, "id:2", Columns.ID, "2", Transfer.class, 2L);
  }

  @Test
  public void testDeleteContainer() {
    testDelete(ListTarget.CONTAINERS, null, "CHANGEABLE", Columns.SERIAL_NUMBER, SequencerPartitionContainerImpl.class,
        6001L);
  }

  @Test
  public void testDeleteRun() {
    testDelete(ListTarget.RUNS, Tabs.PACBIO, "PacBio_Run_1", Columns.ALIAS, Run.class, 2L);
  }

  @Test
  public void testDeleteArray() {
    testDelete(ListTarget.ARRAYS, null, "Array_2", Columns.ALIAS, Array.class, 2L);
  }

  @Test
  public void testDeleteArrayRun() {
    testDelete(ListTarget.ARRAY_RUNS, null, "ArrayRun_1", Columns.ALIAS, ArrayRun.class, 1L);
  }

  @Test
  public void testDeleteInstrument() {
    testAdminDelete(ListTarget.INSTRUMENTS, Tabs.ARRAY_SCANNER, "Deletable", Columns.INSTRUMENT_NAME,
        InstrumentImpl.class, 5L);
  }

  @Test
  public void testDeleteFreezer() {
    testAdminDelete(ListTarget.STORAGE_LOCATIONS, Tabs.FREEZERS, "Empty Freezer", Columns.ALIAS, StorageLocation.class,
        5L);
  }

  @Test
  public void testDeleteRoom() {
    testAdminDelete(ListTarget.STORAGE_LOCATIONS, Tabs.ROOMS, "Empty Room", Columns.ALIAS, StorageLocation.class, 4L);
  }

  @Test
  public void testDeleteLocationMap() {
    testAdminDelete(ListTarget.LOCATION_MAPS, null, "unused.html", Columns.FILENAME, StorageLocationMap.class, 3L);
  }

  @Test
  public void testDeleteLibraryTemplate() {
    testDelete(ListTarget.LIBRARY_TEMPLATES, null, "TestLibTemp", Columns.ALIAS, LibraryTemplate.class, 1L);
  }

  @Test
  public void testDeleteKit() {
    testAdminDelete(ListTarget.KITS, Tabs.MULTIPLEXING, "Test Kit Four", Columns.KIT_NAME, KitDescriptor.class, 4L);
  }

  @Test
  public void testDeleteIndexFamily() {
    testAdminDelete(ListTarget.INDEX_FAMILIES, null, "Unused Family", Columns.NAME, IndexFamily.class, 4L);
  }

  @Test
  public void testDeleteQcType() {
    testAdminDelete(ListTarget.QC_TYPE, null, "unused qc", Columns.NAME, QcType.class, 110L);
  }

  @Test
  public void testDeleteAttachmentCategory() {
    testAdminDelete(ListTarget.ATTACHMENT_CATEGORIES, null, "Submission Forms", Columns.ALIAS, AttachmentCategory.class,
        1L);
  }

  @Test
  public void testDeleteSampleType() {
    testAdminDelete(ListTarget.SAMPLE_TYPES, null, "SYNTHETIC", Columns.NAME, SampleType.class, 3L);
  }

  @Test
  public void testDeleteSequencingControlType() {
    testAdminDelete(ListTarget.SEQUENCING_CONTROL_TYPES, null, "Delete Me", Columns.ALIAS, SequencingControlType.class,
        3L);
  }

  @Test
  public void testDeleteLibraryType() {
    testAdminDelete(ListTarget.LIBRARY_TYPES, null, "Total RNA", Columns.DESCRIPTION, LibraryType.class, 27L);
  }

  @Test
  public void testDeleteSelectionType() {
    testAdminDelete(ListTarget.LIBRARY_SELECTION_TYPES, null, "RACE", Columns.NAME, LibrarySelectionType.class, 21L);
  }

  @Test
  public void testDeleteStrategyType() {
    testAdminDelete(ListTarget.LIBRARY_STRATEGY_TYPES, null, "POOLCLONE", Columns.NAME, LibraryStrategyType.class, 4L);
  }

  @Test
  public void testDeleteLibrarySpikeIn() {
    testAdminDelete(ListTarget.LIBRARY_SPIKE_INS, null, "Unused Spike-In", Columns.ALIAS, LibrarySpikeIn.class, 3L);
  }

  @Test
  public void testDeleteTargetedSequencing() {
    testAdminDelete(ListTarget.TARGETED_SEQUENCINGS, null, "Test TarSeq Three", Columns.ALIAS, TargetedSequencing.class,
        3L);
  }

  @Test
  public void testDeleteRunPurpose() {
    testAdminDelete(ListTarget.RUN_PURPOSES, null, "Unused", Columns.ALIAS, RunPurpose.class, 3L);
  }

  @Test
  public void testDeleteSequencingParameters() {
    testAdminDelete(ListTarget.SEQUENCING_PARAMETERS, null, "Custom (see notes)", Columns.NAME,
        SequencingParameters.class, 1L);
  }

  @Test
  public void testDeleteContainerModel() {
    testAdminDelete(ListTarget.CONTAINER_MODELS, null, "Generic 11-SMRT-Cell PacBio 8Pac", Columns.ALIAS,
        SequencingContainerModel.class,
        14L);
  }

  @Test
  public void testDeleteInstrumentModel() {
    testAdminDelete(ListTarget.INSTRUMENT_MODELS, null, "Deletable", Columns.ALIAS, InstrumentModel.class, 5L);
  }

  @Test
  public void testDeleteBoxSize() {
    testAdminDelete(ListTarget.BOX_SIZES, null, "3", Columns.ROWS, BoxSize.class, 3L);
  }

  @Test
  public void testDeleteBoxUse() {
    testAdminDelete(ListTarget.BOX_USES, null, "Tissue", Columns.ALIAS, BoxUse.class, 6L);
  }

  @Test
  public void testDeleteLab() {
    testAdminDelete(ListTarget.LABS, null, "Almost Unused Institute - Unused Lab", Columns.ALIAS, LabImpl.class, 3L);
  }

  @Test
  public void testDeleteArrayModel() {
    testAdminDelete(ListTarget.ARRAY_MODELS, null, "Unused", Columns.ALIAS, ArrayModel.class, 2L);
  }

  @Test
  public void testDeletePartitionQcType() {
    testAdminDelete(ListTarget.PARTITION_QC_TYPE, null, "Failed: Sasquatch Problem", Columns.DESCRIPTION,
        PartitionQCType.class, 3L);
  }

  @Test
  public void testDeleteScientificName() {
    testAdminDelete(ListTarget.SCIENTIFIC_NAMES, null, "Delete me", Columns.ALIAS, ScientificName.class, 3L);
  }

  @Test
  public void testDeleteReferenceGenome() {
    testAdminDelete(ListTarget.REFERENCE_GENOMES, null, "Sasquatch sg12 random", Columns.ALIAS,
        ReferenceGenomeImpl.class, 4L);
  }

  @Test
  public void testDeletePipeline() {
    testAdminDelete(ListTarget.PIPELINES, null, "Delete me", Columns.ALIAS, Pipeline.class, 3L);
  }

  @Test
  public void testDeleteStudyType() {
    testAdminDelete(ListTarget.STUDY_TYPES, null, "Epigenetics", Columns.NAME, StudyType.class, 6L);
  }

  @Test
  public void testDeleteWorkstation() {
    testAdminDelete(ListTarget.WORKSTATIONS, null, "Workstation 3", Columns.ALIAS, Workstation.class, 3L);
  }

  @Test
  public void testDeleteSop() {
    testAdminDelete(ListTarget.SOPS, Tabs.LIBRARY, "SOP to delete", Columns.ALIAS, Sop.class, 5L);
  }

  @Test
  public void testDeletePrinter() {
    testAdminDelete(ListTarget.PRINTERS, null, "Printer", Columns.PRINTER, Printer.class, 1L);
  }

  @Test
  public void testDeleteSampleClass() {
    testAdminDelete(ListTarget.SAMPLE_CLASSES, null, "Unused", Columns.ALIAS, SampleClassImpl.class, 28L);
  }

  @Test
  public void testDeleteTissueMaterial() {
    testAdminDelete(ListTarget.TISSUE_MATERIALS, null, "Fur", Columns.ALIAS, TissueMaterialImpl.class, 4L);
  }

  @Test
  public void testDeleteTissueOrigin() {
    testAdminDelete(ListTarget.TISSUE_ORIGINS, null, "Big Foot", Columns.DESCRIPTION, TissueOriginImpl.class, 5L);
  }

  @Test
  public void testDeleteTissueType() {
    testAdminDelete(ListTarget.TISSUE_TYPES, null, "Organoid", Columns.DESCRIPTION, TissueTypeImpl.class, 12L);
  }

  @Test
  public void testDeleteTissuePieceType() {
    testAdminDelete(ListTarget.TISSUE_PIECE_TYPE, null, "Unused", Columns.NAME, TissuePieceType.class, 2L);
  }

  @Test
  public void testDeleteSamplePurpose() {
    testAdminDelete(ListTarget.SAMPLE_PURPOSES, null, "Ion Torrent", Columns.ALIAS, SamplePurposeImpl.class, 11L);
  }

  @Test
  public void testDeleteSubproject() {
    testAdminDelete(ListTarget.SUBPROJECTS, null, "Subproject 3", Columns.ALIAS, SubprojectImpl.class, 3L);
  }

  @Test
  public void testDeleteStain() {
    testAdminDelete(ListTarget.STAINS, null, "Unused", Columns.NAME, Stain.class, 3L);
  }

  @Test
  public void testDeleteStainCategory() {
    testAdminDelete(ListTarget.STAIN_CATEGORIES, null, "Three", Columns.NAME, StainCategory.class, 3L);
  }

  @Test
  public void testDeleteDetailedQcStatus() {
    testAdminDelete(ListTarget.DETAILED_QC_STATUS, null, "Reference Required", Columns.DESCRIPTION,
        DetailedQcStatusImpl.class, 8L);
  }

  @Test
  public void testDeleteLibraryDesignCode() {
    testAdminDelete(ListTarget.LIBRARY_DESIGN_CODES, null, "Unused", Columns.DESCRIPTION, LibraryDesignCode.class, 18L);
  }

  @Test
  public void testDeleteLibraryDesign() {
    testAdminDelete(ListTarget.LIBRARY_DESIGNS, null, "Unused", Columns.NAME, LibraryDesign.class, 20L);
  }

  @Test
  public void testDeleteStudy() {
    testAdminDelete(ListTarget.STUDIES, null, "STU400", Columns.NAME, StudyImpl.class, 400L);
  }

  @Test
  public void testDeleteExperiment() {
    testAdminDelete(ListTarget.EXPERIMENTS, null, "EXP2", Columns.NAME, Experiment.class, 2L);
  }

  @Test
  public void testDeleteSubmission() {
    testAdminDelete(ListTarget.SUBMISSIONS, null, "Submission One", Columns.ALIAS, Submission.class, 1L);
  }

  @Test
  public void testDeleteUser() {
    testAdminDelete(ListTarget.USERS, null, "hhenderson", Columns.LOGIN_NAME, UserImpl.class, 4L);
  }

  @Test
  public void testDeleteGroup() {
    testAdminDelete(ListTarget.GROUPS, null, "DeleteGroup", Columns.NAME, Group.class, 3L);
  }

  @Test
  public void testDeleteRunLibraryQcStatus() {
    testAdminDelete(ListTarget.RUN_LIBRARY_QC_STATUSES, null, "DeleteMe", Columns.DESCRIPTION, RunLibraryQcStatus.class,
        4L);
  }

  @Test
  public void testDeleteWorksetCategory() {
    testAdminDelete(ListTarget.WORKSET_CATEGORIES, null, "DeleteCategory", Columns.ALIAS, WorksetCategory.class, 3L);
  }

  @Test
  public void testDeleteWorksetStage() {
    testAdminDelete(ListTarget.WORKSET_STAGES, null, "DeleteStage", Columns.ALIAS, WorksetStage.class, 4L);
  }

  @Test
  public void testDeleteStorageLabel() {
    testAdminDelete(ListTarget.STORAGE_LABELS, null, "Label to delete", Columns.LABEL, StorageLabel.class, 3L);
  }

  @Test
  public void testDeleteMetric() {
    testAdminDelete(ListTarget.METRICS, null, "To Delete", Columns.ALIAS, Metric.class, 4L);
  }

  @Test
  public void testDeleteAssay() {
    testAdminDelete(ListTarget.ASSAYS, null, "Bad Assay", Columns.ALIAS, Assay.class, 4L);
  }

  @Test
  public void testDeleteRequisition() {
    testDelete(ListTarget.REQUISITIONS, null, "Req Two", Columns.ALIAS, Requisition.class, 2L);
  }

  @Test
  public void testDeleteDeliverable() {
    testDelete(ListTarget.DELIVERABLES, null, "deliverable1", Columns.NAME, Deliverable.class, 1L);
  }

  private void testDelete(String listTarget, String tab, String search, String selectByColumn, Class<?> clazz,
      Long id) {
    testDelete(listTarget, tab, search, selectByColumn, search, clazz, id);
  }

  private void testDelete(String listTarget, String tab, String search, String selectByColumn, String columnValue,
      Class<?> clazz,
      Long id) {
    login();
    doTestDelete(listTarget, tab, search, selectByColumn, columnValue, clazz, id);
  }

  private void testAdminDelete(String listTarget, String tab, String search, String selectByColumn, Class<?> clazz,
      Long id) {
    loginAdmin();
    doTestDelete(listTarget, tab, search, selectByColumn, search, clazz, id);
  }

  private void doTestDelete(String listTarget, String tab, String search, String selectByColumn, String columnValue,
      Class<?> clazz,
      Long id) {
    assertNotNull("Couldn't find item in the database", getSession().get(clazz, id));
    AbstractListPage page = null;
    if (tab == null) {
      page = ListPage.getListPage(getDriver(), getBaseUrl(), listTarget);
    } else {
      ListTabbedPage tabbedPage = ListTabbedPage.getTabbedListPage(getDriver(), getBaseUrl(), listTarget);
      tabbedPage.clickTab(tab);
      page = tabbedPage;
    }
    DataTable table = page.getTable();
    if (search != null) {
      if (table.hasAdvancedSearch() && search.equals(columnValue)) {
        table.searchFor("\"" + search + "\"");
      } else {
        table.searchFor(search);
      }
    }
    List<String> values = table.getColumnValues(selectByColumn);
    assertFalse("No values found in column", values.isEmpty());
    boolean found = false;
    for (int i = 0; i < values.size(); i++) {
      if (values.get(i).equals(columnValue)) {
        table.checkBoxForRow(i);
        found = true;
        break;
      }
    }
    assertTrue("Couldn't find item in the list table", found);

    AbstractListPage page2 = page.deleteSelected();
    assertNotNull("Error deleting item", page2);
    DataTable table2 = page2.getTable();
    if (search != null) {
      table2.searchFor(search);
    }
    assertFalse("Found item in list table after delete attempt",
        table2.getColumnValues(selectByColumn).contains(search));
    assertNull("Found item in database after delete attempt", getSession().get(clazz, id));
  }

}
