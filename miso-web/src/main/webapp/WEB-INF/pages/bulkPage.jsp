<%@ include file="../header.jsp" %>
<script src="<c:url value='/scripts/handsontable/dist/pikaday/pikaday.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/handsontable/dist/moment/moment.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/handsontable/dist/handsontable.full.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/handsontable_renderers.js'/>" type="text/javascript"></script>

<div id="maincontent">
<div id="contentcolumn">

  <h1>
    ${title}
    <button id="save" class="fg-button ui-state-default ui-corner-all disabled" disabled="disabled">Save</button>
    <img id="ajaxLoader" src="/styles/images/ajax-loader.gif" class="fg-button"/>
  </h1>

  <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#hothelp_arrowclick'), 'hothelpdiv');">Quick Help
    <div id="hothelp_arrowclick" class="toggleLeft"></div>
  </div>
  <div id="hothelpdiv" class="note" style="display:none;">
    <p>Paste values using Ctrl + V in Windows or Linux, or Command-V (&#8984;-V) on a Mac.</p>
    <p>To fill all empty cells in a column with the value of your selected cell(s), <b>double-click</b> the square in the bottom right
      of your selected cell(s).</p>
    <p>To fill a variable number of columns with the value of your selected cell,  <b>click</b> the square in the bottom right of your
      filled-in selected cell and <b>drag</b> up or down. All selected columns will be filled in.</p>
    <p>To fill down a column with values following an incremental (+1) pattern, select two adjacent cells in a column and then either drag down, or
      double-click the square in the bottom right of the selected cells.</p>
    <div id="hotColumnHelp"></div>
  </div>
  <div class="clear"></div>
  <br/>

 <div id="HOTbulkForm">
   <div id="additionalHotNotes" class="note hidden"></div>
 
   <div id="nonStandardAliasNote" class="table-note hidden">
      <p>Double-check highlighted aliases, as they will be saved even if they are duplicated or do not follow the naming standard!</p>
    </div>

   <div id="successesAndErrors">
     <div id="warnings" class="bs-callout bs-callout-warning hidden">
        <h2>Warning</h2>
        <div id="warningMessages"></div>
     </div>
     <div id="successes" class="parsley-success hidden">
       <p id="successMessage"></p>
     </div>
     <div id="errorsContainer" class="bs-callout bs-callout-warning hidden">
       <h2>Oh snap!</h2>
       <div id="errors"></div>
     </div>
   </div>

   <div id="bulkactions" class="fg-toolbar ui-widget-header ui-corner-bl ui-corner-br ui-helper-clearfix paging_full_numbers"></div>
   <div id="hotContainer"></div>

 </div>

  <script type="text/javascript">
    jQuery(document).ready(function () {
      var target = BulkTarget['${target}'];
      if (target.getUserManualUrl) {
      	Utils.ui.updateHelpLink(target.getUserManualUrl());
      }
      BulkUtils.makeTable(target, ${config}, ${input});
    });
  </script>
</div>
</div>
<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
