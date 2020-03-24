package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.service.PoolService;
import uk.ac.bbsrc.tgac.miso.core.util.IndexChecker;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SequencingOrderDto;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkPropagateTableBackend;

@Controller
@RequestMapping("/sequencingorder")
public class EditSequencingOrderController {

  @Autowired
  private PoolService poolService;
  @Autowired
  private IndexChecker indexChecker;

  private final BulkPropagateTableBackend<Pool, SequencingOrderDto> orderBulkPropagateBackend = new BulkPropagateTableBackend<Pool, SequencingOrderDto>(
      "sequencingorder", SequencingOrderDto.class, "Sequencing Orders", "Pools") {

    @Override
    protected SequencingOrderDto createDtoFromParent(Pool item) {
      SequencingOrderDto dto = new SequencingOrderDto();
      dto.setPool(Dtos.asDto(item, false, false, indexChecker));
      return dto;
    }

    @Override
    protected Stream<Pool> loadParents(List<Long> parentIds) throws IOException {
      return poolService.listByIdList(parentIds).stream();
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
      // no config necessary
    }
  };

  @RequestMapping("/bulk/create")
  public ModelAndView bulkCreateOrders(@RequestParam("ids") String poolIds, ModelMap model) throws IOException {
    return orderBulkPropagateBackend.propagate(poolIds, model);
  }

}
