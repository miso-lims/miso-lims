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

      <br>
      <div id="samples">
        <c:choose>
          <c:when test="${pageMode eq 'create'}">
            <p>Samples can be added after saving the requisition.</p>
          </c:when>
          <c:otherwise>
            <h1>Pauses</h1>
            <div id="requisitionForm_pausesError"></div>
            <div id="listPauses"></div>
            <br>

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
            <br />

            <miso:attachments item="${requisition}" />
            <miso:qcs id="list_qc" item="${requisition}" />
            <miso:list-section-ajax id="list_samples" name="Requisitioned Samples" target="sample"
              config="{requisitionId: ${requisition.id}, supplemental: false, requisition: ${requisitionDto}}" />
            <br>
            <miso:list-section-ajax id="list_supplementalsamples" name="Supplemental Samples" target="sample"
              config="{requisitionId: ${requisition.id}, supplemental: true, requisition: ${requisitionDto}}" />

            <miso:list-section-ajax id="list_libraries" name="Requisitioned Libraries" target="library"
              config="{requisitionId: ${requisition.id}, relation: 'requisitioned', requisition: ${requisitionDto}}" />
            <br>
            <miso:list-section-ajax id="list_supplementallibraries" name="Supplemental Libraries" target="library"
              config="{requisitionId: ${requisition.id}, relation: 'supplemental', requisition: ${requisitionDto}}" />

            <c:if test="${detailedSample}">
              <miso:list-section id="list_extractions" name="Extractions" target="sample" items="${extractions}" />
            </c:if>
            <miso:list-section-ajax id="list_preparedlibraries" name="Prepared Libraries" target="library"
              config="{ requisitionId: ${requisition.id}, relation: 'indirect' }" />
            <miso:list-section id="list_runs" name="Runs" target="run" items="${runs}"
              config="{requisitionId: ${requisition.id}}" />

            <br>
            <h1>Run-Libraries</h1>
            <div id="list_runLibraries">
              <img src="/styles/images/ajax-loader.gif" class="fg-button" />
            </div>
            <br>

            <miso:changelog item="${requisition}" />
          </c:otherwise>
        </c:choose>
      </div>
      <br>

      <script type="text/javascript">
        jQuery(document).ready(function () {
          var requisition = ${ requisitionDto };
          var config = {
            pageMode: '${pageMode}',
          };

          <c:if test="${pageMode eq 'edit'}">
            config["potentialAssayIds"] = ${potentialAssayIds};
            config["numberOfRequisitionedItems"] = ${numberOfRequisitionedItems};
          </c:if>

          var form = FormUtils.createForm('requisitionForm', 'save', requisition, 'requisition', config);
          Requisition.setForm(form);
          Utils.ui.updateHelpLink(FormTarget.requisition.getUserManualUrl());

          if ('${pageMode}' === 'edit') {
            Requisition.setPauses(requisition.pauses);
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