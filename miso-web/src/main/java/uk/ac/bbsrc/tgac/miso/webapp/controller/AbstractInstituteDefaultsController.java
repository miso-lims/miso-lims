package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.Aliasable;
import uk.ac.bbsrc.tgac.miso.core.util.AliasComparator;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.RestException;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkCreateTableBackend;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkEditTableBackend;
import uk.ac.bbsrc.tgac.miso.webapp.util.ListItemsPage;
import uk.ac.bbsrc.tgac.miso.webapp.util.ListItemsPageWithAuthorization;

public abstract class AbstractInstituteDefaultsController<Model extends Aliasable, Dto> {

  private static final Logger log = LoggerFactory.getLogger(EditPoolController.class);

  private final BulkEditTableBackend<Model, Dto> bulkEditBackend = new BulkEditTableBackend<Model, Dto>(getType(), getDtoClass(),
      getName()) {

    @Override
    protected Dto asDto(Model model) {
      return AbstractInstituteDefaultsController.this.asDto(model);
    }

    @Override
    protected Stream<Model> load(List<Long> modelIds) throws IOException {
      return modelIds.stream().map(WhineyFunction.rethrow(AbstractInstituteDefaultsController.this::get)).sorted(new AliasComparator<>());
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
      AbstractInstituteDefaultsController.this.writeConfiguration(mapper, config);
    }
  };

  private final ListItemsPage listPage = new ListItemsPageWithAuthorization(getType(), this::getSecurityManager) {

    @Override
    protected void writeConfigurationExtra(ObjectMapper mapper, ObjectNode config) throws IOException {
      AbstractInstituteDefaultsController.this.writeConfiguration(mapper, config);
    }

  };

  @Autowired
  private SecurityManager securityManager;

  protected abstract Dto asDto(Model model);

  @RequestMapping(value = "/bulk/new", method = RequestMethod.GET)
  public ModelAndView createBulkSamples(@RequestParam("quantity") Integer quantity, ModelMap model) throws IOException {
    if (quantity == null || quantity <= 0)
      throw new RestException("Must specify quantity of " + getType() + " to create", Status.BAD_REQUEST);

    return new BulkCreateTableBackend<Dto>(getType(), getDtoClass(), getName(), getBlankModel(), quantity) {

      @Override
      protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
        AbstractInstituteDefaultsController.this.writeConfiguration(mapper, config);
      }
    }.create(model);
  }

  @RequestMapping(value = "/bulk/edit", method = RequestMethod.GET)
  public ModelAndView edit(@RequestParam("ids") String poolIds, ModelMap model) throws IOException {
    return bulkEditBackend.edit(poolIds, model);
  }

  protected abstract Model get(long id) throws IOException;

  protected abstract Collection<Model> getAll() throws IOException;

  protected abstract Dto getBlankModel();

  protected abstract Class<Dto> getDtoClass();

  protected abstract String getName();

  public SecurityManager getSecurityManager() {
    return securityManager;
  }

  protected abstract String getType();

  @RequestMapping("/list")
  public ModelAndView list(ModelMap model) throws IOException {

    return listPage.list(model,
        getAll().stream().map(AbstractInstituteDefaultsController.this::asDto));
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
  }

}
