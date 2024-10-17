package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Arrays;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateSaveDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLabel;

public class HibernateStorageLabelDaoIT extends AbstractHibernateSaveDaoTest<StorageLabel, HibernateStorageLabelDao> {

  public HibernateStorageLabelDaoIT() {
    super(StorageLabel.class, 1L, 3);
  }

  @Override
  public HibernateStorageLabelDao constructTestSubject() {
    HibernateStorageLabelDao sut = new HibernateStorageLabelDao();
    sut.setEntityManager(getEntityManager());
    return sut;
  }

  @Override
  public StorageLabel getCreateItem() {
    StorageLabel label = new StorageLabel();
    label.setLabel("New Label");
    return label;
  }

  @SuppressWarnings("unchecked")
  @Override
  public UpdateParameters<StorageLabel, String> getUpdateParams() {
    return new UpdateParameters<>(1L, StorageLabel::getLabel, StorageLabel::setLabel, "Changed Label");
  }

  @Test
  public void testGetUsage() throws Exception {
    testGetUsage(HibernateStorageLabelDao::getUsage, 2L, 2L);
  }

  @Test
  public void testGetByLabel() throws Exception {
    testGetBy(HibernateStorageLabelDao::getByLabel, "Label Three", StorageLabel::getLabel);
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(HibernateStorageLabelDao::listByIdList, Arrays.asList(2L, 3L));
  }

}
