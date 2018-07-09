package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import uk.ac.bbsrc.tgac.miso.core.data.SampleCategory;
import uk.ac.bbsrc.tgac.miso.dto.SampleCategoryDto;

@RequestMapping("/rest")
@Controller
@SessionAttributes("sampleidentity")
public class SampleCategoryController {
  protected static final Logger log = LoggerFactory.getLogger(SampleCategoryController.class);

  @GetMapping(value = "/samplecategories", produces = { "application/json" })
  @ResponseBody
  public Set<SampleCategoryDto> getSampleCategories(HttpServletResponse response) {
    ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(true);
    scanner.addIncludeFilter(new AnnotationTypeFilter(SampleCategory.class));
    Set<SampleCategoryDto> results = new HashSet<>();
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
    return results;
  }
}
