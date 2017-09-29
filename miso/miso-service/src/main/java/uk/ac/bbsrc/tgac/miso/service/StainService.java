package uk.ac.bbsrc.tgac.miso.service;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Stain;

public interface StainService {

  public List<Stain> list();

  public Stain get(long id);

}
