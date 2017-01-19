package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryAdditionalInfo;
import uk.ac.bbsrc.tgac.miso.core.store.KitComponentStore;
import uk.ac.bbsrc.tgac.miso.core.store.KitDescriptorStore;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryStore;
import uk.ac.bbsrc.tgac.miso.persistence.LibraryAdditionalInfoDao;

import uk.ac.bbsrc.tgac.miso.service.LibraryAdditionalInfoService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultLibraryAdditionalInfoService implements LibraryAdditionalInfoService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultLibraryAdditionalInfoService.class);

  @Autowired
  private LibraryAdditionalInfoDao libraryAdditionalInfoDao;

  @Autowired
  private LibraryStore libraryStore;

  @Autowired
  private KitComponentStore kitStore;

  @Autowired
  private KitDescriptorStore kitDescriptorStore;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public LibraryAdditionalInfo get(Long id) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return libraryAdditionalInfoDao.getLibraryAdditionalInfo(id);
  }

  @Override
  public Long create(LibraryAdditionalInfo libraryAdditionalInfo, Long libraryId) throws IOException {
    authorizationManager.throwIfNotWritable(libraryStore.get(libraryId));
    libraryAdditionalInfo.setLibrary(libraryStore.get(libraryId));
    if (libraryAdditionalInfo.getPrepKit() != null) {
      libraryAdditionalInfo.setPrepKit(kitDescriptorStore.getKitDescriptorById(libraryAdditionalInfo.getPrepKit().getId()));
    }
    User user = authorizationManager.getCurrentUser();
    libraryAdditionalInfo.setCreatedBy(user);
    libraryAdditionalInfo.setUpdatedBy(user);
    return libraryAdditionalInfoDao.addLibraryAdditionalInfo(libraryAdditionalInfo);
  }

  @Override
  public void update(LibraryAdditionalInfo libraryAdditionalInfo) throws IOException {
    authorizationManager.throwIfNotWritable(libraryAdditionalInfo.getLibrary());
    LibraryAdditionalInfo updated = get(libraryAdditionalInfo.getLibraryId());
    if (libraryAdditionalInfo.getPrepKit() != null) {
      updated.setPrepKit(kitDescriptorStore.getKitDescriptorById(libraryAdditionalInfo.getPrepKit().getId()));
    }
    updated.setArchived(libraryAdditionalInfo.getArchived());
    User user = authorizationManager.getCurrentUser();
    updated.setUpdatedBy(user);
    libraryAdditionalInfoDao.update(updated);
  }

  @Override
  public Set<LibraryAdditionalInfo> getAll() throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return Sets.newHashSet(libraryAdditionalInfoDao.getLibraryAdditionalInfo());
  }

  @Override
  public void delete(Long libraryAdditionalInfoId) throws IOException {
    authorizationManager.throwIfNonAdmin();
    LibraryAdditionalInfo libraryAdditionalInfo = libraryAdditionalInfoDao.getLibraryAdditionalInfo(libraryAdditionalInfoId);
    libraryAdditionalInfoDao.deleteLibraryAdditionalInfo(libraryAdditionalInfo);
  }

}
