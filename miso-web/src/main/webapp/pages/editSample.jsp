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
<link href="<c:url value='/scripts/handsontable/dist/pikaday/pikaday.css'/>" rel="stylesheet" type="text/css" />
<script src="<c:url value='/scripts/handsontable/dist/pikaday/pikaday.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/handsontable/dist/moment/moment.js'/>" type="text/javascript"></script>

<script src="<c:url value='/scripts/datatables_utils.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/natural_sort.js'/>" type="text/javascript"></script>

<script src="<c:url value='/scripts/handsontable/dist/handsontable.full.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/handsontable_renderers.js'/>" type="text/javascript"></script>
<link rel="stylesheet" media="screen" href="/scripts/handsontable/dist/handsontable.full.css">
<script src="<c:url value='/scripts/sample_hot.js'/>" type="text/javascript"></script>

<div id="maincontent">
<div id="contentcolumn">
<c:if test="${sample.id == 0}">
<div id="tabs">
<ul>
  <li><a href="#tab-1"><span>Single</span></a></li>
  <li><a href="#tab-2"><span>Bulk</span></a></li>
</ul>

<div id="tab-1">
</c:if>


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
            ${detailedSample && sample.hasNonStandardAlias()} || ${detailedSample && sample.parent.hasNonStandardAlias()},
            ${sample.id == 0});">Save
  </button>
</h1>

<c:if test="${not empty sample.project}">
  <div class="breadcrumbs">
    <ul>
      <li>
        <a href="/">Home</a>
      </li>
      <li>
        <div class="breadcrumbsbubbleInfo">
          <div class="trigger">
            <a href='<c:url value="/miso/project/${sample.project.id}"/>'>${sample.project.alias}</a>
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

<div class="barcodes">
  <div class="barcodeArea ui-corner-all">
    <span style="float: left; font-size: 24px; font-weight: bold; color:#BBBBBB">Barcode</span>
    <c:if test="${sample.id != 0}">
      <ul class="barcode-ddm">
        <li>
          <a onmouseover="mopen('idBarcodeMenu')" onmouseout="mclosetime()">
            <span style="float:right; margin-top:6px;" class="ui-icon ui-icon-triangle-1-s"></span>
            <span id="idBarcode" style="float:right;"></span>
          </a>

          <div id="idBarcodeMenu"
              onmouseover="mcancelclosetime()"
              onmouseout="mclosetime()">

            <a href="javascript:void(0);"
               onclick="Sample.barcode.printSampleBarcodes(${sample.id});">Print</a>
            <c:if test="${not autoGenerateIdBarcodes}">
              <a href="javascript:void(0);"
               onclick="Sample.ui.showSampleIdBarcodeChangeDialog(${sample.id}, '${sample.identificationBarcode}');">Update Barcode</a>
            </c:if>
          </div>
        </li>
      </ul>
    </c:if>
    <div id="changeSampleIdBarcodeDialog" title="Assign New Barcode"></div>
    <c:if test="${not empty sample.identificationBarcode}">
      <span id="idBarcodePresent" data-idBarcodePresent="false" data-sampleId="${sample.id}" data-idbarcode="${sample.identificationBarcode}"></span>
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
              jQuery('#idBarcode').html("<img style='height:30px; border:0;' alt='${sample.identificationBarcode}' title='${sample.identificationBarcode}' src='<c:url value='/temp/'/>" + json.img + "'/>");
            }
          });
        });
      </script>
    </c:if>
  </div>
  <div id="printServiceSelectDialog" title="Select a Printer"></div>
</div>
<div>
  <table class="in" <c:if test="${detailedSample && sample.isSynthetic()}">style="background-color: #ddd"</c:if>>
    <tr>
      <td class="h">Sample ID:</td>
      <td>
        <c:choose>
          <c:when test="${sample.id != 0}">${sample.id}</c:when>
          <c:otherwise><i>Unsaved</i></c:otherwise>
        </c:choose>
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
                    ${proj.name}: ${proj.alias}
                </option>
              </c:forEach>
            </form:select>
          </td>
        </c:when>
        <c:otherwise>
          <td>
            <input type="hidden" value="${sample.project.id}" name="project" id="project"/>
            <a href='<c:url value="/miso/project/${sample.project.id}"/>'>${sample.project.name} (${sample.project.alias})</a>
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
      <td class="h">
        Alias:
        <c:choose>
          <c:when test="${aliasGenerationEnabled && sample.id == 0}">
            (blank to auto-generate)
          </c:when>
          <c:otherwise>
            *<c:if test="${detailedSample && sample.parent.hasNonStandardAlias()}">
            (cannot auto-generate since parent has non-standard alias)
            </c:if>
          </c:otherwise>
        </c:choose>
      </td>
      <td><form:input id="alias" path="alias" name="alias" data-parsley-required='${!aliasGenerationEnabled || sample.id != 0}'/><span id="aliasCounter" class="counter"></span>
        <%--<td><a href="void(0);" onclick="popup('help/sampleAlias.html');">Help</a></td>--%>
        <c:if test="${detailedSample}">
          <c:if test="${sample.hasNonStandardAlias() || sample.parent.hasNonStandardAlias()}">
	          <ul class="parsley-errors-list filled" id="nonStandardAlias">
	            <li class="parsley-custom-error-message">
	            Double-check this alias -- it will be saved even if it (or the parent sample's alias) is duplicated or does not follow the naming standard!
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
        <%--<td><a href="void(0);" onclick="popup('help/sampleDescription.html');">Help</a></td>--%>
    </tr>
    <tr>
      <td>Date of receipt:</td>
      <td>
        <form:input path="receivedDate" id="receiveddatepicker" placeholder="DD/MM/YYYY"/>
        <script type="text/javascript">
          Utils.ui.addDatePicker("receiveddatepicker");
        </script>
      </td>
    </tr>
    <tr>
      <td class="h">Scientific Name:*</td>
      <td><form:input id="scientificName" path="scientificName"/><span id="scientificNameCounter" class="counter"></span>
        <c:if test="${sessionScope.taxonLookupEnabled}">
        <script>Utils.timer.typewatchFunc(jQuery('#scientificName'), Sample.validateNCBITaxon, 1000, 2);</script>
        </c:if>
      </td>
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
      <td>Discarded:</td>
      <td><form:checkbox id="discarded" path="discarded"/></td>
    </tr>
    <tr>
      <td class="h">Location:</td>
      <td>
        <c:if test="${!empty sample.box.locationBarcode}">${sample.box.locationBarcode},</c:if>
        <c:if test="${!empty sample.boxPosition}"><a href='<c:url value="/miso/box/${sample.box.id}"/>'>${sample.box.alias}, ${sample.boxPosition}</a></c:if>
      </td>
    </tr>
  </table>
  <%@ include file="volumeControl.jspf" %>
  <c:if test="${detailedSample}">

    <script type="text/javascript">
      Sample.options.all = ${sampleOptions};

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
                    ${sample.externalName}
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
                <td><a href='<c:url value="/miso/sample/${sample.parent.id}"/>'>${sample.parent.alias}</a></td>
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
                ${sample.sampleClass.alias}
              </c:otherwise>
            </c:choose>
          </td>
        </tr>
        <tr>
          <td class="h">Sub-project:</td>
          <td>
            <c:choose>
              <c:when test="${sample.id == 0}">
                <form:select id="subProject" path="subproject" onchange="Sample.ui.subProjectChanged()">
                  <%-- list filtered and filled by js --%>
                  <script type="text/javascript">
                    jQuery(document).ready(function () {
                      Sample.ui.filterSubProjectOptions();
                    });
                  </script>
                </form:select>
              </c:when>
              <c:when test="${!empty sample.subproject}">
                ${sample.subproject.alias}
                <input type="hidden" value="${sample.subproject.id}" name="subProject" id="subProject"/>
              </c:when>
              <c:otherwise>
                n/a
              </c:otherwise>
            </c:choose>
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
                <c:choose>
                  <c:when test="${sample.id == 0}">
                    <miso:select id="tissueOrigin" path="tissueOrigin" items="${tissueOrigins}" itemLabel="itemLabel"
                        itemValue="id" defaultLabel="SELECT" defaultValue=""/>
                  </c:when>
                  <c:otherwise>
                    ${sample.tissueOrigin.description}
                  </c:otherwise>
                </c:choose>
              </td>
            </tr>
            <tr>
              <td class="h">Tissue Type:*</td>
              <td>
                <c:choose>
                  <c:when test="${sample.id == 0}">
                    <miso:select id="tissueType" path="tissueType" items="${tissueTypes}" itemLabel="itemLabel"
                        itemValue="id" defaultLabel="SELECT" defaultValue=""/>
                  </c:when>
                  <c:otherwise>
                    ${sample.tissueType.description}
                  </c:otherwise>
                </c:choose>
              </td>
            </tr>
            <tr>
              <td class="h">Tissue Material:</td>
              <td>
                <c:choose>
                  <c:when test="${sample.id == 0}">
                    <miso:select id="tissueMaterial" path="tissueMaterial" items="${tissueMaterials}" itemLabel="alias"
                        itemValue="id" defaultLabel="SELECT" defaultValue=""/>
                  </c:when>
                  <c:otherwise>
                    ${sample.tissueMaterial.alias}
                  </c:otherwise>
                </c:choose>
              </td>
            </tr>
            <tr>
              <td class="h">Region:</td>
              <td><form:input id="region" path="region"/></td>
            </tr>
            <tr>
              <td class="h">External Institute Identifier:</td>
              <td><form:input id="externalInstituteIdentifier" path="externalInstituteIdentifier"/></td>
            </tr>
            <tr>
              <td class="h">Lab:</td>
              <td>
               <miso:select id="lab" path="lab" items="${labs}" itemLabel="itemLabel" itemValue="id" defaultLabel="SELECT"
                   defaultValue=""/>
              </td>
            </tr>
            <tr>
              <td class="h">Passage Number:</td>
              <td>
                <c:choose>
                  <c:when test="${sample.id == 0}"><form:input id="passageNumber" path="passageNumber"/></c:when>
                  <c:otherwise>${!empty sample.passageNumber ? sample.passageNumber : 'n/a'}</c:otherwise>
                </c:choose>
              </td>
            </tr>
            <tr>
              <td class="h">Times Received:*</td>
              <td>
                <c:choose>
                  <c:when test="${sample.id == 0}"><form:input id="timesReceived" path="timesReceived"/></c:when>
                  <c:otherwise>${!empty sample.timesReceived ? sample.timesReceived : 'n/a'}</c:otherwise>
                </c:choose>
              </td>
            </tr>
            <tr>
              <td class="h">Tube Number:*</td>
              <td>
                <c:choose>
                  <c:when test="${sample.id == 0}"><form:input id="tubeNumber" path="tubeNumber"/></c:when>
                  <c:otherwise>${!empty sample.tubeNumber ? sample.tubeNumber : 'n/a'}</c:otherwise>
                </c:choose>
              </td>
            </tr>
          </table>
        </div>
      </c:if>

      <c:if test="${sampleCategory eq 'Tissue Processing'}">
        <br/>
        <c:choose>
        <c:when test="${sampleClass eq 'CV Slide'}">
        <div id="cvSlideTable">
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
          </table>
        </div>
        </c:when>
        <c:when test="${sampleClass eq 'LCM Tube'}">
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
            <tr>
              <td class="h">Concentration (nM):</td>
              <td><form:input id="concentration" path="concentration"/></td>
            </tr>
            <c:if test="${sample.sampleClass.DNAseTreatable}">
              <tr>
                <td class="h">DNAse Treated:</td>
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

      // display tabs correctly for sample creation
      jQuery('#tabs').tabs();
      jQuery('#tabs').removeClass('ui-widget').removeClass('ui-widget-content');
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
<c:if test="${sample.id == 0}">
</div>
<div id="tab-2">
  <h1>
    Create Samples
    <button id="saveSamples" class="disabled fg-button ui-state-default ui-corner-all" disabled="disabled" onclick="Sample.hot.saveData();">Save</button>
  </h1>
  <c:if test="${not empty sample.project}">
	  <div class="breadcrumbs">
	    <ul>
	      <li>
	        <a href="/">Home</a>
	      </li>
	      <li>
	        <div class="breadcrumbsbubbleInfo">
	          <div class="trigger">
	            <a href='<c:url value="/miso/project/${sample.project.id}"/>'>${sample.project.alias}</a>
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
  <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#hothelp_arrowclick'), 'hothelpdiv');">Quick Help
    <div id="hothelp_arrowclick" class="toggleLeft"></div>
  </div>
  <div id="hothelpdiv" class="note" style="display:none;">
    <p>To fill all columns below with the value of your selected cell, <b>double-click</b> the square in the bottom right of your selected cell.
      <br/>To fill a variable number of columns with the value of your selected cell,  <b>click</b> the square in the bottom right of your
      filled-in selected cell and <b>drag</b> up or down. All selected columns will be filled in.
      <br/>To fill down a column with values following an incremental (+1) pattern, select two adjacent cells in a 
      column and then either drag down, or double-click the square in the bottom right of the selected cells.
	    <c:if test="${aliasGenerationEnabled}">
	      <br/>Leave <b>alias</b> cell blank to auto-generate an alias for this sample.
	    </c:if>
    </p>
  </div>
  <div class="clear"></div>
  <br/>
  <br/>

  <div id="HOTbulkForm" data-detailed-sample="${detailedSample}">
    <div id="ctrlV" class="note">
      <p>Paste values using Ctrl + V in Windows or Linux, or Command-V (&#8984;-V) on a Mac.</p>
    </div>
    <div class="floatleft" id="tableProps">
	    <div><label>Project: <select id="projectSelect"></select></label></div>
	    <div id="subpSelectOptions"></div>
	    <div id="classOptions"></div>
	    <div><label>Number of Samples: <input id="numSamples" type="text"/></label></div>
	    <div id="saveSuccesses"  class="parsley-success hidden">
	      <p id="successMessages"></p>
	    </div>
	    <div id="saveErrors" class="bs-callout bs-callout-warning hidden">
	      <h2>Oh snap!</h2>
	      <p>The following rows failed to save:</p>
	      <p id="errorMessages"></p>
	    </div>
	    <div>
	      <button id="makeTable" onclick="Sample.hot.makeNewSamplesTable();">Make Table</button>
          <c:if test="${detailedSample}">
            <button id="lookupIdentities" onclick="Sample.hot.lookupIdentities();" disabled="disabled">Look up Identities</button>
          </c:if>
	    </div>
	  </div>
    <div class="clear"></div>
    <div id="hotContainer"></div>

    <script type="text/javascript">
      Hot.dropdownRef = ${referenceDataJSON};
      Sample.hot.aliasGenerationEnabled = ${aliasGenerationEnabled};
      Hot.autoGenerateIdBarcodes = ${autoGenerateIdBarcodes};
      Sample.hot.selectedProjectId = <c:out value="${sample.project.id}" default="null"/>;
      Hot.detailedSample = JSON.parse(document.getElementById('HOTbulkForm').dataset.detailedSample);
      Hot.saveButton = document.getElementById('saveSamples');
      Hot.fetchSampleOptions(Sample.hot.processSampleOptionsFurther);
    </script>
  </div>
</div>
</div>
</c:if>

<c:if test="${sample.id != 0}">
  <a name="sampleqc"></a>

  <h1>
    <span id="qcsTotalCount"></span>
  </h1>
  <ul class="sddm">
    <li>
      <a onmouseover="mopen('qcmenu')" onmouseout="mclosetime()">Options
        <span style="float:right" class="ui-icon ui-icon-triangle-1-s"></span>
      </a>

      <div id="qcmenu"
           onmouseover="mcancelclosetime()"
           onmouseout="mclosetime()">
        <a href='javascript:void(0);' class="add"
           onclick="Sample.qc.generateSampleQCRow(${sample.id}); return false;">Add Sample QC</a>
      </div>
    </li>
  </ul>
    <div style="clear:both">
      <div id="addSampleQC"></div>
      <form id='addQcForm'>
        <table class="list in" id="sampleQcTable">
          <thead>
          <tr>
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
                <td>${qc.qcCreator}</td>
                <td><fmt:formatDate value="${qc.qcDate}"/></td>
                <td>${qc.qcType.name}</td>
                <fmt:formatNumber var="resultsRounded" value="${qc.results}" maxFractionDigits="2" />
                <td id="results${qc.id}">${resultsRounded} ${qc.qcType.units}</td>
                <c:if test="${(sample.securityProfile.owner.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                                          or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
                  <td id="edit${qc.id}" align="center"><a href="javascript:void(0);"
                                                          onclick="Sample.qc.changeSampleQCRow('${qc.id}','${sample.id}')">
                    <span class="ui-icon ui-icon-pencil"></span></a></td>
                </c:if>
              </tr>
            </c:forEach>
          </c:if>
          </tbody>
        </table>
        <input type='hidden' id='sampleId' name='id' value='${sample.id}'/>
      </form>
    </div>
  <br/>
  <a name="library"></a>

  <c:if test="${ !detailedSample or detailedSample and sampleCategory eq 'Aliquot' }">

  <h1>
    <span id="librariesTotalCount"></span>
  </h1>
  <ul class="sddm">
    <li>
      <a onmouseover="mopen('librarymenu')" onmouseout="mclosetime()">Options
        <span style="float:right" class="ui-icon ui-icon-triangle-1-s"></span>
      </a>

      <div id="librarymenu"
           onmouseover="mcancelclosetime()"
           onmouseout="mclosetime()">
        <a href='<c:url value="/miso/library/new/${sample.id}"/>' class="add">Add Library</a><br/>

        <c:if test="${not empty sample.libraries}">
          <a href='javascript:void(0);' onclick='bulkLibraryQcTable();' class="add">Bulk QC these
            Libraries</a>
          <a href='javascript:void(0);' onclick='bulkLibraryDilutionTable();' class="add">Bulk Add Library
            Dilutions</a>
        </c:if>
      </div>
    </li>
  </ul>
    <div style="clear:both">
      <table class="list" id="library_table">
        <thead>
        <tr>
          <th>Library Name</th>
          <th>Library Alias</th>
          <th>Library Type</th>
          <th>QC Passed</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${sample.libraries}" var="library">
          <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
            <td>
                <b><a href='<c:url value="/miso/library/${library.id}"/>'>${library.name}</a></b></td>
            <td><a href='<c:url value="/miso/library/${library.id}"/>'>${library.alias}</a></td>
            <td>${library.libraryType.description}</td>
            <td>${library.qcPassed}</td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </div>
  <script type="text/javascript">
    jQuery(document).ready(function () {
    	// display sample QCs table, and count samples and libraries
      jQuery('#sampleQcTable').tablesorter();
      var totalQcsCount = jQuery('#sampleQcTable>tbody>tr:visible').length;
      jQuery('#qcsTotalCount').html(totalQcsCount + (totalQcsCount == 1 ? ' QC' : ' QCs'));
      var visibleLibsCount = jQuery('#library_table>tbody>tr:visible').length;
      jQuery('#librariesTotalCount').html(visibleLibsCount + (visibleLibsCount == 1 ? ' Library' : ' Libraries'));

      jQuery('#library_table').dataTable({
        "aaSorting": [
          [1, 'asc']
        ],
        "aoColumns": [
          null,
          { "sType": 'natural' },
          null,
          null
        ],
        "iDisplayLength": 50,
        "bJQueryUI": true,
        "bRetrieve": true
      });
    });
  </script>
  </c:if>

  <c:if test="${not empty samplePools}">
    <br/>
    <h1>${fn:length(samplePools)} Pool<c:if test="${fn:length(samplePools) ne 1}">s</c:if></h1>
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

    <div style="clear:both">
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
        <c:forEach items="${samplePools}" var="pool">
          <tr data-poolId="${pool.id}" onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
            <td><b><a href='<c:url value="/miso/pool/${pool.id}"/>'>${pool.name}</a></b></td>
            <td><a href='<c:url value="/miso/pool/${pool.id}"/>'>${pool.alias}</a></td>
            <td>${pool.platformType.key}</td>
            <td>${pool.creationDate}</td>
            <td>${pool.concentration}</td>
            <sec:authorize access="hasRole('ROLE_ADMIN')">
              <td class="misoicon" onclick="Pool.deletePool(${pool.id}, Utils.page.pageReload);">
                <span class="ui-icon ui-icon-trash"></span>
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
    </div>
  </c:if>

  <c:if test="${not empty sampleRuns}">
    <br/>
    <h1>${fn:length(sampleRuns)} Runs</h1>

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
      <c:forEach items="${sampleRuns}" var="run" varStatus="runCount">
        <tr data-runId="${run.id}" onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
          <td><b><a href='<c:url value="/miso/run/${run.id}"/>'>${run.name}</a></b></td>
          <td><a href='<c:url value="/miso/run/${run.id}"/>'>${run.alias}</a></td>
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
          <td>${run.health.key}</td>
          <sec:authorize access="hasRole('ROLE_ADMIN')">
            <td class="misoicon" onclick="Run.deleteRun(${run.id}, Utils.page.pageReload);">
              <span class="ui-icon ui-icon-trash"></span>
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

  <c:if test="${not empty sample.changeLog}">
    <br/>
    <h1>Changes</h1>
    <div style="clear:both">
      <table class="list" id="changelog_table">
        <thead>
        <tr>
          <th>Editor</th>
          <th>Summary</th>
          <th>Time</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${sample.changeLog}" var="change">
          <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
            <td>${change.user.fullName} (${change.user.loginName})</td>
            <td><b>${change.summary}</b></td>
            <td>${change.time}</td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </div>
  </c:if>
</c:if>
</div>

<c:if test="${not empty sample.libraries}">
<script type="text/javascript">
function bulkLibraryQcTable() {
  //destroy current table and recreate
  jQuery('#library_table').dataTable().fnDestroy();
  //bug fix to reset table width
  jQuery('#library_table').removeAttr("style");

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
    "bJQueryUI": true,
    "bAutoWidth": true,
    "bSort": false,
    "bFilter": false,
    "sDom": '<<"toolbar">f>r<t>ip>'
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

  jQuery("div.toolbar").parent().addClass("fg-toolbar ui-toolbar ui-widget-header ui-corner-tl ui-corner-tr ui-helper-clearfix");
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
    //bug fix to reset table width
    jQuery('#library_table').removeAttr("style");

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
    });

    //headers
    jQuery("#library_table tr:first").prepend("<th>Select <span sel='none' header='select' class='ui-icon ui-icon-arrowstop-1-s' style='float:right' onclick='DatatableUtils.toggleSelectAll(\"#library_table\", this);'></span></th>");
    jQuery("#library_table tr:first").append("<th>Dilution Date <span header='qcDate' class='ui-icon ui-icon-arrowstop-1-s' style='float:right' onclick='DatatableUtils.fillDown(\"#library_table\", this);'></span></th>");
    jQuery("#library_table tr:first").append("<th>Concentration (${libraryDilutionUnits})</th>");

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
      "bJQueryUI": true,
      "bAutoWidth": true,
      "bSort": false,
      "bFilter": false,
      "sDom": '<<"toolbar">f>r<t>ip>'
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
