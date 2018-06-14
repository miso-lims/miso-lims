package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.QC;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleAliquotImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowExecutor;
import uk.ac.bbsrc.tgac.miso.service.ContainerService;
import uk.ac.bbsrc.tgac.miso.service.PoolService;
import uk.ac.bbsrc.tgac.miso.service.QualityControlService;
import uk.ac.bbsrc.tgac.miso.service.SampleClassService;
import uk.ac.bbsrc.tgac.miso.service.SamplePurposeService;
import uk.ac.bbsrc.tgac.miso.service.SampleService;
import uk.ac.bbsrc.tgac.miso.service.SampleValidRelationshipService;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultWorkflowExecutor implements WorkflowExecutor {

  private static final Logger log = LoggerFactory.getLogger(DefaultWorkflowExecutor.class);

  @Autowired
  PoolService poolService;

  @Autowired
  ContainerService containerService;

  @Autowired
  QualityControlService qualityControlService;

  @Autowired
  SampleService sampleService;

  @Autowired
  SampleValidRelationshipService sampleValidRelationshipService;

  @Autowired
  SampleClassService sampleClassService;

  @Autowired
  SamplePurposeService samplePurposeService;

  @Override
  public Pool save(Pool pool) throws IOException {
    return poolService.get(poolService.save(pool));
  }

  @Override
  public SequencerPartitionContainer save(SequencerPartitionContainer spc) throws IOException {
    return containerService.save(spc);
  }

  @Override
  public QC save(QC qc) throws IOException {
    return qualityControlService.save(qc);
  }

  @Override
  public Collection<QcType> getQcTypeList() throws IOException {
    return qualityControlService.listQcTypes();
  }

  @Override
  public Sample save(Sample sample) throws IOException {
    return sampleService.save(sample);
  }

  @Override
  public SampleAliquot createAliquotFromParent(Sample stock) throws IOException {
    SampleAliquot aliquot = new SampleAliquotImpl();
    DetailedSample sample = (DetailedSample) stock;

    aliquot.setScientificName(sample.getScientificName());
    aliquot.setSampleType(sample.getSampleType());
    aliquot.setParent(sample);
    if (sample.getSubproject() != null) aliquot.setSubproject(sample.getSubproject());
    aliquot.setGroupId(sample.getGroupId());
    aliquot.setGroupDescription(sample.getGroupDescription());
    aliquot.setSampleClass(sampleClassService.getAll().stream()
        .filter(sampleClass -> {
          try {
            return sampleClass.getSampleCategory().equals("Aliquot") && sampleValidRelationshipService.getAll().stream()
                .anyMatch(
                    validRelationship -> !validRelationship.getArchived() && validRelationship.getChild().getId() == sampleClass.getId()
                    && validRelationship.getParent().getId() == sample.getSampleClass().getId());
          } catch (IOException e) {
            log.error("Error getting SampleValidRelationship", e);
            return false;
          }
        })
        .findFirst().orElse(null));
    aliquot.setSamplePurpose(samplePurposeService.getAll().stream().filter(samplePurpose -> samplePurpose.getAlias().equals("Library"))
        .findFirst().orElse(null));
    aliquot.setProject(sample.getProject());

    return aliquot;
  }


}
