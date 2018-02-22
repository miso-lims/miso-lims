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

<script src="<c:url value='/scripts/jquery/datatables/js/jquery.dataTables.min.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.mini.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.datepicker.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.checkbox.js'/>" type="text/javascript"></script>
<link href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" rel="stylesheet" type="text/css" />
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables_themeroller.css'/>">
<link href="<c:url value='/scripts/handsontable/dist/pikaday/pikaday.css'/>" rel="stylesheet" type="text/css" />
<script src="<c:url value='/scripts/handsontable/dist/pikaday/pikaday.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/handsontable/dist/moment/moment.js'/>" type="text/javascript"></script>

<script src="<c:url value='/scripts/handsontable/dist/handsontable.full.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/handsontable_renderers.js'/>" type="text/javascript"></script>
<link rel="stylesheet" media="screen" href="/scripts/handsontable/dist/handsontable.full.css">

<div id="maincontent">
<div id="contentcolumn">

<form:form id="sample-form" data-parsley-validate="" action="/miso/sample" method="POST" commandName="sample" autocomplete="off" acceptCharset="utf-8">
<sessionConversation:insertSessionConversationId attributeName="sample"/>
<input type="hidden" id="sampleCategory" name="sampleCategory" value="${sampleId eq 0 ? 'new' : sampleCategory}"/>
<h1>
  <c:choose>
    <c:when test="${sample.id != 0}"><span id="status" data-status="edit">Edit</span></c:when>
    <c:otherwise><span id="status" data-status="create">Create</span></c:otherwise>
  </c:choose> Sample
  <button type="button" class="fg-button ui-state-default ui-corner-all"
      onclick="return Sample.validateSample(${detailedSample},
      ${detailedSample && (sample.hasNonStandardAlias() || sample.parent.hasNonStandardAlias())}, ${sample.id == 0});">
    Save
  </button>
</h1>
<div class="right fg-toolbar ui-helper-clearfix paging_full_numbers">
  <c:if test="${sample.id != 0 && not empty sample.identificationBarcode}"><span class="ui-button ui-state-default" onclick="Utils.printDialog('sample', [${sample.id}]);">Print Barcode</span></c:if>
</div>

<c:if test="${not empty sample.project}">
  <div class="breadcrumbs">
    <ul>
      <li>
        <a href="/">Home</a>
      </li>
      <li>
        <div class="breadcrumbsbubbleInfo">
          <div class="trigger">
            <a href='<c:url value="/miso/project/${sample.project.id}"/>'>${detailedSample ? sample.project.shortName : sample.project.alias}</a>
          </div>
          <div class="breadcrumbspopup">
              ${sample.project.name}
          </div>
        </div>
      </li>
    </ul>
    <c:if test="${not empty nextSample}">
      <span style="float:right; padding-top: 5px; padding-left: 6px">
        <a class='arrowright' href='<c:url value="/miso/sample/${nextSample.id}"/>'>Next Sample <b>${nextSample.name}</b></a>
      </span>
    </c:if>
    <c:if test="${not empty previousSample}">
      <span style="float:right; padding-top: 5px">
        <a class='arrowleft' href='<c:url value="/miso/sample/${previousSample.id}"/>'>Previous Sample <b>${previousSample.name}</b></a>
      </span>
    </c:if>
  </div>
</c:if>
<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
  <div id="note_arrowclick" class="toggleLeft"></div>
</div>
<div id="notediv" class="note" style="display:none;">A Sample contains information about the material upon which the
  sequencing experiments are to be based. Samples can be used in any number of sequencing Experiments in the form
  of a Library that is often processed further into pooled Dilutions.
</div>

<div class="bs-callout bs-callout-warning hidden">
  <h2>Oh snap!</h2>
  <p>This form seems to be invalid!</p>
</div>

<h2>Sample Information</h2>
<div>
  <table class="in" <c:if test="${detailedSample && sample.isSynthetic()}">style="background-color: #ddd"</c:if>>
    <tr>
      <td class="h">Sample ID:</td>
      <td><span id="sampleId">
        <c:choose>
          <c:when test="${sample.id != 0}">${sample.id}</c:when>
          <c:otherwise><i>Unsaved</i></c:otherwise>
        </c:choose>
      </span></td>
    </tr>
    <tr>
      <td colspan="2">
        <c:if test="${!empty warning}">
          <span style="float:right;"><img src="/styles/images/fail.png"/></span>
          <p class="big-warning">${warning}</p>
        </c:if>
      </td>
    </tr>
    <c:if test="${detailedSample && sample.isSynthetic()}"><tr><td colspan="2" style="font-size: 200%; font-weight:bold;">This entity does not exist except for sample tracking purposes!</td></tr></c:if>
    <tr>
      <td>Project:*</td>
      <c:choose>
        <c:when test="${empty sample.project}">
          <td>
            <form:select id="project" path="project" onchange="Sample.ui.projectChanged();">
              <option value="">SELECT</option>
              <c:forEach items="${accessibleProjects}" var="proj">
                <option value="${proj.id}" <c:if test="${proj.id == sample.project.id}">selected="selected"</c:if>>
                  <c:choose>
                    <c:when test="${detailedSample}">${proj.shortName} (${proj.name})</c:when>
                    <c:otherwise>${proj.name}: ${proj.alias}</c:otherwise>
                  </c:choose>
                </option>
              </c:forEach>
            </form:select>
          </td>
        </c:when>
        <c:otherwise>
          <td>
            <input type="hidden" value="${sample.project.id}" name="project" id="project"/>
            <a href='<c:url value="/miso/project/${sample.project.id}"/>'><span id="projectName">${sample.project.name} (<c:if test="${detailedSample}">${sample.project.shortName} &#8212; </c:if>${sample.project.alias})</span></a>
          </td>
        </c:otherwise>
      </c:choose>
    </tr>
    <tr>
      <td>Name:</td>
      <td><span id="name">
        <c:choose>
          <c:when test="${sample.id != 0}">${sample.name}</c:when>
          <c:otherwise><i>Unsaved</i></c:otherwise>
        </c:choose>
      </span></td>
    </tr>
    <tr>
      <td class="h">
        Alias:
        <c:choose>
          <c:when test="${!aliasGenerationEnabled || sample.id != 0}">
            *
          </c:when>
          <c:otherwise>
            (blank to auto-generate)
          </c:otherwise>
        </c:choose>
      </td>
      <td><form:input id="alias" path="alias" name="alias" data-parsley-required='${!aliasGenerationEnabled || sample.id != 0}'/><span id="aliasCounter" class="counter"></span>
        <c:if test="${detailedSample}">
          <c:if test="${sample.hasNonStandardAlias() || sample.parent.hasNonStandardAlias()}">
	          <ul class="parsley-errors-list filled" id="nonStandardAlias">
	            <li class="parsley-custom-error-message">
	            Double-check this alias -- it will be saved even if it is duplicated or does not follow the naming standard!
	            </li>
	          </ul>
	        </c:if>
        </c:if>
      </td>
    </tr>
    <tr>
      <td>Description:</td>
      <td><form:input id="description" path="description"/><span id="descriptionCounter" class="counter"></span>
      </td>
    </tr>
    <c:if test="${not autoGenerateIdBarcodes}">
      <tr>
        <td class="h">Matrix Barcode:</td>
        <td><form:input id="identificationBarcode" path="identificationBarcode" name="identificationBarcode"/></td>
      </tr>
    </c:if>
    <tr>
      <td>Date of receipt:</td>
      <td>
        <form:input path="receivedDate" id="receiveddatepicker" placeholder="YYYY-MM-DD"/>
        <script type="text/javascript">
          Utils.ui.addDatePicker("receiveddatepicker");
        </script>
      </td>
    </tr>
    <tr>
      <td class="h">Scientific Name:*</td>
      <td><form:input id="scientificName" path="scientificName" value="${sample.scientificName.length() > 0 ? sample.scientificName : defaultSciName}" /><span id="scientificNameCounter" class="counter"></span>
        <c:if test="${sessionScope.taxonLookupEnabled}">
        <script>Utils.timer.typewatchFunc(jQuery('#scientificName'), Sample.validateNCBITaxon, 1000, 2);</script>
        </c:if>
      </td>
    </tr>
    <c:if test="${not empty sample.accession}">
      <tr>
        <td class="h">Accession:</td>
        <td><a href="http://www.ebi.ac.uk/ena/data/view/${sample.accession}">${sample.accession}</a>
        </td>
      </tr>
    </c:if>
    <tr>
      <td>Sample Type:*</td>
      <td><form:select id="sampleTypes" path="sampleType" items="${sampleTypes}"/></td>
    </tr>
    <c:choose>
    <c:when test="${not detailedSample}">
	  <tr bgcolor="yellow">
	    <td>QC Passed:</td>
	    <td>
	      <form:radiobutton path="qcPassed" value="" label="Unknown"/>
	      <form:radiobutton path="qcPassed" value="true" label="True"/>
	      <form:radiobutton path="qcPassed" value="false" label="False"/>
	    </td>
	  </tr>
    </c:when>
    <c:otherwise>
      <tr>
        <td>QC Status*:</td>
        <td>
          <miso:select id="detailedQcStatus" path="detailedQcStatus" items="${detailedQcStatuses}" itemLabel="description"
                      itemValue="id" defaultLabel="Not Ready" defaultValue="" onchange="Sample.ui.detailedQcStatusChanged();"/>
        </td>
      </tr>
      <tr id="qcStatusNote" style="display:none">
        <td>QC Status Note*:</td>
        <td><form:input id="detailedQcStatusNote" path="detailedQcStatusNote"/></td>
      </tr> 
      <c:if test="${not empty sample.detailedQcStatusNote}">
	    <script type="text/javascript">
	      jQuery(document).ready(function () {
	        jQuery('#qcStatusNote').show('fast');
	      });
	    </script>
      </c:if>
    </c:otherwise>
    </c:choose>
    <tr>
      <td>Volume (&#181;l):</td>
      <td><form:input id="volume" path="volume"/></td>
    </tr>
    <tr>
      <td><label for="discarded">Discarded:</label></td>
      <td><form:checkbox id="discarded" path="discarded"/></td>
    </tr>
    <tr>
      <td class="h"><label for="locationBarcode">Location:</label></td>
      <td><form:input id="locationBarcode" path="locationBarcode"/></td>
    </tr>
    <tr>
      <td class="h">Box Location:</td>
      <td>
        <c:if test="${!empty sample.box.locationBarcode}">${sample.box.locationBarcode},</c:if>
        <c:if test="${!empty sample.boxPosition}"><a href='<c:url value="/miso/box/${sample.box.id}"/>'>${sample.box.alias}, ${sample.boxPosition}</a></c:if>
      </td>
    </tr>
  </table>
  <%@ include file="volumeControl.jspf" %>
  <c:if test="${detailedSample}">

    <script type="text/javascript">
      <c:if test="${sample.id == 0}">
        jQuery(document).ready(function () {
          Sample.ui.sampleClassChanged();
        });
      </c:if>
    </script>

    <br/>
    <div id="detailedSample">
      <c:if test="${sampleCategory eq 'Identity' or sampleCategory eq 'new'}">
        <br/>
        <div id="detailedSampleIdentity">
          <h2>Identity</h2>
          <table class="in">
            <c:if test="${sample.id == 0}">
              <tr>
                <td class="h">Parent:</td>
                <td id="parentAliasTd"><span id="parentAlias"></span><form:input type="hidden" id="identityId" path="identityId"/></td>
              </tr>
            </c:if>
            <tr>
              <td class="h">External Names (comma separated):*</td>
              <td>
                <c:choose>
                  <c:when test="${sample.id == 0}">
                    <span id="externalNameVal"></span>  <span id="externalNameDialog" title="Find or Create Identity"></span><button type="button" onclick="Sample.ui.showExternalNameChangeDialog()">Find or Create Identity</button>
                    <form:input type="hidden" id="externalName" path="externalName"/>
                  </c:when>
                  <c:otherwise>
                    <span id="externalName">${sample.externalName}</span>
                  </c:otherwise>
                </c:choose>
              </td>
            </tr>
            <tr>
              <td class="h">Sex:</td>
              <td>
                <c:choose>
                  <c:when test="${sample.id == 0}">
                    <form:select id="donorSex" path="donorSex">
                    <c:forEach var="donorSexOption" items="${donorSexOptions}">
                      <option value="${donorSexOption}" <c:if test="${sample.donorSex == donorSexOption}">selected="selected"</c:if>>
                        ${donorSexOption.label}
                      </option>
                    </c:forEach>
                    </form:select>
                  </c:when>
                  <c:otherwise>
                    ${sample.donorSex}
                  </c:otherwise>
                </c:choose>
              </td>
            </tr>
            <tr>
              <td class="h">Consent:</td>
              <td>
                <form:select id="consentLevel" path="consentLevel">
                  <c:forEach var="consentLevelOption" items="${consentLevelOptions}">
                    <option value="${consentLevelOption}" <c:if test="${sample.consentLevel == consentLevelOption}">selected="selected"</c:if>>
                      ${consentLevelOption.label}
                    </option>
                  </c:forEach>
                </form:select>
              </td>
            </tr>
          </table>
        </div>
      </c:if>

      <br/>
      <h2>Details</h2>
      <table class="in">
        <c:if test="${sample.id != 0}">
          <tr>
            <td class="h">Parent:</td>
            <c:choose>
              <c:when test="${empty sample.parent}">
                <td>n/a</td>
              </c:when>
              <c:otherwise>
                <td><a href='<c:url value="/miso/sample/${sample.parent.id}"/>'><span id="parentAlias">${sample.parent.alias}</span></a></td>
              </c:otherwise>
            </c:choose>
          </tr>
        </c:if>
        <tr>
          <td class="h">Sample Class:*</td>
          <td>
            <c:choose>
              <c:when test="${sample.id == 0}">
                <miso:select id="sampleClass" path="sampleClass" items="${sampleClasses}" itemLabel="alias"
                    itemValue="id" defaultLabel="SELECT" defaultValue="" onchange="Sample.ui.sampleClassChanged();"/>
              </c:when>
              <c:otherwise>
                <input type="hidden" id="sampleClass" value="${sample.sampleClass.id}"/>
                <span id="sampleClassAlias">${sample.sampleClass.alias}</span>
              </c:otherwise>
            </c:choose>
          </td>
        </tr>
        <tr>
          <td class="h">Sub-project:</td>
          <td>
            <form:select id="subProject" path="subproject" onchange="Sample.ui.subProjectChanged()">
              <%-- list filtered and filled by js --%>
              <script type="text/javascript">
                jQuery(document).ready(function () {
                  Sample.ui.filterSubProjectOptions(${empty sample.subproject ? null : sample.subproject.id});
                });
              </script>
            </form:select>
          </td>
        </tr>
        <tr>
          <td class="h">Group ID:</td>
          <td>
            <form:input id="groupId" path="groupId"/>
          </td>
        </tr>
        <tr>
          <td class="h">Group Description:</td>
          <td>
            <form:input id="groupDescription" path="groupDescription"/>
          </td>
        </tr>
        <tr>
          <td class="h">Concentration (${sampleConcentrationUnits}):</td>
          <td><form:input id="concentration" path="concentration"/></td>
        </tr>
      </table>

      <c:if test="${sampleCategory eq 'Tissue' or sampleCategory eq 'new'}">
        <br/>
        <div id="detailedSampleTissue">
          <h2>Tissue</h2>
          <table class="in">
            <c:if test="${sample.id == 0}">
              <tr id="tissueClassRow">
                <td class="h">Tissue Class:*</td>
                <td>
                  <miso:select id="tissueClass" path="tissueClass" items="${tissueClasses}" itemLabel="alias"
                      itemValue="id" defaultLabel="SELECT" defaultValue=""/>
                </td>
              </tr>
            </c:if>
            <tr>
              <td class="h">Tissue Origin:*</td>
              <td>
                 <miso:select id="tissueOrigin" path="tissueOrigin" items="${tissueOrigins}" itemLabel="itemLabel"
                     itemValue="id" defaultLabel="SELECT" defaultValue=""/>
              </td>
            </tr>
            <tr>
              <td class="h">Tissue Type:*</td>
              <td>
                 <miso:select id="tissueType" path="tissueType" items="${tissueTypes}" itemLabel="itemLabel"
                     itemValue="id" defaultLabel="SELECT" defaultValue=""/>
              </td>
            </tr>
            <tr>
              <td class="h">Passage Number:</td>
              <td>
                <c:choose>
                  <c:when test="${sample.id == 0}"><form:input id="passageNumber" path="passageNumber"/></c:when>
                  <c:otherwise><span id="passageNumber">${!empty sample.passageNumber ? sample.passageNumber : 'n/a'}</span></c:otherwise>
                </c:choose>
              </td>
            </tr>
            <tr>
              <td class="h">Times Received:*</td>
              <td>
                <c:choose>
                  <c:when test="${sample.id == 0}"><form:input id="timesReceived" path="timesReceived"/></c:when>
                  <c:otherwise><span id="timesReceived">${!empty sample.timesReceived ? sample.timesReceived : 'n/a'}</span></c:otherwise>
                </c:choose>
              </td>
            </tr>
            <tr>
              <td class="h">Tube Number:*</td>
              <td>
                <c:choose>
                  <c:when test="${sample.id == 0}"><form:input id="tubeNumber" path="tubeNumber"/></c:when>
                  <c:otherwise><span id="tubeNumber">${!empty sample.tubeNumber ? sample.tubeNumber : 'n/a'}</span></c:otherwise>
                </c:choose>
              </td>
            </tr>
            <tr>
              <td class="h">Tissue Material:</td>
              <td>
                <miso:select id="tissueMaterial" path="tissueMaterial" items="${tissueMaterials}" itemLabel="alias"
                    itemValue="id" defaultLabel="SELECT" defaultValue=""/>
              </td>
            </tr>
            <tr>
              <td class="h">Region:</td>
              <td><form:input id="region" path="region"/></td>
            </tr>
            <tr>
              <td class="h">Secondary Identifier:</td>
              <td><form:input id="secondaryIdentifier" path="secondaryIdentifier"/></td>
            </tr>
            <tr>
              <td class="h">Lab:</td>
              <td>
               <miso:select id="lab" path="lab" items="${labs}" itemLabel="itemLabel" itemValue="id" defaultLabel="SELECT"
                   defaultValue=""/>
              </td>
            </tr>
          </table>
        </div>
      </c:if>

      <c:if test="${sampleCategory eq 'Tissue Processing' or sampleCategory eq 'new'}">
        <br/>
        <c:choose>
        <c:when test="${sampleClass eq 'Slide' or sampleCategory eq 'new'}">
        <div id="slideTable">
          <h2>Tissue Processing</h2>
          <table class="in">
            <tr>
              <td class="h">Slides Remaining:</td>
              <td id="slidesRemaining">${sample.getSlidesRemaining()}</td>
            </tr>
            <tr>
              <td class="h">Slides:*</td>
              <td><form:input id="slides" path="slides"/></td>
            </tr>
            <tr>
              <td class="h">Discards:*</td>
              <td><form:input id="discards" path="discards"/></td>
            </tr>
            <tr>
              <td class="h">Thickness (&#181;m):</td>
              <td><form:input id="thickness" path="thickness"/></td>
            </tr>
            <tr>
              <td class="h">Stain:</td>
              <td>
               <miso:select id="stain" path="stain" items="${stains}" itemLabel="name" itemValue="id" defaultLabel="(None)" defaultValue=""/>
              </td>
            </tr>
          </table>
        </div>
        </c:when>
        <c:when test="${sampleClass eq 'LCM Tube' or sampleCategory eq 'new'}">
        <div id="lcmTubeTable">
          <h2>Tissue Processing</h2>
          <table class="in">
            <tr>
              <td class="h">Slides Consumed:*</td>
              <td><form:input id="slidesConsumed" path="slidesConsumed"/></td>
            </tr>
          </table>
        </div>
        </c:when>
        </c:choose>
      </c:if>

      <c:if test="${sampleCategory eq 'Stock' or sampleCategory eq 'new'}">
        <br/>
        <div id="detailedSampleStock">
          <h2>Stock</h2>
          <table class="in">
            <tr>
              <td class="h">STR Status</td>
              <td>
                <form:select id="strStatus" path="strStatus">
                  <c:forEach var="strStatusOption" items="${strStatusOptions}">
                    <option value="${strStatusOption}" <c:if test="${sample.strStatus == strStatusOption}">selected="selected"</c:if>>
                      ${strStatusOption.label}
                    </option>
                  </c:forEach>
                </form:select>
              </td>
            </tr>
            <c:if test="${sample.sampleClass.DNAseTreatable}">
              <tr>
                <td class="h"><label for="DNAseTreated">DNAse Treated:</label></td>
                <td><form:checkbox id="DNAseTreated" path="DNAseTreated"/></td>
              </tr>
            </c:if>
          </table>
        </div>
      </c:if>

      <c:if test="${sampleCategory eq 'Aliquot' or sampleCategory eq 'new'}">
        <br/>
        <div id="detailedSampleAliquot">
          <h2>Aliquot</h2>
          <table class="in">
            <tr>
              <td class="h">Purpose:</td>
              <td>
                <miso:select id="samplePurpose" path="samplePurpose" items="${samplePurposes}" itemLabel="alias"
                    itemValue="id" defaultLabel="Unknown" defaultValue=""/>
              </td>
            </tr>
          </table>
        </div>
      </c:if>
    </div>
  </c:if>

  <c:choose>
    <c:when test="${!empty sample.project and sample.securityProfile.profileId eq sample.project.securityProfile.profileId}">
      <table class ="in">
        <tr>
          <td>Permissions</td>
          <td><i>Inherited from project </i>
            <a href='<c:url value="/miso/project/${sample.project.id}"/>'>${sample.project.name} (${sample.project.alias})</a>
            <input type="hidden" value="${sample.project.securityProfile.profileId}"
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
      Validate.attachParsley('#sample-form');

      // display identification barcode image
      if (document.getElementById('idBarcodePresent')) {
        var sampleId = parseInt(document.getElementById('idBarcodePresent').getAttribute('data-sampleId'));
        var idbarcode = document.getElementById('idBarcodePresent').getAttribute('data-idbarcode');
        Fluxion.doAjax(
          'sampleControllerHelperService',
          'getSampleBarcode',
          {
            'sampleId': sampleId,
            'url': ajaxurl
          },
          {
            'doOnSuccess': function (json) {
              var img = '<img style="height:30px; border:0;" alt="'+idbarcode+'" title="'+idbarcode+'" src="/temp/'+json.img+'"/>';
              document.getElementById('idBarcode').innerHTML = img;
            }
          }
        );
      }
      HotUtils.projects = ${projectsDtos};
    });
  </script>

  <c:if test="${sample.id != 0}">
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
            <a onclick="Sample.ui.showSampleNoteDialog(${sample.id});" href="javascript:void(0);" class="add">Add Note</a>
          </div>
        </li>
      </ul>
      <c:if test="${fn:length(sample.notes) > 0}">
        <div class="note" style="clear:both">
          <c:forEach items="${sample.notes}" var="note" varStatus="n">
            <div class="exppreview" id="sample-notes-${n.count}">
              <b>${note.creationDate}</b>: ${note.text}
              <span class="float-right" style="font-weight:bold; color:#C0C0C0;">${note.owner.loginName}
                <c:if test="${(note.owner.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                                or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
                <span style="color:#000000"><a href='#' onclick="Sample.ui.deleteSampleNote('${sample.id}', '${note.noteId}');">
                  <span class="ui-icon ui-icon-trash" style="clear: both; position: relative; float: right; margin-top: -15px;"></span></a></span>
                </c:if>
              </span>
            </div>
          </c:forEach>
        </div>
      </c:if>
      <div id="addSampleNoteDialog" title="Create new Note"></div>
    </div>
    <br/>
  </c:if>
</div>
</form:form>

<c:if test="${sample.id != 0}">
  <miso:qcs id="list_qc" item="${sample}"/>

  <c:if test="${ !detailedSample or detailedSample and sampleCategory eq 'Aliquot' }">
    <miso:list-section id="list_library" name="Libraries" target="library" items="${sampleLibraries}"/>
  </c:if>

  <c:if test="${detailedSample}">
    <miso:list-section id="list_relation" name="Relationships" target="sample" items="${sampleRelations}"/>
  </c:if>

  <miso:list-section id="list_pool" name="Pools" target="pool" items="${samplePools}"/>
  <miso:list-section id="list_run" name="Runs" target="run" items="${sampleRuns}"/>
  
  <miso:list-section id="list_array" name="Arrays" target="array" items="${sampleArrays}"/>
  <miso:list-section id="list_arrayrun" name="Array Runs" target="arrayrun" items="${sampleArrayRuns}"/>
  
  <miso:changelog item="${sample}"/>
</c:if>
<div id="dialog"></div>
</div>

</div>

<script type="text/javascript">
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

    jQuery('#scientificName').simplyCountable({
      counter: '#scientificNameCounter',
      countType: 'characters',
      maxCount: ${maxLengths['scientificName']},
      countDirection: 'down'
    });
  });
</script>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
