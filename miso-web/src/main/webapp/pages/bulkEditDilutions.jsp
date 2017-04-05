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
<link href="<c:url value='/scripts/handsontable/dist/pikaday/pikaday.css'/>" rel="stylesheet" type="text/css" />
<script src="<c:url value='/scripts/handsontable/dist/pikaday/pikaday.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/handsontable/dist/moment/moment.js'/>" type="text/javascript"></script>

<script src="<c:url value='/scripts/natural_sort.js'/>" type="text/javascript"></script>

<script src="<c:url value='/scripts/handsontable/dist/handsontable.full.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/handsontable_renderers.js'/>" type="text/javascript"></script>
<link rel="stylesheet" media="screen" href="/scripts/handsontable/dist/handsontable.full.css">

<div id="maincontent">
<div id="contentcolumn">

  <h1>
    ${method} Library Dilutions
    <button id="saveDilutions" class="fg-button ui-state-default ui-corner-all">Save</button>
  </h1>
  
  <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#hothelp_arrowclick'), 'hothelpdiv');">Quick Help
    <div id="hothelp_arrowclick" class="toggleLeft"></div>
  </div>
  <div id="hothelpdiv" class="note" style="display:none;">
    <p>To fill all empty cells in a column with the value of your selected cell(s), <b>double-click</b> the square in the bottom right of your selected cell(s).
      <br/>To fill a variable number of columns with the value of your selected cell,  <b>click</b> the square in the bottom right of your 
      filled-in selected cell and <b>drag</b> up or down. All selected columns will be filled in.
      <br/>To fill down a column with values following an incremental (+1) pattern, select two adjacent cells in a column and then either drag down, or 
      double-click the square in the bottom right of the selected cells.
    </p>
  </div>
  <div class="clear"></div>
  <br/>
  <br/>

 <div id="HOTbulkForm" data-detailed-sample="${detailedSample}">
   <div id="ctrlV" class="note">
     <p>Paste values using Ctrl + V in Windows or Linux, or Command-V (&#8984;-V) on a Mac.</p>
   </div>
 
   <div id="saveSuccesses"  class="parsley-success hidden">
     <p id="successMessages"></p>
   </div>
     <div id="saveErrors" class="bs-callout bs-callout-warning hidden">
       <h2>Oh snap!</h2>
       <p>The following rows failed to save:</p>
       <p id="errorMessages"></p>
     </div>
   
   <div id="hotContainer"></div>
 
 </div>
  
  <script type="text/javascript">
    jQuery(document).ready(function () {
      Dilution.hot.librariesJSON = Hot.sortByProperty(${librariesJSON}, 'alias');
      Hot.detailedSample = JSON.parse(document.getElementById('HOTbulkForm').dataset.detailedSample);
      Hot.saveButton = document.getElementById('saveDilutions');
      Dilution.hot.propagateOrEdit = "${method}";
      Hot.autoGenerateIdBarcodes = ${autoGenerateIdBarcodes};
      Dilution.hot.units = "${libraryDilutionUnits}";

      Dilution.hot.makeBulkCreateTable = function () {
        Dilution.hot.dilutionsJSON = Dilution.hot.modifyLibrariesForDilutions(Dilution.hot.librariesJSON);
        Dilution.hot.makeHOT(Dilution.hot.dilutionsJSON);
      };

      Hot.saveButton.addEventListener('click', Dilution.hot.saveData, true);
      // can only bulk create at this time, since no good way of selecting multiple dilutions to edit (no List Dilutions table)
      if (Hot.detailedSample) {
       Hot.saveButton.addEventListener('click', Dilution.hot.createData, true);
       Fluxion.doAjax(
         'libraryControllerHelperService',
         'getTargetedSequencingTypes',
         {
           'url': ajaxurl
         },
         {
           'doOnSuccess': function (json) {
             Dilution.hot.tarSeqs = json.targetedSequencings;
             Dilution.hot.makeBulkCreateTable();
           }
         }
       );
      } else {
        // make standard table
        Dilution.hot.tarSeqs = [];
        Dilution.hot.makeBulkCreateTable();
      }
    });
  </script>

</div>
</div>
<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
