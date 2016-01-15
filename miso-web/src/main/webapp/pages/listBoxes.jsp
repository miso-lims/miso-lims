<%@ include file="../header.jsp" %>
<script src="<c:url value='/scripts/jquery/datatables/js/jquery.dataTables.min.js'/>" type="text/javascript"></script>
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" type="text/css">
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables_themeroller.css'/>">

<div id="maincontent">
  <div id="contentcolumn">
    <script type="text/javascript">
      jQuery(document).ready(function () {
        jQuery("#tabs").tabs();
      });
    </script>
    <h1>Boxes</h1>

    <div id="tabs">
      <ul>
        <li>
          <a href="#tab" onclick="Box.ui.changeBoxListing('')"><span>All Boxes</span></a>
        </li>
        <c:forEach items="${boxUses}" var="boxUse">
          <li>
            <a href="#tab" onclick="Box.ui.changeBoxListing('${boxUse.alias}')"><span>${boxUse.alias}</span></a>
          </li>
        </c:forEach>
      </ul>

      <div id="tab">
        <table cellpadding="0" cellspacing="0" border="0" class="display" id="listingBoxesTable">
        </table>
        <script type="text/javascript">
jQuery(document).ready(function () {
    Box.ui.createListingBoxesTable();
    });
        </script>
      </div>
    </div>
  </div>
</div>
<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>
