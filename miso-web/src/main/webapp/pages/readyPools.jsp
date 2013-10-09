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
  User: davey
  Date: 05-Jul-2011
  Time: 16:48:27

--%>
<%@ include file="../header.jsp" %>

<div id="maincontent">
<div id="contentcolumn">
<script type="text/javascript">
  jQuery(document).ready(function () {
    jQuery("#tabs").tabs();
  });
</script>
<h1>Pools</h1>

<div id="tabs">
<ul>
  <li><a href="#tab-1"><span>Ready Illumina Pools</span></a></li>
  <li><a href="#tab-2"><span>Ready LS454 Pools</span></a></li>
  <li><a href="#tab-3"><span>Ready Solid Pools</span></a></li>
</ul>

<div id="tab-1">
  <form id="filter-form1">Filter:
    <input name="filter1" id="filter1" value="" maxlength="30" size="30" type="text">
  </form>
  <br/>
  <table class="list" id="illumina">
    <thead>
    <tr>
      <th>Name</th>
      <th>Alias</th>
      <th>Barcode</th>
      <th>Date Created</th>
      <th class="fit">Edit</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${illuminaPools}" var="ipool">
      <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
        <td>${ipool.name}</td>
        <td>${ipool.alias}</td>
        <td>${ipool.identificationBarcode}</td>
        <td>${ipool.creationDate}</td>
        <td class="misoicon"
            onclick="window.location.href='<c:url value="/miso/pool/${ipool.id}"/>'"><span
            class="ui-icon ui-icon-pencil"/></td>
      </tr>
    </c:forEach>
    </tbody>
  </table>

  <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#ipu_arrowclick'), 'ipudiv');"
       style="font: bold 80% Helvetica;">Used pools
    <div id="ipu_arrowclick" class="toggleLeft"></div>
  </div>
  <div id="ipudiv" class="simplebox ui-corner-all" style="display:none;">
    <table class="list" id="illuminaUsed">
      <thead>
      <tr>
        <th>Name</th>
        <th>Alias</th>
        <th>Barcode</th>
        <th>Date Created</th>
        <th class="fit">Edit</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${illuminaPoolsUsed}" var="ipool">
        <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
          <td>${ipool.name}</td>
          <td>${ipool.alias}</td>
          <td>${ipool.identificationBarcode}</td>
          <td>${ipool.creationDate}</td>
          <td class="misoicon"
              onclick="window.location.href='<c:url value="/miso/pool/${ipool.id}"/>'"><span
              class="ui-icon ui-icon-pencil"/></td>
        </tr>
      </c:forEach>
      </tbody>
    </table>
  </div>

  <script type="text/javascript">
    jQuery(document).ready(function () {
      jQuery("#illumina").tablesorter({
        headers: {
          1: {
            sorter: false
          }
        }
      });
    });

    jQuery(function () {
      var theTable = jQuery("#illumina");

      jQuery("#filter1").keyup(function () {
        jQuery.uiTableFilter(theTable, this.value);
      });

      jQuery('#filter-form1').submit(function () {
        theTable.find("tbody > tr:visible > td:eq(1)").mousedown();
        return false;
      }).focus(); //Give focus to input field
    });
  </script>
  <br/><br/>
</div>

<div id="tab-2">
  <form id="filter-form2">Filter:
    <input name="filter2" id="filter2" value="" maxlength="30" size="30" type="text">
  </form>
  <br/>
  <table class="list" id="ls454">
    <thead>
    <tr>
      <th>Name</th>
      <th>Alias</th>
      <th>Barcode</th>
      <th>Creation Date</th>
      <th class="fit">Edit</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${ls454Pools}" var="lpool">
      <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
        <td>${lpool.name}</td>
        <td>${lpool.alias}</td>
        <td>${lpool.identificationBarcode}</td>
        <td>${lpool.creationDate}</td>
        <td class="misoicon"
            onclick="window.location.href='<c:url value="/miso/pool/${lpool.id}"/>'"><span
            class="ui-icon ui-icon-pencil"/></td>
      </tr>
    </c:forEach>
    </tbody>
  </table>

  <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#lpu_arrowclick'), 'lpudiv');"
       style="font: bold 80% Helvetica;">Used pools
    <div id="lpu_arrowclick" class="toggleLeft"></div>
  </div>
  <div id="lpudiv" class="simplebox ui-corner-all" style="display:none;">
    <table class="list" id="ls454Used">
      <thead>
      <tr>
        <th>Name</th>
        <th>Alias</th>
        <th>Barcode</th>
        <th>Date Created</th>
        <th class="fit">Edit</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${ls454PoolsUsed}" var="lpool">
        <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
          <td>${lpool.id}</td>
          <td>${lpool.alias}</td>
          <td>${lpool.identificationBarcode}</td>
          <td>${lpool.creationDate}</td>
          <td class="misoicon"
              onclick="window.location.href='<c:url value="/miso/pool/${lpool.id}"/>'"><span
              class="ui-icon ui-icon-pencil"/></td>
        </tr>
      </c:forEach>
      </tbody>
    </table>
  </div>
  <script type="text/javascript">
    jQuery(document).ready(function () {
      jQuery("#ls454").tablesorter({
        headers: {
          1: {
            sorter: false
          }
        }
      });
    });

    jQuery(function () {
      var theTable = jQuery("#ls454");

      jQuery("#filter2").keyup(function () {
        jQuery.uiTableFilter(theTable, this.value);
      });

      jQuery('#filter-form2').submit(function () {
        theTable.find("tbody > tr:visible > td:eq(1)").mousedown();
        return false;
      }).focus(); //Give focus to input field
    });
  </script>
  <br/><br/>
</div>

<div id="tab-3">
  <form id="filter-form3">Filter:
    <input name="filter3" id="filter3" value="" maxlength="30" size="30" type="text">
  </form>
  <br/>
  <table class="list" id="solid">
    <thead>
    <tr>
      <th>Name</th>
      <th>Alias</th>
      <th>Barcode</th>
      <th>Creation Date</th>
      <th class="fit">Edit</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${solidPools}" var="spool">
      <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
        <td>${spool.id}</td>
        <td>${spool.alias}</td>
        <td>${spool.identificationBarcode}</td>
        <td>${spool.creationDate}</td>
        <td class="misoicon"
            onclick="window.location.href='<c:url value="/miso/pool/${spool.id}"/>'"><span
            class="ui-icon ui-icon-pencil"/></td>
      </tr>
    </c:forEach>
    </tbody>
  </table>

  <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#spu_arrowclick'), 'spudiv');"
       style="font: bold 80% Helvetica;">Used pools
    <div id="spu_arrowclick" class="toggleLeft"></div>
  </div>
  <div id="spudiv" class="simplebox ui-corner-all" style="display:none;">
    <table class="list" id="solidUsed">
      <thead>
      <tr>
        <th>Name</th>
        <th>Alias</th>
        <th>Barcode</th>
        <th>Date Created</th>
        <th class="fit">Edit</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${solidPoolsUsed}" var="spool">
        <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
          <td>${spool.id}</td>
          <td>${spool.alias}</td>
          <td>${spool.identificationBarcode}</td>
          <td>${spool.creationDate}</td>
          <td class="misoicon"
              onclick="window.location.href='<c:url value="/miso/pool/${spool.id}"/>'"><span
              class="ui-icon ui-icon-pencil"/></td>
        </tr>
      </c:forEach>
      </tbody>
    </table>
  </div>
  <script type="text/javascript">
    jQuery(document).ready(function () {
      jQuery("#solid").tablesorter({
        headers: {
          1: {
            sorter: false
          }
        }
      });
    });

    jQuery(function () {
      var theTable = jQuery("#solid");

      jQuery("#filter3").keyup(function () {
        jQuery.uiTableFilter(theTable, this.value);
      });

      jQuery('#filter-form3').submit(function () {
        theTable.find("tbody > tr:visible > td:eq(1)").mousedown();
        return false;
      }).focus(); //Give focus to input field
    });
  </script>
</div>

<div id="tab-4">
  <form id="filter-form4">Filter:
    <input name="filter4" id="filter4" value="" maxlength="30" size="30" type="text">
  </form>
  <br/>
  <table class="list" id="pacbio">
    <thead>
    <tr>
      <th>Name</th>
      <th>Alias</th>
      <th>Barcode</th>
      <th>Creation Date</th>
      <th class="fit">Edit</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${pacbioPools}" var="ppool">
      <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
        <td>${ppool.name}</td>
        <td>${ppool.alias}</td>
        <td>${ppool.identificationBarcode}</td>
        <td>${ppool.creationDate}</td>
        <td class="misoicon"
            onclick="window.location.href='<c:url value="/miso/pool/${ppool.id}"/>'"><span
            class="ui-icon ui-icon-pencil"/></td>
      </tr>
    </c:forEach>
    </tbody>
  </table>

  <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#ppu_arrowclick'), 'ppudiv');"
       style="font: bold 80% Helvetica;">Used pools
    <div id="ppu_arrowclick" class="toggleLeft"></div>
  </div>
  <div id="ppudiv" class="simplebox ui-corner-all" style="display:none;">
    <table class="list" id="pacbioUsed">
      <thead>
      <tr>
        <th>Name</th>
        <th>Alias</th>
        <th>Barcode</th>
        <th>Date Created</th>
        <th class="fit">Edit</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${pacbioPoolsUsed}" var="ppool">
        <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
          <td>${ppool.id}</td>
          <td>${ppool.alias}</td>
          <td>${ppool.identificationBarcode}</td>
          <td>${ppool.creationDate}</td>
          <td class="misoicon"
              onclick="window.location.href='<c:url value="/miso/pool/${ppool.id}"/>'"><span
              class="ui-icon ui-icon-pencil"/></td>
        </tr>
      </c:forEach>
      </tbody>
    </table>
  </div>
  <script type="text/javascript">
    jQuery(document).ready(function () {
      jQuery("#pacbio").tablesorter({
        headers: {
          1: {
            sorter: false
          }
        }
      });
    });

    jQuery(function () {
      var theTable = jQuery("#pacbio");

      jQuery("#filter4").keyup(function () {
        jQuery.uiTableFilter(theTable, this.value);
      });

      jQuery('#filter-form4').submit(function () {
        theTable.find("tbody > tr:visible > td:eq(1)").mousedown();
        return false;
      }).focus(); //Give focus to input field
    });
  </script>
</div>

</div>
</div>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>