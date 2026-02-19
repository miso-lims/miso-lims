package uk.ac.bbsrc.tgac.miso.webapp.controller.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.ws.rs.core.Response.Status;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunItemQcStatus;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.QcNodeType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.QcStatusUpdate;
import uk.ac.bbsrc.tgac.miso.core.service.QcStatusService;
import uk.ac.bbsrc.tgac.miso.core.service.RunItemQcStatusService;
import uk.ac.bbsrc.tgac.miso.core.service.RunService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.webapp.controller.AbstractRestController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.RestException;

@RestController
@RequestMapping("/api/run-libraries")
public class RunLibraryApiController extends AbstractRestController {

  public record SignoffRequestItem(Long runId, Integer laneNumber, Long aliquotId, String qcStatus, String qcNote) {
  }

  @Autowired
  private QcStatusService qcStatusService;
  @Autowired
  private RunService runService;
  @Autowired
  private RunItemQcStatusService runItemQcStatusService;

  @PostMapping("qc-statuses")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void postSignoffs(@RequestBody List<SignoffRequestItem> signoffs) throws IOException {
    List<QcStatusUpdate> updates = new ArrayList<>();
    Map<String, RunItemQcStatus> statusesByDesc = new HashMap<>();
    for (SignoffRequestItem signoff : signoffs) {
      validateSignoff(signoff);
      QcStatusUpdate update = new QcStatusUpdate();
      Long partitionId = getPartitionId(signoff.runId(), signoff.laneNumber());
      update.setIds(new Long[] {signoff.runId(), partitionId, signoff.aliquotId()});
      update.setEntityType(QcNodeType.RUN_LIBRARY);
      RunItemQcStatus status = getRunItemQcStatus(signoff.qcStatus(), statusesByDesc);
      update.setQcStatusId(status.getId());
      update.setQcNote(signoff.qcNote());
      updates.add(update);
    }
    qcStatusService.update(updates);
  }

  private static void validateSignoff(SignoffRequestItem item) {
    List<ValidationError> errors = new ArrayList<>();
    if (item.runId() == null) {
      errors.add(ValidationError.forRequired("runId"));
    }
    if (item.laneNumber() == null) {
      errors.add(ValidationError.forRequired("laneNumber"));
    }
    if (item.aliquotId() == null) {
      errors.add(ValidationError.forRequired("aliquotId"));
    }
    if (item.qcStatus() == null) {
      errors.add(ValidationError.forRequired("qcStatus"));
    }
    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private long getPartitionId(Long runId, int laneNumber) throws IOException {
    Run run = runService.get(runId);
    if (run == null) {
      throw new RestException("No run found for ID " + runId, Status.BAD_REQUEST);
    }
    List<SequencerPartitionContainer> containers = run.getSequencerPartitionContainers();
    if (containers != null && containers.size() == 1) {
      SequencerPartitionContainer container = containers.get(0);
      List<Partition> partitions = container.getPartitions();
      if (partitions != null && partitions.size() >= laneNumber - 1) {
        Partition partition = container.getPartitionAt(laneNumber);
        if (partition != null && Objects.equals(partition.getPartitionNumber(), laneNumber)) {
          return partition.getId();
        }
      }
    }
    throw new RestException("Couldn't identify lane %d for run ID %d".formatted(laneNumber, runId), Status.BAD_REQUEST);
  }

  private RunItemQcStatus getRunItemQcStatus(String description, Map<String, RunItemQcStatus> statusesByDesc)
      throws IOException {
    if (statusesByDesc.containsKey(description)) {
      return statusesByDesc.get(description);
    }
    RunItemQcStatus status = runItemQcStatusService.getByDescription(description);
    if (status == null) {
      throw new RestException("Invalid qcStatus value: " + description, Status.BAD_REQUEST);
    }
    statusesByDesc.put(description, status);
    return status;
  }

}
