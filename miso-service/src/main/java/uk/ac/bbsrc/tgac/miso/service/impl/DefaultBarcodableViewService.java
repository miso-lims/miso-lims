package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.data.Barcodable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BarcodableView;
import uk.ac.bbsrc.tgac.miso.persistence.BarcodableViewDao;
import uk.ac.bbsrc.tgac.miso.service.BarcodableService;
import uk.ac.bbsrc.tgac.miso.service.BarcodableViewService;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultBarcodableViewService implements BarcodableViewService {
  @Autowired
  BarcodableViewDao barcodableViewDao;

  @Resource()
  private Map<EntityType, BarcodableService> barcodableServicesMap;

  @Override
  public List<BarcodableView> searchByBarcode(String barcode) {
    return barcodableViewDao.searchByBarcode(barcode);
  }

  @Override
  public List<BarcodableView> searchByBarcode(String barcode, Collection<EntityType> typeFilter) {
    return barcodableViewDao.searchByBarcode(barcode, typeFilter);
  }

  @Override
  public <T extends Barcodable> T getEntity(BarcodableView view) throws IOException {
    EntityType entityType = view.getId().getTargetType();
    long id = view.getId().getTargetId();
    @SuppressWarnings("unchecked")
    T barcodable  = (T) barcodableServicesMap.get(entityType).get(id);

    return barcodable;
  }
}
