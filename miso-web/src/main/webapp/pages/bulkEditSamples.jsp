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
<link href="<c:url value='/scripts/handsontable/dist/pikaday/pikaday.css'/>" rel="stylesheet" type="text/css" />
<script src="<c:url value='/scripts/handsontable/dist/pikaday/pikaday.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/handsontable/dist/moment/moment.js'/>" type="text/javascript"></script>

<script src="<c:url value='/scripts/natural_sort.js'/>" type="text/javascript"></script>

<script src="<c:url value='/scripts/handsontable/dist/handsontable.full.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/handsontable_renderers.js'/>" type="text/javascript"></script>
<link rel="stylesheet" media="screen" href="/scripts/handsontable/dist/handsontable.full.css">
<script src="<c:url value='/scripts/sample_hot.js'/>" type="text/javascript"></script>

<div id="maincontent">
<div id="contentcolumn">

	<h1>
	  ${method} Samples
	  <button id="saveSamples" class="fg-button ui-state-default ui-corner-all">Save</button>
	</h1>
	
	<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#hothelp_arrowclick'), 'hothelpdiv');">Quick Help
    <div id="hothelp_arrowclick" class="toggleLeft"></div>
  </div>
  <div id="hothelpdiv" class="note" style="display:none;">
    <p>To fill all empty cells in a column with the value of your selected cell(s), <b>double-click</b> the square in the bottom right of your selected cell(s).
      <br/>To fill a variable number of columns with the value of your selected cell,  <b>click</b> the square in the bottom right of your 
      filled-in selected cell and <b>drag</b> up or down. All selected columns will be filled in.
      <c:if test="${aliasGenerationEnabled}">
        <br/>Leave <b>alias</b> cell blank to auto-generate an alias for this sample.
      </c:if>
    </p>
  </div>
  <div class="clear"></div>
  <br/>
  <br/>

	<div id="HOTbulkForm" data-detailed-sample="${detailedSample}">
	
		<div id="saveSuccesses"  class="parsley-success hidden">
	    <p id="successMessages"></p>
	  </div>
	    <div id="saveErrors" class="bs-callout bs-callout-warning hidden">
	      <h2>Oh snap!</h2>
	      <p>The following rows failed to save:</p>
	      <p id="errorMessages"></p>
	    </div>
		<c:if test="${detailedSample}">
			<button id="addQcs" onclick="Sample.hot.regenerateWithQcs();">Show QCs Columns</button>
		</c:if>
		
		<div id="hotContainer"></div>
	
	</div>
	
	<script type="text/javascript">
	  jQuery(document).ready(function () {
	    Sample.hot.samplesJSON = ${samplesJSON};
	    Hot.dropdownRef = ${referenceDataJSON};
	    Sample.hot.aliasGenerationEnabled = ${aliasGenerationEnabled};
	    Hot.detailedSample = JSON.parse(document.getElementById('HOTbulkForm').dataset.detailedSample);
	    Hot.saveButton = document.getElementById('saveSamples');
	    Sample.hot.createOrEdit = "${method}";
      Hot.autoGenerateIdBarcodes = ${autoGenerateIdBarcodes};

	    Sample.hot.makeBulkEditTable = function () {
        Sample.hot.samplesJSON = Sample.hot.modifySamplesForEdit(Sample.hot.samplesJSON);
        var sampleCategory = Sample.hot.getCategoryFromClassId(Sample.hot.samplesJSON[0].sampleClassId);
        Sample.hot.makeHOT(Sample.hot.samplesJSON, 'update', null, sampleCategory);
      };

      Sample.hot.makeBulkCreateTable = function () {
        var sourceSampleCategory = Sample.hot.getCategoryFromClassId(Sample.hot.samplesJSON[0].sampleClassId);
        Sample.hot.newSamplesJSON = Sample.hot.modifySamplesForPropagate(Sample.hot.samplesJSON);
        var targetSampleCategory = Sample.hot.getCategoryFromClassId(Sample.hot.newSamplesJSON[0].sampleClassId);
        Sample.hot.makeHOT(Sample.hot.newSamplesJSON, 'propagate', sourceSampleCategory, targetSampleCategory);
        Hot.hotTable.updateSettings({
          cells: function (row, col, prop) {
            var cellProperties = {};
            if (prop == 'sampleClassAlias') {
              cellProperties.readOnly = false;
            }
            return cellProperties;
          }
        });
      };

	    // get SampleOptions and make the appropriate table
      if (Boolean(Hot.detailedSample)) {
        if (Sample.hot.createOrEdit == "Create") {
          Sample.hot.sampleClassId = parseInt(${sampleClassId});
          Hot.saveButton.addEventListener('click', Sample.hot.propagateData, true);
          Hot.fetchSampleOptions(Sample.hot.makeBulkCreateTable);
        } else {
          Hot.saveButton.addEventListener('click', Sample.hot.updateData, true);
          Hot.fetchSampleOptions(Sample.hot.makeBulkEditTable);
        }
      }
	  });
	</script>
	
	<div>
    <c:forEach items="${samples}" var="sample">
        <h2>${sample.name}</h2>
        <p>id: ${sample.id}</p>
    </c:forEach>
	</div>

</div>
</div>
<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
