package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;

import org.hibernate.Cache;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Controller
@RequestMapping("/rest/admin")
public class AdminRestController extends DefaultRestController {
  @Autowired
  private SessionFactory sessionFactory;
  @Autowired
  private AuthorizationManager authorizationManager;

  @RequestMapping(value = "/cache/clear", method = RequestMethod.GET)
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
}
