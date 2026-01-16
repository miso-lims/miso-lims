<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../header.jsp" %>

<div id="maincontent">
  <div id="contentcolumn">

    <h1 class="noPrint">
      <c:out value="${title}" />
      <c:if test="${isAdmin}">
        <button id="save" type="button" class="fg-button ui-state-default ui-corner-all">Save</button>
      </c:if>
    </h1>

    <div id="warnings"></div>

    <form:form id="sopForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8"></form:form>

    <div class="sectionDivider noPrint">SOP Fields</div>
    <div id="sopFieldsError"></div>

    <div id="sopFieldsUnsupported" style="display:none;">
      <div class="messagebox">
        SOP Fields are only supported for Run category SOPs.
      </div>
    </div>

    <div id="listSopFields"></div>

    <script type="text/javascript">
      jQuery(function () {
        var sop = <c:out value="${sopJson}" escapeXml="false" />;

        var config = {
          pageMode: '${pageMode}',
          isAdmin: ${isAdmin},
          sopId: sop && sop.id ? sop.id : null
        };

        if (sop && sop.id && window.Warning && window.WarningTarget && WarningTarget.sop) {
          Warning.generateHeaderWarnings("warnings", WarningTarget.sop, sop);
        }

        var form = FormUtils.createForm("sopForm", config.isAdmin ? "save" : null, sop, "sop", config);

        if (window.Sop && typeof Sop.setForm === "function") {
          Sop.setForm(form);
        }
        if (window.Sop && typeof Sop.setListConfig === "function") {
          Sop.setListConfig(config);
        }
        if (window.Sop && typeof Sop.setFields === "function") {
          Sop.setFields((sop && sop.sopFields) ? sop.sopFields : []);
        }

        function normalizeCategory(value) {
          return String(value || "").trim().toUpperCase();
        }

        function getSelectedCategory() {
          var $cat = jQuery("#sopForm_category");
          if ($cat.length) return normalizeCategory($cat.val());
          return normalizeCategory(sop && sop.category);
        }

        function syncCategoryFromSelect() {
          var $cat = jQuery("#sopForm_category");
          if (!$cat.length) return;
          var selected = normalizeCategory($cat.val());
          sop.category = selected;
          $cat.val(selected);
        }

        function toggleFieldsUI() {
          var cat = getSelectedCategory();
          if (cat !== "RUN") {
            jQuery("#listSopFields").hide();
            jQuery("#sopFieldsUnsupported").show();
            jQuery("#sopFieldsError").empty();

            if (window.Sop && typeof Sop.setFields === "function") {
              Sop.setFields([]);
            }
          } else {
            jQuery("#sopFieldsUnsupported").hide();
            jQuery("#listSopFields").show();
          }
        }

        syncCategoryFromSelect();
        toggleFieldsUI();

        jQuery(document).on("change", "#sopForm_category", function () {
          syncCategoryFromSelect();
          toggleFieldsUI();
        });

        jQuery("#save").on("mousedown", function () {
          syncCategoryFromSelect();
        });

        if (window.FormTarget && FormTarget.sop && typeof FormTarget.sop.getUserManualUrl === "function") {
          Utils.ui.updateHelpLink(FormTarget.sop.getUserManualUrl());
        }
      });
    </script>

  </div>
</div>

<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>
