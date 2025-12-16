<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../header.jsp" %>

<div id="maincontent">
  <div id="contentcolumn">

    <h1 class="noPrint">
      <c:choose><c:when test="${pageMode eq 'create'}">Create</c:when><c:otherwise>Edit</c:otherwise></c:choose> SOP
      <c:if test="${isAdmin}">
        <button id="save" type="button" class="fg-button ui-state-default ui-corner-all">Save</button>
      </c:if>
    </h1>

    <form:form id="sopForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8"></form:form>

    <div class="sectionDivider noPrint">SOP Fields</div>
    <div id="sopForm_fieldsError" class="errorContainer"></div>

    <div id="sopFieldsUnsupported" style="display:none;">
      <div class="messagebox">
        SOP Fields are only supported for Run category SOPs.
      </div>
    </div>

    <div id="listSopFields"></div>

    <script type="text/javascript">
      jQuery(function () {
        var sop = ${sopDto};
        var config = {
          pageMode: '${pageMode}',
          isAdmin: ${isAdmin},
          sopId: sop && sop.id ? sop.id : null
        };

        var form = FormUtils.createForm("sopForm", config.isAdmin ? "save" : null, sop, "sop", config);
        Sop.setForm(form);
        Sop.setListConfig(config);
        Sop.setFields((sop && sop.fields) ? sop.fields : []);

        var category = sop && sop.category;
        var isRun = category === "RUN";
        jQuery("#listSopFields").toggle(isRun);
        jQuery("#sopFieldsUnsupported").toggle(!isRun);

        Utils.ui.updateHelpLink(FormTarget.sop.getUserManualUrl());
      });
    </script>

  </div>
</div>

<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>
