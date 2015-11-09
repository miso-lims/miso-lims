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

<script src="<c:url value='/scripts/datatables_utils.js?ts=${timestamp.time}'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/datatables/js/jquery.dataTables.min.js'/>" type="text/javascript"></script>
<link href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" rel="stylesheet" type="text/css">

<script src="<c:url value='/scripts/stats_ajax.js?ts=${timestamp.time}'/>" type="text/javascript"></script>

<div id="maincontent">
<div id="contentcolumn">
<c:if test="${library.id == 0}">
<div id="tabs">
<ul>
  <li><a href="#tab-1"><span>Single</span></a></li>
  <li><a href="#tab-2"><span>Bulk</span></a></li>
</ul>

<div id="tab-1">
</c:if>

<form:form action="/miso/library" method="POST" commandName="library" autocomplete="off" acceptCharset="utf-8">
<sessionConversation:insertSessionConversationId attributeName="library"/>
<nav class="navbar navbar-default" role="navigation">
   <div class="navbar-header">
      <span class="navbar-brand navbar-center">
        <c:choose>
          <c:when test="${library.id != 0}">Edit</c:when>
          <c:otherwise>Create</c:otherwise>
        </c:choose> Library
      </span>
   </div>
   <div class="navbar-right container-fluid">
      <button type="button" class="btn btn-default navbar-btn" onclick="return validate_library(this.form);">Save</button>
   </div>
</nav>
<div class="breadcrumbs">
  <ul>
    <li>
      <a href='<c:url value="/"/>'>Home</a>
    </li>
    <li>
      <div class="breadcrumbsbubbleInfo">
        <div class="trigger">
          <a href='<c:url value="/miso/project/${library.sample.project.id}"/>'>${library.sample.project.name}</a>
        </div>
        <div class="breadcrumbspopup">
            ${library.sample.project.alias}
        </div>
      </div>
    </li>
    <li>
      <div class="breadcrumbsbubbleInfo">
        <div class="trigger">
          <a href='<c:url value="/miso/sample/${library.sample.id}"/>'>${library.sample.name}</a>
        </div>
        <div class="breadcrumbspopup">
            ${library.sample.alias}
        </div>
      </div>
    </li>
  </ul>
  <c:if test="${not empty nextLibrary}">
    <span style="float:right; padding-top: 5px; padding-left: 6px">
      <a class='arrowright' href='<c:url value="/miso/library/${nextLibrary.id}"/>'>Next Library <b>${nextLibrary.name}</b></a>
    </span>
  </c:if>
  <c:if test="${not empty previousLibrary}">
    <span style="float:right; padding-top: 5px">
      <a class='arrowleft' href='<c:url value="/miso/library/${previousLibrary.id}"/>'>Previous Library <b>${previousLibrary.name}</b></a>
    </span>
  </c:if>
</div>
<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
  <div id="note_arrowclick" class="toggleLeft"></div>
</div>
<div id="notediv" class="note" style="display:none;">A Library is the first step in constructing sequenceable
  material from an initial Sample. A Library is then diluted down to a Dilution, and put in a Pool.
</div>
<h2>Library Information</h2>

<div class="barcodes">
  <div class="barcodeArea ui-corner-all" style="width:220px">
    <c:choose>
      <c:when test="${empty library.locationBarcode}">
        <span style="float: left; font-size: 24px; font-weight: bold; color:#BBBBBB">Location</span>
        <form:input path="locationBarcode" size="8" class="form-control float-right"/>
      </c:when>
      <c:otherwise>
        <span style="float: left; font-size: 24px; font-weight: bold; color:#BBBBBB">Location</span>
        <ul class="barcode-ddm">
          <li>
            <a onmouseover="mopen('locationBarcodeMenu')" onmouseout="mclosetime()">
              <span style="float:right; margin-top:6px;" class="ui-icon ui-icon-triangle-1-s"></span>
              <span id="locationBarcode" style="float:right; margin-top:6px; padding-bottom: 11px;">
                  ${sample.locationBarcode}
              </span>
            </a>

            <div id="locationBarcodeMenu" onmouseover="mcancelclosetime()" onmouseout="mclosetime()">
              <a href="javascript:void(0);" onclick="Library.barcode.showLibraryLocationChangeDialog(${library.id});">
                Change location
              </a>
            </div>
          </li>
        </ul>
        <div id="changeLibraryLocationDialog" title="Change Library Location"></div>
        <div id="changeLibraryLocationDialog1" title="Change Library Location"></div>
      </c:otherwise>
    </c:choose>
  </div>
  <div class="barcodeArea ui-corner-all">
    <span style="float: left; font-size: 24px; font-weight: bold; color:#BBBBBB">ID</span>
    <c:if test="${not empty library.identificationBarcode}">
      <ul class="nav navbar-right">
        <li class="dropdown">
         <a id="idBarcode" class="dropdown-toggle" data-toggle="dropdown" href="javascript:void(0);">
           <i class="fa fa-caret-down"></i>
         </a>
         <ul class="dropdown-menu dropdown-tasks">
           <li role="presentation"><a href="javascript:void(0);" onclick="Library.barcode.printLibraryBarcodes(${library.id});">Print</a></li>
         </ul>
       </li>
      </ul>
      <script type="text/javascript">
        jQuery(document).ready(function () {
          Fluxion.doAjax(
            'libraryControllerHelperService',
            'getLibraryBarcode',
            {'libraryId':${library.id},
              'url': ajaxurl
            },
            {'doOnSuccess': function (json) {
              jQuery('#idBarcode').prepend("<img style='height:30px; border:0; padding-right:4px;' src='<c:url value='/temp/'/>" + json.img + "'/>");
            }
            });
        });
      </script>
    </c:if>
  </div>
  <div id="printServiceSelectDialog" title="Select a Printer"></div>
</div>

<table class="in">
<tr>
  <td class="h">Library ID:</td>
  <td>
    <c:choose>
      <c:when test="${library.id != 0}">${library.id}</c:when>
      <c:otherwise><i>Unsaved</i></c:otherwise>
    </c:choose>
  </td>
</tr>
<tr>
  <td>Name:</td>
  <td>
    <c:choose>
      <c:when test="${library.id != 0}">${library.name}</c:when>
      <c:otherwise><i>Unsaved</i></c:otherwise>
    </c:choose>
  </td>
</tr>
<tr>
  <td>Parent Sample:</td>
  <td>
    <c:choose>
      <c:when test='${empty library.sample}'>
        <i>Unassigned</i>
      </c:when>
      <c:otherwise>
        <a href='<c:url value="/miso/sample/${library.sample.id}"/>'>${library.sample.name}</a>
      </c:otherwise>
    </c:choose>
  </td>
</tr>
<tr>
  <td class="h">Alias:</td>
  <td>
    <div class="input-group">
      <c:choose>
        <c:when test="${not empty autogeneratedLibraryAlias}">
          <form:input path="alias" value="${autogeneratedLibraryAlias}" class="validateable form-control"/>
        </c:when>
        <c:otherwise>
          <form:input path="alias" class="validateable form-control"/>
        </c:otherwise>
      </c:choose>
      <span id="aliascounter" class="input-group-addon"></span>
    </div>
  </td>
</tr>
<tr>
  <td>Description:</td>
  <td>
    <div class="input-group">
      <form:input path="description" class="validateable form-control"/>
      <span id="descriptioncounter" class="input-group-addon"></span>
    </div>
  </td>
</tr>
<tr>
  <td class="h">Creation date:</td>
  <td><fmt:formatDate value="${library.creationDate}"/></td>
</tr>
<c:if test="${not empty library.accession}">
  <tr>
    <td class="h">Accession:</td>
    <td><a href="http://www.ebi.ac.uk/ena/data/view/${library.accession}">${library.accession}</a>
    </td>
  </tr>
</c:if>
<tr>
  <td>Paired:</td>
  <td>
    <c:choose>
      <c:when test="${library.id != 0}">
        <form:checkbox path="paired" checked="checked"/>
      </c:when>
      <c:otherwise>
        <form:checkbox path="paired"/>
      </c:otherwise>
    </c:choose>
  </td>
</tr>
<tr>
  <c:choose>
    <c:when test="${library.id ==0 or empty library.libraryType}">
      <td>Platform - Library Type:</td>
      <td>
        <form:select id="platformNames" path="platformName" items="${platformNames}"
                     onchange="Library.ui.changePlatformName(this);"/>
        <form:select id="libraryTypes" path="libraryType"/>
      </td>
      <script type="text/javascript">
        jQuery(document).ready(function () {
          Library.ui.changePlatformName(jQuery("#platformNames"));
        });
      </script>
    </c:when>
    <c:when test="${fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')
        or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_TECH')}">
      <td>Platform - Library Type:</td>
      <td>
        <form:select id="platformNames" path="platformName" items="${platformNames}"
                     onchange="Library.ui.changePlatformName(this);"/>
        <form:select id="libraryTypes" path="libraryType"/>
      </td>
      <script type="text/javascript">
        jQuery(document).ready(function () {
          Library.ui.changePlatformNameWithLibraryType(jQuery("#platformNames"), '${library.libraryType.libraryTypeId}');
        });
      </script>
    </c:when>
    <c:otherwise>
      <td>Platform - Library Type</td>
      <td>${library.platformName} - ${library.libraryType.description}</td>
    </c:otherwise>
  </c:choose>
</tr>
<tr>
  <c:choose>
    <c:when test="${library.id == 0
                  or empty library.librarySelectionType
                  or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')
                  or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_TECH')}">
      <td>Library Selection Type:</td>
      <td>
        <form:select id="librarySelectionTypes" path="librarySelectionType" items="${librarySelectionTypes}"
                     itemLabel="name" itemValue="librarySelectionTypeId"/>
      </td>
    </c:when>
    <c:otherwise>
      <td>Library Selection Type:</td>
      <td>${library.librarySelectionType.name}</td>
    </c:otherwise>
  </c:choose>
</tr>
<tr>
  <c:choose>
    <c:when
        test="${library.id == 0
              or empty library.libraryStrategyType
              or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')
              or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_TECH')}">
      <td>Library Strategy Type:</td>
      <td>
        <form:select id="libraryStrategyTypes" path="libraryStrategyType" items="${libraryStrategyTypes}"
                     itemLabel="name" itemValue="libraryStrategyTypeId"/>
      </td>
    </c:when>
    <c:otherwise>
      <td>Library Strategy Type:</td>
      <td>${library.libraryStrategyType.name}</td>
    </c:otherwise>
  </c:choose>
</tr>
<tr>
  <c:choose>
    <c:when test="${library.id == 0 }">
      <td>Barcoding Strategy:</td>
      <td>
        <select name='tagBarcodeStrategies' id='tagBarcodeStrategies' onchange='Library.tagbarcode.populateAvailableBarcodesForStrategy(this);'>
          <option value="">Please select a platform...</option>
        </select>
      </td>
    </c:when>
    <c:when test="${fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')
    or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_INTERNAL')}">
      <td>Barcoding Strategy:</td>
      <td>
        <c:choose>
          <c:when test="${empty library.tagBarcodes}">
            <select id="tagBarcodeStrategies" id='tagBarcodeStrategies' onchange="Library.tagbarcode.populateAvailableBarcodesForStrategy(this);">
              <option selected="selected" value="">No barcoding</option>
              <c:forEach items="${availableTagBarcodeStrategies}" var="tagBarcodeStrategy">
              <option value="${tagBarcodeStrategy.name}">${tagBarcodeStrategy.name}</option>
              </c:forEach>
            </select>
          </c:when>
          <c:otherwise>
            <select name='tagBarcodeStrategies' id='tagBarcodeStrategies' onchange='Library.tagbarcode.populateAvailableBarcodesForStrategy(this);'>
            </select>
            <script type="text/javascript">
              jQuery(document).ready(function () {
                Library.ui.changePlatformNameWithTagBarcodeStrategy(jQuery("#platformNames"), '${selectedTagBarcodeStrategy}');
              });
            </script>
          </c:otherwise>
        </c:choose>
      </td>
    </c:when>
    <c:otherwise>
      <td>Barcoding Strategy:</td>
      <td>
        <c:choose>
          <c:when test="${empty library.tagBarcodes}">
            <select id="tagBarcodeStrategies" id='tagBarcodeStrategies' onchange="Library.tagbarcode.populateAvailableBarcodesForStrategy(this);">
              <option selected="selected" value="">No barcoding</option>
              <c:forEach items="${availableTagBarcodeStrategies}" var="tagBarcodeStrategy">
              <option value="${tagBarcodeStrategy.name}">${tagBarcodeStrategy.name}</option>
              </c:forEach>
            </select>
          </c:when>
          <c:otherwise>
            ${selectedTagBarcodeStrategy}
          </c:otherwise>
        </c:choose>
      </td>
    </c:otherwise>
  </c:choose>
</tr>

<tr>
  <c:choose>
    <c:when test="${library.id == 0 or empty library.tagBarcodes}">
      <td>Barcodes:</td>
      <td id="tagBarcodesDiv">
      </td>
    </c:when>
    <c:otherwise>
      <td>Barcodes:</td>
      <td id="tagBarcodesDiv">
        <c:forEach items="${availableTagBarcodeStrategyBarcodes}" var="barcodemap">
          <form:select path="tagBarcodes['${barcodemap.key}']" items="${barcodemap.value}"
                       itemLabel="name" itemValue="id"/>
        </c:forEach>
      </td>
    </c:otherwise>
  </c:choose>
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
  <c:when
      test="${!empty library.sample and library.securityProfile.profileId eq library.sample.project.securityProfile.profileId}">
    <tr>
      <td>Permissions</td>
      <td><i>Inherited from sample </i>
        <a href='<c:url value="/miso/sample/${library.sample.id}"/>'>${library.sample.name}</a>
        <input type="hidden" value="${library.sample.securityProfile.profileId}"
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
</form:form>

<c:if test="${library.id != 0}">
  <div id="simplebox">
    <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#upload_arrowclick'), 'uploaddiv');">
      Library Files
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
                      <input id="ajax_upload_form" type="file" name="files[]" data-url="<c:url value="/miso/upload/library"/>" multiple/>
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
                formData: {'libraryId': '${library.id}'},
                dataType: 'json',
                done: function (e, data) {
                  jQuery.each(data.result.files, function (index, file) {
                    var r = "<a href='"+file.url+"'><a class='listbox' href='"+file.url+"'><div onMouseOver='this.className=\"boxlistboxhighlight\"' onMouseOut='this.className=\"boxlistbox\"' class='boxlistbox'>"+file.name+"</div></a></a>";
                    jQuery(r).prependTo('#libraryfiles');
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

      <div id="libraryfiles">
        <c:forEach items="${libraryFiles}" var="file">
          <div id="file${file.key}">
            <div onMouseOver="this.className='boxlistboxhighlight'" onMouseOut="this.className='boxlistbox'" class="boxlistbox">
              <a href="<c:url value='/miso/download/library/${library.id}/${file.key}'/>">${file.value}</a>
              <c:if test="${(library.securityProfile.owner.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                              or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
              <a href='#' onclick="Library.ui.deleteLibraryFile('${library.id}', '${file.value}', '${file.key}');">
                <i class="fa fa-trash-o fa-lg fa-fw pull-right" style="padding-top:4px"></i>
              </a>
              </c:if>
            </div>
          </div>
        </c:forEach>
      </div>
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
                <a role="menuitem" onclick="Library.ui.showLibraryNoteDialog(${library.id});" href="javascript:void(0);">Add Note</a>
              </li>
            </ul>
          </li>
        </ul>
      </div>
    </nav>
    <c:if test="${fn:length(library.notes) > 0}">
      <div class="note" style="clear:both">
        <c:forEach items="${library.notes}" var="note" varStatus="n">
          <div class="exppreview" id="library-notes-${n.count}">
            <b>${note.creationDate}</b>: ${note.text}
              <span class="float-right" style="font-weight:bold; color:#C0C0C0;">${note.owner.loginName}
                <c:if test="${(project.securityProfile.owner.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                                or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
                <span style="color:#000000"><a href='#' onclick="Library.ui.deleteLibraryNote('${library.id}', '${note.noteId}');">
                  <i class="fa fa-trash-o fa-fw"></i></a></span>
                </c:if>
              </span>
          </div>
        </c:forEach>
      </div>
    </c:if>
    <div id="addLibraryNoteDialog" title="Create new Note"></div>
  </div>
</c:if>

<c:if test="${library.id != 0}">
<div id="qc-div" class="panel panel-default padded-panel">
  <nav id="navbar-libqc" class="navbar navbar-default navbar-static" role="navigation">
    <div class="navbar-header">
      <span id="qcsTotalCount" class="navbar-brand navbar-center">Library QCs</span>
    </div>
    <div class="collapse navbar-collapse bs-example-js-navbar-collapse">
      <ul class="nav navbar-nav navbar-right">
        <li id="qc-menu" class="dropdown">
          <a id="qcdrop1" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">Options <b class="caret"></b></a>
          <ul class="dropdown-menu dropdown-menu-right" role="menu" aria-labelledby="qcdrop1">
            <li role="presentation">
              <a onclick="Library.qc.insertLibraryQCRow(${library.id});" href='javascript:void(0);'>Add Library QC</a>
            </li>
          </ul>
        </li>
      </ul>
    </div>
  </nav>

  <span style="clear:both">
    <div id="addLibraryQC"></div>
    <form id='addQcForm'>
      <table class="table table-striped table-bordered" id="libraryQcTable">
        <thead>
        <tr>
          <th>QCed By</th>
          <th>QC Date</th>
          <th>Method</th>
          <th>Results</th>
          <th>Insert Size</th>
          <c:if test="${(library.securityProfile.owner.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                      or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
            <th align="center">Edit</th>
          </c:if>
        </tr>
        </thead>
        <tbody>
        <c:if test="${not empty library.libraryQCs}">
          <c:forEach items="${library.libraryQCs}" var="qc">
            <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
              <td>${qc.qcCreator}</td>
              <td><fmt:formatDate value="${qc.qcDate}"/></td>
              <td>${qc.qcType.name}</td>
              <td id="result${qc.id}">${qc.results} ${qc.qcType.units}</td>
              <td id="insert${qc.id}">${qc.insertSize} bp</td>
              <c:if test="${(library.securityProfile.owner.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                          or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
                <td id="edit${qc.id}" align="center">
                  <a href="javascript:void(0);" onclick="Library.qc.changeLibraryQCRow('${qc.id}','${library.id}')">
                    <span class="fa fa-pencil-square-o fa-lg"></span>
                  </a>
                </td>
              </c:if>
            </tr>
          </c:forEach>
        </c:if>
        </tbody>
      </table>
      <input type='hidden' id='qcLibraryId' name='id' value='${library.id}'/>
    </form>
  </span>

  <script type="text/javascript">
    jQuery(document).ready(function () {
      jQuery("#libraryQcTable").tablesorter({
        headers: {
        }
      });

      jQuery('#qcsTotalCount').html(jQuery('#libraryQcTable>tbody>tr:visible').length.toString() + " QCs");
      jQuery('#ldsTotalCount').html(jQuery('#libraryDilutionTable>tbody>tr:visible').length.toString() + " Library Dilutions");
    });
  </script>
</div>

<div id="ldi-div" class="panel panel-default padded-panel">
  <nav id="navbar-ldiqc" class="navbar navbar-default navbar-static" role="navigation">
    <div class="navbar-header">
      <span id="ldsTotalCount" class="navbar-brand navbar-center">Library Dilutions</span>
    </div>
    <div class="collapse navbar-collapse bs-example-js-navbar-collapse">
      <ul class="nav navbar-nav navbar-right">
        <li id="ld-menu" class="dropdown">
          <a id="lddrop1" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">Options <b class="caret"></b></a>
          <ul class="dropdown-menu dropdown-menu-right" role="menu" aria-labelledby="lddrop1">
            <li role="presentation">
              <a href='javascript:void(0);' onclick="Library.dilution.insertLibraryDilutionRow(${library.id});">
                Add Library Dilution
              </a>
            </li>
            <c:if test="${not empty library.libraryDilutions}">
              <li role="presentation"><a href='<c:url value="/miso/poolwizard/new/${library.sample.project.id}"/>'>Create Pools</a></li>
            </c:if>
            </li>
          </ul>
        </li>
      </ul>
    </div>
  </nav>

  <span style="clear:both">
    <div id="addLibraryDilution"></div>
    <form id='addDilutionForm'>
      <table class="table table-striped table-bordered" id="libraryDilutionTable">
        <thead>
        <tr>
          <th>LD Name</th>
          <th>Done By</th>
          <th>Date</th>
          <th>Results (${libraryDilutionUnits})</th>
          <th>ID Barcode</th>
            <%-- <th>Location Barcode</th> --%>
          <c:if test="${(dil.dilutionCreator eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                      or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
            <th align="center">Edit</th>
          </c:if>
          <c:choose>
            <c:when test="${library.platformName ne 'Illumina'}">
              <th>Add EmPCR</th>
            </c:when>
            <c:otherwise>
              <th>Construct Pool</th>
            </c:otherwise>
          </c:choose>
        </tr>
        </thead>
        <tbody>
        <c:if test="${not empty library.libraryDilutions}">
          <c:forEach items="${library.libraryDilutions}" var="dil">
            <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
              <td>${dil.name}</td>
              <td>${dil.dilutionCreator}</td>
              <td><fmt:formatDate value="${dil.creationDate}"/></td>
              <td id="results${dil.id}">${dil.concentration} ${libraryDilutionUnits}</td>
              <td class="fit">
                <c:if test="${not empty dil.identificationBarcode}">
                  <div class="barcodes">
                    <ul class="nav">
                      <li class="dropdown">
                       <a id="dil${dil.id}IdBarcode" class="dropdown-toggle" data-toggle="dropdown" href="javascript:void(0);">
                         <i class="fa fa-caret-down"></i>
                       </a>
                       <ul class="dropdown-menu dropdown-tasks">
                         <li role="presentation"><a href="javascript:void(0);" onclick="Library.barcode.printDilutionBarcode(${dil.id}, '${library.platformName}');">Print</a></li>
                       </ul>
                     </li>
                    </ul>
                    <script type="text/javascript">
                      jQuery(document).ready(function () {
                        Fluxion.doAjax(
                          'libraryControllerHelperService',
                          'getLibraryDilutionBarcode',
                          {'dilutionId':${dil.id},
                            'url': ajaxurl
                          },
                          {'doOnSuccess': function (json) {
                            jQuery('#dil${dil.id}IdBarcode').prepend(
                              "<img style='border:0;padding-right:4px;' src='<c:url value='/temp/'/>" + json.img + "'/>"
                            );
                          }
                          });
                      });
                    </script>
                  </div>
                </c:if>
              </td>
              <c:if test="${(sample.securityProfile.owner.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                          or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
                <td id="edit${dil.id}" align="center">
                  <a href="javascript:void(0);" onclick="Library.dilution.changeLibraryDilutionRow('${dil.id}','${library.id}')">
                    <span class="fa fa-pencil-square-o fa-lg"></span>
                  </a>
                </td>
              </c:if>
              <c:choose>
                <c:when test="${library.platformName ne 'Illumina'}">
                  <td id="empcr${dil.id}" align="center">
                    <a href="javascript:void(0);" onclick="Library.empcr.insertEmPcrRow(${dil.id});">
                      <span class="fa fa-flask fa-lg"></span>
                    </a>
                  </td>
                </c:when>
                <c:otherwise>
                  <td id="pool${dil.id}" align="center">
                    <a href="<c:url value="/miso/poolwizard/new/${library.sample.project.id}"/>">
                      <span class="fa fa-flask fa-lg"></span>
                    </a>
                  </td>
                </c:otherwise>
              </c:choose>
            </tr>
          </c:forEach>
        </c:if>
        </tbody>
      </table>
      <input type='hidden' id='dilLibraryId' name='id' value='${library.id}'/>
    </form>
  </span>
</div>

<c:if test="${library.platformName ne 'Illumina'}">
<div id="emp-div" class="panel panel-default padded-panel">
  <nav id="navbar-empcr" class="navbar navbar-default navbar-static" role="navigation">
    <div class="navbar-header">
      <span id="empcrTotalCount" class="navbar-brand navbar-center">emPCRs</span>
    </div>
  </nav>

  <div id="addEmPcr"></div>
  <form id='addEmPcrForm'>
    <table class="table table-striped table-bordered" id="emPcrTable">
      <thead>
      <tr>
        <th>Library Dilution ID</th>
        <th>PCRed By</th>
        <th>PCR Date</th>
        <th>Results (${emPCRUnits})</th>
        <th>Add Dilution</th>
      </tr>
      </thead>
      <tbody>
      <c:if test="${not empty emPCRs}">
        <c:forEach items="${emPCRs}" var="empcr">
          <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
            <td>${empcr.libraryDilution.id}</td>
            <td>${empcr.pcrCreator}</td>
            <td><fmt:formatDate value="${empcr.creationDate}"/></td>
            <td>${empcr.concentration} ${emPCRUnits}</td>
            <td id="empcrdil${dil.id}" align="center">
              <a href="javascript:void(0);" onclick="Library.empcr.insertEmPcrDilutionRow(${empcr.id});">
                <span class="fa fa-flask fa-lg"></span>
              </a>
            </td>
          </tr>
        </c:forEach>
      </c:if>
      </tbody>
    </table>
  </form>
</div>

<div id="edi-div" class="panel panel-default padded-panel">
  <nav id="navbar-edi" class="navbar navbar-default navbar-static" role="navigation">
    <div class="navbar-header">
      <span id="ediTotalCount" class="navbar-brand navbar-center">emPCR Dilutions</span>
    </div>
  </nav>

  <div id="addEmPcrDilution"></div>
  <form id='addEmPcrDilutionForm'>
    <table class="table table-striped table-bordered" id="emPcrDilutionTable">
      <thead>
      <tr>
        <th>emPCR ID</th>
        <th>Done By</th>
        <th>Date</th>
        <th>Results (${emPCRDilutionUnits})</th>
        <th>Construct Pool</th>
      </tr>
      </thead>
      <tbody>
      <c:if test="${not empty emPcrDilutions}">
        <c:forEach items="${emPcrDilutions}" var="dil">
          <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
            <td>${dil.emPCR.id}</td>
            <td>${dil.dilutionCreator}</td>
            <td><fmt:formatDate value="${dil.creationDate}"/></td>
            <td>${dil.concentration} ${emPCRDilutionUnits}</td>
            <td id="pool${dil.id}" align="center">
              <a href="<c:url value="/miso/poolwizard/new/${library.sample.project.id}"/>">
                <span class="fa fa-flask fa-lg"></span>
              </a>
            </td>
          </tr>
        </c:forEach>
      </c:if>
      </tbody>
    </table>
  </form>
  <script type="text/javascript">
    jQuery(document).ready(function () {
      jQuery("#emPcrDilutionTable").tablesorter({
        headers: {
        }
      });
    });
  </script>
</div>
</c:if>

<c:if test="${not empty libraryPools}">
<div id="pool-div" class="panel panel-default padded-panel">
  <nav id="navbar-pool" class="navbar navbar-default navbar-static" role="navigation">
    <div class="navbar-header">
      <span class="navbar-brand navbar-center">${fn:length(libraryPools)} Pools</span>
    </div>
    <div class="collapse navbar-collapse bs-example-js-navbar-collapse">
      <ul class="nav navbar-nav navbar-right">
        <li id="lp-menu" class="dropdown">
          <a id="lpdrop1" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">Options <b class="caret"></b></a>
          <ul class="dropdown-menu dropdown-menu-right" role="menu" aria-labelledby="lpdrop1">
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
      <c:forEach items="${libraryPools}" var="pool">
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

<c:if test="${not empty libraryRuns}">
<div id="run-div" class="panel panel-default padded-panel">
  <nav id="navbar-run" class="navbar navbar-default navbar-static" role="navigation">
    <div class="navbar-header">
      <span class="navbar-brand navbar-center">${fn:length(libraryRuns)} Runs</span>
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
    <c:forEach items="${libraryRuns}" var="run" varStatus="runCount">
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
                  <c:if test="${not empty poolLibraryMap[partition.pool.id]}">
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

<c:if test="${library.id == 0}">
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
            <a href='<c:url value="/miso/project/${library.sample.project.id}"/>'>${library.sample.project.name}</a>
          </div>
          <div class="breadcrumbspopup">
              ${library.sample.project.alias}
          </div>
        </div>
      </li>
      <li>
        <div class="breadcrumbsbubbleInfo">
          <div class="trigger">
            <a href='<c:url value="/miso/sample/${library.sample.id}"/>'>${library.sample.name}</a>
          </div>
          <div class="breadcrumbspopup">
              ${library.sample.alias}
          </div>
        </div>
      </li>
    </ul>
  </div>

<div id="bulk-lib-div" class="panel panel-default padded-panel">
  <nav class="navbar navbar-default" role="navigation">
     <div class="navbar-header">
        <span class="navbar-brand navbar-center">
          Create Libraries
        </span>
     </div>
     <div class="navbar-right container-fluid">
       <button id="bulkLibSaveButton" type="button" onClick="submitBulkLibraries();" class="btn btn-default navbar-btn">Save</button>
     </div>
  </nav>

  This system will create <b>ONE</b> library for each of the selected samples below:
  <br/>
  <table id="cinput" class="table table-striped table-bordered display">
    <thead>
    <tr>
      <th style="width: 5%">Select <span sel="none" header="select" class="ui-icon ui-icon-arrowstop-1-s"
                                         style="float:right"
                                         onclick="DatatableUtils.toggleSelectAll('#cinput', this);"></span></th>
      <th style="width: 10%">Sample</th>
      <th style="width: 10%">Description <span header="description" class="ui-icon ui-icon-arrowstop-1-s"
                                               style="float:right"
                                               onclick="DatatableUtils.fillDown('#cinput', this);"></span></th>
      <th style="width: 5%">Paired <span header="paired" class="ui-icon ui-icon-arrowstop-1-s" style="float:right"
                                         onclick="DatatableUtils.fillDown('#cinput', this);"></span></th>
      <th style="width: 10%">Platform <span header="platform" class="ui-icon ui-icon-arrowstop-1-s"
                                            style="float:right"
                                            onclick="DatatableUtils.fillDown('#cinput', this);"></span></th>
      <th style="width: 10%">Type <span header="libraryType" class="ui-icon ui-icon-arrowstop-1-s"
                                        style="float:right"
                                        onclick="DatatableUtils.fillDown('#cinput', this);"></span></th>
      <th style="width: 10%">Selection <span header="selectionType" class="ui-icon ui-icon-arrowstop-1-s"
                                             style="float:right"
                                             onclick="DatatableUtils.fillDown('#cinput', this);"></span></th>
      <th style="width: 10%">Strategy <span header="strategyType" class="ui-icon ui-icon-arrowstop-1-s"
                                            style="float:right"
                                            onclick="DatatableUtils.fillDown('#cinput', this);"></span></th>
      <th style="width: 10%">Barcode Kit <span header="barcodeStrategy" class="ui-icon ui-icon-arrowstop-1-s"
                                               style="float:right"
                                               onclick="Library.ui.fillDownTagBarcodeStrategySelects('#cinput', this);"></span>
      </th>
      <th style="width: 10%">Tag Barcodes <span header="tagBarcodes" class="ui-icon ui-icon-arrowstop-1-s"
                                                style="float:right"
                                                onclick="Library.ui.fillDownTagBarcodeSelects('#cinput', this);"></span>
      </th>
      <th style="width: 10%">Location Barcode <span header="locationBarcode" class="ui-icon ui-icon-arrowstop-1-s"
                                                    style="float:right"
                                                    onclick="DatatableUtils.fillDown('#cinput', this);"></span>
      </th>
    </tr>
    </thead>
    <tbody>
    </tbody>
  </table>
  <div id="pager"></div>
</div>
</div> <!-- tab2 -->
</div> <!-- tabs -->

<script type="text/javascript">
var headers = ['rowsel',
               'parentSample',
               'description',
               'paired',
               'platform',
               'libraryType',
               'selectionType',
               'strategyType',
               'barcodeStrategy',
               'tagBarcodes',
               'locationBarcode'];

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
    "bSort": false,
    "bFilter": false
  });

  var sampleArray = new Array();
  var sampleDescArray = new Array();
  <c:forEach items="${projectSamples}" var="s" varStatus="n">
  sampleArray.push("${s.alias}");
  sampleDescArray.push("${s.description}");
  </c:forEach>

  jQuery.each(sampleArray, function (index, value) {
    fnClickAddRow(
      [ "",
        value,
        sampleDescArray[index],
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        ""]
    );
  });

  jQuery('#cinput .rowSelect').click(function () {
    if (jQuery(this).parent().hasClass('row_selected')) {
      jQuery(this).parent().removeClass('row_selected');
    }
    else {
      jQuery(this).parent().addClass('row_selected');
    }
  });

  setEditables(oTable);

  jQuery("#tabs").tabs();
  jQuery("#tabs").removeClass('ui-widget').removeClass('ui-widget-content');
});

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
        "",
        "",
        "",
        ""]
    );
  }

  var nTr = table.fnSettings().aoData[a[0]].nTr;

  jQuery(nTr.cells[0]).addClass("rowSelect");

  for (var i = 2; i < headers.length; i++) {
    jQuery(nTr.cells[i]).attr("name", headers[i]);
    if (headers[i] === "platform") {
      jQuery(nTr.cells[i]).addClass("platform");
    }
    else if (headers[i] === "libraryType") {
      jQuery(nTr.cells[i]).addClass("libraryType");
    }
    else if (headers[i] === "selectionType") {
      jQuery(nTr.cells[i]).addClass("selectionType");
    }
    else if (headers[i] === "strategyType") {
      jQuery(nTr.cells[i]).addClass("strategyType");
    }
    else if (headers[i] === "barcodeStrategy") {
      jQuery(nTr.cells[i]).addClass("barcodeStrategy");
    }
    else if (headers[i] === "tagBarcodes") {
      jQuery(nTr.cells[i]).addClass("tagBarcodes");
    }
    else if (headers[i] === "paired") {
      jQuery(nTr.cells[i]).addClass("passedCheck");
    }
    else {
      jQuery(nTr.cells[i]).addClass("defaultEditable");
    }
  }
}

function setEditables(datatable) {
  jQuery('#cinput .defaultEditable').editable(function (value, settings) {
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
    "width": "100%"
  });

  jQuery("#cinput .platform").editable(function (value, settings) {
    return value;
  },
  {
    data: '{${platformNamesString}}',
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
    },
    "width": "100%"
  });

  jQuery("#cinput .libraryType").editable(function (value, settings) {
    return value;
  },
  {
    loadurl: '../../library/librarytypes',
    loaddata: function (value, settings) {
      DatatableUtils.collapseInputs('#cinput');
      var row = datatable.fnGetPosition(this)[0];
      var platform = datatable.fnGetData(row, 4);
      if (!Utils.validation.isNullCheck(platform)) {
        return {'platform': platform};
      }
      else {
        alert("Please select a platform in this row");
        return {'platform': ''};
      }
    },
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
    },
    "width": "100%"
  });

  jQuery("#cinput .selectionType").editable(function (value, settings) {
    return value;
  },
  {
    data: '{${librarySelectionTypesString}}',
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
    },
    "width": "100%"
  });

  jQuery("#cinput .strategyType").editable(function (value, settings) {
    return value;
  },
  {
    data: '{${libraryStrategyTypesString}}',
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
    },
    "width": "100%"
  });

  jQuery("#cinput .barcodeStrategy").editable(function (value, settings) {
    return value;
  },
  {
    loadurl: '../../library/barcodeStrategies',
    loaddata: function (value, settings) {
      DatatableUtils.collapseInputs('#cinput');
      var row = datatable.fnGetPosition(this)[0];
      var platform = datatable.fnGetData(row, 4);
      if (!Utils.validation.isNullCheck(platform)) {
        return {'platform': platform};
      }
      else {
        alert("Please select a platform in this row");
        return {'platform': ''};
      }
    },
    type: 'select',
    onblur: 'submit',
    placeholder: '',
    style: 'inherit',
    callback: function (sValue, y) {
      var aPos = datatable.fnGetPosition(this);
      datatable.fnUpdate(sValue, aPos[0], aPos[1]);

      var cell = datatable.fnGetPosition(this)[2];
      var nTr = datatable.fnSettings().aoData[aPos[0]].nTr;

      Fluxion.doAjax(
        'libraryControllerHelperService',
        'getBarcodesPositions',
        {'strategy': sValue,
          'url': ajaxurl
        },
        {'doOnSuccess': function (json) {
          var randomId = makeid();
          jQuery(nTr.cells[cell + 1]).html("<div id='" + randomId + "'></div>");
          for (var i = 0; i < json.numApplicableBarcodes; i++) {
            //jQuery(nTr.cells[cell+1]).append("<span class='tagBarcodeSelectDiv' position='"+(i+1)+"' id='tagbarcodes"+(i+1)+"'>- <i>Select...</i></span>");
            jQuery('#' + randomId).append("<span class='tagBarcodeSelectDiv' position='" + (i + 1) + "' id='tagbarcodes" + (i + 1) + "'>- <i>Select...</i></span>");
            if (json.numApplicableBarcodes > 1 && i == 0) {
              // jQuery(nTr.cells[cell+1]).append("|");
              jQuery('#' + randomId).append("|");
            }
          }

          //bind editable to selects
          jQuery("#cinput .tagBarcodeSelectDiv").editable(function (value, settings) {
            return value;
          },
          {
            loadurl: '../../library/barcodesForPosition',
            loaddata: function (value, settings) {
              var ret = {};
              ret["position"] = jQuery(this).attr("position");
              if (!Utils.validation.isNullCheck(sValue)) {
                ret['barcodeStrategy'] = sValue;
              }
              else {
                ret['barcodeStrategy'] = '';
              }

              return ret;
            },
            type: 'select',
            onblur: 'submit',
            placeholder: '',
            style: 'inherit',
            submitdata: function (tvalue, tsettings) {
              return {
                "row_id": this.parentNode.getAttribute('id'),
                "column": datatable.fnGetPosition(this)[2]
              };
            }
          });
        }
        });
    },
    submitdata: function (value, settings) {
      return {
        "row_id": this.parentNode.getAttribute('id'),
        "column": datatable.fnGetPosition(this)[2]
      };
    },
    "width": "100%"
  });

  jQuery("#cinput .passedCheck").editable(function (value, settings) {
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
    },
    "width": "100%"
  });
}

function makeid() {
  var text = "";
  var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

  for (var i = 0; i < 5; i++)
    text += possible.charAt(Math.floor(Math.random() * possible.length));

  return text;
}

function submitBulkLibraries() {
  jQuery('#bulkLibSaveButton').attr('disabled', 'disabled');
  jQuery('#bulkLibSaveButton').html("Processing...");

  DatatableUtils.collapseInputs('#cinput');

  var table = jQuery('#cinput').dataTable();
  var nodes = DatatableUtils.fnGetSelected(table);
  var ok = true;
  var arr = [];
  for (var i = 0; i < nodes.length; i++) {
    var obj = {};
    for (var j = 1; j < (nodes[i].cells.length); j++) {
      obj[headers[j]] = jQuery(nodes[i].cells[j]).text();
    }

    if (Utils.validation.isNullCheck(obj["description"]) ||
        Utils.validation.isNullCheck(obj["platform"]) ||
        Utils.validation.isNullCheck(obj["libraryType"]) ||
        Utils.validation.isNullCheck(obj["selectionType"]) ||
        Utils.validation.isNullCheck(obj["strategyType"])) {
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
      'libraryControllerHelperService',
      'bulkSaveLibraries',
      {'projectId':${library.sample.project.id},
        'libraries': "[" + arr.join(',') + "]",
        'url': ajaxurl
      },
      {'doOnSuccess': function (json) {
        window.location.href = '<c:url value='/miso/project/${library.sample.project.id}'/>';
      }
      });
  }
  else {
    alert("Red data rows do not have one or more of the following: description, platform, library type, selection type or strategy type!");
  }
  jQuery('#bulkLibSaveButton').removeAttr('disabled');
  jQuery('#bulkLibSaveButton').html("Save");
}
</script>
</c:if>
</div>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>