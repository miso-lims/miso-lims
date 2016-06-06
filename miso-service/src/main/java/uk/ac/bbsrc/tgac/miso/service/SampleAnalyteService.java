package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.SampleAnalyte;

public interface SampleAnalyteService {

  SampleAnalyte get(Long sampleAnalyteId) throws IOException;

  Set<SampleAnalyte> getAll() throws IOException;

  void delete(Long sampleAnalyteId) throws IOException;
  
  /**
   * copies all the editable properties from one SampleAnalyte instance to another
   * 
   * @param target the persisted SampleAnalyte to copy changes into
   * @param source the modified SampleAnalyte to copy changes from
   */
  public void applyChanges(SampleAnalyte target, SampleAnalyte source) throws IOException;
  
  /**
   * loads the contained objects into the target object from the database using IDs that are already present in the target
   * 
   * @param target the SampleAnalyte whose attributes are to be populated from the database. Must already contain the IDs to 
   * be used for lookup
   * @throws IOException
   */
  void loadMembers(SampleAnalyte target) throws IOException;
  
  /**
   * loads the contained objects into the target object from the database using IDs found in the source object
   * 
   * @param target the SampleAnalyte whose attributes are to be populated from the database
   * @param source the SampleAnalyte containing IDs to be used for lookup
   * @throws IOException 
   */
  void loadMembers(SampleAnalyte target, SampleAnalyte source) throws IOException;

}