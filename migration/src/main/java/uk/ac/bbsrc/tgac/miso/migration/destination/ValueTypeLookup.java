package uk.ac.bbsrc.tgac.miso.migration.destination;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.Institute;
import uk.ac.bbsrc.tgac.miso.core.data.Lab;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryAdditionalInfo;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.QcPassedDetail;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SamplePurpose;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.Subproject;
import uk.ac.bbsrc.tgac.miso.core.data.TissueMaterial;
import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;
import uk.ac.bbsrc.tgac.miso.core.data.TissueType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.migration.util.UniqueKeyHashMap;

/**
 * Used to look up entities by their alias, name, or other attributes when the ID is not known
 */
public class ValueTypeLookup {

  private Map<Long, SampleClass> sampleClassById;
  private Map<String, SampleClass> sampleClassByAlias;
  private Map<Long, TissueType> tissueTypeById;
  private Map<String, TissueType> tissueTypeByAlias;
  private Map<Long, TissueMaterial> tissueMaterialById;
  private Map<String, TissueMaterial> tissueMaterialByAlias;
  private Map<Long, KitDescriptor> kitById;
  private Map<String, KitDescriptor> kitByName;
  private Map<Long, SamplePurpose> samplePurposeById;
  private Map<String, SamplePurpose> samplePurposeByAlias;
  private Map<String, Institute> institutesByAlias;
  private Map<Long, Lab> labsById;
  private Map<Long, Map<String, Lab>> labsByInstituteId;
  private static final String UNSPECIFIED_LAB = "Not Specified";
  private Map<Long, TissueOrigin> tissueOriginsById;
  private Map<String, TissueOrigin> tissueOriginsByAlias;
  private Map<String, TissueOrigin> tissueOriginsByDescription;
  private Map<Long, LibrarySelectionType> librarySelectionsById;
  private Map<String, LibrarySelectionType> librarySelectionsByName;
  private Map<Long, LibraryStrategyType> libraryStrategiesById;
  private Map<String, LibraryStrategyType> libraryStrategiesByName;
  private Map<Long, LibraryType> libraryTypeById;
  private Map<String, Map<String, LibraryType>> libraryTypeByPlatformAndDescription;
  private Map<Long, LibraryDesign> libraryDesignById;
  private Map<String, LibraryDesign> libraryDesignByName;
  private Map<Long, Index> indexById;
  private Map<String, Map<String, Index>> indexByFamilyAndSequence;
  private Map<Long, QcType> sampleQcTypeById;
  private Map<String, QcType> sampleQcTypeByName;
  private Map<Long, QcType> libraryQcTypeById;
  private Map<String, QcType> libraryQcTypeByName;
  private Map<Long, SequencerReference> sequencerById;
  private Map<String, SequencerReference> sequencerByName;
  private Map<Long, Subproject> subprojectById;
  private Map<String, Subproject> subprojectByAlias;
  private Map<Long, QcPassedDetail> qcPassedDetailById;
  private Map<String, QcPassedDetail> qcPassedDetailByDescription;
  
  /**
   * Create a ValueTypeLookup loaded with data from the provided MisoServiceManager
   * 
   * @param misoServiceManager
   * @throws IOException if an there is an error pulling data from misoServiceManager
   */
  public ValueTypeLookup(MisoServiceManager misoServiceManager) throws IOException {
    setSampleClasses(misoServiceManager.getSampleClassDao().getSampleClass());
    setTissueTypes(misoServiceManager.getTissueTypeDao().getTissueType());
    setTissueMaterials(misoServiceManager.getTissueMaterialDao().getTissueMaterial());
    setKits(misoServiceManager.getKitDao().listAllKitDescriptors());
    setSamplePurposes(misoServiceManager.getSamplePurposeDao().getSamplePurpose());
    setLabs(misoServiceManager.getLabDao().getLabs());
    setTissueOrigins(misoServiceManager.getTissueOriginDao().getTissueOrigin());
    setLibrarySelections(misoServiceManager.getLibraryDao().listAllLibrarySelectionTypes());
    setLibraryStrategies(misoServiceManager.getLibraryDao().listAllLibraryStrategyTypes());
    setLibraryTypes(misoServiceManager.getLibraryDao().listAllLibraryTypes());
    setLibraryDesigns(misoServiceManager.getLibraryDesignDao().getLibraryDesigns());
    setIndices(misoServiceManager.getIndexDao().listAllIndices());
    setSampleQcTypes(misoServiceManager.getSampleQcDao().listAllSampleQcTypes());
    setLibraryQcTypes(misoServiceManager.getLibraryQcDao().listAllLibraryQcTypes());
    setSequencers(misoServiceManager.getSequencerReferenceDao().listAll());
    setSubprojects(misoServiceManager.getSubprojectDao().getSubproject());
    setQcPassedDetails(misoServiceManager.getQcPassedDetailDao().getQcPassedDetails());
  }

  private void setSampleClasses(Collection<SampleClass> sampleClasses) {
    Map<Long, SampleClass> mapById = new UniqueKeyHashMap<>();
    Map<String, SampleClass> mapByAlias = new UniqueKeyHashMap<>();
    for (SampleClass sampleClass : sampleClasses) {
      mapByAlias.put(sampleClass.getAlias(), sampleClass);
      mapById.put(sampleClass.getId(), sampleClass);
    }
    this.sampleClassByAlias = mapByAlias;
    this.sampleClassById = mapById;
  }

  private void setTissueTypes(Collection<TissueType> tissueTypes) {
    Map<Long, TissueType> mapById = new UniqueKeyHashMap<>();
    Map<String, TissueType> mapByAlias = new UniqueKeyHashMap<>();
    for (TissueType tt : tissueTypes) {
      mapByAlias.put(tt.getAlias(), tt);
      mapById.put(tt.getId(), tt);
    }
    this.tissueTypeById = mapById;
    this.tissueTypeByAlias = mapByAlias;
  }

  private void setTissueMaterials(Collection<TissueMaterial> tissueMaterials) {
    Map<Long, TissueMaterial> mapById = new UniqueKeyHashMap<>();
    Map<String, TissueMaterial> mapByAlias = new UniqueKeyHashMap<>();
    for (TissueMaterial tm : tissueMaterials) {
      mapByAlias.put(tm.getAlias(), tm);
      mapById.put(tm.getId(), tm);
    }
    this.tissueMaterialById = mapById;
    this.tissueMaterialByAlias = mapByAlias;
  }

  private void setKits(Collection<KitDescriptor> kits) {
    Map<Long, KitDescriptor> mapById = new UniqueKeyHashMap<>();
    Map<String, KitDescriptor> mapByName = new UniqueKeyHashMap<>();
    for (KitDescriptor kit : kits) {
      mapByName.put(kit.getName(), kit);
      mapById.put(kit.getId(), kit);
    }
    this.kitById = mapById;
    this.kitByName = mapByName;
  }

  private void setSamplePurposes(Collection<SamplePurpose> samplePurposes) {
    Map<Long, SamplePurpose> mapById = new UniqueKeyHashMap<>();
    Map<String, SamplePurpose> mapByAlias = new UniqueKeyHashMap<>();
    for (SamplePurpose sp : samplePurposes) {
      mapByAlias.put(sp.getAlias(), sp);
      mapById.put(sp.getId(), sp);
    }
    this.samplePurposeById = mapById;
    this.samplePurposeByAlias = mapByAlias;
  }

  private void setLabs(Collection<Lab> labs) {
    Map<Long, Lab> labMapById = new UniqueKeyHashMap<>();
    Map<Long, Map<String, Lab>> labMapByInstituteId = new UniqueKeyHashMap<>();
    Map<String, Institute> instMapByAlias = new UniqueKeyHashMap<>();
    for (Lab lab : labs) {
      labMapById.put(lab.getId(), lab);
      if (labMapByInstituteId.get(lab.getInstitute().getId()) == null) {
        instMapByAlias.put(lab.getInstitute().getAlias(), lab.getInstitute());
        labMapByInstituteId.put(lab.getInstitute().getId(), new UniqueKeyHashMap<String, Lab>());
      }
      labMapByInstituteId.get(lab.getInstitute().getId()).put(lab.getAlias(), lab);
    }
    this.labsById = labMapById;
    this.labsByInstituteId = labMapByInstituteId;
    this.institutesByAlias = instMapByAlias;
  }

  private void setTissueOrigins(Collection<TissueOrigin> tissueOrigins) {
    Map<Long, TissueOrigin> mapById = new UniqueKeyHashMap<>();
    Map<String, TissueOrigin> mapByAlias = new UniqueKeyHashMap<>();
    Map<String, TissueOrigin> mapByDescription = new UniqueKeyHashMap<>();
    for (TissueOrigin to : tissueOrigins) {
      mapById.put(to.getId(), to);
      mapByAlias.put(to.getAlias(), to);
      mapByDescription.put(to.getDescription(), to);
    }
    this.tissueOriginsById = mapById;
    this.tissueOriginsByAlias = mapByAlias;
    this.tissueOriginsByDescription = mapByDescription;
  }

  private void setLibrarySelections(Collection<LibrarySelectionType> librarySelections) {
    Map<Long, LibrarySelectionType> mapById = new UniqueKeyHashMap<>();
    Map<String, LibrarySelectionType> mapByName = new UniqueKeyHashMap<>();
    for (LibrarySelectionType ls : librarySelections) {
      mapById.put(ls.getId(), ls);
      mapByName.put(ls.getName(), ls);
    }
    this.librarySelectionsById = mapById;
    this.librarySelectionsByName = mapByName;
  }

  private void setLibraryStrategies(Collection<LibraryStrategyType> libraryStrategies) {
    Map<Long, LibraryStrategyType> mapById = new UniqueKeyHashMap<>();
    Map<String, LibraryStrategyType> mapByName = new UniqueKeyHashMap<>();
    for (LibraryStrategyType ls : libraryStrategies) {
      mapById.put(ls.getId(), ls);
      mapByName.put(ls.getName(), ls);
    }
    this.libraryStrategiesById = mapById;
    this.libraryStrategiesByName = mapByName;
  }

  private void setLibraryTypes(Collection<LibraryType> libraryTypes) {
    Map<Long, LibraryType> mapById = new UniqueKeyHashMap<>();
    Map<String, Map<String, LibraryType>> mapByPlatformAndDesc = new UniqueKeyHashMap<>();
    for (LibraryType lt : libraryTypes) {
      if (!mapByPlatformAndDesc.containsKey(lt.getPlatformType())) {
        mapByPlatformAndDesc.put(lt.getPlatformType(), new UniqueKeyHashMap<String, LibraryType>());
      }
      mapByPlatformAndDesc.get(lt.getPlatformType()).put(lt.getDescription(), lt);
      mapById.put(lt.getId(), lt);
    }
    this.libraryTypeById = mapById;
    this.libraryTypeByPlatformAndDescription = mapByPlatformAndDesc;
  }

  private void setLibraryDesigns(Collection<LibraryDesign> libraryDesigns) {
    Map<Long, LibraryDesign> mapById = new UniqueKeyHashMap<>();
    Map<String, LibraryDesign> mapByName = new UniqueKeyHashMap<>();
    for (LibraryDesign ld : libraryDesigns) {
      mapByName.put(ld.getName(), ld);
      mapById.put(ld.getId(), ld);
    }
    this.libraryDesignById = mapById;
    this.libraryDesignByName = mapByName;
  }

  private void setIndices(Collection<Index> indices) {
    Map<Long, Index> mapById = new UniqueKeyHashMap<>();
    Map<String, Map<String, Index>> mapByFamilyAndSequence = new UniqueKeyHashMap<>();
    for (Index index : indices) {
      Map<String, Index> mapBySequence = mapByFamilyAndSequence.get(index.getFamily().getName());
      if (mapBySequence == null) {
        mapBySequence = new UniqueKeyHashMap<String, Index>();
        mapByFamilyAndSequence.put(index.getFamily().getName(), mapBySequence);
      }
      mapBySequence.put(index.getSequence(), index);
      mapById.put(index.getId(), index);
    }
    this.indexById = mapById;
    this.indexByFamilyAndSequence = mapByFamilyAndSequence;
  }

  private void setSampleQcTypes(Collection<QcType> qcTypes) {
    Map<Long, QcType> mapById = new UniqueKeyHashMap<>();
    Map<String, QcType> mapByName = new UniqueKeyHashMap<>();
    for (QcType qc : qcTypes) {
      mapByName.put(qc.getName(), qc);
      mapById.put(qc.getQcTypeId(), qc);
    }
    this.sampleQcTypeById = mapById;
    this.sampleQcTypeByName = mapByName;
  }

  private void setLibraryQcTypes(Collection<QcType> qcTypes) {
    Map<Long, QcType> mapById = new UniqueKeyHashMap<>();
    Map<String, QcType> mapByName = new UniqueKeyHashMap<>();
    for (QcType qc : qcTypes) {
      mapByName.put(qc.getName(), qc);
      mapById.put(qc.getQcTypeId(), qc);
    }
    this.libraryQcTypeById = mapById;
    this.libraryQcTypeByName = mapByName;
  }

  private void setSequencers(Collection<SequencerReference> sequencers) {
    Map<Long, SequencerReference> mapById = new UniqueKeyHashMap<>();
    Map<String, SequencerReference> mapByName = new UniqueKeyHashMap<>();
    for (SequencerReference sequencer : sequencers) {
      mapByName.put(sequencer.getName(), sequencer);
      mapById.put(sequencer.getId(), sequencer);
    }
    this.sequencerById = mapById;
    this.sequencerByName = mapByName;
  }
  
  private void setSubprojects(Collection<Subproject> subprojects) {
    Map<Long, Subproject> mapById = new UniqueKeyHashMap<>();
    Map<String, Subproject> mapByAlias = new UniqueKeyHashMap<>();
    for (Subproject subproject : subprojects) {
      mapByAlias.put(subproject.getAlias(), subproject);
      mapById.put(subproject.getId(), subproject);
    }
    this.subprojectById = mapById;
    this.subprojectByAlias = mapByAlias;
  }
  
  private void setQcPassedDetails(Collection<QcPassedDetail> qcPassedDetails) {
    Map<Long, QcPassedDetail> mapById = new UniqueKeyHashMap<>();
    Map<String, QcPassedDetail> mapByDesc = new UniqueKeyHashMap<>();
    for (QcPassedDetail qcPassedDetail : qcPassedDetails) {
      mapByDesc.put(qcPassedDetail.getDescription(), qcPassedDetail);
      mapById.put(qcPassedDetail.getId(), qcPassedDetail);
    }
    this.qcPassedDetailById = mapById;
    this.qcPassedDetailByDescription = mapByDesc;
  }
  
  /**
   * Attempts to find an existing SampleClass
   * 
   * @param sampleClass a partially-formed SampleClass, which must have its ID or alias set in order for this method to resolve the
   * SampleClass
   * @return the existing SampleClass if a matching one is found; null otherwise
   */
  public SampleClass resolve(SampleClass sampleClass) {
    if (sampleClass == null) return null;
    if (sampleClass.getId() != null) return sampleClassById.get(sampleClass.getId());
    if (sampleClass.getAlias() != null) return sampleClassByAlias.get(sampleClass.getAlias());
    return null;
  }

  /**
   * Attempts to find an existing TissueType
   * 
   * @param tissueType a partially-formed TissueType, which must have its ID or alias set in order for this method to resolve the TissueType
   * @return the existing TissueType if a matching one is found; null otherwise
   */
  public TissueType resolve(TissueType tissueType) {
    if (tissueType == null) return null;
    if (tissueType.getId() != null) return tissueTypeById.get(tissueType.getId());
    if (tissueType.getAlias() != null) return tissueTypeByAlias.get(tissueType.getAlias());
    return null;
  }

  /**
   * Attempts to find an existing TissueMaterial
   * 
   * @param tissueMaterial a partially-formed TissueMaterial, which must have its ID or alias set in order for this method to resolve the
   * TissueMaterial
   * @return the existing TissueMaterial if a matching one is found; null otherwise
   */
  public TissueMaterial resolve(TissueMaterial tissueMaterial) {
    if (tissueMaterial == null) return null;
    if (tissueMaterial.getId() != null) return tissueMaterialById.get(tissueMaterial.getId());
    if (tissueMaterial.getAlias() != null) return tissueMaterialByAlias.get(tissueMaterial.getAlias());
    return null;
  }

  /**
   * Attempts to find an existing KitDescriptor
   * 
   * @param kit a partially-formed KitDescriptor, which must have its ID or name set in order for this method to resolve the KitDescriptor
   * @return the existing KitDescriptor if a matching one is found; null otherwise
   */
  public KitDescriptor resolve(KitDescriptor kit) {
    if (kit == null) return null;
    if (kit.getId() != KitDescriptor.UNSAVED_ID) return kitById.get(kit.getId());
    if (kit.getName() != null) return kitByName.get(kit.getName());
    return null;
  }

  /**
   * Attempts to find an existing SamplePurpose
   * 
   * @param samplePurpose a partially-formed SamplePurpose, which must have its ID or alias set in order for this method to resolve the
   * SamplePurpose
   * @return the existing SamplePurpose if a matching one is found; null otherwise
   */
  public SamplePurpose resolve(SamplePurpose samplePurpose) {
    if (samplePurpose == null) return null;
    if (samplePurpose.getId() != null) return samplePurposeById.get(samplePurpose.getId());
    if (samplePurpose.getAlias() != null) return samplePurposeByAlias.get(samplePurpose.getAlias());
    return null;
  }

  /**
   * Attempts to find an existing Lab
   * 
   * @param lab a partially-formed Lab, which must have its ID, institute ID, or institute alias set in order for this method to resolve the
   * Lab. The Lab's alias is used as well; if neither Lab ID nor Lab alias are set, the Lab alias 'Not Specified' is assumed, and the
   * Institute is first resolved by ID or alias
   * @return the existing Lab if a matching one is found; null otherwise
   */
  public Lab resolve(Lab lab) {
    if (lab == null) return null;
    if (lab.getId() != null) return labsById.get(lab.getId());
    if (lab.getInstitute() != null) {
      Long instId = lab.getInstitute().getId();
      if (instId == null && lab.getInstitute().getAlias() != null) {
        Institute i = institutesByAlias.get(lab.getInstitute().getAlias());
        if (i != null) instId = i.getId();
      }
      if (instId != null) {
        String labAlias = lab.getAlias();
        if (labAlias == null) labAlias = UNSPECIFIED_LAB;
        Map<String, Lab> labsByAlias = labsByInstituteId.get(instId);
        if (labsByAlias == null) return null;
        return labsByAlias.get(labAlias);
      }
    }
    return null;
  }

  /**
   * Attempts to find an existing TissueOrigin
   * 
   * @param tissueOrigin a partially-formed TissueOrigin, which must have its ID, alias, or description set in order for this method to
   * resolve the TissueOrigin
   * @return the existing TissueOrigin if a matching one is found; null otherwise
   */
  public TissueOrigin resolve(TissueOrigin tissueOrigin) {
    if (tissueOrigin == null) return null;
    if (tissueOrigin.getId() != null) return tissueOriginsById.get(tissueOrigin.getId());
    if (tissueOrigin.getAlias() != null) {
      TissueOrigin byAlias = tissueOriginsByAlias.get(tissueOrigin.getAlias());
      if (byAlias != null) return byAlias;
    }
    if (tissueOrigin.getDescription() != null) return tissueOriginsByDescription.get(tissueOrigin.getDescription());
    return null;
  }

  /**
   * Attempts to find an existing LibrarySelectionType
   * 
   * @param librarySelectionType a partially-formed LibrarySelectionType, which must have its ID or name set in order for this method to
   * resolve the LibrarySelectionType
   * @return the existing LibrarySelectionType if a matching one is found; null otherwise
   */
  public LibrarySelectionType resolve(LibrarySelectionType librarySelectionType) {
    if (librarySelectionType == null) return null;
    if (librarySelectionType.getId() != LibrarySelectionType.UNSAVED_ID) {
      return librarySelectionsById.get(librarySelectionType.getId());
    }
    if (librarySelectionType.getName() != null) return librarySelectionsByName.get(librarySelectionType.getName());
    return null;
  }

  /**
   * Attempts to find an existing LibraryStrategyType
   * 
   * @param libraryStrategyType a partially-formed LibraryStrategyType, which must have its ID or name set in order for this method to
   * resolve the LibraryStrategyType
   * @return the existing LibraryStrategyType if a matching one is found; null otherwise
   */
  public LibraryStrategyType resolve(LibraryStrategyType libraryStrategyType) {
    if (libraryStrategyType == null) return null;
    if (libraryStrategyType.getId() != LibrarySelectionType.UNSAVED_ID) {
      return libraryStrategiesById.get(libraryStrategyType.getId());
    }
    if (libraryStrategyType.getName() != null) return libraryStrategiesByName.get(libraryStrategyType.getName());
    return null;
  }

  /**
   * Attempts to find an existing LibraryType
   * 
   * @param libraryType a partially-formed LibraryType, which must have its ID or platform AND description set in order for this method to
   * resolve the LibraryType
   * @return the existing LibraryType if a matching one is found; null otherwise
   */
  public LibraryType resolve(LibraryType libraryType) {
    if (libraryType == null) return null;
    if (libraryType.getId() != LibraryType.UNSAVED_ID) return libraryTypeById.get(libraryType.getId());
    if (libraryType.getDescription() != null && libraryType.getPlatformType() != null) {
      Map<String, LibraryType> mapByDesc = libraryTypeByPlatformAndDescription.get(libraryType.getPlatformType());
      return mapByDesc == null ? null : mapByDesc.get(libraryType.getDescription());
    }
    return null;
  }

  /**
   * Attempts to find an existing LibraryDesign
   * 
   * @param libraryDesign a partially-formed LibraryDesign, which must have its ID or name set in order for this method to resolve the
   * LibraryDesign
   * @return the existing LibraryDesign if a matching one is found; null otherwise
   */
  public LibraryDesign resolve(LibraryDesign libraryDesign) {
    if (libraryDesign == null) return null;
    if (libraryDesign.getId() != null) return libraryDesignById.get(libraryDesign.getId());
    if (libraryDesign.getName() != null) return libraryDesignByName.get(libraryDesign.getName());
    return null;
  }

  /**
   * Attempts to find an existing Index
   * 
   * @param index a partially-formed Index, which must have either its ID or its sequence AND family name set in order for this method to
   * resolve the Index
   * @return the existing Index if a matching one is found; null otherwise
   */
  public Index resolve(Index index) {
    if (index == null) return null;
    if (index.getId() != Index.UNSAVED_ID) return indexById.get(index.getId());
    if (index.getFamily() != null && index.getFamily().getName() != null && index.getSequence() != null) {
      Map<String, Index> mapBySequence = indexByFamilyAndSequence.get(index.getFamily().getName());
      return mapBySequence == null ? null : mapBySequence.get(index.getSequence());
    }
    return null;
  }

  /**
   * Attempts to find an existing Sample QcType
   * 
   * @param qcType a partially-formed QcType, which must have its ID or name set in order for this method to resolve the QcType
   * @return the existing QcType if a matching one is found; null otherwise
   */
  public QcType resolveForSample(QcType qcType) {
    if (qcType == null) return null;
    if (qcType.getQcTypeId() != QcType.UNSAVED_ID) return sampleQcTypeById.get(qcType.getQcTypeId());
    if (qcType.getName() != null) return sampleQcTypeByName.get(qcType.getName());
    return null;
  }

  /**
   * Attempts to find an existing Library QcType
   * 
   * @param qcType a partially-formed QcType, which must have its ID or name set in order for this method to resolve the QcType
   * @return the existing QcType if a matching one is found; null otherwise
   */
  public QcType resolveForLibrary(QcType qcType) {
    if (qcType == null) return null;
    if (qcType.getQcTypeId() != QcType.UNSAVED_ID) return libraryQcTypeById.get(qcType.getQcTypeId());
    if (qcType.getName() != null) return libraryQcTypeByName.get(qcType.getName());
    return null;
  }

  /**
   * Attempts to find an existing SequencerReference
   * 
   * @param sequencer a partially-formed SequencerReference, which must have either its ID or its name set in order for this method to
   * resolve the SequencerReference
   * @return the existing SequencerReference if a matching one is found; null otherwise
   */
  public SequencerReference resolve(SequencerReference sequencer) {
    if (sequencer == null) return null;
    if (sequencer.getId() != Index.UNSAVED_ID) return sequencerById.get(sequencer.getId());
    if (sequencer.getName() != null) return sequencerByName.get(sequencer.getName());
    return null;
  }

  /**
   * Attempts to find an existing Subproject
   * 
   * @param subproject a partially-formed Subproject, which must have either its ID or its
   * alias set in order for this method to resolve the Subproject
   * @return the existing Subproject if a matching one is found; null otherwise
   */
  public Subproject resolve(Subproject subproject) {
    if (subproject == null) return null;
    if (subproject.getId() != null) return subprojectById.get(subproject.getId());
    if (subproject.getAlias() != null) return subprojectByAlias.get(subproject.getAlias());
    return null;
  }
  
  /**
   * Attempts to find an existing QcPassedDetail
   * 
   * @param qcPassedDetail a partially-formed QcPassedDetail, which must have either its ID or its
   * description set in order for this method to resolve the QcPassedDetail
   * @return the existing QcPassedDetail if a matching one is found; null otherwise
   */
  public QcPassedDetail resolve(QcPassedDetail qcPassedDetail) {
    if (qcPassedDetail == null) return null;
    if (qcPassedDetail.getId() != null) return qcPassedDetailById.get(qcPassedDetail.getId());
    if (qcPassedDetail.getDescription() != null) return qcPassedDetailByDescription.get(qcPassedDetail.getDescription());
    return null;
  }
  
  /**
   * Resolves all value type entities for a Sample
   * 
   * @param sample the sample containing partially-formed value type entities to be resolved. Full, existing entities are loaded into sample
   * in place of the partially-formed entities
   * @throws IOException if no value is found matching the available data in sample
   */
  public void resolveAll(Sample sample) throws IOException {
    for (SampleQC qc : sample.getSampleQCs()) {
      QcType type = resolveForSample(qc.getQcType());
      if (type == null)
        throw new IOException(String.format("QcType not found: id=%d, name=%s", qc.getQcType().getQcTypeId(), qc.getQcType().getName()));
      qc.setQcType(type);
    }

    if (LimsUtils.isDetailedSample(sample)) {
      DetailedSample detailed = (DetailedSample) sample;

      SampleClass sc = resolve(detailed.getSampleClass());
      if (sc == null) throw new IOException(String.format("SampleClass not found: id=%d, alias=%s",
          detailed.getSampleClass().getId(), detailed.getSampleClass().getAlias()));
      detailed.setSampleClass(sc);
      
      if (detailed.getSubproject() != null) { // Optional field
        Subproject subproj = resolve(detailed.getSubproject());
        if (subproj != null) { // may be a new subproject that needs saved
          detailed.setSubproject(subproj);
        }
      }
      
      if (detailed.getQcPassedDetail() != null) { // Optional field
        QcPassedDetail qcDet = resolve(detailed.getQcPassedDetail());
        if (qcDet == null) throw new IOException(String.format("QcPassedDetail not found: id=%d, description=%s",
            detailed.getQcPassedDetail().getId(), detailed.getQcPassedDetail().getDescription()));
        detailed.setQcPassedDetail(qcDet);
      }
      
      if (LimsUtils.isTissueSample(detailed)) {
        SampleTissue tissue = (SampleTissue) detailed;

        TissueOrigin to = resolve(tissue.getTissueOrigin());
        if (to == null) throw new IOException(String.format(
            "TissueOrigin not found: id=%d, alias=%s, description=%s",
            tissue.getTissueOrigin().getId(),
            tissue.getTissueOrigin().getAlias(),
            tissue.getTissueOrigin().getDescription()));
        tissue.setTissueOrigin(to);

        TissueType tt = resolve(tissue.getTissueType());
        if (tt == null) {
          if (tissue.getTissueType() != null) {
            throw new IOException(
                String.format("TissueType not found: id=%d, alias=%s", tissue.getTissueType().getId(), tissue.getTissueType().getAlias()));
          } else {
            throw new IOException("Sample " + tissue.getAlias() + " is missing a tissueType");
          }
        }
        tissue.setTissueType(tt);

        if (tissue.getLab() != null) { // optional field
          Lab lab = resolve(tissue.getLab());
          if (lab == null) throw new IOException("Lab not found");
          tissue.setLab(lab);
        }

        if (tissue.getTissueMaterial() != null) { // optional field
          TissueMaterial tm = resolve(tissue.getTissueMaterial());
          if (tm == null) throw new IOException("TissueMaterial not found");
          tissue.setTissueMaterial(tm);
        }
      }

      if (LimsUtils.isAliquotSample(detailed)) {
        SampleAliquot aliquot = (SampleAliquot) detailed;

        if (aliquot.getSamplePurpose() != null) { // optional field
          SamplePurpose sp = resolve(aliquot.getSamplePurpose());
          if (sp == null) throw new IOException("SamplePurpose not found");
          aliquot.setSamplePurpose(sp);
        }

      }
    }
  }

  /**
   * Resolves all value type entities for a Library
   * 
   * @param library the Library containing partially-formed value type entities to be resolved. Full, existing entities are loaded into
   * library in place of the partially-formed entities
   * @throws IOException if no value is found matching the available data in library
   */
  public void resolveAll(Library library) throws IOException {
    if (library.getLibrarySelectionType() != null) { // optional field
      LibrarySelectionType sel = resolve(library.getLibrarySelectionType());
      if (sel == null) throw new IOException(String.format("LibrarySelectionType not found (id=%d or name=%s)",
          library.getLibrarySelectionType().getId(), library.getLibrarySelectionType().getName()));
      library.setLibrarySelectionType(sel);
    }

    if (library.getLibraryStrategyType() != null) { // optional field
      LibraryStrategyType strat = resolve(library.getLibraryStrategyType());
      if (strat == null) throw new IOException(String.format("LibraryStrategyType not found (id=%d or name=%s)",
          library.getLibraryStrategyType().getId(), library.getLibraryStrategyType().getName()));
      library.setLibraryStrategyType(strat);
    }

    LibraryType lt = resolve(library.getLibraryType());
    if (lt == null) {
      throw new IOException(String.format(
          "LibraryType not found (id=%d OR (platform=%s AND description=%s))",
          library.getLibraryType().getId(),
          library.getLibraryType().getPlatformType(),
          library.getLibraryType().getDescription()));
    }
    library.setLibraryType(lt);

    if (library.getIndices() != null && !library.getIndices().isEmpty()) {
      List<Index> resolvedIndices = new ArrayList<>();
      for (Index index : library.getIndices()) {
        Index resolvedIndex = resolve(index);
        if (resolvedIndex == null) {
          throw new IOException(String.format(
              "Index not found (family=%s, sequence=%s)",
              index.getFamily() == null ? null : index.getFamily().getName(),
              index.getSequence()));
        }
        resolvedIndices.add(resolvedIndex);
      }
      library.setIndices(resolvedIndices);
    }
    for (LibraryQC qc : library.getLibraryQCs()) {
      QcType type = resolveForLibrary(qc.getQcType());
      if (type == null)
        throw new IOException(String.format("QcType not found: id=%d, name=%s", qc.getQcType().getQcTypeId(), qc.getQcType().getName()));
      qc.setQcType(type);
    }
    if (library.getLibraryAdditionalInfo() != null) {
      LibraryAdditionalInfo lai = library.getLibraryAdditionalInfo();

      if (lai.getPrepKit() != null) { // optional field
        KitDescriptor kit = resolve(lai.getPrepKit());
        if (kit == null) throw new IOException(
            String.format("KitDescriptor not found (id=%d or name=%s)", lai.getPrepKit().getId(), lai.getPrepKit().getName()));
        lai.setPrepKit(kit);
      }

      if (lai.getLibraryDesign() != null) { // optional field
        LibraryDesign ld = resolve(lai.getLibraryDesign());
        if (ld == null) throw new IOException("LibraryDesign not found");
        lai.setLibraryDesign(ld);
      }
    }
  }

  /**
   * Resolves all value type entities for a Run and its Pools
   * 
   * @param run the Run containing partially-formed value type entities to be resolved. Full, existing entities are loaded into run in place
   * of the partially-formed entities
   * @throws IOException if no value is found matching the available data in run
   */
  public void resolveAll(Run run) throws IOException {
    SequencerReference sequencer = resolve(run.getSequencerReference());
    if (sequencer == null) {
      throw new IOException(String.format(
          "SequencerReference not found (id=%d or name=%s)",
          run.getSequencerReference() == null ? null : run.getSequencerReference().getId(),
          run.getSequencerReference() == null ? null : run.getSequencerReference().getName()));
    }
    Platform platform = sequencer.getPlatform();
    PlatformType platformType = platform.getPlatformType();
    run.setSequencerReference(sequencer);
    if (run.getPlatformType() == null) {
      run.setPlatformType(platformType);
    }
    if (run.getSequencerPartitionContainers() != null) {
      for (SequencerPartitionContainer<SequencerPoolPartition> flowcell : run.getSequencerPartitionContainers()) {
        if (flowcell.getPlatform() == null) flowcell.setPlatform(platform);
        if (flowcell.getPartitions() != null) {
          for (SequencerPoolPartition lane : flowcell.getPartitions()) {
            if (lane.getPool() != null && lane.getPool().getPlatformType() == null) {
              lane.getPool().setPlatformType(platformType);
            }
          }
        }
      }
    }
  }

}
