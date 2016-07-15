package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.SampleAdditionalInfo;

public interface SampleAdditionalInfoService {

  SampleAdditionalInfo get(Long sampleAdditionalInfoId) throws IOException;

  Set<SampleAdditionalInfo> getAll() throws IOException;

  void delete(Long sampleAdditionalInfoId) throws IOException;

  /**
   * copies all editable properties from one SampleAdditionalInfo instance to another
   * 
   * @param target the persisted SampleAdditionalInfo to copy changes into
   * @param source the modified SampleAdditionalInfo to copy changes from
   * @throws IOException
   */
  void applyChanges(SampleAdditionalInfo target, SampleAdditionalInfo source) throws IOException;
  
  /**
   * loads the contained objects into the target object from the database using IDs that are already present in the target
   * 
   * @param target the SampleAdditionalInfo whose attributes are to be populated from the database. Must already contain the IDs to 
   * be used for lookup
   * @throws IOException
   */
  void loadMembers(SampleAdditionalInfo target) throws IOException;
  
  /**
   * loads the contained objects into the target object from the database using IDs found in the source object
   * 
   * @param target the SampleAdditionalInfo whose attributes are to be populated from the database
   * @param source the SampleAdditionalInfo containing IDs to be used for lookup
   * @throws IOException 
   */
  void loadMembers(SampleAdditionalInfo target, SampleAdditionalInfo source) throws IOException;

}