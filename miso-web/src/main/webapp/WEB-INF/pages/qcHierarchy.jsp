<%@ include file="../header.jsp" %>

<script src="/scripts/highcharts/modules/sankey.js"></script>
<script src="/scripts/highcharts/modules/organization.js"></script>

<div id="maincontent">
<div id="contentcolumn">


<h1>QC Hierarchy</h1>

<div style="display: flex; width: 100%">
  <div id="graphScrollContainer" style="overflow-x: auto; flex: 1 1 100%; min-width: 322px">
    <div id="graphContainer" style="margin: 0 auto;"></div>
  </div>
  <div id="editContainer" style="flex: 0 0 auto; margin: 10px">
    <form id="editForm" data-parsley-validate="" autocomplete="off"><table><tbody>
      <tr><td>Selected:</td><td id="selectedLabel" style="max-width: 300px; overflow: hidden; text-overflow: ellipsis;"></td></tr>
      <tr><td>Type:</td><td id="selectedType"></td></tr>
      <tr><td>QC Status:</td><td><select id="selectedStatus" style="width: 100%"></select></td></tr>
      <tr><td>QC Note:</td><td><input id="selectedNote" type="text" style="width: 100%"></td></tr>
      <tr><td></td><td><button id="applySelected" type="button" class="fg-button ui-state-default ui-corner-all">Apply</button></td></tr>
    </tbody></table></form>
  </div>
</div>

<script type="text/javascript">
jQuery(document).ready(function () {
  QcHierarchy.buildGraph(${hierarchy}, '${selectedType}', ${selectedId});
});
</script>


</div>
</div>

<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>
