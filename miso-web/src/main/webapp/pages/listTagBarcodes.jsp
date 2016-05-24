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

<%@ include file="../header.jsp" %>
<script src="<c:url value='/scripts/jquery/datatables/js/jquery.dataTables.min.js'/>" type="text/javascript"></script>
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" type="text/css">
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables_themeroller.css'/>">

<div id="maincontent">
  <div id="contentcolumn">
    <h1>
      Tag Barcodes
    </h1>

    <div class="dataTables_wrapper" role="grid">
    <table cellpadding="0" cellspacing="0" border="0" class="display dataTable" id="listingTagBarcodeTable">
    </table>
    </div>
    <script type="text/javascript">
       jQuery('#listingTagBarcodeTable').html('');
       jQuery('#listingTagBarcodeTable').dataTable({
         "aaData": [
           <c:forEach items="${tagbarcodes}" var="barcode">[
            '${barcode.family.platformType.key}', 
            '${barcode.family.name}',
            '${barcode.name}',
            '${barcode.sequence}',
            '${barcode.family.archived}',
            '${barcode.id}'
          ],
         </c:forEach>
         ],
         "aoColumns": [
           { "sTitle": "Platform"},
           { "sTitle": "Family"},
           { "sTitle": "Name"},
           { "sTitle": "Sequence"},
           { "sTitle": "Archived"},
           { "sTitle": "ID", "bVisible": false}
         ],
         "bJQueryUI": true,
         "bAutoWidth": false,
         "iDisplayLength": 25,
         "aaSorting": [
           [0, "asc"],
           [1, "asc"],
           [2, "asc"]
         ]
       });
    </script>
  </div>
</div>
<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
