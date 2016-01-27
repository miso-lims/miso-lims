package uk.ac.bbsrc.tgac.miso.persistence;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Institute;

public interface InstituteDao {
  
  List<Institute> getInstitute();
  
  Institute getInstitute(Long id);
  
  Long addInstitute(Institute institute);
  
  void deleteInstitute(Institute institute);
  
  void update(Institute institute);
  
}
