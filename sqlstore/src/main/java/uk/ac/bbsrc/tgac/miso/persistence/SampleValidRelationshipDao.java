package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;

public interface SampleValidRelationshipDao {

  public List<SampleValidRelationship> list();

  public SampleValidRelationship get(Long id);

  public SampleValidRelationship getByClasses(SampleClass parent, SampleClass child) throws IOException;

  public Long create(SampleValidRelationship sampleValidRelationship);

  public void update(SampleValidRelationship sampleValidRelationship);

  public void delete(SampleValidRelationship sampleValidRelationship) throws IOException;

}