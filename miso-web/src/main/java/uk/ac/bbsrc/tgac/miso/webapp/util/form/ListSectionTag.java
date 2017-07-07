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

import org.springframework.web.servlet.tags.RequestContextAwareTag;

import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("serial")
public class ListSectionTag extends RequestContextAwareTag {
  private static long id;
  private Object items;

  private String name;

  private String target;

  @Override
  protected int doStartTagInternal() throws Exception {
    if (items == null) {
      return SKIP_BODY;
    }
    @SuppressWarnings("unchecked")
    Collection<Object> items = (Collection<Object>) this.items;
    if (items.size() == 0) {
      return SKIP_BODY;
    }
    ObjectMapper mapper = new ObjectMapper();

    pageContext.getOut().append(String.format(
        "<br/><h1>%2$s</h1><table id='list%1$d' class='display no-border ui-widget-content'></table><script type='text/javascript'>jQuery(document).ready(function () { ListUtils.createStaticTable('list%1$d', ListTarget.%3$s, {}, %4$s);});</script>",
        id++, name, target, mapper.writeValueAsString(items)));
    return SKIP_BODY;
  }

  public Object getItems() {
    return items;
  }

  public String getName() {
    return name;
  }

  public String getTarget() {
    return target;
  }

  public void setItems(Object items) {
    this.items = items;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setTarget(String target) {
    this.target = target;
  }
}
