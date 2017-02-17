<%--
  ~ Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
  ~ MISO project contacts: Robert Davey @ TGAC
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
<script src="<c:url value='/scripts/datatables_utils.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/natural_sort.js'/>" type="text/javascript"></script>

<div id="maincontent">
<div id="contentcolumn">
<form:form id="pool-form" data-parsley-validate="" action="/miso/pool" method="POST" commandName="pool" autocomplete="off">
<sessionConversation:insertSessionConversationId attributeName="pool"/>
<h1><c:choose><c:when
    test="${pool.id != 0}">Edit</c:when><c:otherwise>Create</c:otherwise></c:choose> Pool
  <button type="button" onclick="return Pool.validatePool();" class="fg-button ui-state-default ui-corner-all">Save</button>
</h1>
<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
  <div id="note_arrowclick" class="toggleLeft"></div>
</div>
<div id="notediv" class="note" style="display:none;">A Pool contains <b>one or more</b> Dilutions that are
  to be placed, as part of an Experiment, in a sequencer instrument Run partition (lane/chamber/cell). Pools
  with more than one Dilution are said to be multiplexed.
</div>

<div class="bs-callout bs-callout-warning hidden">
  <h2>Oh snap!</h2>
  <p>This form seems to be invalid!</p>
</div>

<h2>Pool Information</h2>

<div class="barcodes">
  <div class="barcodeArea ui-corner-all">
    <span style="float: left; font-size: 24px; font-weight: bold; color:#BBBBBB">Barcode</span>
    <c:if test="${pool.id != 0}">
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
               onclick="Pool.barcode.showPoolIdBarcodeChangeDialog(${pool.id}, '${pool.identificationBarcode}');">Update Barcode</a>
            </c:if>
          </div>
        </li>
      </ul>
    </c:if>
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
    <td colspan="2">
    <c:if test="${pool.hasDuplicateIndices()}">
      <p style="font-size:200%; font-weight:bold">This pool contains duplicate indices!</p>
    </c:if>
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
    <td><form:input id="alias" path="alias"/><span id="aliasCounter" class="counter"></span></td>
  </tr>
  <tr>
    <td class="h">Description:</td>
    <td><form:input id="description" path="description"/><span id="descriptionCounter" class="counter"></span></td>
  </tr>
  <tr>
    <td>Platform Type:</td>
    <td>
      <c:choose>
        <c:when test="${pool.id != 0}">
          ${pool.platformType.key}
        </c:when>
        <c:otherwise>
          <form:select id="platformType" path="platformType" items="${platformTypes}"/>
        </c:otherwise>
      </c:choose>
    </td>
  </tr>
  <tr>
    <td class="h">Desired Concentration (${poolConcentrationUnits}):*</td>
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
    <c:choose>
    <c:when test="${pool.id != 0}">
      <td><form:checkbox path="readyToRun"/></td>
    </c:when>
    <c:otherwise>
      <td><form:checkbox path="readyToRun" checked="checked" /></td>
    </c:otherwise>
    </c:choose>
  </tr>
  
  <tr>
    <td>Volume (&#181;l):</td>
    <td><form:input id="volume" path="volume"/></td>
  </tr>
  <tr>
    <td>Discarded:</td>
    <td><form:checkbox id="discarded" path="discarded"/></td>
  </tr>
  <tr>
    <td class="h">Location:</td>
    <td>
      <c:if test="${!empty pool.box.locationBarcode}">${pool.box.locationBarcode},</c:if>
      <c:if test="${!empty pool.boxPosition}"><a href='<c:url value="/miso/box/${pool.box.id}"/>'>${pool.box.alias}, ${pool.boxPosition}</a></c:if>
    </td>
  </tr>
</table>

<%@ include file="volumeControl.jspf" %>
<%@ include file="permissions.jsp" %>
<br/>

<script type="text/javascript">
  jQuery(document).ready(function () {
    // Attach Parsley form validator
    Validate.attachParsley('#pool-form');
  });
</script>
</form:form>

<!--notes start -->
<c:if test="${pool.id != 0}">
    <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#notes_arrowclick'), 'notes');">Notes
      <div id="notes_arrowclick" class="toggleLeftDown"></div>
    </div>
    <div id="notes">
      <h1>Notes</h1>
      <ul class="sddm">
        <li>
          <a onmouseover="mopen('notesmenu')" onmouseout="mclosetime()">Options
            <span style="float:right" class="ui-icon ui-icon-triangle-1-s"></span>
          </a>

          <div id="notesmenu"
               onmouseover="mcancelclosetime()"
               onmouseout="mclosetime()">
            <a onclick="Pool.ui.showPoolNoteDialog(${pool.id});" href="javascript:void(0);" class="add">Add Note</a>
          </div>
        </li>
      </ul>
      <c:if test="${fn:length(pool.notes) > 0}">
        <div class="note" style="clear:both">
          <c:forEach items="${pool.notes}" var="note" varStatus="n">
            <div class="exppreview" id="pool-notes-${n.count}">
              <b>${note.creationDate}</b>: ${note.text}
              <span class="float-right" style="font-weight:bold; color:#C0C0C0;">${note.owner.loginName}</span>
                <c:if test="${(note.owner.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                                or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
                  <span style="color:#000000">
                    <a href='#' onclick="Pool.ui.deletePoolNote('${pool.id}', '${note.noteId}');">
                      <span class="ui-icon ui-icon-trash" style="clear: both; position: relative; float: right; margin-top: -15px;"></span>
                    </a>
                  </span>
                </c:if>
            </div>
          </c:forEach>
        </div>
      </c:if>
      <div id="addPoolNoteDialog" title="Create new Note"></div>
    </div>
    <br/>
</c:if>
<!-- notes end -->

<c:if test="${pool.id != 0}">
  <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#qcs_arrowclick'), 'qcs');">
    QCs
    <div id="qcs_arrowclick" class="toggleLeftDown"></div>
  </div>
  <div id="qcs" style="display:none;">
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

        var qcsCount = jQuery('#poolQcTable>tbody>tr:visible').length;
        jQuery('#qcsTotalCount').html(qcsCount + (qcsCount == 1 ? ' QC' : ' QCs'));
      });
    </script>
  </span>
  </div>
</c:if>

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#experiments_arrowclick'), 'experiments');")>
  Experiments
  <div id="experiments_arrowclick" class="toggleLeftDown"></div>
</div>
<div id="experiments" style="display:none;">
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
</div>
<br/>

<c:if test="${pool.id != 0}">
<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#orders_arrowclick'), 'orders');">
  Orders
  <div id="orders_arrowclick" class="toggleLeftDown"></div>
</div>
<div id="orders" style="display:block">
  <h1>Orders</h1>
  <span onclick="Pool.orders.createOrder()" class="sddm fg-button ui-state-default ui-corner-all">Add Order</span>

  <table cellpadding="0" cellspacing="0" border="0" class="display" id="edit-order-table"></table>

  <br/>
  <h1>Order Completion</h1>
  <table cellpadding="0" cellspacing="0" border="0" class="display" id="order-completion-table"></table>
</div>
  <br/>

</c:if>

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#pooled_arrowclick'), 'pooled');">
  Pooled Elements
  <div id="pooled_arrowclick" class="toggleLeftDown"></div>
</div>
<div id="pooled" style="display:block">
<h1>Pooled Elements</h1>
<c:choose>
<c:when test="${pool.id == 0}">
<p>Please save the pool before adding elements.</p>
</c:when>
<c:otherwise>
  <h2>Selected element(s):</h2>
  <div id="pooledList">
    <table cell-padding="0" width="100%" cellspacing="0" border="0" class="display" id="pooledElementsDatatable">
	  <thead>
	  <tr>
	    <th>Dilution Name</th>
	    <th>Concentration (${libraryDilutionUnits})</th>
	    <th>Library</th>
	    <th>Sample</th>
	    <th>Indices</th>
	    <th>Low Quality</th>
	    <th>Remove</th>
	  </tr>
	  </thead>
	  <tbody>
	    <c:if test="${not empty pool.poolableElements}">
		  <c:forEach items="${pool.poolableElements}" var="dil">
		    <tr id="pooled_${dil.name}">
		      <td>${dil.name}</td>
		      <td>${dil.concentration}</td>
		      <td><a href="<c:url value="/miso/library/${dil.library.id}"/>">${dil.library.alias} (${dil.library.name})</a></td>
		      <td><a href="<c:url value="/miso/sample/${dil.library.sample.id}"/>">${dil.library.sample.alias} (${dil.library.sample.name})</a></td>
		      <td><c:forEach items="${dil.library.indices}" var="index" varStatus="iCount">
		        <c:if test="${iCount.count gt 1}"><br/></c:if>${iCount.count}: ${index.label}
		        </c:forEach></td>
		      <td><c:if test="${dil.library.lowQuality}">&#9888;</c:if></td>
		      <td><span onclick='Pool.ui.removePooledElement(${pool.id}, ${dil.id}, "${dil.name}");' class="ui-icon ui-icon-circle-close ui-button"></span></td>
		    </tr>
		  </c:forEach>
	    </c:if>
	  </tbody>
    </table>
  </div>

  <script type="text/javascript">
    jQuery(document).ready(function () {
      jQuery('#pooledElementsDatatable').dataTable({
        "aaSorting": [
          [0, 'desc']              
        ],
        "aoColumns": [
          null,
          null,
          null,
          null,
          null,
          null,
          null
        ],
        "iDisplayLength": 50,
        "bJQueryUI": true,
        "bRetrieve": true,
        "sPaginationType": "full_numbers"
      });
    });
  </script>
  
  <h2 class="hrule">Select poolable elements:</h2>
  
  <div id="elementSelectDatatableDiv"></div>
  
  <script type="text/javascript">
      jQuery(document).ready(function () {
          Pool.ui.createElementSelectDatatable('${pool.platformType.key}', ${pool.id}, '${libraryDilutionUnits}');
      });
  </script>

  <script type="text/javascript">
    jQuery(document).ready(function () {
      jQuery('#runsDatatable').dataTable({
        "aaData": ${runsJSON},
        "aaSorting": [
          [0, 'desc']
        ],
        "aoColumns": [
          {
            "sTitle" : "Name",
            "mData" : "name",
            "mRender": function (data, type, full) {
              return "<a href=\"/miso/run/" + full.id + "\">" + data + "</a>";
            }
          },
          {
            "sTitle" : "Alias",
            "mData" : "alias",
            "mRender": function (data, type, full) {
              return "<a href=\"/miso/run/" + full.id + "\">" + data + "</a>";
            }
          },
          { "sTitle" : "Status", "mData" : "status" },
          { "sTitle" : "Start Date", "mData" : "startDate" },
          { "sTitle" : "End Date", "mData" : "endDate" },
          { "sTitle" : "Type", "mData" : "platformType" },
          { "sTitle" : "Last Modified", "mData" : "lastModified" }
        ],
        "iDisplayLength": 50,
        "bJQueryUI": true,
        "bRetrieve": true,
        "sPaginationType": "full_numbers"
      });
    });
  </script>
</c:otherwise>
</c:choose>
</div>

<c:if test="${pool.id != 0}">
<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#runs_arrowclick'), 'runs');">
  Runs
  <div id="runs_arrowclick" class="toggleLeftDown"></div>
</div>
<div id="runs" style="display:block;">
  <br/>
  <h1>Runs</h1>
  <div id="runsDatatableDiv">
      <table cell-padding="0" width="100%" cellspacing="0" border="0" class="display" id="runsDatatable">
      </table>
  </div>
</div>
</c:if>

<c:if test="${not empty pool.changeLog}">
  <br/>
  <h1>Changes</h1>
  <span style="clear:both">
    <table class="list" id="changelog_table">
      <thead>
      <tr>
        <th>Editor</th>
        <th>Summary</th>
        <th>Time</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${pool.changeLog}" var="change">
        <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
          <td>${change.user.fullName} (${change.user.loginName})</td>
          <td><b>${change.summary}</b></td>
          <td>${change.time}</td>
        </tr>
      </c:forEach>
      </tbody>
    </table>
  </span>
</c:if>

<div id="order-dialog" title="Order" hidden="true">
Partitions: <input type="text" name="partitions" value="1" id="orderPartitions" /><br/>
Platform: <select id="orderPlatformId" onchange="Pool.orders.changePlatform()"><c:forEach items="${platforms}" var="platform"><option value="${platform.id}">${platform.nameAndModel}</option></c:forEach></select><br/>
Sequencing Parameters: <select id="orderParameterId"></select>
</div>

</div>
</div>

<script type="text/javascript">
  Utils.ui.addMaxDatePicker("creationDate", 0);
  jQuery(document).ready(function () {
    jQuery('#alias').simplyCountable({
      counter: '#aliasCounter',
      countType: 'characters',
      maxCount: ${maxLengths['alias']},
      countDirection: 'down'
    });
    jQuery('#description').simplyCountable({
      counter: '#descriptionCounter',
      countType: 'characters',
      maxCount: ${maxLengths['description']},
      countDirection: 'down'
    });
  });
  Defaults = { 'all': {}};
  Defaults.all.sequencingParameters = ${sequencingParametersJson};
  Defaults.all.platforms = [ <c:forEach items="${platforms}" var="platform">{ 'id' : ${platform.id}, 'nameAndModel' : '${platform.nameAndModel}'}, </c:forEach> ];
  Pool.orders.makeTable(${pool.id});
</script>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
