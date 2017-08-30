package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Cache;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyConsumer;
import uk.ac.bbsrc.tgac.miso.service.BoxService;
import uk.ac.bbsrc.tgac.miso.service.LibraryDilutionService;
import uk.ac.bbsrc.tgac.miso.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.service.PoolService;
import uk.ac.bbsrc.tgac.miso.service.SampleService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Controller
@RequestMapping("/rest/admin")
public class AdminRestController extends DefaultRestController {
  public static class RegenerationResponse {
    public static <T extends Barcodable> RegenerationResponse regenerate(String target, PaginatedDataSource<T> source,
        WhineyConsumer<T> update) throws IOException {
      RegenerationResponse response = new RegenerationResponse();
      response.target = target;
      response.updated = source.list(0, 0, true, "id").stream().peek(x -> response.total++)
          .filter(item -> isStringEmptyOrNull(item.getIdentificationBarcode())).peek(x -> response.blank++)
          .peek(WhineyConsumer.log(log, update))
          .count();
      return response;

    }

    private long blank;

    private String target;

    private long total;

    private long updated;

    public long getBlank() {
      return blank;
    }

    public String getTarget() {
      return target;
    }

    public long getTotal() {
      return total;
    }

    public long getUpdated() {
      return updated;
    }

    public void setBlank(long blank) {
      this.blank = blank;
    }

    public void setTarget(String target) {
      this.target = target;
    }

    public void setTotal(long total) {
      this.total = total;
    }

    public void setUpdated(long updated) {
      this.updated = updated;
    }
  }

  protected static final Logger log = LoggerFactory.getLogger(AdminRestController.class);

  @Autowired
  private AuthorizationManager authorizationManager;

  @Value("${miso.autoGenerateIdentificationBarcodes}")
  private Boolean autoGenerateIdBarcodes;
  @Autowired
  private BoxService boxService;

  @Autowired
  private LibraryDilutionService libraryDilutionService;
  @Autowired
  private LibraryService libraryService;
  @Autowired
  private PoolService poolService;
  @Autowired
  private SampleService sampleService;

  @Autowired
  private SessionFactory sessionFactory;

  @RequestMapping(value = "/cache/clear", method = RequestMethod.POST)
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public boolean clearCache() {
    try {
      if (!authorizationManager.isAdminUser()) {
        throw new RestException("Only admins can reset the cache.");
      }
    } catch (IOException e) {
      throw new RestException("Could not determine if user is admin.", e);
    }

    Cache cache = sessionFactory.getCache();

    if (cache == null) {
      return false;
    }
    cache.evictCollectionRegions();
    cache.evictDefaultQueryRegion();
    cache.evictEntityRegions();
    cache.evictNaturalIdRegions();
    cache.evictQueryRegions();
    return true;
  }

  @RequestMapping(value = "/barcode/regen", method = RequestMethod.POST)
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public List<RegenerationResponse> regenerateAllBarcodes() throws IOException {
    if (!authorizationManager.isAdminUser()) {
      throw new RestException("Only admins can regenerate barcodes.");
    }
    if (!autoGenerateIdBarcodes) {
      throw new RestException("Barcodes are not automatically generated.");
    }
    List<RegenerationResponse> response = new ArrayList<>();
    response.add(RegenerationResponse.regenerate("samples", sampleService, sampleService::update));
    response.add(RegenerationResponse.regenerate("libraries", libraryService, libraryService::update));
    response.add(RegenerationResponse.regenerate("dilutions", libraryDilutionService, libraryDilutionService::update));
    response.add(RegenerationResponse.regenerate("pools", poolService, poolService::save));
    response.add(RegenerationResponse.regenerate("boxes", boxService, boxService::save));
    return response;

  }

}
