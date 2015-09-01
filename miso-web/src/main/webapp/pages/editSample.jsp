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
  Date: 15-Feb-2010
  Time: 15:09:06
--%>
<%@ include file="../header.jsp" %>
<script src="<c:url value='/scripts/jquery/js/jquery.breadcrumbs.popup.js'/>" type="text/javascript"></script>

<!-- fileupload -->
<script src="<c:url value='/scripts/jquery/fileupload/js/jquery.fileupload.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/fileupload/js/jquery.fileupload-process.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/fileupload/js/jquery.fileupload-jquery-ui.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/fileupload/js/jquery.iframe-transport.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/fileupload/js/vendor/jquery.ui.widget.js'/>" type="text/javascript"></script>
<link href="<c:url value='/scripts/jquery/fileupload/css/jquery.fileupload.css'/>" rel="stylesheet" type="text/css">
<link href="<c:url value='/scripts/jquery/fileupload/css/jquery.fileupload-ui.css'/>" rel="stylesheet" type="text/css">

<script src="<c:url value='/scripts/stats_ajax.js?ts=${timestamp.time}'/>" type="text/javascript"></script>

<div id="maincontent">
<div id="contentcolumn">
<c:if test="${sample.id == 0 and not empty sample.project}">
<div id="tabs">
<ul>
  <li><a href="#tab-1"><span>Single</span></a></li>
  <li><a href="#tab-2"><span>Bulk</span></a></li>
</ul>

<div id="tab-1">
</c:if>

<form:form action="/miso/sample" method="POST" commandName="sample" autocomplete="off" acceptCharset="utf-8">
<sessionConversation:insertSessionConversationId attributeName="sample"/>
<nav class="navbar navbar-default" role="navigation">
  <div class="navbar-header">
    <span class="navbar-brand navbar-center">
    <c:choose>
      <c:when test="${sample.id != 0}">Edit</c:when>
      <c:otherwise>Create</c:otherwise>
    </c:choose> Sample
    </span>
  </div>
  <div class="navbar-right container-fluid">
    <button class="btn btn-default navbar-btn" href='javascript:void(0);' onclick="return validate_sample(this.form);">Save</button>
  </div>
</nav>

<c:if test="${not empty sample.project}">
  <div class="breadcrumbs">
    <ul>
      <li>
        <a href="/">Home</a>
      </li>
      <li>
        <%--
        <div class="breadcrumbsbubbleInfo">
          <div class="trigger">
            <a href='<c:url value="/miso/project/${sample.project.id}"/>'>${sample.project.name}</a>
          </div>
          <div class="breadcrumbspopup">
              ${sample.project.alias}
          </div>
        </div>
        --%>
        <a data-toggle="tooltip" data-placement="bottom" title="${sample.project.alias}" href='<c:url value="/miso/project/${sample.project.id}"/>'>${sample.project.name}</a>
      </li>
    </ul>
    <div class="btn-group pull-right">
      <c:if test="${not empty previousSample}">
        <a class='btn btn-default previous' href='<c:url value="/miso/sample/${previousSample.id}"/>'><i class="fa fa-arrow-circle-o-left"></i> Previous Sample <b>${previousSample.name}</b></a>
      </c:if>
      <c:if test="${not empty nextSample}">
        <a class='btn btn-default next' href='<c:url value="/miso/sample/${nextSample.id}"/>'>Next Sample <b>${nextSample.name}</b> <i class="fa fa-arrow-circle-o-right"></i></a>
      </c:if>
    </div>
  </div>
  <script>
    jQuery("[data-toggle=tooltip]").tooltip();
  </script>
</c:if>
<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
  <div id="note_arrowclick" class="toggleLeft"></div>
</div>
<div id="notediv" class="note" style="display:none;">A Sample contains information about the material upon which the
  sequencing experiments are to be based. Samples can be used in any number of sequencing Experiments in the form
  of a Library that is often processed further into pooled Dilutions.
</div>
<h2>Sample Information</h2>

<div class="barcodes">
  <div class="barcodeArea ui-corner-all" style="width:220px">
    <c:choose>
      <c:when test="${empty sample.locationBarcode}">
        <span style="float: left; font-size: 24px; font-weight: bold; color:#BBBBBB">Location</span>
        <form:input path="locationBarcode" size="8" class="form-control float-right"/>
      </c:when>
      <c:otherwise>
        <span style="float: left; font-size: 24px; font-weight: bold; color:#BBBBBB">Location</span>
        <ul class="barcode-ddm">
          <li>
            <a onmouseover="mopen('locationBarcodeMenu')" onmouseout="mclosetime()">
              <span style="float:right; margin-top:6px;" class="ui-icon ui-icon-triangle-1-s"></span>
              <span id="locationBarcode" style="float:right; margin-top:6px; padding-bottom: 11px;">${sample.locationBarcode}</span>
            </a>

            <div id="locationBarcodeMenu"
                 onmouseover="mcancelclosetime()"
                 onmouseout="mclosetime()">
              <a href="javascript:void(0);"
                 onclick="Sample.ui.showSampleLocationChangeDialog(${sample.id});">Change
                location</a>
            </div>
          </li>
        </ul>
        <div id="changeSampleLocationDialog" title="Change Sample Location"></div>
      </c:otherwise>
    </c:choose>
  </div>
  <div class="barcodeArea ui-corner-all">
    <span style="float: left; font-size: 24px; font-weight: bold; color:#BBBBBB">ID</span>
    <c:if test="${not empty sample.identificationBarcode}">
      <ul class="nav navbar-right">
        <li class="dropdown">
         <a id="idBarcode" class="dropdown-toggle" data-toggle="dropdown" href="javascript:void(0);">
           <i class="fa fa-caret-down"></i>
         </a>
         <ul class="dropdown-menu dropdown-tasks">
           <li role="presentation"><a href="javascript:void(0);" onclick="Sample.barcode.printSampleBarcodes(${sample.id});">Print</a></li>
         </ul>
       </li>
      </ul>
      <script type="text/javascript">
        jQuery(document).ready(function () {
          Fluxion.doAjax(
            'sampleControllerHelperService',
            'getSampleBarcode',
            {
              'sampleId':${sample.id},
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
  <div id="printServiceSelectDialog" title="Select a Printer"></div>
</div>
<div>
  <table class="in">
    <tr>
      <td class="h">Sample ID:</td>
      <td>
        <c:choose>
          <c:when test="${sample.id != 0}">${sample.id}</c:when>
          <c:otherwise><i>Unsaved</i></c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Project ID:</td>
      <c:choose>
        <c:when test="${empty sample.project}">
          <td>
            <div id="projectlist" class="checklist">
              <form:checkboxes items="${accessibleProjects}" path="project" itemValue="id"
                               itemLabel="name" onclick="Utils.ui.uncheckOthers('project', this);"/>
            </div>
          </td>
        </c:when>
        <c:otherwise>
          <td>
            <input type="hidden" value="${sample.project.id}" name="project" id="project"/>
            <a href='<c:url value="/miso/project/${sample.project.id}"/>'>${sample.project.name}</a>
          </td>
        </c:otherwise>
      </c:choose>
    </tr>
    <tr>
      <td>Name:</td>
      <td>
        <c:choose>
          <c:when test="${sample.id != 0}">${sample.name}</c:when>
          <c:otherwise><i>Unsaved</i></c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td class="h">Alias:</td>
      <td>
        <div class="input-group">
          <form:input path="alias" class="validateable form-control"/><span id="aliascounter" class="input-group-addon"></span>
        </div>
      </td>
    </tr>
    <tr>
      <td>Description:</td>
      <td>
        <div class="input-group">
          <form:input path="description" class="validateable form-control"/><span id="descriptioncounter" class="input-group-addon"></span>
        </div>
      </td>
    </tr>
    <tr>
      <td>Date of receipt:</td>
      <td>
        <form:input path="receivedDate" id="receiveddatepicker" class="form-control"/>
        <script type="text/javascript">
          Utils.ui.addDatePicker("receiveddatepicker");
        </script>
      </td>
    </tr>
    <tr>
      <td class="h">Scientific Name:</td>
      <td><form:input path="scientificName" class="form-control"/>
        <c:if test="${sessionScope.taxonLookupEnabled}">
        <script>Utils.timer.typewatchFunc(jQuery('#scientificName'), validate_ncbi_taxon, 1000, 2);</script>
      </td>
      </c:if>
        <%--<td><a href="void(0);" onclick="popup('help/sampleScientificName.html');">Help</a></td>--%>
    </tr>
    <c:if test="${not empty sample.accession}">
      <tr>
        <td class="h">Accession:</td>
        <td><a href="http://www.ebi.ac.uk/ena/data/view/${sample.accession}">${sample.accession}</a>
        </td>
          <%--<td><a href="void(0);" onclick="popup('help/sampleAccession.html');">Help</a></td>--%>
      </tr>
    </c:if>
    <tr>
      <td>Sample Type:</td>
      <td><form:select id="sampleTypes" path="sampleType" items="${sampleTypes}"/></td>
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
  <c:choose>
    <c:when test="${!empty sample.project and sample.securityProfile.profileId eq sample.project.securityProfile.profileId}">
      <tr>
        <td>Permissions</td>
        <td><i>Inherited from project </i>
          <a href='<c:url value="/miso/project/${sample.project.id}"/>'>${sample.project.name}</a>
          <input type="hidden" value="${sample.project.securityProfile.profileId}"
                 name="securityProfile" id="securityProfile"/>
        </td>
      </tr>
    </table>
    </c:when>
    <c:otherwise>
    </table>
    <%@ include file="permissions.jsp" %>
    </c:otherwise>
  </c:choose>
</div>
</form:form>

<c:if test="${sample.id != 0}">
  <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#upload_arrowclick'), 'uploaddiv');">
    Sample Files
    <div id="upload_arrowclick" class="toggleLeft"></div>
  </div>
  <div id="uploaddiv" class="panel panel-default padded-panel" style="display:none;">
    <table class="in">
      <tr>
        <td>
          <span id="upload-area">
            <div class="fileupload-buttonbar">
              <div class="fileupload-buttonbar">
                <div>
                  <span class="btn btn-success fileinput-button">
                    <i class="glyphicon glyphicon-plus"></i>
                    <span>Add files...</span>
                    <input id="ajax_upload_form" type="file" name="files[]" data-url="<c:url value="/miso/upload/sample"/>" multiple/>
                  </span>
                </div>
              </div>
            </div>
            <div id="progress" class="progress">
              <div class="progress-bar progress-bar-success"></div>
            </div>

            <div id="selectedFiles" class="files"></div>
          </span>

          <script>
            jQuery('#ajax_upload_form').fileupload({
              formData: {'sampleId': '${sample.id}'},
              dataType: 'json',
              done: function (e, data) {
                jQuery.each(data.result.files, function (index, file) {
                  var r = "<a href='"+file.url+"'><a class='listbox' href='"+file.url+"'><div onMouseOver='this.className=\"boxlistboxhighlight\"' onMouseOut='this.className=\"boxlistbox\"' class='boxlistbox'>"+file.name+"</div></a></a>";
                  jQuery(r).prependTo('#samplefiles');
                });

                //reset progress
                jQuery('#progress .progress-bar').css('width', '0%');
              },
              progress: function (e, data) {
                var progress = parseInt(data.loaded / data.total * 100, 10);
                jQuery('#progress .progress-bar').css('width', progress + '%');
              }
            }).prop('disabled', !jQuery.support.fileInput)
              .parent().addClass(jQuery.support.fileInput ? undefined : 'disabled');
          </script>
        </td>
      </tr>
    </table>

    <div id="samplefiles">
      <c:forEach items="${sampleFiles}" var="file">
        <div id="file${file.key}">
          <div onMouseOver="this.className='boxlistboxhighlight'" onMouseOut="this.className='boxlistbox'" class="boxlistbox">
            <a href="<c:url value='/miso/download/sample/${sample.id}/${file.key}'/>">${file.value}</a>
            <c:if test="${(library.securityProfile.owner.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                            or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
            <a href='#' onclick="Sample.ui.deleteSampleFile('${sample.id}', '${file.value}', '${file.key}');">
              <i class="fa fa-trash-o fa-lg fa-fw pull-right" style="padding-top:4px"></i>
            </a>
            </c:if>
          </div>
        </div>
      </c:forEach>
    </div>
  </div>

  <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#notes_arrowclick'), 'notes');">Notes
    <c:choose>
      <c:when test="${fn:length(sample.notes) > 0}">
        <div id="notes_arrowclick" class="toggleLeftDown"></div>
      </div>
      <div id="notes" class="panel panel-default padded-panel">
      </c:when>
      <c:otherwise>
        <div id="notes_arrowclick" class="toggleLeft"></div>
      </div>
      <div id="notes" class="panel panel-default padded-panel" style="display:none">
      </c:otherwise>
    </c:choose>

    <nav id="navbar-notes" class="navbar navbar-default navbar-static" role="navigation">
      <div class="navbar-header">
        <span class="navbar-brand navbar-center">Notes</span>
      </div>
      <div class="collapse navbar-collapse">
        <ul class="nav navbar-nav navbar-right">
          <%--<li><a href="#">Link</a></li>--%>
          <li id="notes-menu" class="dropdown">
            <a id="notedrop1" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">Options <b class="caret"></b></a>
            <ul class="dropdown-menu dropdown-menu-right" role="menu" aria-labelledby="notedrop1">
              <li role="presentation">
                <a role="menuitem" onclick="Sample.ui.showSampleNoteDialog(${sample.id});" href="javascript:void(0);">Add Note</a>
              </li>
            </ul>
          </li>
        </ul>
      </div>
    </nav>
  <c:if test="${fn:length(sample.notes) > 0}">
    <div class="note" style="clear:both">
      <c:forEach items="${sample.notes}" var="note" varStatus="n">
        <div class="exppreview" id="sample-notes-${n.count}">
          <b>${note.creationDate}</b>: ${note.text}
          <span class="float-right" style="font-weight:bold; color:#C0C0C0;">${note.owner.loginName}
            <c:if test="${(project.securityProfile.owner.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                            or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
            <span style="color:#000000"><a href='#' onclick="Sample.ui.deleteSampleNote('${sample.sampleId}', '${note.noteId}');">
              <i class="fa fa-trash-o fa-fw"></i></a></span>
            </c:if>
          </span>
        </div>
        </c:forEach>
      </div>
    </c:if>
    <div id="addSampleNoteDialog" title="Create new Note"></div>
  </div>
</c:if>

<c:if test="${sample.id != 0}">
  <div id="sampleqc-div" class="panel panel-default padded-panel">
    <a name="sampleqc"></a>

    <nav id="navbar-sampleqc" class="navbar navbar-default navbar-static" role="navigation">
      <div class="navbar-header">
        <span id="qcsTotalCount" class="navbar-brand navbar-center">Sample QCs</span>
      </div>
      <div class="collapse navbar-collapse bs-example-js-navbar-collapse">
        <ul class="nav navbar-nav navbar-right">
          <li id="sampleqc-menu" class="dropdown">
            <a id="sampleqcdrop1" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">Options <b class="caret"></b></a>
            <ul class="dropdown-menu dropdown-menu-right" role="menu" aria-labelledby="sampleqcdrop1">
              <li role="presentation">
                <a onclick="Sample.qc.generateSampleQCRow(${sample.id});" href='javascript:void(0);'>Add Sample QC</a>
              </li>
            </ul>
          </li>
        </ul>
      </div>
    </nav>

    <span style="clear:both">
      <div id="addSampleQC"></div>
      <form id='addQcForm'>
        <table class="table table-striped table-bordered" id="sampleQcTable">
          <thead>
          <tr>
              <%--
              <sec:authorize access="hasRole('ROLE_ADMIN')">
                  <th class="fit">ID</th>
              </sec:authorize>
              --%>
            <th>QCed By</th>
            <th>QC Date</th>
            <th>Method</th>
            <th>Results</th>
            <c:if test="${(sample.securityProfile.owner.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                                  or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
              <th align="center">Edit</th>
            </c:if>
          </tr>
          </thead>
          <tbody>
          <c:if test="${not empty sample.sampleQCs}">
            <c:forEach items="${sample.sampleQCs}" var="qc">
              <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
                  <%--
                  <sec:authorize access="hasRole('ROLE_ADMIN')">
                      <td class="fit">${qc.qcId}</td>
                  </sec:authorize>
                  --%>
                <td>${qc.qcCreator}</td>
                <td><fmt:formatDate value="${qc.qcDate}"/></td>
                <td>${qc.qcType.name}</td>
                <td id="results${qc.id}">${qc.results} ${qc.qcType.units}</td>
                <c:if test="${(sample.securityProfile.owner.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                                          or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
                  <td id="edit${qc.id}" align="center"><a href="javascript:void(0);"
                                                          onclick="Sample.qc.changeSampleQCRow('${qc.id}','${sample.id}')">
                    <span class="fa fa-pencil-square-o fa-lg"></span></a></td>
                </c:if>
              </tr>
            </c:forEach>
          </c:if>
          </tbody>
        </table>
        <input type='hidden' id='sampleId' name='id' value='${sample.id}'/>
      </form>
    </span>
    <script type="text/javascript">
      jQuery(document).ready(function () {
        jQuery('#sampleQcTable').dataTable({
          "aaSorting": [
            [2, 'asc']
          ],
          "aoColumns": [
            null,
            { "sType": 'natural' },
            null,
            null
            <sec:authorize access="hasRole('ROLE_ADMIN')">, null</sec:authorize>
          ],
          "iDisplayLength": 50,
          "bJQueryUI": false,
          "bRetrieve": true
        });

        jQuery('#qcsTotalCount').html(jQuery('#sampleQcTable>tbody>tr:visible').length.toString() + " QCs");
        jQuery('#librariesTotalCount').html(jQuery('#library_table>tbody>tr:visible').length.toString() + " Libraries");
      });
    </script>
  </div>

  <div id="library-div" class="panel panel-default padded-panel">
    <a name="library"></a>

    <nav id="navbar-library" class="navbar navbar-default navbar-static" role="navigation">
      <div class="navbar-header">
        <span id="librariesTotalCount" class="navbar-brand navbar-center">Libraries</span>
      </div>
      <div class="collapse navbar-collapse bs-example-js-navbar-collapse">
        <ul class="nav navbar-nav navbar-right">
          <li id="library-menu" class="dropdown">
            <a id="librarydrop1" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">Options <b class="caret"></b></a>
            <ul class="dropdown-menu dropdown-menu-right" role="menu" aria-labelledby="librarydrop1">
              <li role="presentation"><a href='<c:url value="/miso/library/new/${sample.id}"/>'>Add Library</a></li>
              <c:if test="${not empty sample.libraries}">
              <li role="presentation"><a href='javascript:void(0);' onclick='bulkLibraryQcTable();'>Bulk QC these Libraries</a></li>
              <li role="presentation"><a href='javascript:void(0);' onclick='bulkLibraryDilutionTable();'>Bulk Add Library Dilutions</a></li>
              </c:if>
            </ul>
          </li>
        </ul>
      </div>
    </nav>
      <span style="clear:both">
        <table class="table table-striped table-bordered" id="library_table">
          <thead>
          <tr>
            <th>Library Name</th>
            <th>Library Alias</th>
            <th>Library Type</th>
            <th>QC Passed</th>
            <th class="fit">Edit</th>
          </tr>
          </thead>
          <tbody>
          <c:forEach items="${sample.libraries}" var="library">
            <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
              <td><b>${library.name}</b></td>
              <td>${library.alias}</td>
              <td>${library.libraryType.description}</td>
              <td>${library.qcPassed}</td>
              <td class="misoicon"
                  onclick="window.location.href='<c:url value="/miso/library/${library.id}"/>'"><span
                  class="fa fa-pencil-square-o fa-lg"/></td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
      </span>
    <script type="text/javascript">
      jQuery(document).ready(function () {
        jQuery('#library_table').dataTable({
          "aaSorting": [
            [1, 'asc']
          ],
          "aoColumns": [
            null,
            { "sType": 'natural' },
            null,
            null,
            null
          ],
          "iDisplayLength": 50,
          "bJQueryUI": false,
          "bRetrieve": true
        });
      });
    </script>
  </div>

  <c:if test="${not empty samplePools}">
  <div id="pool-div" class="panel panel-default padded-panel">
    <nav id="navbar-pool" class="navbar navbar-default navbar-static" role="navigation">
      <div class="navbar-header">
        <span class="navbar-brand navbar-center">${fn:length(samplePools)} Pools</span>
      </div>
      <div class="collapse navbar-collapse bs-example-js-navbar-collapse">
        <ul class="nav navbar-nav navbar-right">
          <li id="pool-menu" class="dropdown">
            <a id="pooldrop1" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">Options <b class="caret"></b></a>
            <ul class="dropdown-menu dropdown-menu-right" role="menu" aria-labelledby="pooldrop1">
              <li role="presentation">
                <a href="javascript:void(0);" onclick="Pool.barcode.selectPoolBarcodesToPrint('#pools_table');">Print Barcodes</a>
              </li>
            </ul>
          </li>
        </ul>
      </div>
    </nav>
    <span style="clear:both">
      <table class="table table-striped table-bordered" id="pools_table">
        <thead>
        <tr>
          <th>Pool Name</th>
          <th>Pool Alias</th>
          <th>Pool Platform</th>
          <th>Pool Creation Date</th>
          <th>Pool Concentration</th>
          <th class="fit">Edit</th>
          <sec:authorize access="hasRole('ROLE_ADMIN')">
            <th class="fit">DELETE</th>
          </sec:authorize>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${samplePools}" var="pool">
          <tr poolId="${pool.id}" onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
            <td><b>${pool.name}</b></td>
            <td>${pool.alias}</td>
            <td>${pool.platformType.key}</td>
            <td>${pool.creationDate}</td>
            <td>${pool.concentration}</td>
              <%-- <td class="misoicon" onclick="window.location.href='<c:url value="/miso/pool/${fn:toLowerCase(pool.platformType.key)}/${pool.id}"/>'"><span class="fa fa-pencil-square-o fa-lg"/></td> --%>
            <td class="misoicon" onclick="window.location.href='<c:url value="/miso/pool/${pool.id}"/>'">
              <span class="fa fa-pencil-square-o fa-lg"/>
            </td>
            <sec:authorize access="hasRole('ROLE_ADMIN')">
              <td class="misoicon" onclick="Pool.deletePool(${pool.id}, Utils.page.pageReload);">
                <span class="fa fa-trash-o fa-lg"/>
              </td>
            </sec:authorize>
          </tr>
        </c:forEach>
        </tbody>
      </table>
      <script type="text/javascript">
        jQuery(document).ready(function () {
          jQuery('#pools_table').dataTable({
            "aaSorting": [
              [1, 'asc'],
              [3, 'asc']
            ],
            "aoColumns": [
              null,
              { "sType": 'natural' },
              null,
              null,
              null,
              null
              <sec:authorize access="hasRole('ROLE_ADMIN')">, null</sec:authorize>
            ],
            "iDisplayLength": 50,
            "bJQueryUI": false,
            "bRetrieve": true
          });
        });
      </script>
    </span>
  </div>
  </c:if>

  <c:if test="${not empty sampleRuns}">
  <div id="run-div" class="panel panel-default padded-panel">
    <nav id="navbar-run" class="navbar navbar-default navbar-static" role="navigation">
      <div class="navbar-header">
        <span class="navbar-brand navbar-center">${fn:length(sampleRuns)} Runs</span>
      </div>
    </nav>

    <table class="table table-striped table-bordered" id="run_table">
      <thead>
      <tr>
        <th>Run Name</th>
        <th>Run Alias</th>
        <th>Partitions</th>
        <th class="fit">Edit</th>
        <sec:authorize access="hasRole('ROLE_ADMIN')">
          <th class="fit">DELETE</th>
        </sec:authorize>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${sampleRuns}" var="run" varStatus="runCount">
        <tr runId="${run.id}" onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
          <td><b>${run.name}</b></td>
          <td>${run.alias}</td>
          <td>
            <c:forEach items="${run.sequencerPartitionContainers}" var="container" varStatus="fCount">
              <table class="containerSummary">
                <tr>
                  <c:forEach items="${container.partitions}" var="partition">
                    <td id="partition${runCount.count}_${fCount.count}_${partition.partitionNumber}"
                        class="smallbox">${partition.partitionNumber}</td>
                    <c:if test="${not empty poolSampleMap[partition.pool.id]}">
                      <script type="text/javascript">
                        jQuery(document).ready(function () {
                          jQuery('#partition${runCount.count}_${fCount.count}_${partition.partitionNumber}').addClass("partitionOccupied");
                          jQuery('#partition${runCount.count}_${fCount.count}_${partition.partitionNumber}').prop("title", "${partition.pool.name}");
                          <c:if test="${metrixEnabled}">
                            jQuery('#partition${runCount.count}_${fCount.count}_${partition.partitionNumber}').click(function() {
                              //TODO open colorbox with SAV style plots in
                              Stats.getInterOpMetricsForLane('${run.alias}', '${partition.pool.platformType}', '${partition.partitionNumber}');
                            });
                          </c:if>
                        });
                      </script>
                    </c:if>
                  </c:forEach>
                </tr>
              </table>
              <c:if test="${fn:length(run.sequencerPartitionContainers) > 1}">
                <br/>
              </c:if>
            </c:forEach>
          </td>
          <td class="misoicon" onclick="window.location.href='<c:url value="/miso/run/${run.id}"/>'">
            <span class="fa fa-pencil-square-o fa-lg"/>
          </td>
          <sec:authorize access="hasRole('ROLE_ADMIN')">
            <td class="misoicon" onclick="Run.deleteRun(${run.id}, Utils.page.pageReload);">
              <span class="fa fa-trash-o fa-lg"/>
            </td>
          </sec:authorize>
        </tr>
      </c:forEach>
      </tbody>
    </table>
    <script type="text/javascript">
      jQuery(document).ready(function () {
        jQuery('#run_table').dataTable({
          "aaSorting": [
            [0, 'asc'],
            [1, 'asc']
          ],
          "aoColumns": [
            null,
            null,
            null,
            null
            <sec:authorize access="hasRole('ROLE_ADMIN')">, null</sec:authorize>
          ],
          "iDisplayLength": 50,
          "bJQueryUI": false,
          "bRetrieve": true
        });
      });
    </script>
  </div>
  </c:if>
</c:if>

<c:if test="${sample.id == 0 and not empty sample.project}">
</div>
<div id="tab-2" align="center">
  <div class="breadcrumbs">
    <ul>
      <li>
        <a href="/">Home</a>
      </li>
      <li>
        <div class="breadcrumbsbubbleInfo">
          <div class="trigger">
            <a href='<c:url value="/miso/project/${sample.project.id}"/>'>${sample.project.name}</a>
          </div>
          <div class="breadcrumbspopup">
              ${sample.project.alias}
          </div>
        </div>
      </li>
    </ul>
  </div>
  <nav class="navbar navbar-default" role="navigation">
     <div class="navbar-header">
        <span class="navbar-brand navbar-center">
          Create Samples
        </span>
     </div>
     <div class="navbar-right container-fluid">
        <button type="button" class="btn btn-default navbar-btn" id="bulkSampleButton" onClick="submitBulkSamples();">Save</button>
     </div>
  </nav>

  <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#options_arrowclick'), 'optionsdiv');">Table
    Options
    <div id="options_arrowclick" class="toggleLeft"></div>
  </div>
  <div id="optionsdiv" class="note" style="display:none;">
    <input type="checkbox" name="autoIncrementSampleAlias" checked="checked"/>Increment Sample Aliases
    Automatically<br/>
  </div>
  <table id="cinput" class="table table-striped table-bordered display">
    <thead>
    <tr>
      <th>Sample Alias</th>
      <th>Description</th>
      <th>Scientific Name</th>
      <th>Receipt Date</th>
      <th>Type</th>
        <%-- <th>ID Barcode</th> --%>
      <th>Location Barcode</th>
      <th>Notes</th>
      <th>Copy</th>
      <th>Delete</th>
    </tr>
    </thead>
    <tbody>
    </tbody>
  </table>
  <div id="pager"></div>
</div>
</div>

<script type="text/javascript">
var sampleheaders = ['alias', 'description', 'scientificName', 'receivedDate', 'sampleType', 'locationBarcode', 'note'];
jQuery(document).ready(function () {
  var oTable = jQuery('#cinput').dataTable({
    "aoColumnDefs": [
      {
        "bUseRendered": false,
        "aTargets": [ 0 ]
      }
    ],
    "bPaginate": false,
    "bInfo": false,
    "bJQueryUI": false,
    "bAutoWidth": true,
    "bSort": false
  });
  //jQuery("div.toolbar").html("<a onclick=\"fnClickAddRow();\" href=\"javascript:void(0);\">Add a new row</a>");

  jQuery("#cinput_wrapper").prepend("<div class='float-right toolbar'></div>");
  //jQuery("div.toolbar").parent().addClass("fg-toolbar ui-toolbar ui-widget-header ui-corner-tl ui-corner-tr ui-helper-clearfix");
  jQuery("div.toolbar").html("<button onclick=\"bulkCopySample();\" class=\"fg-button ui-state-default ui-corner-all\"><span class=\"add\">Bulk Copy</span></button> <button onclick=\"fnClickAddRow();\" class=\"fg-button ui-state-default ui-corner-all\"><span class=\"add\">Add Row</span></button>");
  //setEditables(oTable);

  jQuery("#tabs").tabs();
  jQuery("#tabs").removeClass('ui-widget').removeClass('ui-widget-content');
});

function bulkCopySample() {
  var table = jQuery('#cinput').dataTable();
  var numrows = table.fnGetNodes().length;
  var nodes = table.fnGetNodes();

  if (numrows > 0) {
    var copynumer = prompt("Note: This will copy the LAST row of table. \nPlease enter the copy number of samples to add ", "1");


    var obj = {};
    for (var j = 0; j < (nodes[numrows - 1].cells.length) - 1; j++) {
      obj[sampleheaders[j]] = jQuery(nodes[numrows - 1].cells[j]).text();
    }

    if (Utils.validation.isNullCheck(obj.alias) ||
        Utils.validation.isNullCheck(obj.description) ||
        Utils.validation.isNullCheck(obj.scientificName) ||
        Utils.validation.isNullCheck(obj.sampleType)) {
      ok = false;
      jQuery(nodes[numrows - 1]).css('background', '#EE9966');
    }
    else {
      for (var i = 0; i < copynumer - 1; i++) {
        copyRow(numrows - 1);
      }
    }
  }
  else {
    alert("Please Enter some Sample Data first to Bulk Copy");
  }
}

function fnClickAddRow(rowdata) {
  var table = jQuery('#cinput').dataTable();
  var a = [];
  if (rowdata && rowdata.length > 0) {
    a = table.fnAddData(rowdata);
  }
  else {
    a = table.fnAddData(
      [ "",
        "",
        "",
        "",
        "",
        "",
        "",
        "<span style='text-align:center;' name='copy-button' class='ui-icon ui-icon-arrowrefresh-1-n' title='Copy row'></span>",
        "<span style='text-align:center;' name='del-button' class='fa fa-trash-o fa-lg' title='Delete row'></span>"
      ]
    );
  }

  var tr = table.fnGetNodes(a[0]);
  jQuery(tr).find("span[name='copy-button']").each(function () {
    jQuery(this).click(function () {
      copyRow(table.fnGetPosition(tr));
    });
  });

  jQuery(tr).find("span[name='del-button']").each(function () {
    jQuery(this).click(function () {
      if (confirm("Are you sure you want to delete row " + table.fnGetPosition(tr) + "?")) {
        table.fnDeleteRow(table.fnGetPosition(tr));
      }
    });
  });

  var nTr = table.fnSettings().aoData[a[0]].nTr;
  for (var i = 0; i < sampleheaders.length; i++) {
    jQuery(nTr.cells[i]).attr("name", sampleheaders[i]);
    if (sampleheaders[i] === "sampleType") {
      jQuery(nTr.cells[i]).addClass("sampleSelect");
    }
    else if (sampleheaders[i] === "receivedDate") {
      jQuery(nTr.cells[i]).addClass("dateSelect");
    }
    else {
      jQuery(nTr.cells[i]).addClass("defaultEditable");
    }
  }

  setEditables(table);
}

function copyRow(row) {
  DatatableUtils.collapseInputs('#cinput');

  var table = jQuery('#cinput').dataTable();
  var numrows = table.fnGetNodes().length;
  var lastRow = table.fnGetData(numrows - 1);
  var rowToCopy = table.fnGetData(row);
  if (jQuery("input[name=autoIncrementSampleAlias]").length > 0 && jQuery('input[name=autoIncrementSampleAlias]').is(':checked')) {
    var re = new RegExp(/[A-z0-9]+_S([0-9]+)_[\s\S]*/);
    if (rowToCopy[0].match(re) && lastRow[0].match(re)) {
      rowToCopy[0] = lastRow[0].replace(/_S([\d]+)_/, function (a, b) {
        return "_S" + (parseInt(b, 10) + 1) + "_";
      });
    }
  }
  fnClickAddRow(rowToCopy);
}

function setEditables(datatable) {
  //jQuery('td:not(:eq(8)):not(:eq(3)):not(:eq(4))', datatable.fnGetNodes()).editable(function(value, settings) {
  //jQuery('td .defaultEditable', datatable.fnGetNodes()).editable(function(value, settings) {
  jQuery('.defaultEditable').editable(function (value, settings) {
    return value;
  },
  {
    callback: function (sValue, y) {
      var aPos = datatable.fnGetPosition(this);
      datatable.fnUpdate(sValue, aPos[0], aPos[1]);
    },
    submitdata: function (value, settings) {
      return {
        "row_id": this.parentNode.getAttribute('id'),
        "column": datatable.fnGetPosition(this)[2]
      };
    },
    onblur: 'submit',
    placeholder: '',
    height: '14px'
  });

  jQuery(".sampleSelect").editable(function (value, settings) {
    return value;
  },
  {
    data: '{${sampleTypesString}}',
    type: 'select',
    onblur: 'submit',
    placeholder: '',
    style: 'inherit',
    callback: function (sValue, y) {
      var aPos = datatable.fnGetPosition(this);
      datatable.fnUpdate(sValue, aPos[0], aPos[1]);
    },
    submitdata: function (value, settings) {
      return {
        "row_id": this.parentNode.getAttribute('id'),
        "column": datatable.fnGetPosition(this)[2]
      };
    }
  });

  jQuery(".dateSelect").editable(function (value, settings) {
    return value;
  },
  {
    type: 'datepicker',
    width: '100px',
    onblur: 'submit',
    placeholder: '',
    style: 'inherit',
    datepicker: {
      dateFormat: 'dd/mm/yy',
      showButtonPanel: true,
      maxDate: 0
    },
     callback: function (sValue, y) {
      var aPos = datatable.fnGetPosition(this);
      datatable.fnUpdate(sValue, aPos[0], aPos[1]);
    },
    submitdata: function (value, settings) {
      return {
        "row_id": this.parentNode.getAttribute('id'),
        "column": datatable.fnGetPosition(this)[2]
      };
    }
  });

  jQuery('.defaultEditable').bind('keydown', function (evt) {
    if (evt.keyCode == 9) {
      /* Submit the current element */
      jQuery('input', this)[0].blur();

      /* Activate the next element */
      if (jQuery(this).next('.defaultEditable').length == 1) {
        jQuery(this).next('.defaultEditable').click();
      }
      else if (jQuery('.defaultEditable', jQuery(this.parentNode).next()).length > 0) {
        jQuery('.defaultEditable:eq(0)', jQuery(this.parentNode).next()).click();
      }
      return false;
    }
  });
}

function submitBulkSamples() {
  jQuery('#bulkSampleButton').attr('disabled', 'disabled');
  jQuery('#bulkSampleButton').html("Processing...");

  DatatableUtils.collapseInputs('#cinput');

  var table = jQuery('#cinput').dataTable();
  var nodes = table.fnGetNodes();
  var ok = true;
  var arr = [];
  for (var i = 0; i < nodes.length; i++) {
    var obj = {};
    for (var j = 0; j < (nodes[i].cells.length) - 1; j++) {
      obj[sampleheaders[j]] = jQuery(nodes[i].cells[j]).text();
    }

    if (Utils.validation.isNullCheck(obj.alias) ||
        Utils.validation.isNullCheck(obj.description) ||
        Utils.validation.isNullCheck(obj.scientificName) ||
        Utils.validation.isNullCheck(obj.sampleType)) {
      ok = false;
      jQuery(nodes[i]).css('background', '#EE9966');
    }
    else {
      jQuery(nodes[i]).css('background', '#CCFF99');
      arr.push(JSON.stringify(obj));
    }
  }

  if (ok) {
    Fluxion.doAjax(
      'sampleControllerHelperService',
      'bulkSaveSamples',
      {'projectId':${sample.project.id},
        'samples': "[" + arr.join(',') + "]",
        'url': ajaxurl
      },
      {'doOnSuccess': function (json) {
        var taxonErrorSamples = json.taxonErrorSamples;
        var savedSamples = json.savedSamples;
        if (savedSamples.length == nodes.length) {
          if (taxonErrorSamples.length > 0) {
            table.find("tr:gt(0)").each(function () {
              for (var j = 0; j < taxonErrorSamples.length; j++) {
                if (jQuery(this.cells[0]).text() === taxonErrorSamples[j]) {
                  jQuery(this).css('background', '#EE9966');
                }
              }
            });

            alert("Samples saved, but those highlighted in red did not have valid taxon information. Any submissions made from these samples may not be valid!");
          }
          else {
            window.location.href = '<c:url value='/miso/project/${sample.project.id}'/>';
          }
        }
        else {
          jQuery('#bulkSampleButton').removeAttr('disabled');
          jQuery('#bulkSampleButton').html("Save");

          table.find("tr:gt(0)").each(function () {
            for (var j = 0; j < savedSamples.length; j++) {
              if (jQuery(this.cells[0]).text() === savedSamples[j]) {
                table.fnDeleteRow(this);
              }
              else {
                jQuery(this).css('background', '#EE9966');
              }
            }
          });

          alert("Samples highlighted in red did not save. Please check that the sample alias is unique!");
        }
      }
    });
  }
  else {
    alert("The highlighted data rows in red are missing an alias, description, scientific name or sample type.");
    setEditables(table);
  }
  jQuery('#bulkSampleButton').removeAttr('disabled');
  jQuery('#bulkSampleButton').html("Save");
}

</script>
</c:if>

<c:if test="${not empty sample.libraries}">
<script type="text/javascript">
function bulkLibraryQcTable() {
  //destroy current table and recreate
  jQuery('#library_table').dataTable().fnDestroy();
  jQuery('#library_table').addClass("display");

  //remove edit header and column
  jQuery('#library_table tr:first th:eq(4)').remove();
  jQuery('#library_table tr:first th:eq(3)').remove();

  var libraryheaders = ['rowsel',
                        'name',
                        'alias',
                        'libraryType',
                        'qcDate',
                        'qcType',
                        'insertSize',
                        'results'];

  jQuery('#library_table').find("tr").each(function () {
    jQuery(this).removeAttr("onmouseover").removeAttr("onmouseout");
    jQuery(this).find("td:eq(4)").remove();
    jQuery(this).find("td:eq(3)").remove();
    //jQuery(this).find("td:eq(2)").addClass("passedCheck");
  });

  //headers
  jQuery("#library_table tr:first").prepend("<th>Select <span sel='none' header='select' class='ui-icon ui-icon-arrowstop-1-s' style='float:right' onclick='DatatableUtils.toggleSelectAll(\"#library_table\", this);'></span></th>");
  jQuery("#library_table tr:first").append("<th>QC Date <span header='qcDate' class='ui-icon ui-icon-arrowstop-1-s' style='float:right' onclick='DatatableUtils.fillDown(\"#library_table\", this);'></span></th>");
  jQuery("#library_table tr:first").append("<th>QC Method <span header='qcType' class='ui-icon ui-icon-arrowstop-1-s' style='float:right' onclick='DatatableUtils.fillDown(\"#library_table\", this);'></span></th>");
  jQuery("#library_table tr:first").append("<th>Insert Size</th>");
  jQuery("#library_table tr:first").append("<th>Results</th>");

  //columns
  jQuery("#library_table tr:gt(0)").prepend("<td class='rowSelect'></td>");
  jQuery("#library_table tr:gt(0)").append("<td class='dateSelect'></td>");
  jQuery("#library_table tr:gt(0)").append("<td class='typeSelect'></td>");
  jQuery("#library_table tr:gt(0)").append("<td class='defaultEditable'></td>");
  jQuery("#library_table tr:gt(0)").append("<td class='defaultEditable'></td>");

  var datatable = jQuery('#library_table').dataTable({
    "aoColumnDefs": [
      {
        "bUseRendered": false,
        "aTargets": [ 0 ]
      }
    ],
    "bPaginate": false,
    "bInfo": false,
    "bJQueryUI": false,
    "bAutoWidth": true,
    "bSort": false,
    "bFilter": false
  });

  jQuery('#library_table').find("tr:gt(0)").each(function () {
    for (var i = 0; i < this.cells.length; i++) {
      jQuery(this.cells[i]).attr("name", libraryheaders[i]);
    }
  });

  jQuery('#library_table .rowSelect').click(function () {
    if (jQuery(this).parent().hasClass('row_selected'))
      jQuery(this).parent().removeClass('row_selected');
    else
      jQuery(this).parent().addClass('row_selected');
  });

  //jQuery("div.toolbar").parent().addClass("fg-toolbar ui-toolbar ui-widget-header ui-corner-tl ui-corner-tr ui-helper-clearfix");
  jQuery("#library_table_wrapper").prepend("<div class='float-right toolbar'></div>");
  jQuery("div.toolbar").html("<button id=\"bulkLibraryQcButton\" onclick=\"Sample.qc.saveBulkLibraryQc();\" class=\"fg-button ui-state-default ui-corner-all\"><span class=\"add\">Save QCs</span></button>");

  jQuery('#library_table .defaultEditable').editable(function (value, settings) {
    return value;
  },
  {
    callback: function (sValue, y) {
      var aPos = datatable.fnGetPosition(this);
      datatable.fnUpdate(sValue, aPos[0], aPos[1]);
    },
    submitdata: function (value, settings) {
      return {
        "row_id": this.parentNode.getAttribute('id'),
        "column": datatable.fnGetPosition(this)[2]
      };
    },
    onblur: 'submit',
    placeholder: '',
    height: '14px'
  });

  jQuery("#library_table .typeSelect").editable(function (value, settings) {
    return value;
  },
  {
    data: '{${libraryQcTypesString}}',
    type: 'select',
    onblur: 'submit',
    placeholder: '',
    style: 'inherit',
    callback: function (sValue, y) {
      var aPos = datatable.fnGetPosition(this);
      datatable.fnUpdate(sValue, aPos[0], aPos[1]);
    },
    submitdata: function (value, settings) {
      return {
        "row_id": this.parentNode.getAttribute('id'),
        "column": datatable.fnGetPosition(this)[2]
      };
    }
  });

  jQuery("#library_table .dateSelect").editable(function (value, settings) {
    return value;
  },
  {
    type: 'datepicker',
    width: '100px',
    onblur: 'submit',
    placeholder: '',
    style: 'inherit',
    datepicker: {
      dateFormat: 'dd/mm/yy',
      showButtonPanel: true,
      maxDate: 0
    },
    callback: function (sValue, y) {
      var aPos = datatable.fnGetPosition(this);
      datatable.fnUpdate(sValue, aPos[0], aPos[1]);
    },
    submitdata: function (value, settings) {
      return {
        "row_id": this.parentNode.getAttribute('id'),
        "column": datatable.fnGetPosition(this)[2]
      };
    }
  });

  jQuery("#library_table .passedCheck").editable(function (value, settings) {
    return value;
  },
  {
    type: 'checkbox',
    onblur: 'submit',
    placeholder: '',
    style: 'inherit',
    callback: function (sValue, y) {
      var aPos = datatable.fnGetPosition(this);
      datatable.fnUpdate(sValue, aPos[0], aPos[1]);
    },
    submitdata: function (value, settings) {
      return {
        "row_id": this.parentNode.getAttribute('id'),
        "column": datatable.fnGetPosition(this)[2]
      };
    }
  });
}

function bulkLibraryDilutionTable() {
  if (!jQuery('#library_table').hasClass("display")) {
    //destroy current table and recreate
    jQuery('#library_table').dataTable().fnDestroy();
    jQuery('#library_table').addClass("display");

    //remove edit header and column
    jQuery('#library_table tr:first th:eq(4)').remove();
    jQuery('#library_table tr:first th:eq(3)').remove();

    var dilutionheaders = ['rowsel',
                           'name',
                           'alias',
                           'libraryType',
                           'dilutionDate',
                           'results'];

    jQuery('#library_table').find("tr").each(function () {
      jQuery(this).removeAttr("onmouseover").removeAttr("onmouseout");
      jQuery(this).find("td:eq(4)").remove();
      jQuery(this).find("td:eq(3)").remove();
      //jQuery(this).find("td:eq(2)").addClass("passedCheck");
    });

    //headers
    jQuery("#library_table tr:first").prepend("<th>Select <span sel='none' header='select' class='ui-icon ui-icon-arrowstop-1-s' style='float:right' onclick='DatatableUtils.toggleSelectAll(\"#library_table\", this);'></span></th>");
    jQuery("#library_table tr:first").append("<th>Dilution Date <span header='qcDate' class='ui-icon ui-icon-arrowstop-1-s' style='float:right' onclick='DatatableUtils.fillDown(\"#library_table\", this);'></span></th>");
    jQuery("#library_table tr:first").append("<th>Results</th>");

    //columns
    jQuery("#library_table tr:gt(0)").prepend("<td class='rowSelect'></td>");
    jQuery("#library_table tr:gt(0)").append("<td class='dateSelect'></td>");
    jQuery("#library_table tr:gt(0)").append("<td class='defaultEditable'></td>");

    var datatable = jQuery('#library_table').dataTable({
      "aoColumnDefs": [
        {
          "bUseRendered": false,
          "aTargets": [ 0 ]
        }
      ],
      "bPaginate": false,
      "bInfo": false,
      "bJQueryUI": false,
      "bAutoWidth": true,
      "bSort": false,
      "bFilter": false
    });

    jQuery('#library_table').find("tr:gt(0)").each(function () {
      for (var i = 0; i < this.cells.length; i++) {
        jQuery(this.cells[i]).attr("name", dilutionheaders[i]);
      }
    });

    jQuery('#library_table .rowSelect').click(function () {
      if (jQuery(this).parent().hasClass('row_selected'))
        jQuery(this).parent().removeClass('row_selected');
      else
        jQuery(this).parent().addClass('row_selected');
    });

    jQuery("#library_table_wrapper").prepend("<div class='float-right toolbar'></div>");
    jQuery("div.toolbar").parent().addClass("fg-toolbar ui-toolbar ui-widget-header ui-corner-tl ui-corner-tr ui-helper-clearfix");
    jQuery("div.toolbar").html("<button id=\"bulkLibraryDilutionButton\" onclick=\"Sample.library.saveBulkLibraryDilutions();\" class=\"fg-button ui-state-default ui-corner-all\"><span class=\"add\">Save Dilutions</span></button>");

    jQuery('#library_table .defaultEditable').editable(function (value, settings) {
      return value;
    },
    {
      callback: function (sValue, y) {
        var aPos = datatable.fnGetPosition(this);
        datatable.fnUpdate(sValue, aPos[0], aPos[1]);
      },
      submitdata: function (value, settings) {
        return {
          "row_id": this.parentNode.getAttribute('id'),
          "column": datatable.fnGetPosition(this)[2]
        };
      },
      onblur: 'submit',
      placeholder: '',
      height: '14px'
    });

    jQuery("#library_table .dateSelect").editable(function (value, settings) {
      return value;
    },
    {
      type: 'datepicker',
      width: '100px',
      onblur: 'submit',
      placeholder: '',
      style: 'inherit',
      datepicker: {
        dateFormat: 'dd/mm/yy',
        showButtonPanel: true,
        maxDate: 0
      },
      callback: function (sValue, y) {
        var aPos = datatable.fnGetPosition(this);
        datatable.fnUpdate(sValue, aPos[0], aPos[1]);
      },
      submitdata: function (value, settings) {
        return {
          "row_id": this.parentNode.getAttribute('id'),
          "column": datatable.fnGetPosition(this)[2]
        };
      }
    });
  }
}
</script>
</c:if>

</div>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>