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
  User: bian
  Date: 19-Apr-2010
  Time: 13:38:56

--%>
<%@ include file="../header.jsp" %>
<script src="<c:url value='/scripts/jquery/datatables/js/jquery.dataTables.min.js'/>" type="text/javascript"></script>
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" type="text/css">
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables_themeroller.css'/>"
      type="text/css">
<script src="<c:url value='/scripts/datatables_utils.js?ts=${timestamp.time}'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/natural_sort.js?ts=${timestamp.time}'/>" type="text/javascript"></script>

<script type="text/javascript" src="<c:url value='/scripts/parsley/parsley.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/scripts/pool_validation.js?ts=${timestamp.time}'/>"></script>

<div id="maincontent">
<div id="contentcolumn">
<form:form id="pool-form" data-parsley-validate="" method="POST" commandName="pool" autocomplete="off">
<sessionConversation:insertSessionConversationId attributeName="pool"/>
<h1><c:choose><c:when
    test="${pool.id != 0}">Edit</c:when><c:otherwise>Create</c:otherwise></c:choose> Pool
  <button type="submit" onclick="return validate_pool();" class="fg-button ui-state-default ui-corner-all">Save</button>
</h1>
<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
  <div id="note_arrowclick" class="toggleLeft"></div>
</div>
<div id="notediv" class="note" style="display:none;">A Pool contains <b>one or more</b> Dilutions or Plates that are
  to be placed, as part of an Experiment, in a sequencer instrument Run partition (lane/chamber/cell). Pools
  with more than one Dilution or a Plate with multiple libraries are said to be multiplexed.
</div>

<div class="bs-callout bs-callout-warning hidden">
  <h2>Oh snap!</h2>
  <p>This form seems to be invalid!</p>
</div>

<h2>Pool Information</h2>

<div class="barcodes">
  <div class="barcodeArea ui-corner-all">
    <span style="float: left; font-size: 24px; font-weight: bold; color:#BBBBBB">ID</span>
    <ul class="barcode-ddm">
      <li>
        <a onmouseover="mopen('idBarcodeMenu')" onmouseout="mclosetime()">
          <span style="float:right; margin-top:6px;" class="ui-icon ui-icon-triangle-1-s"></span>
          <span id="idBarcode" style="float:right"></span>
        </a>

        <div id="idBarcodeMenu"
             onmouseover="mcancelclosetime()"
             onmouseout="mclosetime()">
          <a href="javascript:void(0);" 
             onclick="Pool.barcode.printPoolBarcodes(${pool.id});">Print</a>
          <c:if test="${not autoGenerateIdBarcodes}">
            <a href="javascript:void(0);"
             onclick="Pool.barcode.showPoolIdBarcodeChangeDialog(${pool.id}, '${pool.identificationBarcode}');">Assign New Barcode</a>
          </c:if>
        </div>
      </li>
    </ul>
    <div id="changePoolIdBarcodeDialog" title="Assign New Barcode"></div>
    <c:if test="${not empty pool.identificationBarcode}">
      <script type="text/javascript">
        jQuery(document).ready(function () {
          Fluxion.doAjax(
            'poolControllerHelperService',
            'getPoolBarcode',
            {'poolId':${pool.id},
              'url': ajaxurl
            },
            {'doOnSuccess': function (json) {
              jQuery('#idBarcode').html("<img style='height:30px; border:0;' alt='${pool.identificationBarcode}' title='${pool.identificationBarcode}' src='<c:url value='/temp/'/>" + json.img + "'/>");
            }
            });
        });
      </script>
    </c:if>
  </div>
</div>
<div id="printServiceSelectDialog" title="Select a Printer"></div>
<table class="in">
  <tr>
    <td class="h">Pool ID:</td>
    <td>
      <c:choose>
        <c:when test="${pool.id != 0}">${pool.id}</c:when>
        <c:otherwise><i>Unsaved</i></c:otherwise>
      </c:choose>
    </td>
  </tr>
  <tr>
    <td class="h">Name:</td>
    <td>
      <c:choose>
        <c:when test="${pool.id != 0}">${pool.name}</c:when>
        <c:otherwise><i>Unsaved</i></c:otherwise>
      </c:choose>
    </td>
  </tr>
  <tr>
    <td class="h">Alias:*</td>
    <td><form:input id="alias" path="alias"/></td>
  </tr>
  <tr>
    <td>Platform Type:</td>
    <td>
      <c:choose>
        <c:when test="${pool.id != 0}">
          ${pool.platformType.key}
        </c:when>
        <c:otherwise>
          <form:select id="platformType" path="platformType" onchange="Pool.ui.prepareElements();" items="${platformTypes}"/>
        </c:otherwise>
      </c:choose>
    </td>
  </tr>
  <tr>
    <td class="h">Desired Concentration:*</td>
    <td><form:input id="concentration" path="concentration"/></td>
  </tr>
  <tr>
    <td class="h">Creation Date:*</td>
    <td><c:choose>
      <c:when test="${pool.id != 0}">
        <fmt:formatDate pattern="dd/MM/yy" type="both" value="${pool.creationDate}"/>
      </c:when>
      <c:otherwise><form:input path="creationDate"/></c:otherwise>
    </c:choose>
    </td>
  </tr>
  <tr bgcolor="yellow">
    <td>QC Passed:</td>
    <td>
      <form:radiobutton path="qcPassed" value="" label="Unknown"/>
      <form:radiobutton path="qcPassed" value="true" label="True"/>
      <form:radiobutton path="qcPassed" value="false" label="False"/>
    </td>
  </tr>
  <tr>
    <td class="h">Ready To Run</td>
    <td><form:checkbox path="readyToRun"/></td>
  </tr>

</table>
<%@ include file="permissions.jsp" %>
<br/>

<c:if test="${pool.id != 0}">
  <h1>
    <div id="qcsTotalCount">
    </div>
  </h1>
  <ul class="sddm">
    <li><a
        onmouseover="mopen('qcmenu')"
        onmouseout="mclosetime()">Options <span style="float:right"
                                                class="ui-icon ui-icon-triangle-1-s"></span></a>

      <div id="qcmenu"
           onmouseover="mcancelclosetime()"
           onmouseout="mclosetime()">
        <a href='javascript:void(0);' class="add"
           onclick="Pool.qc.insertPoolQCRow(${pool.id}); return false;">Add Pool QC</a>
      </div>
    </li>
  </ul>
  <span style="clear:both">
    <div id="addPoolQC"></div>
    <div id='addQcForm'>
      <table class="list" id="poolQcTable">
        <thead>
        <tr>
          <th>QCed By</th>
          <th>QC Date</th>
          <th>Method</th>
          <th>Results</th>
          <c:if test="${(library.securityProfile.owner.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                                or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
            <th align="center">Edit</th>
          </c:if>
        </tr>
        </thead>
        <tbody>
        <c:if test="${not empty pool.poolQCs}">
          <c:forEach items="${pool.poolQCs}" var="qc">
            <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
              <td>${qc.qcCreator}</td>
              <td><fmt:formatDate value="${qc.qcDate}"/></td>
              <td>${qc.qcType.name}</td>
              <td id="result${qc.id}">${qc.results} ${qc.qcType.units}</td>
              <c:if test="${(library.securityProfile.owner.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                                        or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
                <td id="edit${qc.id}" align="center"><a href="javascript:void(0);"
                                                        onclick="Pool.qc.changePoolQCRow('${qc.id}','${pool.id}')">
                  <span class="ui-icon ui-icon-pencil"></span></a></td>
              </c:if>
            </tr>
          </c:forEach>
        </c:if>
        </tbody>
      </table>
      <input type='hidden' id='qcPoolId' name='id' value='${pool.id}'/>
    </div>
    <script type="text/javascript">
      jQuery(document).ready(function () {
        jQuery("#poolQcTable").tablesorter({
          headers: {
          }
        });

        jQuery('#qcsTotalCount').html(jQuery('#poolQcTable>tbody>tr:visible').length.toString() + " QCs");
      });
    </script>
  </span>
</c:if>

<h1>Experiments</h1>

<div class="note">
  <h2>Selected experiment(s):</h2>

  <div id="exptlist" class="elementList ui-corner-all">
    <c:if test="${not empty pool.experiments}">
      <c:forEach items="${pool.experiments}" var="exp">
        <div onMouseOver="this.className='dashboardhighlight'" onMouseOut="this.className='dashboard'"
             class="dashboard">
          <span class='float-left'>
          <input type="hidden" id="experiments${exp.id}" value="${exp.id}" name="experiments"/>
          <b>Experiment:</b> <a href="<c:url value="/miso/experiment/${exp.id}"/>">${exp.alias} (${exp.name})</a><br/>
          <b>Description:</b> ${exp.description}<br/>
          <b>Project:</b> <a
              href="<c:url value="/miso/project/${exp.study.project.id}"/>">${exp.study.project.alias}
            (${exp.study.project.name})</a><br/>
          </span>
          <span onclick='Utils.ui.confirmRemove(jQuery(this).parent());'
                class='float-right ui-icon ui-icon-circle-close'></span>
        </div>
      </c:forEach>
    </c:if>
    <input type="hidden" value="on" name="_experiments"/>
  </div>
</div>
<h2 class="hrule">Select experiments:</h2>
<table class="in">
  <tr>
    <td width="30%" style="vertical-align:top">
      <label for="selectExpts"><b>Search experiments:</b></label><br/>
      <input type="text" id='selectExpts' name="selectExpts" value=""
             onKeyup="Utils.timer.timedFunc(Pool.search.poolSearchExperiments(this, 'ILLUMINA'),200);"/>

      <div id='exptresult'></div>
    </td>
  </tr>
</table>
<br/>

<h1>Pooled Elements</h1>

<div class="note">
  <h2>Selected elements(s):</h2>

  <div id="dillist" class="elementList ui-corner-all">
    <c:if test="${not empty pool.poolableElements}">
      <c:forEach items="${pool.poolableElements}" var="dil">
        <div onMouseOver="this.className='dashboardhighlight'" onMouseOut="this.className='dashboard'"
             class="dashboard">
          <span style="float:left" id="element${dil.id}">
          <input type="hidden" id="poolableElements${dil.id}" value="${dil.name}" name="poolableElements"/>
          <b>Element:</b> ${dil.name}<br/>

          <script type="text/javascript">
            jQuery(document).ready(function () {
              Pool.ui.getPoolableElementInfo('${pool.id}', '${dil.id}');
            });
          </script>
            <%-- TODO how to get round this?
          <c:catch var="dilcheck">${dil.emPCR}</c:catch>
          <c:if test="${empty dilcheck}">
            <b>emPCR:</b> ${dil.emPCR.name}<br/>
          </c:if>
          --%>

          <%--
          <b>Library:</b> <a href="<c:url value="/miso/library/${dil.library.id}"/>">${dil.library.alias} (${dil.library.name})</a><br/>
          <b>Sample:</b> <a href="<c:url value="/miso/sample/${dil.library.sample.id}"/>">${dil.library.sample.alias} (${dil.library.sample.name})</a><br/>
          <c:choose>
            <c:when test="${fn:length(pool.poolableElements) > 1}">
              <c:choose>
                <c:when test="${not empty dil.library.tagBarcodes}">
                  <b>Barcodes:</b></br>
                  <c:forEach items="${dil.library.tagBarcodes}" varStatus="status" var="barcodemap">
                    ${status.count}: ${barcodemap.value.name} (${barcodemap.value.sequence})
                    <c:if test="${status.count lt fn:length(dil.library.tagBarcodes)}">
                    <br/>
                    </c:if>
                  </c:forEach>
                  </span>
                  <span class="counter">
                    <img src="<c:url value='/styles/images/status/green.png'/>" border='0'>
                  </span>
                </c:when>
                <c:otherwise>
                  <b>Barcode:</b>
                  <a href="<c:url value="/miso/library/${dil.library.id}"/>">Choose tag barcode</a>
                  </span>
                  <span class="counter">
                    <img src="<c:url value='/styles/images/status/red.png'/>" border='0'>
                  </span>
                </c:otherwise>
              </c:choose>
            </c:when>
            <c:otherwise>
              </span>
            </c:otherwise>
          </c:choose>
          --%>
          </span>
          <span onclick='Utils.ui.confirmRemove(jQuery(this).parent());'
                class='float-right ui-icon ui-icon-circle-close'></span>
        </div>
      </c:forEach>
    </c:if>
  </div>
</div>
<input type="hidden" value="on" name="_poolableElements"/>
</form:form>

<h2 class="hrule">Select poolable elements:</h2>

<div id="elementSelectDatatableDiv">

</div>

<script type="text/javascript">
    jQuery(document).ready(function () {
        <c:choose>
        <c:when test="${pool.id != 0}">
        Pool.ui.createElementSelectDatatable('${pool.platformType.key}');
        </c:when>
        <c:otherwise>
        Pool.ui.createElementSelectDatatable('Illumina');
        </c:otherwise>
        </c:choose>
    });
</script>
<%--<table class="in">--%>
  <%--<tr>--%>
    <%--<td width="30%" style="vertical-align:top">--%>
      <%--<label for="searchElements"><b>Search poolable elements:</b></label><br/>--%>
      <%--&lt;%&ndash; <input type="text" id='ldiInput' name="ldiInput" value="" onKeyup="Utils.timer.timedFunc(poolSearchLibraryDilution(this, 'ILLUMINA'),200);"/> &ndash;%&gt;--%>
      <%--<input type="text" id='searchElements' name="searchElements"/>--%>

      <%--<div id='searchElementsResult'></div>--%>
    <%--</td>--%>
    <%--<td width="30%" style="vertical-align:top">--%>
      <%--<label for="ldiBarcodes"><b>Select elements by barcode(s):</b></label><br/>--%>
      <%--<textarea id="ldiBarcodes" name="ldiBarcodes" rows="6" cols="40"></textarea><br/>--%>
      <%--<button type="button" class="br-button ui-state-default ui-corner-all"--%>
              <%--onclick="Pool.ui.selectElementsByBarcodes(jQuery('#ldiBarcodes').val());">Select--%>
      <%--</button>--%>
      <%--<div id="importlist"></div>--%>
    <%--</td>--%>
    <%--<td width="30%" style="vertical-align:top">--%>
      <%--<b>Select elements by barcode file:</b><br/>--%>

      <%--<form method='post'--%>
            <%--id='ajax_upload_form'--%>
            <%--action="<c:url value="/miso/upload/dilution-to-pool"/>"--%>
            <%--enctype="multipart/form-data"--%>
            <%--target="target_upload"--%>
            <%--onsubmit="Pool.ui.dilutionFileUploadProgress();">--%>
        <%--<input type="file" name="file"/><br/>--%>
        <%--<button type="submit" class="br-button ui-state-default ui-corner-all">Upload</button>--%>
      <%--</form>--%>
      <%--<iframe id='target_upload' name='target_upload' src='' style='display: none'></iframe>--%>
      <%--<div id="statusdiv"></div>--%>
      <%--<div id="dilimportfile"></div>--%>
    <%--</td>--%>
  <%--</tr>--%>
<%--</table>--%>
</div>

<c:if test="${not empty pool.changeLog}">
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
      <c:forEach items="${pool.changeLog}" var="change">
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

<script type="text/javascript">
  Utils.ui.addMaxDatePicker("creationDate", 0);
  <%--<c:choose>--%>
  <%--<c:when test="${pool.id != 0}">--%>
  <%--Utils.timer.typewatchFunc(jQuery('#searchElements'), function () {--%>
    <%--Pool.search.poolSearchElements(jQuery('#searchElements'), '${pool.platformType.key}')--%>
  <%--}, 300, 2);--%>
  <%--</c:when>--%>
  <%--<c:otherwise>--%>
  <%--Utils.timer.typewatchFunc(jQuery('#searchElements'), function () {--%>
    <%--Pool.search.poolSearchElements(jQuery('#searchElements'), jQuery('#platformType').val())--%>
  <%--}, 300, 2);--%>
  <%--</c:otherwise>--%>
  <%--</c:choose>--%>
</script>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
