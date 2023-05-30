<%@ include file="../header.jsp" %>

<div id="maincontent">
  <div id="contentcolumn">
    <h1 id="tableTitle"></h1>
    <div id="headerMessages"></div>
    <table class="display no-border ui-widget-content" id="listingTable">
    </table>
    <script type="text/javascript">
      jQuery(document).ready(function () {
        var target = ${targetType};
        <c:choose>
          <c:when test="${empty customTitle}">
            document.getElementById('tableTitle').innerText = target.name;
          </c:when>
          <c:otherwise>
            document.getElementById('tableTitle').innerText = '${customTitle}';
          </c:otherwise>
        </c:choose>
        ListUtils.createTable("listingTable", target, ${projectId}, ${config});
        if (target.hasOwnProperty('getUserManualUrl')) {
          Utils.ui.updateHelpLink(target.getUserManualUrl());
        }
      });
    </script>
  </div>
</div>
<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
