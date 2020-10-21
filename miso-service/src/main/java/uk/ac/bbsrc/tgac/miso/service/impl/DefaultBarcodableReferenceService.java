package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BarcodableReference;
import uk.ac.bbsrc.tgac.miso.core.service.BarcodableReferenceService;
import uk.ac.bbsrc.tgac.miso.persistence.BarcodableViewDao;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultBarcodableReferenceService implements BarcodableReferenceService {

  @Autowired
  private BarcodableViewDao barcodableViewDao;

  @Override
  public BarcodableReference checkForExisting(String identificationBarcode) throws IOException {
    return barcodableViewDao.checkForExisting(identificationBarcode);
  }

}
