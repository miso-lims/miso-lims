<%--
  ~ Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
  ~ MISO project contacts: Robert Davey @ TGAC
  --%>
<%@ include file="../header.jsp" %>

<div id="maincontent">
  <div id="contentcolumn">

    <h1 class="noPrint">
      <span id="pageTitle"><c:out value="${title}" /></span>
      <button id="save" type="button" class="fg-button ui-state-default ui-corner-all">Save</button>
    </h1>

    <!-- Match *.jsp Quick Help layout/alignment -->
    <div class="right fg-toolbar ui-helper-clearfix paging_full_numbers"></div>

    <div class="sectionDivider noPrint"
         onclick="Utils.ui.toggleLeftInfo(jQuery('#quickHelp_arrowclick'), 'quickHelpSection');">
      Quick Help
      <div id="quickHelp_arrowclick" class="toggleLeft"></div>
    </div>

    <div id="quickHelpSection" class="note noPrint" style="display:none;">
      <b>Sop Fields:</b><br/>
      Add one or more fields for this SOP. Each field has a Name, Type, and optional Units.
      Field names must be unique (case-insensitive).
      <br/>
    </div>

    <div id="warnings"></div>
    <div id="sopForm_error" class="errorContainer"></div>
    <form:form id="sopForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8"></form:form>

    <br/>

    <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#fields_arrowclick'), 'sopFieldsSection');">
      SOP Fields
      <div id="fields_arrowclick" class="toggleLeftDown"></div>
    </div>

    <!-- Assay-style embedded table container -->
    <div id="sopFieldsSection">
      <div id="sopFieldsUnsupported" class="note" style="display:none;">
        SOP fields are only supported for RUN SOPs.
      </div>

      <div id="sopFieldsError" class="errorContainer"></div>

      <!-- MUST match Sop list id in sop_ajax.js -->
      <div id="listSopFields"></div>
    </div>

    <br/>

    <!-- Required scripts (must be BEFORE inline script below) -->
    <script type="text/javascript" src="<c:url value='/scripts/sop_ajax.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/scripts/list_sopfield.js'/>"></script>

    <script type="text/javascript">
      jQuery(function () {
        var dto = ${sop};

        if (!jQuery("#pageTitle").text() || jQuery("#pageTitle").text().trim() === "") {
          jQuery("#pageTitle").text(dto && dto.id ? ("Edit SOP: " + (dto.alias || dto.id)) : "Create SOP");
        }

        if (dto && dto.id && window.Warning && window.WarningTarget && WarningTarget.sop) {
          Warning.generateHeaderWarnings("warnings", WarningTarget.sop, dto);
        }

        // Build form (capture form API)
        var form = FormUtils.createForm("sopForm", "save", dto, "sop", {});

        function findCategorySelect() {
          var $sel = jQuery("#sopForm select[name='category']");
          if ($sel.length) return $sel;

          $sel = jQuery("#sopForm select#category");
          if ($sel.length) return $sel;

          $sel = jQuery("#sopForm select[id$='_category']");
          if ($sel.length) return $sel;

          $sel = jQuery("#sopForm select").filter(function () {
            var all = jQuery(this).find("option").map(function () {
              return (jQuery(this).val() || jQuery(this).text() || "").toString().toUpperCase();
            }).get().join(" ");
            return all.indexOf("RUN") !== -1 && all.indexOf("LIBRARY") !== -1 && all.indexOf("SAMPLE") !== -1;
          }).first();

          return $sel;
        }

        function getCategoryValue() {
          var $cat = findCategorySelect();
          if ($cat && $cat.length) {
            return String($cat.val() || "").toUpperCase();
          }
          return String(dto && dto.category ? dto.category : "").toUpperCase();
        }

        function showFieldsTable() {
          var listConfig = {
            isAdmin: true,
            pageMode: (dto && dto.id) ? "edit" : "create",
            sopId: (dto && dto.id) ? dto.id : null
          };

          if (window.Sop && typeof Sop.setForm === "function") {
            Sop.setForm(form);
          }
          if (window.Sop && typeof Sop.setListConfig === "function") {
            Sop.setListConfig(listConfig);
          }
          if (window.Sop && typeof Sop.setFields === "function") {
            Sop.setFields((dto && dto.sopFields) ? dto.sopFields : []);
          }
        }

        function toggleFieldsUI(enabled) {
          if (!enabled) {
            jQuery("#listSopFields").hide();
            jQuery("#sopFieldsUnsupported").show();
            jQuery("#sopFieldsError").empty();
            if (dto) dto.sopFields = [];
            return;
          }

          jQuery("#sopFieldsUnsupported").hide();
          jQuery("#listSopFields").show();
          showFieldsTable();
        }
        
        setTimeout(function () {
          toggleFieldsUI(getCategoryValue() === "RUN");

          var $cat = findCategorySelect();
          if ($cat && $cat.length) {
            $cat.off("change.sopFields").on("change.sopFields", function () {
              toggleFieldsUI(getCategoryValue() === "RUN");
            });
          }
        }, 0);

        if (window.FormTarget && FormTarget.sop && typeof FormTarget.sop.getUserManualUrl === "function") {
          Utils.ui.updateHelpLink(FormTarget.sop.getUserManualUrl());
        }
      });
    </script>

  </div>
</div>

<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>
