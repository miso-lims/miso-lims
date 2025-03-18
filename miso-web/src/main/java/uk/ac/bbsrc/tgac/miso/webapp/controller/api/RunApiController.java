package uk.ac.bbsrc.tgac.miso.webapp.controller.api;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.service.RunService;
import uk.ac.bbsrc.tgac.miso.webapp.controller.AbstractRestController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.NotFoundException;

@RestController
@RequestMapping("/api/runs")
public class RunApiController extends AbstractRestController {

  @Autowired
  private RunService runService;

  public record SignoffRequest(Boolean qcPassed) {
  }

  @PostMapping("/{runId}/qc-status")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void postSignoff(@PathVariable long runId, @RequestBody SignoffRequest request) throws IOException {
    Run run = runService.get(runId);
    if (run == null) {
      throw new NotFoundException("No run found with ID: %d".formatted(runId));
    }
    run.setQcPassed(request.qcPassed());
    runService.update(run);
  }

}
