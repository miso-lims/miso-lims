package uk.ac.bbsrc.tgac.miso.webapp.controller;

/**
 * Created by zakm on 07/08/2015.
 */
import java.io.IOException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.bbsrc.tgac.miso.core.data.KitComponentDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitComponentDescriptorImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;

@Controller
@RequestMapping("/kitcomponentdescriptor")
@SessionAttributes("kitComponentDescriptor")
public class EditKitComponentDescriptorController {
  protected static final Logger log = LoggerFactory.getLogger(EditKitComponentDescriptorController.class);

  @Autowired
  private RequestManager requestManager;

  @Autowired
  private DataObjectFactory dataObjectFactory;

  public void setDataObjectFactory(DataObjectFactory dataObjectFactory) {
    this.dataObjectFactory = dataObjectFactory;
  }

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  @RequestMapping(value = "/kit{kitDescriptorId}/new", method = RequestMethod.GET)
  public ModelAndView setupForm(@PathVariable Long kitDescriptorId, ModelMap model) throws IOException {
    try {
      KitDescriptor kitDescriptor = requestManager.getKitDescriptorById(kitDescriptorId);
      Collection<KitComponentDescriptor> kitComponentDescriptors = requestManager
          .listKitComponentDescriptorsByKitDescriptorId(kitDescriptorId);
      KitComponentDescriptor kitComponentDescriptor = new KitComponentDescriptorImpl();
      kitComponentDescriptor.setKitDescriptor(kitDescriptor);
      model.put("title", "Components of Kit " + kitDescriptorId);

      if (kitDescriptor == null) {
        throw new SecurityException("No such Kit Component Descriptor");
      }

      model.put("kitComponentDescriptor", kitComponentDescriptor);
      model.put("kitDescriptor", kitDescriptor);
      model.put("kitComponentDescriptors", kitComponentDescriptors);

      return new ModelAndView("/pages/editKitComponentDescriptor.jsp", model);
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to show Kit Component Descriptor", ex);
      }
      throw ex;
    }
  }

  @RequestMapping(method = RequestMethod.POST)
  public String processSubmit(@ModelAttribute("kitComponentDescriptor") KitComponentDescriptor kitComponentDescriptor,
      ModelMap model, SessionStatus session) throws IOException {
    session.setComplete();
    model.clear();
    return "redirect:/miso/kitcomponentdescriptor/kit" + kitComponentDescriptor.getKitDescriptor().getId() + "/new";
  }
}