package uk.ac.bbsrc.tgac.miso.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Stain;
import uk.ac.bbsrc.tgac.miso.persistence.StainDao;
import uk.ac.bbsrc.tgac.miso.service.StainService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultStainService implements StainService {
  @Autowired
  private StainDao stainDao;

@Override
  public Stain get(long id) {
    return stainDao.get(id);
  }

@Override
  public List<Stain> list() {
    return stainDao.list();
  }

}
