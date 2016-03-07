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
import uk.ac.bbsrc.tgac.miso.core.store.KitStore;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryStore;
import uk.ac.bbsrc.tgac.miso.persistence.LibraryAdditionalInfoDao;
import uk.ac.bbsrc.tgac.miso.persistence.SampleGroupDao;
import uk.ac.bbsrc.tgac.miso.persistence.TissueOriginDao;
import uk.ac.bbsrc.tgac.miso.persistence.TissueTypeDao;
import uk.ac.bbsrc.tgac.miso.service.LibraryAdditionalInfoService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional
@Service
public class DefaultLibraryAdditionalInfoService implements LibraryAdditionalInfoService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultLibraryAdditionalInfoService.class);
  
  @Autowired
  private LibraryAdditionalInfoDao libraryAdditionalInfoDao;
  
  @Autowired
  private LibraryStore libraryStore;
  
  @Autowired
  private TissueOriginDao tissueOriginDao;
  
  @Autowired
  private TissueTypeDao tissueTypeDao;
  
  @Autowired
  private SampleGroupDao sampleGroupDao;
  
  @Autowired
  private KitStore kitStore;
  
  @Autowired
  private AuthorizationManager authorizationManager;
  
  @Override
  public LibraryAdditionalInfo get(Long id) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return libraryAdditionalInfoDao.getLibraryAdditionalInfo(id);
  }

  @Override
  public Long create(LibraryAdditionalInfo libraryAdditionalInfo, Long libraryId)
      throws IOException {
    authorizationManager.throwIfNonAdmin();
    libraryAdditionalInfo.setLibrary(libraryStore.get(libraryId));
    libraryAdditionalInfo.setTissueOrigin(tissueOriginDao.getTissueOrigin(libraryAdditionalInfo.getTissueOrigin().getTissueOriginId()));
    libraryAdditionalInfo.setTissueType(tissueTypeDao.getTissueType(libraryAdditionalInfo.getTissueType().getTissueTypeId()));
    if (libraryAdditionalInfo.getSampleGroupId() != null) {
      libraryAdditionalInfo.setSampleGroupId(sampleGroupDao.getSampleGroup(libraryAdditionalInfo.getSampleGroupId().getSampleGroupId()));
    }
    if (libraryAdditionalInfo.getPrepKit() != null) {
      libraryAdditionalInfo.setPrepKit(kitStore.getKitDescriptorById(libraryAdditionalInfo.getPrepKit().getKitDescriptorId()));
    }
    User user = authorizationManager.getCurrentUser();
    libraryAdditionalInfo.setCreatedBy(user);
    libraryAdditionalInfo.setUpdatedBy(user);
    return libraryAdditionalInfoDao.addLibraryAdditionalInfo(libraryAdditionalInfo);
  }

  @Override
  public void update(LibraryAdditionalInfo libraryAdditionalInfo)
      throws IOException {
    authorizationManager.throwIfNonAdmin();
    LibraryAdditionalInfo updated = get(libraryAdditionalInfo.getId());
    updated.setTissueOrigin(tissueOriginDao.getTissueOrigin(libraryAdditionalInfo.getTissueOrigin().getTissueOriginId()));
    updated.setTissueType(tissueTypeDao.getTissueType(libraryAdditionalInfo.getTissueType().getTissueTypeId()));
    if (libraryAdditionalInfo.getSampleGroupId() != null) {
      updated.setSampleGroupId(sampleGroupDao.getSampleGroup(libraryAdditionalInfo.getSampleGroupId().getSampleGroupId()));
    }
    if (libraryAdditionalInfo.getPrepKit() != null) {
      updated.setPrepKit(kitStore.getKitDescriptorById(libraryAdditionalInfo.getPrepKit().getKitDescriptorId()));
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
