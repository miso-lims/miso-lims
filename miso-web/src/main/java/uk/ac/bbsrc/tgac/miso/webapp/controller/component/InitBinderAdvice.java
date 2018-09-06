package uk.ac.bbsrc.tgac.miso.webapp.controller.component;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

@ControllerAdvice("uk.ac.bbsrc.tgac.miso.webapp.controller")
public class InitBinderAdvice {

  @InitBinder
  public void initBinder(WebDataBinder binder) {
    binder.registerCustomEditor(Long.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String text) throws IllegalArgumentException {
        setValue(isStringEmptyOrNull(text) ? null : Long.valueOf(text));
      }
    });
    binder.registerCustomEditor(BigDecimal.class, new PropertyEditorSupport() {
      @Override
      public String getAsText() {
        return toNiceString((BigDecimal) getValue());
      }

      @Override
      public void setAsText(String text) {
        setValue(isStringEmptyOrNull(text) ? null : new BigDecimal(text));
      }
    });
  }

}
