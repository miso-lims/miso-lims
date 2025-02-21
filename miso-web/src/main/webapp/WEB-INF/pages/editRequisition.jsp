<%@ include file="../header.jsp" %>

  <div id="maincontent">
    <div id="contentcolumn">

      <h1>
        <c:choose>
          <c:when test="${pageMode eq 'create'}">Create</c:when>
          <c:otherwise>Edit</c:otherwise>
        </c:choose> Requisition
        <button id="save" type="button" class="fg-button ui-state-default ui-corner-all">Save</button>
      </h1>

      <form:form id="requisitionForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8"></form:form>

      <br />
      <h1>Assays</h1>
      <div id="requisitionForm_assaysError"></div>
      <div id="listAssays"></div>

      <div id="samples">
        <c:choose>
          <c:when test="${pageMode eq 'create'}">
            <p>Samples can be added after saving the requisition.</p>
          </c:when>
          <c:otherwise>
            <div class="sectionDivider"
              onclick="Utils.ui.toggleLeftInfo(jQuery('#pauses_section_arrowclick'), 'pauses_section');">
              Pauses
              <div id="pauses_section_arrowclick" class="toggleLeftDown"></div>
            </div>
            <div id="pauses_section" class="expandable_section">
              <h1>Pauses</h1>
              <div id="requisitionForm_pausesError"></div>
              <div id="listPauses"></div>
            </div>

            <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#notes_arrowclick'), 'notes');">Notes
              <div id="notes_arrowclick" class="toggleLeftDown"></div>
            </div>
            <div id="notes">
              <h1>Notes</h1>
              <ul class="sddm">
                <li>
                  <a onmouseover="mopen('notesmenu')" onmouseout="mclosetime()">Options
                    <span style="float:right" class="ui-icon ui-icon-triangle-1-s"></span>
                  </a>
                  <div id="notesmenu" onmouseover="mcancelclosetime()" onmouseout="mclosetime()">
                    <a onclick="Utils.notes.showNoteDialog('requisition', ${requisition.id});"
                      href="javascript:void(0);" class="add">Add Note</a>
                  </div>
                </li>
              </ul>
              <c:if test="${fn:length(requisition.notes) > 0}">
                <div class="note" style="clear:both">
                  <c:forEach items="${requisition.notes}" var="note" varStatus="n">
                    <div class="exppreview" id="requisition-notes-${n.count}">
                      <b>${note.creationDate}</b>: ${note.text}
                      <span class="float-right" style="font-weight:bold; color:#C0C0C0;">${note.owner.loginName}
                        <c:if test="${miso:isCurrentUser(note.owner.loginName) or miso:isAdmin()}">
                          <span style="color:#000000">
                            <a href='#'
                              onclick="Utils.notes.deleteNote('requisition', '${requisition.id}', '${note.id}'); return false;">
                              <span class="ui-icon ui-icon-trash note-delete-icon"></span>
                            </a>
                          </span>
                        </c:if>
                      </span>
                    </div>
                  </c:forEach>
                </div>
              </c:if>
              <div id="addRequisitionNoteDialog" title="Create new Note"></div>
            </div>

            <div class="sectionDivider"
              onclick="Utils.ui.toggleLeftInfo(jQuery('#attachments_section_arrowclick'), 'attachments_section');">
              Attachments
              <div id="attachments_section_arrowclick" class="toggleLeftDown"></div>
            </div>
            <div id="attachments_section" class="expandable_section">
              <miso:attachments item="${requisition}" collapseId="attachments_section" />
            </div>

            <div class="sectionDivider"
              onclick="Utils.ui.toggleLeftInfo(jQuery('#qcs_section_arrowclick'), 'qcs_section');">
              QCs
              <div id="qcs_section_arrowclick" class="toggleLeftDown"></div>
            </div>
            <div id="qcs_section" class="expandable_section">
              <miso:qcs id="list_qc" item="${requisition}" />
            </div>

            <div class="sectionDivider"
              onclick="Utils.ui.toggleLeftInfo(jQuery('#req_samples_section_arrowclick'), 'req_samples_section');">
              Requisitioned Samples
              <div id="req_samples_section_arrowclick" class="toggleLeftDown"></div>
            </div>
            <div id="req_samples_section" class="expandable_section">
              <miso:list-section-ajax id="list_samples" name="Requisitioned Samples" target="sample"
                config="{requisitionId: ${requisition.id}, supplemental: false, requisition: ${requisitionDto}, collapseId: 'req_samples_section'}" />
            </div>

            <div class="sectionDivider"
              onclick="Utils.ui.toggleLeftInfo(jQuery('#supl_samples_section_arrowclick'), 'supl_samples_section');">
              Supplemental Samples
              <div id="supl_samples_section_arrowclick" class="toggleLeftDown"></div>
            </div>
            <div id="supl_samples_section" class="expandable_section">
              <miso:list-section-ajax id="list_supplementalsamples" name="Supplemental Samples" target="sample"
                config="{requisitionId: ${requisition.id}, supplemental: true, requisition: ${requisitionDto}, identities: ${identityDtos}, collapseId: 'supl_samples_section'}" />
            </div>

            <div class="sectionDivider"
              onclick="Utils.ui.toggleLeftInfo(jQuery('#req_libraries_section_arrowclick'), 'req_libraries_section');">
              Requisitioned Libraries
              <div id="req_libraries_section_arrowclick" class="toggleLeftDown"></div>
            </div>
            <div id="req_libraries_section" class="expandable_section">
              <miso:list-section-ajax id="list_libraries" name="Requisitioned Libraries" target="library"
                config="{requisitionId: ${requisition.id}, relation: 'requisitioned', requisition: ${requisitionDto}, collapseId: 'req_libraries_section'}" />
            </div>

            <div class="sectionDivider"
              onclick="Utils.ui.toggleLeftInfo(jQuery('#supl_libraries_section_arrowclick'), 'supl_libraries_section');">
              Supplemental Libraries
              <div id="supl_libraries_section_arrowclick" class="toggleLeftDown"></div>
            </div>
            <div id="supl_libraries_section" class="expandable_section">
              <miso:list-section-ajax id="list_supplementallibraries" name="Supplemental Libraries" target="library"
                config="{requisitionId: ${requisition.id}, relation: 'supplemental', requisition: ${requisitionDto}, identities: ${identityDtos}, collapseId: 'supl_libraries_section'}" />
            </div>
            <br />

            <c:if test="${detailedSample}">
              <miso:list-section-ajax id="list_preparedsamples" name="Prepared Samples" target="sample" 
                config="{requisitionId: ${requisition.id}, filter: 'samples-prepared'}" />
            </c:if>
            <miso:list-section-ajax id="list_preparedlibraries" name="Prepared Libraries" target="library"
              config="{ requisitionId: ${requisition.id}, relation: 'indirect' }" />
            <miso:list-section id="list_runs" name="Runs" target="run" items="${runs}"
              config="{requisitionId: ${requisition.id}}" />

            <br />
            <h1>Run-Libraries</h1>
            <div id="list_runLibraries">
              <img src="/styles/images/ajax-loader.gif" class="fg-button" />
            </div>

            <miso:changelog item="${requisition}" />
          </c:otherwise>
        </c:choose>
      </div>
      <br />

      <script type="text/javascript">
        jQuery(document).ready(function () {
          var requisition = ${ requisitionDto };
          var config = {
            pageMode: '${pageMode}',
          };
          var assayConfig = {
            requisitionId: requisition.id
          };

          <c:if test="${pageMode eq 'edit'}">
            assayConfig["potentialAssayIds"] = ${potentialAssayIds};
            assayConfig["numberOfRequisitionedItems"] = ${numberOfRequisitionedItems};
          </c:if>

          var form = FormUtils.createForm('requisitionForm', 'save', requisition, 'requisition', config);
          Requisition.setForm(form);
          Utils.ui.updateHelpLink(FormTarget.requisition.getUserManualUrl());

          Requisition.setAssaysListConfig(assayConfig);
          var assayIds = requisition.assayIds || [];
          Requisition.setAssays(assayIds.map(function (assayId) {
            return Utils.array.findUniqueOrThrow(Utils.array.idPredicate(assayId), Constants.assays);
          }));

          if ('${pageMode}' === 'edit') {
            Requisition.setPauses(requisition.pauses);
            if (!requisition.pauses || !requisition.pauses.length) {
              Utils.ui.collapse('#pauses_section', '#pauses_section_arrowclick');
            }
            <c:if test="${empty requisition.notes}">
              Utils.ui.collapse('#notes', '#notes_arrowclick');
            </c:if>
            $.ajax({
              url: Urls.rest.requisitions.listRunLibraries(${ requisition.id }),
              dataType: 'json'
            }).done(function (data) {
              $('#list_runLibraries').empty();
              FormUtils.setTableData(ListTarget.runaliquot, { requisitionId: ${ requisition.id }}, 'list_runLibraries', data);
          }).fail(function () {
            Utils.showOkDialog('Error', ['Failed to load run-libraries']);
          });
          }
        });
      </script>

    </div>
  </div>

  <%@ include file="adminsub.jsp" %>
    <%@ include file="../footer.jsp" %>