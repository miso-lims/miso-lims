package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.OrderLibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolOrder;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryAliquotService;
import uk.ac.bbsrc.tgac.miso.core.util.IndexChecker;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.PoolOrderDto;
import uk.ac.bbsrc.tgac.miso.service.PoolOrderService;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.ClientErrorException;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.NotFoundException;
import uk.ac.bbsrc.tgac.miso.webapp.util.PageMode;

@Controller
@RequestMapping("/poolorder")
public class EditPoolOrderController {

  @Autowired
  private PoolOrderService poolOrderService;
  @Autowired
  private LibraryAliquotService libraryAliquotService;
  @Autowired
  private IndexChecker indexChecker;
  @Autowired
  private ObjectMapper mapper;

  @GetMapping("/new")
  public ModelAndView create(@RequestParam(name = "aliquotIds", required = false) String aliquotIds, ModelMap model)
      throws IOException {
    model.put("title", "New Pool Order");
    model.put(PageMode.PROPERTY, PageMode.CREATE.getLabel());
    PoolOrder order = new PoolOrder();
    if (aliquotIds != null) {
      for (Long aliquotId : LimsUtils.parseIds(aliquotIds)) {
        LibraryAliquot ali = libraryAliquotService.get(aliquotId);
        if (ali == null) {
          throw new ClientErrorException("Library aliquot " + aliquotId + " not found");
        }
        OrderLibraryAliquot orderLib = new OrderLibraryAliquot();
        orderLib.setAliquot(ali);
        order.getOrderLibraryAliquots().add(orderLib);
      }
    }
    return orderPage(order, model);
  }

  @GetMapping("/{orderId}")
  public ModelAndView edit(@PathVariable long orderId, ModelMap model) throws IOException {
    PoolOrder order = poolOrderService.get(orderId);
    if (order == null) {
      throw new NotFoundException("Pool order not found");
    }
    model.put("title", "Pool Order " + orderId);
    model.put(PageMode.PROPERTY, PageMode.EDIT.getLabel());
    return orderPage(order, model);
  }

  private ModelAndView orderPage(PoolOrder order, ModelMap model) throws JsonProcessingException {
    PoolOrderDto dto = Dtos.asDto(order, indexChecker);
    model.put("poolOrder", order);
    model.put("orderDto", mapper.writeValueAsString(dto));
    model.put("libraryDtos", dto.getOrderAliquots());
    return new ModelAndView("/WEB-INF/pages/editPoolOrder.jsp", model);
  }

}
