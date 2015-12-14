package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import uk.ac.bbsrc.tgac.miso.core.data.SampleCategory;
import uk.ac.bbsrc.tgac.miso.dto.SampleCategoryDto;

@RequestMapping("/rest")
@Controller
@SessionAttributes("sampleidentity")
public class SampleIdentityController {
  protected static final Logger log = LoggerFactory.getLogger(SampleIdentityController.class);

  @RequestMapping(value = "/samplecategories", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public ResponseEntity<Set<SampleCategoryDto>> getSampleCategories(HttpServletResponse response) {
    if (response.containsHeader("x-authentication-failed")) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(true);
    scanner.addIncludeFilter(new AnnotationTypeFilter(SampleCategory.class));
    Set<SampleCategoryDto> results = new HashSet<SampleCategoryDto>();
    for (BeanDefinition definition : scanner.findCandidateComponents("uk.ac.bbsrc.tgac.miso.core.data")) {
      SampleCategoryDto category = new SampleCategoryDto();
      category.setClassName(definition.getBeanClassName());
      try {
        Class<?> clazz = Class.forName(definition.getBeanClassName());
        SampleCategory annotation = clazz.getAnnotation(SampleCategory.class);
        if (annotation == null) {
          log.error("No annotation for " + definition.getBeanClassName());
          continue;
        }
        category.setAlias(annotation.alias());

        results.add(category);
      } catch (ClassNotFoundException e) {
        log.error("Looking for sample categories", e);
      }
    }
    if (results.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } else {
      return new ResponseEntity<>(results, HttpStatus.OK);
    }
  }
}
