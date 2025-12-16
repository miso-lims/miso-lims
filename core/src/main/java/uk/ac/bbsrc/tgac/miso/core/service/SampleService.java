package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.EntityReference;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.IdentityView;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface SampleService
    extends PaginatedDataSource<Sample>, BarcodableService<Sample>, DeleterService<Sample>, NoteService<Sample>,
    BulkSaveService<Sample> {

  @Override
  default EntityType getEntityType() {
    return EntityType.SAMPLE;
  }

  List<Sample> list() throws IOException;

  List<IdentityView> getIdentitiesByExternalNameOrAliasAndProject(String externalName, Long projectId,
      boolean exactMatch)
      throws IOException;

  List<IdentityView> getIdentities(Collection<String> externalNames, boolean exactMatch, Project project)
      throws IOException;

  void confirmExternalNameUniqueForProjectIfRequired(String externalNames, Sample sample) throws IOException;

  Sample getByBarcode(String barcode) throws IOException;

  Sample getByLibraryAliquotId(long aliquotId) throws IOException;

  Sample save(Sample sample) throws IOException;

  EntityReference getNextInProject(Sample sample);

  EntityReference getPreviousInProject(Sample sample);

  List<Sample> getChildren(Collection<Long> parentIds, String targetSampleCategory, long effectiveRequisitionId)
      throws IOException;

  void saveBarcode(long sampleId, String barcode) throws IOException;

}
