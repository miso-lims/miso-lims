package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;

public interface SampleValidRelationshipDao extends ProviderDao<SampleValidRelationship> {

  public SampleValidRelationship getByClasses(SampleClass parent, SampleClass child) throws IOException;

  public void delete(SampleValidRelationship sampleValidRelationship) throws IOException;

}
