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

<%--
  Created by IntelliJ IDEA.
  User: bian
  Date: 19-Apr-2010
  Time: 11:24:27

--%>
<%@ include file="../header.jsp" %>
<script src="<c:url value='/scripts/datatables_utils.js?ts=${timestamp.time}'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/datatables/jquery.dataTables.min.js'/>" type="text/javascript"></script>
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/datatable.css'/>" type="text/css">

<div id="maincontent">
<div id="contentcolumn">
<h1>Pools</h1>

<div id="tabs">
<ul>
  <li><a href="#tab-1"><span>Illumina Pools</span></a></li>
  <li><a href="#tab-2"><span>LS454 Pools</span></a></li>
  <li><a href="#tab-3"><span>Solid Pools</span></a></li>
</ul>

<div id="tab-1">
  <h1>
    <div id="illuminatotalCount">
    </div>
  </h1>
  <ul class="sddm">
    <li><a
            onmouseover="mopen('ipomenu')"
            onmouseout="mclosetime()">Options <span style="float:right"
                                                    class="ui-icon ui-icon-triangle-1-s"></span></a>

      <div id="ipomenu"
           onmouseover="mcancelclosetime()"
           onmouseout="mclosetime()">
        <a href='<c:url value="/miso/pool/illumina/new"/>'>Add Illumina Pool</a>
        <a href='javascript:void(0);' onclick="Pool.barcode.selectPoolBarcodesToPrint('#illumina'); return false;">Print Barcodes
          ...</a>
      </div>
    </li>
  </ul>

  <form id="filter-form1">Filter: <input name="filter1" id="filter1" value="" maxlength="30" size="30"
                                         type="text"></form>
  <br/>
  <table class="list" id="illumina">
    <thead>
    <tr>
      <th>Name</th>
      <th>Alias</th>
      <th>Date Created</th>
      <th>Information</th>
      <th>Average Insert Size</th>
      <th class="fit">Edit</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${illuminaPools}" var="ipool">
      <tr poolId="${ipool.id}" onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
        <td>${ipool.name}</td>
        <td>${ipool.alias}</td>
        <td>${ipool.creationDate}</td>
        <td>
          <c:if test="${not empty ipool.dilutions}">
            <ul>
              <c:forEach items="${ipool.dilutions}" var="dil">
                <li><b>${dil.name}</b>
                  <ul><u>${dil.library.sample.project.alias}</u>
                    <li>${dil.library.alias} (${dil.library.name})</li>
                    <li>${dil.library.description} (${dil.library.sample.name})</li>
                  </ul>
                </li>
              </c:forEach>
            </ul>
          </c:if>
        </td>

        <td>
          <div id="average${ipool.id}" class="averageInsertSize"></div>
        </td>
        <td class="misoicon"
            onclick="window.location.href='<c:url value="/miso/pool/illumina/${ipool.id}"/>'"><span class="ui-icon ui-icon-pencil"/></td>
      </tr>

    </c:forEach>
    </tbody>
  </table>
  <script type="text/javascript">
    jQuery(function() {
      var theTable = jQuery("#illumina");

      jQuery("#filter1").keyup(function() {
        jQuery.uiTableFilter(theTable, this.value);
        writeTotalNo();
      });

      jQuery('#filter-form1').submit(
              function() {
                theTable.find("tbody > tr:visible > td:eq(1)").mousedown();
                return false;
              }).focus(); //Give focus to input field
    });
  </script>
  <br/><br/>
</div>

<div id="tab-2">
  <h1>
    <div id="ls454totalCount">
    </div>
  </h1>
  <ul class="sddm">
    <li><a
            onmouseover="mopen('lpomenu')"
            onmouseout="mclosetime()">Options <span style="float:right"
                                                    class="ui-icon ui-icon-triangle-1-s"></span></a>

      <div id="lpomenu"
           onmouseover="mcancelclosetime()"
           onmouseout="mclosetime()">
        <a href='<c:url value="/miso/pool/ls454/new"/>'>Add LS454 Pool</a>
        <a href='javascript:void(0);' onclick="Pool.barcode.selectPoolBarcodesToPrint('#ls454'); return false;">Print Barcodes
          ...</a>
      </div>
    </li>
  </ul>

  <form id="filter-form2">Filter: <input name="filter2" id="filter2" value="" maxlength="30" size="30"
                                         type="text"></form>
  <br/>
  <table class="list" id="ls454">
    <thead>
    <tr>
      <th>Name</th>
      <th>Alias</th>
      <th>Creation Date</th>
      <th>Information</th>
      <th>Average Insert Size</th>
      <th class="fit">Edit</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${ls454Pools}" var="lpool">
      <tr poolId="${lpool.id}" onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
        <td>${lpool.name}</td>
        <td>${lpool.alias}</td>
        <td>${lpool.creationDate}</td>
        <td>
          <c:if test="${not empty lpool.dilutions}">
            <ul>
              <c:forEach items="${lpool.dilutions}" var="dil">
                <li><b>${dil.name}</b>
                  <ul><u>${dil.library.sample.project.alias}</u>
                    <li>${dil.library.alias} (${dil.library.name})</li>
                    <li>${dil.library.description} (${dil.library.sample.name})</li>
                  </ul>
                </li>
              </c:forEach>
            </ul>
          </c:if>
        </td>
        <td>
          <div id="average${lpool.id}" class="averageInsertSize"></div>
        </td>
        <td class="misoicon"
            onclick="window.location.href='<c:url value="/miso/pool/ls454/${lpool.id}"/>'"><span class="ui-icon ui-icon-pencil"/></td>
      </tr>

    </c:forEach>
    </tbody>
  </table>
  <script type="text/javascript">

    jQuery(function() {
      var theTable = jQuery("#ls454");

      jQuery("#filter2").keyup(function() {
        jQuery.uiTableFilter(theTable, this.value);
        writeTotalNo();
      });

      jQuery('#filter-form2').submit(
              function() {
                theTable.find("tbody > tr:visible > td:eq(1)").mousedown();
                return false;
              }).focus(); //Give focus to input field
    });
  </script>
  <br/><br/>
</div>

<div id="tab-3">
  <h1>
    <div id="solidtotalCount">
    </div>
  </h1>
  <ul class="sddm">
    <li><a
            onmouseover="mopen('spomenu')"
            onmouseout="mclosetime()">Options <span style="float:right"
                                                    class="ui-icon ui-icon-triangle-1-s"></span></a>

      <div id="spomenu"
           onmouseover="mcancelclosetime()"
           onmouseout="mclosetime()">
        <a href='<c:url value="/miso/pool/solid/new"/>'>Add Solid Pool</a>
        <a href='javascript:void(0);' onclick="Pool.barcode.selectPoolBarcodesToPrint('#solid'); return false;">Print Barcodes
          ...</a>
      </div>
    </li>
  </ul>

  <form id="filter-form3">Filter: <input name="filter3" id="filter3" value="" maxlength="30" size="30"
                                         type="text"></form>
  <br/>
  <table class="list" id="solid">
    <thead>
    <tr>
      <th>Name</th>
      <th>Alias</th>
      <th>Creation Date</th>
      <th>Information</th>
      <th>Average Insert Size</th>
      <th class="fit">Edit</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${solidPools}" var="spool">
      <tr poolId="${spool.id}" onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
        <td>${spool.name}</td>
        <td>${spool.alias}</td>
        <td>${spool.creationDate}</td>
        <td>
          <c:if test="${not empty spool.dilutions}">
            <ul>
              <c:forEach items="${spool.dilutions}" var="dil">
                <li><b>${dil.name}</b>
                  <ul><u>${dil.library.sample.project.alias}</u>
                    <li>${dil.library.alias} (${dil.library.name})</li>
                    <li>${dil.library.description} (${dil.library.sample.name})</li>
                  </ul>
                </li>
              </c:forEach>
            </ul>
          </c:if>
        </td>
        <td>
          <div id="average${spool.id}" class="averageInsertSize"></div>
        </td>
        <td class="misoicon"
            onclick="window.location.href='<c:url value="/miso/pool/solid/${spool.id}"/>'"><span class="ui-icon ui-icon-pencil"/></td>
      </tr>

    </c:forEach>
    </tbody>
  </table>
  <script type="text/javascript">
    jQuery(document).ready(function() {
      jQuery("#tabs").tabs();
      Pool.ui.listPoolAverageInsertSizes();

      writeTotalNo();

      jQuery("#illumina").tablesorter({
        headers: {
          3: {
            sorter: false
          },
          4: {
            sorter: false
          },
          5: {
            sorter: false
          }
        }
      });

      jQuery("#ls454").tablesorter({
        headers: {
          3: {
           sorter: false
          },
          4: {
           sorter: false
          },
          5: {
           sorter: false
          }
        }
      });

      jQuery("#solid").tablesorter({
        headers: {
          3: {
            sorter: false
          },
          4: {
            sorter: false
          },
          5: {
            sorter: false
          }
        }
      });
    });

    jQuery(function() {
      var theTable = jQuery("#solid");

      jQuery("#filter3").keyup(function() {
        jQuery.uiTableFilter(theTable, this.value);
        writeTotalNo();
      });

      jQuery('#filter-form3').submit(
        function() {
          theTable.find("tbody > tr:visible > td:eq(1)").mousedown();
          return false;
        }).focus(); //Give focus to input field
    });

    function writeTotalNo() {
      jQuery('#illuminatotalCount').html(jQuery('#illumina>tbody>tr:visible').length.toString() + " Illumina Pools");
      jQuery('#ls454totalCount').html(jQuery('#ls454>tbody>tr:visible').length.toString() + " LS454 Pools");
      jQuery('#solidtotalCount').html(jQuery('#solid>tbody>tr:visible').length.toString() + " Solid Pools");
    }
  </script>
</div>

</div>
</div>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>