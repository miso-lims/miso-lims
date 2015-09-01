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

<div id="maincontent">
<div id="contentcolumn">
<form:form method="POST" commandName="pool" autocomplete="off">
<sessionConversation:insertSessionConversationId attributeName="pool"/>
<nav class="navbar navbar-default" role="navigation">
   <div class="navbar-header">
      <span class="navbar-brand navbar-center">
        <c:choose>
          <c:when test="${pool.id != 0}">Edit</c:when>
          <c:otherwise>Create</c:otherwise>
        </c:choose> Pool
      </span>
   </div>
   <div class="navbar-right container-fluid">
      <button type="button" class="btn btn-default navbar-btn" onclick="return validate_pool(this.form);">Save</button>
   </div>
</nav>

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
  <div id="note_arrowclick" class="toggleLeft"></div>
</div>
<div id="notediv" class="note" style="display:none;">A Pool contains <b>one or more</b> Dilutions or Plates that are
  to be placed, as part of an Experiment, in a sequencer instrument Run partition (lane/chamber/cell). Pools
  with more than one Dilution or a Plate with multiple libraries are said to be multiplexed.
</div>
<h2>Pool Information</h2>

<div class="barcodes">
  <div class="barcodeArea ui-corner-all">
    <span style="float: left; font-size: 24px; font-weight: bold; color:#BBBBBB">ID</span>
    <c:if test="${not empty pool.identificationBarcode}">
      <ul class="nav navbar-right">
        <li class="dropdown">
         <a id="idBarcode" class="dropdown-toggle" data-toggle="dropdown" href="javascript:void(0);">
           <i class="fa fa-caret-down"></i>
         </a>
         <ul class="dropdown-menu dropdown-tasks">
           <li role="presentation"><a href="javascript:void(0);" onclick="Pool.barcode.printPoolBarcodes(${pool.id});">Print</a></li>
         </ul>
       </li>
      </ul>
      <script type="text/javascript">
        jQuery(document).ready(function () {
          Fluxion.doAjax(
            'poolControllerHelperService',
            'getPoolBarcode',
            {'poolId':${pool.id},
              'url': ajaxurl
            },
            {'doOnSuccess': function (json) {
              jQuery('#idBarcode').prepend("<img style='height:30px; border:0; padding-right:4px' src='<c:url value='/temp/'/>" + json.img + "'/>");
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
    <td class="h">Pool Name:</td>
    <td>
      <c:choose>
        <c:when test="${pool.id != 0}">${pool.name}</c:when>
        <c:otherwise><i>Unsaved</i></c:otherwise>
      </c:choose>
    </td>
  </tr>
  <tr>
    <td class="h">Pool Alias:</td>
    <td><form:input path="alias" class="form-control"/></td>
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
    <td class="h">Desired Concentration</td>
    <td><form:input path="concentration" class="form-control"/></td>
  </tr>
  <tr>
    <td class="h">Creation Date:</td>
    <td><c:choose>
      <c:when test="${pool.id != 0}">
        <fmt:formatDate pattern="dd/MM/yy" type="both" value="${pool.creationDate}"/>
      </c:when>
      <c:otherwise><form:input path="creationDate" class="form-control"/></c:otherwise>
    </c:choose>
    </td>
  </tr>
  <tr>
    <td>QC Passed:</td>
    <td>
      <div id="qc-radio" class="btn-group" data-toggle="buttons">
        <label class='btn btn-default'><form:radiobutton path="qcPassed" value="" label="Unknown"/></label>
        <label class='btn btn-default'><form:radiobutton path="qcPassed" value="true" label="True"/></label>
        <label class='btn btn-default'><form:radiobutton path="qcPassed" value="false" label="False"/></label>
      </div>
      <script>
        jQuery(document).ready(function () {
          var c = jQuery('#qc-radio :input:checked');
          c.parent('.btn').addClass('active');
          var inpv = c.val();
          if (inpv === "") { c.parent('.btn').removeClass('btn-default').addClass("btn-warning"); }
          if (inpv === "true") { c.parent('.btn').removeClass('btn-default').addClass("btn-success"); }
          if (inpv === "false") { c.parent('.btn').removeClass('btn-default').addClass("btn-danger"); }
        });
      </script>
    </td>
  </tr>
  <tr>
    <td class="h">Ready To Run</td>
    <td><form:checkbox path="readyToRun"/></td>
  </tr>
</table>
<%@ include file="permissions.jsp" %>

<c:if test="${pool.id != 0}">
<div id="poolqcs" class="panel panel-default padded-panel">
  <nav id="navbar-qc" class="navbar navbar-default navbar-static" role="navigation">
    <div class="navbar-header">
      <span id="qcsTotalCount" class="navbar-brand navbar-center">Sample QCs</span>
    </div>
    <div class="collapse navbar-collapse bs-example-js-navbar-collapse">
      <ul class="nav navbar-nav navbar-right">
        <li id="qc-menu" class="dropdown">
          <a id="qcdrop1" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">Options <b class="caret"></b></a>
          <ul class="dropdown-menu dropdown-menu-right" role="menu" aria-labelledby="qcdrop1">
            <li role="presentation">
              <a href='javascript:void(0);' onclick="Pool.qc.insertPoolQCRow(${pool.id});">Add Pool QC</a>
            </li>
          </ul>
        </li>
      </ul>
    </div>
  </nav>

  <span style="clear:both">
    <div id="addPoolQC"></div>
    <div id='addQcForm'>
      <table class="table table-bordered table-striped" id="poolQcTable">
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
                  <span class="fa fa-pencil-square-o fa-lg"></span></a></td>
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
</div>
</c:if>

<div id="poolexps" class="panel panel-default padded-panel container-fluid">
  <nav id="navbar-exp" class="navbar navbar-default navbar-static" role="navigation">
    <div class="navbar-header">
      <span class="navbar-brand navbar-center">Experiments</span>
    </div>
  </nav>

  <div class="row-fluid">
    <div class="col-sm-12 col-md-6 small-pad">
      <div id="selected-exps-panel" class="panel panel-default panel-primary">
        <div class="panel-heading">
          <h3 class="panel-title">Selected Experiments</h3>
        </div>
        <div class="panel-body padded-panel">
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
                        class='fa fa-fw fa-2x fa-times-circle-o pull-right'></span>
                </div>
              </c:forEach>
            </c:if>
            <input type="hidden" value="on" name="_experiments"/>
          </div>
        </div>
      </div>
    </div>

    <div class="col-sm-12 col-md-6 small-pad">
      <div id="select-exps-panel" class="panel panel-default panel-primary">
        <div class="panel-heading">
          <h3 class="panel-title">Select Experiments</h3>
        </div>
        <div class="panel-body padded-panel">
          <table class="in">
            <tr>
              <td width="30%" style="vertical-align:top">
                <label for="selectExpts"><b>Search experiments:</b></label><br/>
                <input type="text" id='selectExpts' name="selectExpts" value="" class="form-control"
                       onKeyup="Utils.timer.timedFunc(Pool.search.poolSearchExperiments(this, 'ILLUMINA'),200);"/>
                <div id='exptresult'></div>
              </td>
            </tr>
          </table>
        </div>
      </div>
    </div>
  </div>
</div>

<div id="poolelems" class="panel panel-default padded-panel">
  <nav id="navbar-elems" class="navbar navbar-default navbar-static" role="navigation">
    <div class="navbar-header">
      <span class="navbar-brand navbar-center">Pooled Elements</span>
    </div>
  </nav>

  <div class="note">
    <h2>Selected elements(s):</h2>

    <div id="dillist" class="elementList ui-corner-all">
      <c:if test="${not empty pool.poolableElements}">
        <c:forEach items="${pool.poolableElements}" var="dil">
          <div id="element-wrapper-${dil.id}" class="dashboard">
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
            <span onclick='Utils.ui.confirmRemove(jQuery(this).parent());' class='fa fa-fw fa-2x fa-times-circle-o pull-right'></span>
          </div>
        </c:forEach>
      </c:if>
    </div>
  </div>
  <input type="hidden" value="on" name="_poolableElements"/>
  </form:form>

  <h2 class="hrule">Select poolable elements:</h2>

  <div id="elementSelectDatatableDiv"></div>

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
</div>

</div> <!-- contentcolumn -->
</div> <!-- maincontent -->

<script type="text/javascript">
  Utils.ui.addMaxDatePicker("creationDate", 0);
</script>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>