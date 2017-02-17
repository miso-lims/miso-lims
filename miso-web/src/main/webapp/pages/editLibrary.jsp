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
  User: davey
  Date: 15-Feb-2010
  Time: 15:09:06

--%>
<%@ include file="../header.jsp" %>
<script src="<c:url value='/scripts/jquery/js/jquery.breadcrumbs.popup.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/datatables_utils.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/datatables/js/jquery.dataTables.min.js'/>" type="text/javascript"></script>
<link href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" rel="stylesheet" type="text/css">

<div id="maincontent">
<div id="contentcolumn">
<c:if test="${library.id == 0}">
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.mini.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.datepicker.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.checkbox.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.radio.js'/>" type="text/javascript"></script>

<div id="tabs">
<ul>
  <li><a href="#tab-1"><span>Single</span></a></li>
  <li><a href="#tab-2"><span>Bulk</span></a></li>
</ul>

<div id="tab-1">
</c:if>

<form:form id="library-form" data-parsley-validate="" action="/miso/library" method="POST" commandName="library" autocomplete="off" acceptCharset="utf-8">
<sessionConversation:insertSessionConversationId attributeName="library"/>
<h1>
  <c:choose>
    <c:when test="${library.id != 0}">Edit</c:when>
    <c:otherwise>Create</c:otherwise>
  </c:choose> Library
  <button type="button" class="fg-button ui-state-default ui-corner-all"
          onclick="return Library.validateLibrary();">Save
  </button>
</h1>
<div class="breadcrumbs">
  <ul>
    <li>
      <a href='<c:url value="/"/>'>Home</a>
    </li>
    <li>
      <div class="breadcrumbsbubbleInfo">
        <div class="trigger">
          <a href='<c:url value="/miso/project/${library.sample.project.id}"/>'>${library.sample.project.alias}</a>
        </div>
        <div class="breadcrumbspopup">
            ${library.sample.project.name}
        </div>
      </div>
    </li>
    <li>
      <div class="breadcrumbsbubbleInfo">
        <div class="trigger">
          <a href='<c:url value="/miso/sample/${library.sample.id}"/>'>${library.sample.alias}</a>
        </div>
        <div class="breadcrumbspopup">
            ${library.sample.name}
        </div>
      </div>
    </li>
  </ul>
  <c:if test="${not empty nextLibrary}">
    <span style="float:right; padding-top: 5px; padding-left: 6px">
      <a class='arrowright' href='<c:url value="/miso/library/${nextLibrary.id}"/>'>Next Library <b>${nextLibrary.alias}</b></a>
    </span>
  </c:if>
  <c:if test="${not empty previousLibrary}">
    <span style="float:right; padding-top: 5px">
      <a class='arrowleft' href='<c:url value="/miso/library/${previousLibrary.id}"/>'>Previous Library <b>${previousLibrary.alias}</b></a>
    </span>
  </c:if>
</div>
<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
  <div id="note_arrowclick" class="toggleLeft"></div>
</div>
<div id="notediv" class="note" style="display:none;">A Library is the first step in constructing sequenceable
  material from an initial Sample. A Library is then diluted down to a Dilution, and put in a Pool.
</div>

<div class="bs-callout bs-callout-warning hidden">
  <h2>Oh snap!</h2>
  <p>This form seems to be invalid</p>
</div>

<h2>Library Information</h2>

<div class="barcodes">
  <div class="barcodeArea ui-corner-all">
    <span style="float: left; font-size: 24px; font-weight: bold; color:#BBBBBB">Barcode</span>
    <c:if test="${library.id != 0}">
      <ul class="barcode-ddm">
        <li>
          <a onmouseover="mopen('idBarcodeMenu')" onmouseout="mclosetime()">
            <span style="float:right; margin-top:6px;" class="ui-icon ui-icon-triangle-1-s"></span>
            <span id="idBarcode" style="float:right"></span>
          </a>

          <div id="idBarcodeMenu" onmouseover="mcancelclosetime()" onmouseout="mclosetime()">
            <a href="javascript:void(0);"
               onclick="Library.barcode.printLibraryBarcodes(${library.id});">Print</a>
            <c:if test="${not autoGenerateIdBarcodes}">
              <a href="javascript:void(0);"
               onclick="Library.barcode.showLibraryIdBarcodeChangeDialog(${library.id}, '${library.identificationBarcode}');">Update Barcode</a>
            </c:if>
          </div>
        </li>
      </ul>
    </c:if>
    <div id="changeLibraryIdBarcodeDialog" title="Assign New Barcode"></div>
    <c:if test="${not empty library.identificationBarcode}">
      <script type="text/javascript">
        jQuery(document).ready(function () {
          Fluxion.doAjax(
            'libraryControllerHelperService',
            'getLibraryBarcode',
            {'libraryId':${library.id},
              'url': ajaxurl
            },
            {'doOnSuccess': function (json) {
              jQuery('#idBarcode').html("<img style='height:30px; border:0;' alt='${library.identificationBarcode}' title='${library.identificationBarcode}' src='<c:url value='/temp/'/>" + json.img + "'/>");
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
  <td class="h">Name:</td>
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
        <a href='<c:url value="/miso/sample/${library.sample.id}"/>'>${library.sample.alias} (${library.sample.name})</a>
      </c:otherwise>
    </c:choose>
  </td>
</tr>
<tr>
  <td class="h">Alias:*</td>
  <td>
    <c:choose>
      <c:when test="${not empty autogeneratedLibraryAlias}">
        <form:input id="alias" path="alias" value="${autogeneratedLibraryAlias}" class="validateable"/>
      </c:when>
      <c:otherwise>
        <form:input id="alias" path="alias" class="validateable"/>
      </c:otherwise>
    </c:choose>

    <span id="aliasCounter" class="counter"></span>
    <c:if test="${detailedSample && library.hasNonStandardAlias()}">
      <ul class="parsley-errors-list filled" id="nonStandardAlias">
        <li class="parsley-custom-error-message">
        Double-check this alias -- it will be saved even if it is duplicated or does not follow the naming standard!
        </li>
      </ul>
    </c:if>
  </td>
</tr>
<tr>
  <td>Description:</td>
  <td>
    <%-- FIX MISO-107
    <c:choose>
      <c:when test='${not empty library.sample}'>
        <form:input path="description" value="${library.sample.description}" class="validateable"/>
      </c:when>
      <c:otherwise>
        <form:input path="description" class="validateable"/>
      </c:otherwise>
    </c:choose>
    --%>
    <form:input id="description" path="description" class="validateable"/>
    <span id="descriptionCounter" class="counter"></span></td>
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
<c:choose>
  <c:when test="${!empty library.sample && detailedSample}">
    <input type="hidden" value="true" name="paired" id="paired"/>
  </c:when>
  <c:otherwise>
	<tr>
	  <td>Paired:</td>
	  <td>
	    <c:choose>
	      <c:when test="${library.id != 0}">
	        <form:checkbox id="paired" path="paired" checked="checked"/>
	      </c:when>
	      <c:otherwise>
	        <form:checkbox id="paired" path="paired"/>
	      </c:otherwise>
	    </c:choose>
	  </td>
	</tr>
  </c:otherwise>
</c:choose>

<tr>
  <c:choose>
    <c:when test="${library.id == 0 or empty library.libraryType or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')
        or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_TECH')}">
      <td>Platform - Library Type:</td>
      <td>
        <form:select id="platformTypes" path="platformType" items="${platformTypes}"
                     onchange="Library.ui.changePlatformType(null);" class="validateable"/>
        <form:select id="libraryTypes" path="libraryType"/>
      </td>
      <script type="text/javascript">
        jQuery(document).ready(function () {
          Library.ui.changePlatformType(<c:out value="${library.libraryType.id}" default="0"/>, function() {
            <c:if test="${not empty library.libraryType}">jQuery('#libraryTypes').val('${library.libraryType.id}');</c:if>
            Library.setOriginalIndices();
          });
        });
      </script>
    </c:when>
    <c:otherwise>
      <td>Platform - Library Type</td>
      <td>${library.platformType} - ${library.libraryType.description}</td>
    </c:otherwise>
  </c:choose>
</tr>
<tr>
<c:if test="${!empty library.sample && detailedSample}">
  <tr>
    <td>Library Design:</td>
    <td>
      <miso:select id="libraryDesignTypes" path="libraryDesign" items="${libraryDesigns}" itemLabel="name" itemValue="id" defaultLabel="(None)" defaultValue="-1" onchange="Library.ui.changeDesign()"/>
      &nbsp;&nbsp;&nbsp;Design Code: <miso:select id="libraryDesignCodes" path="libraryDesignCode" items="${libraryDesignCodes}" itemLabel="code" itemValue="id" defaultLabel="(None)" defaultValue="-1"/>
    </td>
  </tr>
</c:if>
  <c:choose>
    <c:when test="${library.id == 0
                  or empty library.librarySelectionType
                  or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')
                  or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_TECH')}">
      <td>Library Selection Type:</td>
      <td>
        <form:select id="librarySelectionTypes" path="librarySelectionType" items="${librarySelectionTypes}"
                     itemLabel="name" itemValue="id" defaultLabel="(None)" defaultValue="-1" />
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
                     itemLabel="name" itemValue="id" defaultLabel="(None)" defaultValue="-1" />
      </td>
    </c:when>
    <c:otherwise>
      <td>Library Strategy Type:</td>
      <td>${library.libraryStrategyType.name}</td>
    </c:otherwise>
  </c:choose>
</tr>
<tr>
  <td>Index Family:</td>
  <td>
    <miso:select id='indexFamily' name='indexFamily' path="currentFamily" items="${indexFamilies}" itemLabel="name" itemValue="id" onchange='Library.ui.updateIndices();'/>
  </td>
</tr>

<tr>
  <td>Indices:</td>
  <td id="indicesDiv">
  </td>
  <script type="text/javascript">
    Library = Library || {};
    Library.indexFamilies = ${indexFamiliesJSON};
    Library.setOriginalIndices = function() {
      Library.lastIndexPosition = 0;
      jQuery('#indicesDiv').empty();
      document.getElementById('indexFamily').value = '${library.getCurrentFamily().id}';
      <c:forEach items="${library.indices}" var="index">
        <c:if test="${index.id != 0}">
          Library.ui.createIndexBox(${index.id});
        </c:if>
      </c:forEach>
      Library.ui.createIndexNextBox();
    };
    Library.setOriginalIndices();
  </script>
</tr>

<tr bgcolor="yellow">
  <td>QC Passed:*</td>
  <td>
    <form:radiobutton path="qcPassed" value="" label="Unknown"/>
    <form:radiobutton path="qcPassed" value="true" label="True"/>
    <form:radiobutton path="qcPassed" value="false" label="False"/>
  </td>
</tr>

<tr>
  <td>Low Quality Sequencing:</td>
  <td>
    <form:checkbox path="lowQuality"/>
  </td>
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
    <c:if test="${!empty library.box.locationBarcode}">${ library.box.locationBarcode},</c:if>
    <c:if test="${!empty library.boxPosition}"><a href='<c:url value="/miso/box/${library.box.id}"/>'>${library.box.alias}, ${library.boxPosition}</a></c:if>
  </td>
</tr>
</table>
<%@ include file="volumeControl.jspf" %>

<c:if test="${detailedSample}">
<br/>
<br/>
<h2>Details</h2>
<table class="in">
  <tr>
    <td>Library Kit:*</td>
    <td>
      <miso:select id="libraryKit" path="kitDescriptor" items="${prepKits}" itemLabel="name"
          itemValue="id" defaultLabel="SELECT" defaultValue=""/>
    </td>
  </tr>
  <c:if test="${not empty library.sample.groupId}">
  <tr>
    <td class="h">Group ID:</td>
    <td>${library.sample.groupId}</td>
  </tr>
  <tr>
    <td class="h">Group Description:</td>
    <td>${library.sample.groupDescription}</td>
  </tr>
  </c:if>
  <tr>
    <td class="h">Archived:</td>
    <td><form:checkbox id="archived" path="archived"/></td>
  </tr>
</table>
</c:if>
<script type="text/javascript">
  Hot.detailedSample = ${detailedSample};
</script>

<c:choose>
  <c:when
      test="${!empty library.sample and library.securityProfile.profileId eq library.sample.project.securityProfile.profileId}">
    <table class="in">
    <tr>
      <td>Permissions</td>
      <td><i>Inherited from sample </i>
        <a href='<c:url value="/miso/sample/${library.sample.id}"/>'>${library.sample.name} (${library.sample.alias})</a>
        <input type="hidden" value="${library.sample.securityProfile.profileId}"
               name="securityProfile" id="securityProfile"/>
      </td>
    </tr>
    </table>
  </c:when>
  <c:otherwise>
    <%@ include file="permissions.jsp" %>
  </c:otherwise>
</c:choose>

<script type="text/javascript">
  jQuery(document).ready(function () {
    // Attach Parsley form validator
    Validate.attachParsley('#library-form');
  });
</script>

<c:if test="${library.id != 0}">
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

        <div id="notesmenu" onmouseover="mcancelclosetime()" onmouseout="mclosetime()">
          <a onclick="Library.ui.showLibraryNoteDialog(${library.id});" href="javascript:void(0);" class="add">Add Note</a>
        </div>
      </li>
    </ul>
    <c:if test="${fn:length(library.notes) > 0}">
      <div class="note" style="clear:both">
        <c:forEach items="${library.notes}" var="note" varStatus="n">
          <div class="exppreview" id="library-notes-${n.count}">
            <b>${note.creationDate}</b>: ${note.text}
              <span class="float-right" style="font-weight:bold; color:#C0C0C0;">${note.owner.loginName}
                <c:if test="${(note.owner.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                                or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
                <span style="color:#000000"><a href='#' onclick="Library.ui.deleteLibraryNote('${library.id}', '${note.noteId}');">
                  <span class="ui-icon ui-icon-trash" style="clear: both; position: relative; float: right; margin-top: -15px;"/></a></span>
                </c:if>
              </span>
          </div>
        </c:forEach>
      </div>
    </c:if>
    <div id="addLibraryNoteDialog" title="Create new Note"></div>
  </div>
</c:if>
</form:form>
<br/>
<c:if test="${library.id != 0}">
<h1>
  <div id="qcsTotalCount">
  </div>
</h1>
<ul class="sddm">
  <li>
    <a onmouseover="mopen('qcmenu')" onmouseout="mclosetime()">Options
      <span style="float:right" class="ui-icon ui-icon-triangle-1-s"></span>
    </a>

    <div id="qcmenu" onmouseover="mcancelclosetime()" onmouseout="mclosetime()">
      <a href='javascript:void(0);' class="add" onclick="Library.qc.insertLibraryQCRow(${library.id}); return false;">Add Library QC</a>
    </div>
  </li>
</ul>
<span style="clear:both">
  <div id="addLibraryQC"></div>
  <form id='addQcForm'>
    <table class="list" id="libraryQcTable">
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

            <fmt:formatNumber var="resultsRounded"
              value="${qc.results}"
              maxFractionDigits="2" />

            <td id="result${qc.id}">${resultsRounded} ${qc.qcType.units}</td>
            <td id="insert${qc.id}">${qc.insertSize} bp</td>
            <c:if test="${(library.securityProfile.owner.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                        or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
              <td id="edit${qc.id}" align="center">
                <a href="javascript:void(0);" onclick="Library.qc.changeLibraryQCRow('${qc.id}','${library.id}')">
                  <span class="ui-icon ui-icon-pencil"></span>
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

    var qcsCount = jQuery('#libraryQcTable>tbody>tr:visible').length;
    jQuery('#qcsTotalCount').html(qcsCount + (qcsCount == 1 ? ' QC' : ' QCs'));
    var libDilsCount = jQuery('#libraryDilutionTable>tbody>tr:visible').length;
    jQuery('#ldsTotalCount').html(libDilsCount + (libDilsCount == 1 ? ' Library Dilution' : ' Library Dilutions'));
  });
</script>

<h1>
  <div id="ldsTotalCount">
  </div>
</h1>
<ul class="sddm">
  <li>
    <a onmouseover="mopen('ldmenu')" onmouseout="mclosetime()">Options
      <span style="float:right" class="ui-icon ui-icon-triangle-1-s"></span>
    </a>

    <div id="ldmenu" onmouseover="mcancelclosetime()" onmouseout="mclosetime()">
      <a href='javascript:void(0);' class="add" onclick="Library.dilution.insertLibraryDilutionRow(${library.id}, <c:out value="${library.kitDescriptor.id}" default="0"/>, ${autoGenerateIdBarcodes}); return false;">
        Add Library Dilution
      </a>
      <c:if test="${not empty library.libraryDilutions}">
      <a href='<c:url value="/miso/poolwizard/new/${library.sample.project.id}"/>'>Create Pools</a>
      </c:if>
    </div>
  </li>
</ul>
<span style="clear:both">
  <div id="addLibraryDilution"></div>
  <form id='addDilutionForm'>
    <table class="list" id="libraryDilutionTable">
      <thead>
      <tr>
        <th>LD Name</th>
        <th>Done By</th>
        <th>Date</th>
        <th>Concentration (${libraryDilutionUnits})</th>
        <c:if test="${detailedSample}">
          <th>Targeted Sequencing</th>
        </c:if>
        <th>ID Barcode</th>
        <th align="center">Edit</th>
        <th align="center">Pool</th>
      </tr>
      </thead>
      <tbody>
      <c:if test="${not empty library.libraryDilutions}">
        <c:forEach items="${library.libraryDilutions}" var="dil">
          <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
            <td>${dil.name}</td>
            <td>${dil.dilutionCreator}</td>
            <td><fmt:formatDate value="${dil.creationDate}"/></td>
            <fmt:formatNumber var="concentrationRounded"
                          value="${dil.concentration}"
                          maxFractionDigits="2" />
            <td id="results${dil.id}">${concentrationRounded}</td>
            <c:if test="${detailedSample}">
              <td id="tarSeq${dil.id}">
                <c:if test="${empty dil.targetedSequencing}">
                  NONE
                </c:if>
                <c:if test="${not empty dil.targetedSequencing}">
                  ${dil.targetedSequencing.alias}
                </c:if>
              </td>
            </c:if>
            <td class="fit" id="idBarcode${dil.id}">
              <c:if test="${not empty dil.identificationBarcode}">
                <div class="barcodes">
                  <div class="barcodeArea ui-corner-all">
                    <ul class="barcode-ddm">
                      <li>
                        <a onmouseover="mopen('dil${dil.id}IdBarcodeMenu')" onmouseout="mclosetime()">
                          <span style="float:right; margin-top:6px;" class="ui-icon ui-icon-triangle-1-s"></span>
                          <span id="dil${dil.id}IdBarcode" style="float:right"></span>
                        </a>

                        <div id="dil${dil.id}IdBarcodeMenu" onmouseover="mcancelclosetime()" onmouseout="mclosetime()">
                          <a href="javascript:void(0);" onclick="Library.barcode.printDilutionBarcode(${dil.id}, '${library.platformType}');">Print</a>
                        </div>
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
                            jQuery('#dil${dil.id}IdBarcode').html(
                              "<img style='border:0;' src='<c:url value='/temp/'/>" + json.img + "'/><br/>"
                            );
                          }
                          });
                      });
                    </script>
                  </div>
                </div>

              </c:if>
            </td>
            <td id="edit${dil.id}" align="center">
              <a href="javascript:void(0);" onclick="Library.dilution.changeLibraryDilutionRow('${dil.id}',${autoGenerateIdBarcodes}, ${detailedSample})">
                <span class="ui-icon ui-icon-pencil"></span>
              </a>
            </td>
            <td>
              <a href="<c:url value="/miso/poolwizard/new/${library.sample.project.id}"/>">Construct New Pool</a>
            </td>
          </tr>
        </c:forEach>
      </c:if>
      </tbody>
    </table>
    <input type='hidden' id='dilLibraryId' name='id' value='${library.id}'/>
  </form>
</span>

  <c:if test="${not empty libraryPools}">
    <br/>
    <h1>${fn:length(libraryPools)} Pool<c:if test="${fn:length(libraryPools) ne 1}">s</c:if></h1>
    <ul class="sddm">
      <li>
        <a onmouseover="mopen('poolsmenu')" onmouseout="mclosetime()">Options
          <span style="float:right" class="ui-icon ui-icon-triangle-1-s"></span>
        </a>

        <div id="poolsmenu" onmouseover="mcancelclosetime()" onmouseout="mclosetime()">
        <a href="javascript:void(0);" onclick="Pool.barcode.selectPoolBarcodesToPrint('#pools_table');">Print Barcodes ...</a>
        </div>
      </li>
    </ul>

    <span style="clear:both">
      <table class="list" id="pools_table">
        <thead>
        <tr>
          <th>Pool Name</th>
          <th>Pool Alias</th>
          <th>Pool Platform</th>
          <th>Pool Creation Date</th>
          <th>Pool Concentration (${poolConcentrationUnits})</th>
          <sec:authorize access="hasRole('ROLE_ADMIN')">
            <th class="fit">DELETE</th>
          </sec:authorize>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${libraryPools}" var="pool">
          <tr poolId="${pool.id}" onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
            <td><b><a href="<c:url value='/miso/pool/${pool.id}'/>">${pool.name}</a></b></td>
            <td><a href="<c:url value='/miso/pool/${pool.id}'/>">${pool.alias}</a></td>
            <td>${pool.platformType.key}</td>
            <td>${pool.creationDate}</td>
            <td>${pool.concentration}</td>
            <sec:authorize access="hasRole('ROLE_ADMIN')">
              <td class="misoicon" onclick="Pool.deletePool(${pool.id}, Utils.page.pageReload);">
                <span class="ui-icon ui-icon-trash"/>
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
              null
              <sec:authorize access="hasRole('ROLE_ADMIN')">, null</sec:authorize>
            ],
            "iDisplayLength": 50,
            "bJQueryUI": true,
            "bRetrieve": true
          });
        });
      </script>
    </span>
  </c:if>

  <c:if test="${not empty libraryRuns}">
    <br/>
    <h1>${fn:length(libraryRuns)} Runs</h1>

    <table class="list" id="run_table">
      <thead>
      <tr>
        <th>Run Name</th>
        <th>Run Alias</th>
        <th>Partitions</th>
        <th>Status</th>
        <sec:authorize access="hasRole('ROLE_ADMIN')">
          <th class="fit">DELETE</th>
        </sec:authorize>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${libraryRuns}" var="run" varStatus="runCount">
        <tr runId="${run.id}" onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
          <td><b><a href="<c:url value='/miso/run/${run.id}'/>">${run.name}</a></b></td>
          <td><a href="<c:url value='/miso/run/${run.id}'/>">${run.alias}</a></td>
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
          <td>${run.health}</td>
          <sec:authorize access="hasRole('ROLE_ADMIN')">
            <td class="misoicon" onclick="Run.deleteRun(${run.id}, Utils.page.pageReload);">
              <span class="ui-icon ui-icon-trash"/>
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
          "bJQueryUI": true,
          "bRetrieve": true
        });
      });
    </script>
  </c:if>

  <c:if test="${not empty library.changeLog}">
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
      <c:forEach items="${library.changeLog}" var="change">
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
            <a href='<c:url value="/miso/project/${library.sample.project.id}"/>'>${library.sample.project.alias}</a>
          </div>
          <div class="breadcrumbspopup">
              ${library.sample.project.name}
          </div>
        </div>
      </li>
      <li>
        <div class="breadcrumbsbubbleInfo">
          <div class="trigger">
            <a href='<c:url value="/miso/sample/${library.sample.id}"/>'>${library.sample.alias}</a>
          </div>
          <div class="breadcrumbspopup">
              ${library.sample.name}
          </div>
        </div>
      </li>
    </ul>
  </div>
  <h1>Create Libraries
    <button id="bulkLibSaveButton" onClick="submitBulkLibraries();"
            class="fg-button ui-state-default ui-corner-all">Save
    </button>
  </h1>
  <br/>

  This system will create <b>ONE</b> library for each of the selected samples below:
  <br/>
  <table id="cinput" class="display">
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
      <th style="width: 10%">Index Family <span header="indexFamily" class="ui-icon ui-icon-arrowstop-1-s"
                                               style="float:right"
                                               onclick="Library.ui.fillDownIndexFamilySelects('#cinput', this);"></span>
      </th>
      <th style="width: 10%">Indices <span header="indices" class="ui-icon ui-icon-arrowstop-1-s"
                                                style="float:right"
                                                onclick="Library.ui.fillDownIndexSelects('#cinput', this);"></span>
      </th>
      <th style="width: 10%">Location <span header="locationBarcode" class="ui-icon ui-icon-arrowstop-1-s"
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
</div>

<script type="text/javascript">
var headers = ['rowsel',
               'parentSample',
               'description',
               'paired',
               'platform',
               'libraryType',
               'selectionType',
               'strategyType',
               'indexFamily',
               'indices',
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
    "bJQueryUI": true,
    "bAutoWidth": true,
    "bSort": false,
    "bFilter": false,
    "sDom": '<<"toolbar">f>r<t>ip>'
  });

  var sampleArray = [];
  var sampleDescArray = [];

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
        ""
      ]
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
  } else {
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
    else if (headers[i] === "indexFamily") {
      jQuery(nTr.cells[i]).addClass("indexFamily");
    }
    else if (headers[i] === "indices") {
      jQuery(nTr.cells[i]).addClass("indices");
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
    data: '{${platformTypesString}}',
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

  jQuery("#cinput .indexFamily").editable(function (value, settings) {
    return value;
  },
  {
    loadurl: '../../library/indexFamilies',
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

      jQuery.ajax('../../library/indexPositionsJson', {
        success: function (json) {
          var randomId = makeid();
          jQuery(nTr.cells[cell + 1]).html("<div id='" + randomId + "'></div>");
          for (var i = 0; i < json.numApplicableIndices; i++) {
            jQuery('#' + randomId).append("<span class='indexSelectDiv' position='" + (i + 1) + "' id='indices" + (i + 1) + "'>- <i>Select...</i></span>");
            if (json.numApplicableIndices > 1 && i == 0) {
              jQuery('#' + randomId).append("|");
            }
          }

          //bind editable to selects
          jQuery("#cinput .indexSelectDiv").editable(function (value, settings) {
              return value;
            },
            {
              loadurl: '../../library/indicesForPosition',
              loaddata: function (value, settings) {
                var ret = {};
                ret["position"] = jQuery(this).attr("position");
                if (!Utils.validation.isNullCheck(sValue)) {
                  ret['indexFamily'] = sValue;
                } else {
                  ret['indexFamily'] = '';
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
            }
          );
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

  for (var i = 0; i < 5; i++) {
    text += possible.charAt(Math.floor(Math.random() * possible.length));
  }
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
    } else {
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
  } else {
    alert("Red data rows do not have one or more of the following: description, platform, library type, selection type or strategy type!");
  }
  jQuery('#bulkLibSaveButton').removeAttr('disabled');
  jQuery('#bulkLibSaveButton').html("Save");
}
</script>
</c:if>
</div>
</div>

<script type="text/javascript">
  Library.designs = ${libraryDesignsJSON};
  jQuery(document).ready(function () {
    Library.ui.changeDesign(<c:out value="${library.libraryType.id}" default="0"/>, function() {
      Library.setOriginalIndices();
    });
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
</script>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
