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
<script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.breadcrumbs.popup.js'/>"></script>

<script src="<c:url value='/scripts/jquery/datatables/js/jquery.dataTables.min.js'/>" type="text/javascript"></script>
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" type="text/css">

<script src="<c:url value='/scripts/datatables_utils.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/natural_sort.js'/>" type="text/javascript"></script>

<div id="maincontent">
  <div id="contentcolumn">
    <h1>Create Pool Wizard</h1>

    <div class="breadcrumbs">
      <ul>
        <li><a href='<c:url value="/"/>'>Home</a></li>
        <li>
          <div class="breadcrumbsbubbleInfo">
            <div class="trigger">
              <a href='<c:url value="/miso/project/${project.id}"/>'>${project.alias}</a>
            </div>
            <div class="breadcrumbspopup">${project.name}</div>
          </div>
        </li>
      </ul>
    </div>
    <br/><br/>

    <div id="studyTriggerDiv">
      <input id="newStudyTrigger" type="radio" onchange="jQuery('#newStudyForm').slideToggle();"/> Create a new Study
    </div>
    <div id="newStudyForm" style="display:none;">
      <button onClick="addStudy('newStudy');"
              class="fg-button ui-state-default ui-corner-all">Save Study
      </button>
      <form id="newStudy" method="POST" autocomplete="off">
        <table>
          <tbody>
          <tr>
            <td>Study Type:</td>
            <td>
              <select name="studyType">${studyTypes}</select>
            </td>
          </tr>
          <tr>
            <td>Study Description:</td>
            <td>
              <input type="text" id="studyDescription" name="studyDescription"/>
            </td>
          </tr>
          </tbody>
        </table>
      </form>
      <br/>
    </div>
    <hr/>
    <br/>
    This system will create <b>ONE</b> pool for each of the selected dilutions below:
    <br/>

    <table>
      <tbody>
      <tr>
        <td>Platform Type:</td>
        <td>
          <select id="platformType" name="platformType"
                  onchange="selectDilutionsByPlatform();">${platforms}</select>
        </td>
      </tr>
      <tr>
        <td>Pool Alias:</td>
        <td>
          <input type="text" id="alias" name="alias"/><br/>
        </td>
      </tr>
      <tr>
        <td>Concentration (${poolConcentrationUnits}):</td>
        <td>
          <input type="text" id="concentration" name="concentration"/><br/>
        </td>
      </tr>
      <tr>
        <td>Remove selected dilutions?</td>
        <td>
          <input id="removeDilutions" type="checkbox"/>
        </td>
      </tr>
      </tbody>
    </table>

    <h1>
      <div id="qcsTotalCount">
      </div>
    </h1>
    <ul class="sddm">
      <li>
        <a onmouseover="mopen('qcmenu')" onmouseout="mclosetime()">Options
          <span style="float:right" class="ui-icon ui-icon-triangle-1-s"></span>
        </a>

        <div id="qcmenu"
             onmouseover="mcancelclosetime()"
             onmouseout="mclosetime()">
          <a href='javascript:void(0);' class="add"
             onclick="Pool.wizard.insertPoolQCRow(); return false;">Add Pool QC</a>
        </div>
      </li>
    </ul>
    <span style="clear:both">
      <div id="addPoolQC"></div>
      <div id='addQcForm'>
        <table class="list" id="poolQcTable">
          <thead>
          <tr>
            <th>QC Date</th>
            <th>Method</th>
            <th>Results</th>
          </tr>
          </thead>
          <tbody>
          <c:if test="${not empty pool.poolQCs}">
            <c:forEach items="${pool.poolQCs}" var="qc">
              <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
                <td>${qc.qcCreator}</td>
                <td><fmt:formatDate value="${qc.qcDate}"/></td>
                <td>${qc.qcType.name}</td>
                <td id="result${qc.id}">${qc.results} ${qc.qcType.units}</td>
                <c:if test="${(library.securityProfile.owner.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                                          or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
                  <td id="edit${qc.id}" align="center"><a href="javascript:void(0);"
                                                          onclick="Pool.qc.changePoolQCRow('${qc.id}','${pool.id}')">
                    <span class="ui-icon ui-icon-pencil"></span></a></td>
                </c:if>
              </tr>
            </c:forEach>
          </c:if>
          </tbody>
        </table>
        <input type='hidden' id='qcPoolId' name='id' value='${pool.id}'/>
      </div>
      <script type="text/javascript">
        jQuery(document).ready(function () {
          jQuery("#poolQcTable").tablesorter({
            headers: {
            }
          });

          jQuery('#qcsTotalCount').html(jQuery('#poolQcTable>tbody>tr:visible').length.toString() + " QCs");
        });
      </script>
    </span>

    <table width="100%">
      <tbody>
      <tr>
        <td width="50%" valign="top">
          <div class="simplebox ui-corner-all">
            <h2>Available Poolables</h2>
            <button id="selectallbutton" onClick="selectallrows();"
                    class="ui-state-default ui-corner-all">Select All
            </button>
            <button id="selectnonebutton" onClick="selectnorow();"
                    class=" ui-state-default ui-corner-all">Select None
            </button>

            <button id="createPoolButton" onClick="createNewPool();"
                    class="fg-button ui-state-default ui-corner-all">Create New Pool
            </button>
            <table id="dlTable" class="display">
              <thead>
              <tr>
                <th>Dilution ID</th>
                <th>Dilution Name</th>
                <th>Concentration (${libraryDilutionUnits})</th>
                <th>Parent Library</th>
                <th>Description</th>
                <th>Parent Library Barcode</th>
              </tr>
              </thead>
              <tbody id="dilutions">
              </tbody>
            </table>
            <p>Don't see a dilution you're expecting? Make sure that the library has "QC Passed" set to "true".</p>
          </div>
        </td>
        <td width="50%" valign="top">
          <div class="simplebox ui-corner-all">
            <h2>Created Pools</h2>
            <div id="poolResult"></div>
          </div>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</div>

<script type="text/javascript">
  var ldselected = [];
  var dilutions;
  var oTable;
  var headers = ['dilutionId', 'dilutionName', 'concentration', 'library', 'description', 'libraryBarcode'];

  function addStudy(form) {
    if (jQuery('#studyDescription').val() == "") {
      alert('You have not entered a study description');
    }
    else {
      Fluxion.doAjax(
        'poolWizardControllerHelperService',
        'addStudy',
        {'form': jQuery('#' + form).serializeArray(), 'projectId':${project.id}, 'url': ajaxurl},
        {'doOnSuccess': function (json) {
          jQuery('#newStudyForm').html(json.html);
          jQuery('#studyTriggerDiv').html("");
        }
      });
    }
  }

  function selectDilutionsByPlatform() {
    dilutions = [];
    Fluxion.doAjax(
      'poolWizardControllerHelperService',
      'populateDilutions',
      {'platformType': jQuery('#platformType').val(), 'projectId':${project.id}, 'url': ajaxurl},
      {'doOnSuccess': function (json) {
        var table = jQuery('#dlTable').dataTable();
        table.fnClearTable();
        dilutions = json.dilutions;
        jQuery.each(dilutions, function (index, value) {
          var a = table.fnAddData([value.id, value.name, value.concentration, value.library, value.description, value.libraryBarcode]);
          var nTr = table.fnSettings().aoData[a[0]].nTr;
        });
        ldselected = [];
      }
    });
  }

  function createNewPool() {
    if (Utils.validation.isNullCheck(jQuery('#concentration').val())) {
      alert('You have not entered a final concentration for the new pool');
    }
    else {
      jQuery('#createPoolButton').attr('disabled', 'disabled');
      jQuery('#createPoolButton').html("Processing...");

      Fluxion.doAjax(
        'poolWizardControllerHelperService',
        'addPool',
        {'dilutions': ldselected,
          'platformType': jQuery('#platformType').val(),
          'alias': jQuery('#alias').val(),
          'concentration': jQuery('#concentration').val(),
          'qcs': Utils.mappifyTable('poolQcTable'),
          'url': ajaxurl},
        {'doOnSuccess': function (json) {
          jQuery('#poolResult').append(json.html);
          if (jQuery("#removeDilutions").attr('checked')) {
            var anSelected = fnGetSelected(oTable);
            jQuery.each(anSelected, function (index, value) {
              oTable.fnDeleteRow(value);
            });

          }
          jQuery('#concentration').val("");
          jQuery('#alias').val("");
          selectnorow();
          jQuery('#createPoolButton').removeAttr('disabled');
          jQuery('#createPoolButton').html("Create New Pool");
        },
        'doOnError': function (json) {
          alert(json.error);
          jQuery('#createPoolButton').removeAttr('disabled');
          jQuery('#createPoolButton').html("Create New Pool");
        }
      });
    }
  }

  function fnGetSelected(oTableLocal) {
    var aReturn = new Array();
    var aTrs = oTableLocal.fnGetNodes();

    for (var i = 0; i < aTrs.length; i++) {
      if (jQuery(aTrs[i]).hasClass('row_selected')) {
        aReturn.push(aTrs[i]);
      }
    }
    return aReturn;
  }

  jQuery(document).ready(function () {
    Fluxion.doAjax(
      'poolWizardControllerHelperService',
      'populateDilutions',
      {'platformType': "Illumina", 'projectId':${project.id}, 'url': ajaxurl},
      {'doOnSuccess': function (json) {
        oTable = jQuery('#dlTable').dataTable({
          "aoColumnDefs": [
            {
              "bUseRendered": false,
              "aTargets": [ 0 ]
            }
          ],
          "aaSorting": [
            [1, 'asc']
          ],
          "aoColumns": [
            null,
            { "sType": 'natural' },
            { "sType": 'natural' },
            { "sType": 'natural' },
            { "sType": 'natural' },
            { "sType": 'natural' }
          ],
          "fnRowCallback": function (nRow, aData, iDisplayIndex) {
            if (jQuery.inArray(aData[0], ldselected) != -1) {
              jQuery(nRow).addClass('row_selected');
            }
            return nRow;
          },
          "iDisplayLength": 50,
          "bInfo": true,
          "bJQueryUI": true,
          "bAutoWidth": true,
          "bFilter": true,
          "sDom": '<<"toolbar">f>r<t>ip>'
        });
        dilutions = json.dilutions;

        jQuery.each(dilutions, function (index, value) {
          var a = oTable.fnAddData([value.id, value.name, value.concentration, value.library, value.description, value.libraryBarcode]);
          var nTr = oTable.fnSettings().aoData[a[0]].nTr;
        });
      }
    });

    /* Click event handler */
    jQuery('#dlTable tbody tr').live('click', function () {
      var aData = oTable.fnGetData(this);
      var iId = aData[0];

      if (jQuery.inArray(iId, ldselected) == -1) {
        ldselected[ldselected.length++] = iId;
      }
      else {
        ldselected = jQuery.grep(ldselected, function (value) {
          return value != iId;
        });
      }
      jQuery(this).toggleClass('row_selected');
    });
  });

  function selectallrows() {
    jQuery.each(dilutions, function (index, value) {
      if (jQuery.inArray(value.id, ldselected) == -1) {
        ldselected[ldselected.length++] = value.id;
      }
    });
    jQuery.each(jQuery('#dlTable tbody tr'), function (index, value) {
      jQuery(this).addClass('row_selected');
    });
  }

  function selectnorow() {
    ldselected = [];
    jQuery.each(jQuery('#dlTable tbody tr'), function (index, value) {
      var aData = oTable.fnGetData(this);
      var iId = aData[0];
      jQuery(this).removeClass('row_selected');
    });
  }
</script>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>