<%--
  ~ Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
  ~ MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
  ~ **********************************************************************
  ~
  ~ This file is part of MISO.
  ~
  ~ MISO is free software: you can redistribute it and/or modify
  ~ it under the terms of the GNU General Public License as published by
  ~ the Free Software Foundation, either version 3 of the License, or
  ~ (at your option) any later version.
  ~
  ~ MISO is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ GNU General Public License for more details.
  ~
  ~ You should have received a copy of the GNU General Public License
  ~ along with MISO.  If not, see <http://www.gnu.org/licenses/>.
  ~
  ~ **********************************************************************
  --%>

<%--
  Created by IntelliJ IDEA.
  User: davey
  Date: 12/01/12
  Time: 12:07
 --%>
<%@ include file="../header.jsp" %>
<script src="<c:url value='/scripts/parsley/parsley.min.js'/>" type="text/javascript"></script>
<!--  sequencer_partition_container_ajax.js is already included in header -->

<div id="maincontent">
<div id="contentcolumn">
<form:form id="container-form" data-parsley-validate="" action="/miso/container" method="POST" commandName="container" autocomplete="off">
<sessionConversation:insertSessionConversationId attributeName="container"/>
<h1>
  <c:choose>
    <c:when test="${container.id != 0}">Edit</c:when>
    <c:otherwise>Create</c:otherwise>
  </c:choose> Sequencer Partition Container
  <button class="fg-button ui-state-default ui-corner-all"
          onclick="return Container.validateContainer();">Save</button>
  <sec:authorize access="hasRole('ROLE_ADMIN')">
      <c:if test="${container.id != 0}">
          <button type="button" onclick="Container.deleteContainer(${container.id})" class="fg-button ui-state-default ui-corner-all">Delete</button>
      </c:if>
  </sec:authorize>
</h1>

<div class="bs-callout bs-callout-warning hidden">
  <h2>Oh snap!</h2>
  <p>This form seems to be invalid!</p>
</div>

<table class="in">
  <tr>
    <td class="h">Container ID:</td>
    <td>
      <c:choose>
        <c:when test="${container.id != 0}"><input type='hidden' id='containerId' name='id' value='${container.id}'/>${container.id}</c:when>
        <c:otherwise><i>Unsaved</i></c:otherwise>
      </c:choose>
    </td>
  </tr>

  <tr>
    <td>Platform*:</td>
    <td>
      <c:choose>
        <c:when test="${container.id != 0 and not empty container.platform}">
          <div id="platformTypesDiv">${container.platform.platformType.key}</div>
        </c:when>
        <c:otherwise>
          <div id="platformTypesDiv"></div>
        </c:otherwise>
      </c:choose>
    </td>
  </tr>
  <tr>
    <td></td>
    <td>
      <div class="parsley-errors-list filled" id="platformError">
        <div class="parsley-required"></div>
      </div>
    </td>
  </tr>
  <c:choose>
    <c:when test="${container.id != 0 and not empty container.platform}">
      <tr>
        <td>Sequencer*:</td>
        <td id="sequencerReferenceSelect" platformId="${container.platform.platformId}">${container.platform.instrumentModel}</td>
      </tr>
    </c:when>
    <c:when test="${container.id != 0}">
      <tr>
        <td>Sequencer*:</td>
        <td id="sequencerReferenceSelect"><i>Not yet processed on a run - unknown</i></td>
      </tr>
    </c:when>
    <c:otherwise>
      <tr>
        <td>Sequencer:</td>
        <td id="sequencerReferenceSelect"><i>Please choose a platform above...</i></td>
      </tr>
    </c:otherwise>
  </c:choose>
</table>

<table width="100%">
  <tbody>
  <tr>
    <td width="50%" valign="top">
      <h2>Container Parameters</h2>

      <div id="containerPartitions">
        <div class="parsley-errors-list filled" id="containerError">
          <div class="parsley-required"></div>
        </div>
        <c:if test="${container.id != 0}">
          <div class="note ui-corner-all">
            <c:if test="${multiplexed and not empty container.identificationBarcode}">
              <ul class="sddm">
                <li>
                  <a onmouseover="mopen('containermenu')" onmouseout="mclosetime()">Options
                    <span style="float:right" class="ui-icon ui-icon-triangle-1-s"></span>
                  </a>

                  <div class="run" id="containermenu"
                       onmouseover="mcancelclosetime()"
                       onmouseout="mclosetime()">
                    <a href="javascript:void(0);"
                       onclick="Container.generateCasava17DemultiplexCSV(${container.id});">Demultiplex
                      CSV (pre-1.8)</a>
                    <a href="javascript:void(0);"
                       onclick="Container.generateCasava18DemultiplexCSV(${container.id});">Demultiplex
                      CSV (1.8+)</a>
                  </div>
                </li>
              </ul>
            </c:if>
            <div style="clear:both"></div>
            <table class="in">
              <tr>
                <c:choose>
                  <c:when test="${empty container.identificationBarcode}">
                    <td>ID Barcode:</td>
                    <td>
                      <button onclick="Container.lookupContainer(this);" type="button"
                              class="right-button ui-state-default ui-corner-all">Lookup
                      </button>
                      <div style="overflow: hidden">
                        <form:input path="identificationBarcode"/>
                      </div>
                    </td>
                  </c:when>
                  <c:otherwise>
                    <td>ID Barcode:</td>
                    <td>
                      <span id="idBarcode">${container.identificationBarcode}</span>
                      <a href="javascript:void(0);"
                         onclick="Container.ui.editContainerIdBarcode(jQuery('#idBarcode'))">
                        <span class="fg-button ui-icon ui-icon-pencil"></span>
                      </a>
                    </td>
                  </c:otherwise>
                </c:choose>
              </tr>
              <tr>
                <c:choose>
                  <c:when test="${empty container.locationBarcode}">
                    <td>Location:</td>
                    <td><form:input path="locationBarcode"/></td>
                  </c:when>
                  <c:otherwise>
                    <td>Location:</td>
                    <td>
                      <span id="locationBarcode">${container.locationBarcode}</span>
                      <c:if test="${(container.securityProfile.owner.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                                                    or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
                        <a href="javascript:void(0);"
                           onclick="Container.ui.editContainerLocationBarcode(jQuery('#locationBarcode'), 0)">
                          <span class="fg-button ui-icon ui-icon-pencil"></span>
                        </a>
                      </c:if>
                    </td>
                  </c:otherwise>
                </c:choose>
              </tr>
              <tr>
                <c:choose>
                  <c:when test="${empty container.validationBarcode}">
                    <td>Validation:</td>
                    <td><form:input path="validationBarcode"/></td>
                  </c:when>
                  <c:otherwise>
                    <td>Validation:</td>
                    <td>
                      <span id="validationBarcode">${container.validationBarcode}</span>
                      <c:if test="${(container.securityProfile.owner.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                                                    or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
                        <a href="javascript:void(0);"
                           onclick="Container.ui.editContainerValidationBarcode(jQuery('#validationBarcode'), 0)">
                          <span class="fg-button ui-icon ui-icon-pencil"></span>
                        </a>
                      </c:if>
                    </td>
                  </c:otherwise>
                </c:choose>
              </tr>
                <%--
                <tr>
                  <td>Paired: ${container.paired}</td>
                </tr>
                --%>
            </table>
            <div id='partitionErrorDiv'></div>
            <div id="partitionDiv">
              <i class="italicInfo">Click in a partition box to beep/type in barcodes, or double click a pool on the
                right to sequentially add pools to the container</i>
              <table class="in">
                <th>Partition No.</th>
                <th>Pool</th>
                <c:forEach items="${container.partitions}" var="partition" varStatus="partitionCount">
                  <tr>
                    <td>${partition.partitionNumber}</td>
                    <td width="90%">
                      <c:choose>
                        <c:when test="${not empty partition.pool}">
                          <ul partition="${partitionCount.index}" bind="partitions[${partitionCount.index}].pool"
                              class="runPartitionDroppable">
                            <div class="dashboard" style="position:relative">
                                <%-- <a href='<c:url value="/miso/pool/${fn:toLowerCase(container.platformType.key)}/${partition.pool.id}"/>'> --%>
                              <a href='<c:url value="/miso/pool/${partition.pool.id}"/>'>
                                  ${partition.pool.name}
                                (${partition.pool.creationDate})
                              </a><br/>
                              <span style="font-size:8pt" id='partition_span_${partitionCount.index}'>
                              <c:choose>
                                <c:when test="${not empty partition.pool.experiments}">
                                  <i><c:forEach items="${partition.pool.experiments}" var="experiment">
                                    ${experiment.study.project.alias} (${experiment.name}: ${fn:length(partition.pool.dilutions)} dilutions)<br/>
                                  </c:forEach>
                                  </i>
                                  <script>
                                    jQuery(document).ready(function () {
                                      Container.partition.checkPoolExperiment('#partition_span_${partitionCount.index}', ${partition.pool.id}, ${partitionCount.index});
                                    });
                                  </script>
                                  <input type="hidden"
                                       name="partitions[${partitionCount.index}].pool"
                                       id="pId${partitionCount.index}"
                                       value="${partition.pool.id}"/>
                                </c:when>
                                <c:otherwise>
                                  <i>No experiment linked to this pool</i>
                                </c:otherwise>
                              </c:choose>
                              </span>
                              <c:if test="${empty container.run or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
                              <span style='position: absolute; top: 0; right: 0;' onclick='Container.pool.confirmPoolRemove(this, "${partition.partitionNumber}");' class='float-right ui-icon ui-icon-circle-close'></span>
                              </c:if>
                            </div>
                          </ul>
                        </c:when>
                        <c:otherwise>
                          <div id="p_div_${partitionCount.index}" class="elementListDroppableDiv">
                            <ul class='runPartitionDroppable' bind='partitions[${partitionCount.index}].pool' partition='${partitionCount.index}' ondblclick='Container.partition.populatePartition(this);'></ul>
                          </div>
                        </c:otherwise>
                      </c:choose>
                    </td>
                  </tr>
                </c:forEach>
              </table>
            </div>
          </div>
        </c:if>
      </div>
    </td>
    <td width="50%" valign="top">
      <h2>Available Pools</h2>
      <c:choose>
        <c:when test="${not empty container.platform}">
          <input id="showOnlyReady" type="checkbox" checked="true"
                 onclick="Container.pool.toggleReadyToRunCheck(this, '${container.platform.platformType.key}');"/>Only Ready to Run pools?
          <div align="right" style="margin-top: -23px; margin-bottom:3px">Filter:
            <input type="text" size="8" id="searchPools" name="searchPools"/>
          </div>
          <script type="text/javascript">
            Utils.timer.typewatchFunc(jQuery('#searchPools'), function () {
              Container.pool.poolSearch(jQuery('#searchPools').val(), '${container.platform.platformType.key}');
            }, 300, 2);
          </script>
        </c:when>
        <c:otherwise>
          <input id="showOnlyReady" type="checkbox" checked="true"
                 onclick="Container.pool.toggleReadyToRunCheck(this, jQuery('input[name=platformTypes]:checked').val());"/>Only Ready to Run pools?
          <div align="right" style="margin-top: -23px; margin-bottom:3px">Filter:
            <input type="text" size="8" id="searchPools" name="searchPools"/>
          </div>
          <script type="text/javascript">
            Utils.timer.typewatchFunc(jQuery('#searchPools'), function () {
              Container.pool.poolSearch(jQuery('#searchPools').val(), jQuery('input[name=platformTypes]:checked').val());
            }, 300, 2);
          </script>
        </c:otherwise>
      </c:choose>
      <div id='poolList' class="elementList ui-corner-all" style="height:500px">
      </div>
    </td>
  </tr>
  </tbody>
</table>
</form:form>

<script type="text/javascript">
  jQuery(document).ready(function () {
    // Attach Parsley form validator
    Validate.attachParsley('#container-form');
  })
</script>

<c:if test="${not empty container.changeLog}">
  <br/>
  <h1>Changes</h1>
  <span style="clear:both">
    <table class="list" id="changelog_table">
      <thead>
      <tr>
        <th>Summary</th>
        <th>Time</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${container.changeLog}" var="change">
        <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
          <td><b>${change.summary}</b></td>
          <td>${change.time}</td>
        </tr>
      </c:forEach>
      </tbody>
    </table>
  </span>
</c:if>
</div>
</div>

<script type="text/javascript">
  <c:choose>
    <c:when test="${container.id == 0 or empty container.platform}">
      jQuery(document).ready(function () {
        Container.ui.populatePlatformTypes();
      });
    </c:when>
    <c:otherwise>
      Container.pool.poolSearch("", '${container.platform.platformType.key}');
    </c:otherwise>
  </c:choose>
</script>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
