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

import java.util.stream.Collectors;

import org.springframework.web.servlet.tags.RequestContextAwareTag;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.core.data.ChangeLoggable;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;

@SuppressWarnings("serial")
public class ChangeLogTag extends RequestContextAwareTag {
  private static long id;
  private Object item;

  @Override
  protected int doStartTagInternal() throws Exception {
    ChangeLoggable item = (ChangeLoggable) this.item;
    if (item.getChangeLog().isEmpty()) {
      return SKIP_BODY;
    }
    ObjectMapper mapper = new ObjectMapper();

    pageContext.getOut().append(String.format(
        "<br/><h1>Changes</h1><table id='changelog%1$d' class='display no-border ui-widget-content'></table><script type='text/javascript'>jQuery(document).ready(function () { ListUtils.createStaticTable('changelog%1$d', ListTarget.changelog, {}, %2$s);});</script>",
        id++, mapper.writeValueAsString(item.getChangeLog().stream().map(Dtos::asDto).collect(Collectors.toList()))));
    return SKIP_BODY;
  }

  public Object getItem() {
    return item;
  }

  public void setItem(Object item) {
    this.item = item;
  }

}
