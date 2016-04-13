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
<script src="<c:url value='/scripts/library_hot.js'/>" type="text/javascript"></script>
<script type="text/javascript" src="<c:url value='/scripts/parsley/parsley.min.js'/>"></script>

<div id="maincontent">
<div id="contentcolumn">

  <h1>${method} Libraries</h1>
  
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
      Library.hot.dropdownRef = ${referenceDataJSON};
      Library.hot.dropdownRef.tagBarcodes = {};
      Library.hot.dropdownRef.barcodeKits = {};
      Library.hot.detailedSample = JSON.parse(document.getElementById('HOTbulkForm').dataset.detailedSample);
      Library.hot.button = document.getElementById('saveLibraries');
      Library.hot.propagateOrEdit = "${method}";
      Library.libraryPropagationRulesJSON = ${libraryPropagationRulesJSON};

      Library.hot.makeBulkCreateTable = function () {
        Library.hot.librariesJSON = Library.hot.prepLibrariesForTable(Library.hot.librariesJSON);
        Library.hot.makeHOT(Library.hot.librariesJSON);
      };

      // get SampleOptions and make the appropriate table
      if (Boolean(Library.hot.detailedSample)) {
        if (Library.hot.propagateOrEdit == 'Propagate') {
          Library.hot.button.addEventListener('click', Library.hot.createData, true);
          Library.hot.fetchSampleOptions(Library.hot.makeBulkCreateTable);
  	    } else {
  	      Library.hot.button.addEventListener('click', Library.hot.updateData, true);
  	      Library.hot.fetchSampleOptions(Library.hot.makeBulkUpdateTable);
	      }
      }
    });
  </script>

</div>
</div>
<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>