/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.bbsrc.tgac.miso.webapp.util.form;

import java.util.Collection;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings({"serial", "squid:S1948"})
public class PaneTag extends RequestContextAwareTag {
  private boolean alwaysShow;

  private Object items;

  private String target;

  @Override
  protected int doStartTagInternal() throws Exception {
    if (items == null) {
      return SKIP_BODY;
    }
    @SuppressWarnings("unchecked")
    Collection<Object> items = (Collection<Object>) this.items;
    if (items.size() == 0 && !alwaysShow) {
      return SKIP_BODY;
    }
    ObjectMapper mapper = TagUtils.getObjectMapper(pageContext);

    pageContext.getOut().append(String.format(
        "<div id='%1$s'></div><script type='text/javascript'>jQuery(document).ready(function () { Pane.createPane('%1$s', PaneTarget.%2$s, %3$s);});</script>",
        getId(), target, mapper.writeValueAsString(items)));
    return SKIP_BODY;
  }

  public Object getItems() {
    return items;
  }

  public String getTarget() {
    return target;
  }

  public boolean isAlwaysShow() {
    return alwaysShow;
  }

  public void setAlwaysShow(boolean alwaysShow) {
    this.alwaysShow = alwaysShow;
  }

  public void setItems(Object items) {
    this.items = items;
  }

  public void setTarget(String target) {
    this.target = target;
  }
}
