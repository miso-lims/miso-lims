<%@ include file="../header.jsp" %>

<div id="maincontent">
  <div id="contentcolumn">
    <h1>My Account</h1>

    <div class="portlet">
      <div class="portlet-header">My Account</div>
      <div class="portlet-content">
        User: ${userRealName} <a href="<c:url value='/user/${userId}'/>">Edit</a><br/>
        Groups: ${userGroups}<br/><br/>
        API Key: ${apiKey}
      </div>
    </div>

    <div class="column">
      <c:if test="${miso:isAdmin()}">
        <div class="portlet">
          <div class="portlet-header">Administration</div>
          <div class="portlet-content">
            <a href="javascript:void(0);" onclick="Admin.clearCache();">Clear Cache</a><br/>
            <c:if test="${autoGenerateIdBarcodes}">
              <a href="javascript:void(0);" onclick="Admin.regenBarcodes();">Regenerate All Barcodes</a><br/>
            </c:if>
            <a href="javascript:void(0);" onclick="Admin.refreshConstants();">Refresh Constants</a><br/>
          </div>
        </div>
      </c:if>
    </div>
  </div>
</div>

<script type="text/javascript">
  jQuery(function () {
    jQuery(".column").sortable({
      connectWith: '.column',
      handle: '.portlet-header'
    });

    jQuery(".portlet").addClass("ui-widget ui-widget-content ui-helper-clearfix ui-corner-all")
        .find(".portlet-header")
        .addClass("ui-widget-header ui-corner-all")
        .prepend('<span class="ui-icon ui-icon-minusthick"></span>')
        .end()
        .find(".portlet-content");

    jQuery(".portlet-header .ui-icon").click(function () {
      jQuery(this).toggleClass("ui-icon-minusthick").toggleClass("ui-icon-plusthick");
      jQuery(this).parents(".portlet:first").find(".portlet-content").toggle();
    });

    jQuery(".column").disableSelection();
  });

</script>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
