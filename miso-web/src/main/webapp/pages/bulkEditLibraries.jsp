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
<script src="<c:url value='/scripts/library_hot.js'/>" type="text/javascript"></script>

<div id="maincontent">
<div id="contentcolumn">

  <h1>${method} Libraries</h1>
  
  <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#hothelp_arrowclick'), 'hothelpdiv');">Quick Help
    <div id="hothelp_arrowclick" class="toggleLeft"></div>
  </div>
  <div id="hothelpdiv" class="note" style="display:none;">
    <p>To fill all columns below with the value of your selected cell, <b>double-click</b> the square in the bottom right of your selected cell.
      <br/>To fill a variable number of columns with the value of your selected cell,  <b>click</b> the square in the bottom right of your 
      filled-in selected cell and <b>drag</b> up or down. All selected columns will be filled in.
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
   <button id="saveLibraries">Save</button>
   <c:if test="${detailedSample}">
   <!--  TODO: add these back in later
      <button id="addQcs" onclick="Sample.hot.regenerateWithQcs();">Add QCs</button>
     <button id="hideAddnalCols" onclick="Sample.hot.hideAdditionalCols();">Hide Extra Columns</button>
  -->
   </c:if>
   
   <div id="hotContainer"></div>
 
 </div>
  
  <script type="text/javascript">
    jQuery(document).ready(function () {
      Library.hot.librariesJSON = ${librariesJSON};
      Hot.dropdownRef = ${referenceDataJSON};
      Hot.dropdownRef.tagBarcodes = {};
      Hot.dropdownRef.barcodeKits = {};
      Hot.detailedSample = JSON.parse(document.getElementById('HOTbulkForm').dataset.detailedSample);
      Hot.saveButton = document.getElementById('saveLibraries');
      Library.hot.propagateOrEdit = "${method}";
      Library.designs = ${libraryDesignsJSON};

      Library.hot.makeBulkCreateTable = function () {
        Library.hot.librariesJSON = Library.hot.prepLibrariesForTable(Library.hot.librariesJSON);
        Library.hot.makeHOT(Library.hot.librariesJSON);
      };

      // get SampleOptions and make the appropriate table
      if (Boolean(Hot.detailedSample)) {
        if (Library.hot.propagateOrEdit == 'Propagate') {
          Hot.saveButton.addEventListener('click', Library.hot.createData, true);
          Hot.fetchSampleOptions(Library.hot.makeBulkCreateTable);
  	    } else {
  	      Hot.saveButton.addEventListener('click', Library.hot.updateData, true);
  	      Hot.fetchSampleOptions(Library.hot.makeBulkUpdateTable);
	      }
      }
    });
  </script>

</div>
</div>
<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
