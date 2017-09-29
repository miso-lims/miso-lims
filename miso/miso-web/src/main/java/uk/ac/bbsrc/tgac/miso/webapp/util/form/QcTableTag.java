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

import uk.ac.bbsrc.tgac.miso.core.data.QualityControllable;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;

@SuppressWarnings("serial")
public class QcTableTag extends RequestContextAwareTag {
  private transient Object item;

  @Override
  protected int doStartTagInternal() throws Exception {

    QualityControllable<?> qcItem = (QualityControllable<?>) item;
    ObjectMapper mapper = new ObjectMapper();

    pageContext.getOut().append(String.format(
        "<br/><h1>QCs</h1><table id='%1$s' class='display no-border ui-widget-content'></table><script type='text/javascript'>jQuery(document).ready(function () { ListUtils.createStaticTable('%1$s', ListTarget.qc('%2$s'), { entityId : %3$d }, %4$s);});</script>",
        getId(), qcItem.getQcTarget(), qcItem.getId(),
        mapper.writeValueAsString(qcItem.getQCs().stream().map(Dtos::asDto).collect(Collectors.toList()))));
    return SKIP_BODY;
  }

  public Object getItem() {
    return item;
  }

  public void setItem(Object item) {
    this.item = item;
  }

}
