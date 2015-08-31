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
<div id="maincontent">
<div id="contentcolumn">
<script type="text/javascript">
  jQuery(document).ready(function () {
    jQuery("#tabs").tabs();
  });
</script>
<h1>Consumables</h1>

<div id="tabs">
<ul>
  <li><a href="#tab-1"><span>Sequencing Kits</span></a></li>
  <li><a href="#tab-2"><span>EMPCR Kits</span></a></li>
  <li><a href="#tab-3"><span>Library Kits</span></a></li>
  <li><a href="#tab-4"><span>Clustering Kits</span></a></li>
  <li><a href="#tab-5"><span>Multiplexing Kits</span></a></li>
  <%--<li><a href="#tab-6"><span>others</span></a></li>--%>
</ul>

<div id="tab-1">
  <h1>
    <div id="SequencingtotalCount">
    </div>
  </h1>
  <a href="<c:url value="/miso/kitdescriptor/new"/>" class="add">Create Kit Descriptor</a>

  <form id="filter-form1">Filter: <input name="filter" id="filter1" value="" maxlength="30" size="30" type="text">
  </form>
  <br/>
  <table class="list" id="table1">
    <thead>
    <tr>
      <th>Name</th>
      <th>Description</th>
      <th>Version</th>
      <th>Manufacturer</th>
      <th>Part Number</th>
      <th>Stock Level</th>
      <th>Type</th>
      <th>Platform</th>
      <%-- GLT-201: Comment to remove 'Edit Column' --%>
      <th class="fit">Edit</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${sequencing}" var="sequencing">
      <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
        <td class="misoicon"
            onclick="window.location.href='<c:url value="/miso/kitdescriptor/${sequencing.kitDescriptorId}"/>'">
            <b>${sequencing.name}</b></td>
        <td>${sequencing.description}</td>
        <td>${sequencing.version}</td>
        <td>${sequencing.manufacturer}</td>
        <td>${sequencing.partNumber}</td>
        <td>${sequencing.stockLevel}</td>
        <td>${sequencing.kitType.key}</td>
        <td>${sequencing.platformType.key}</td>
        <%-- GLT-201: Comment to remove 'Edit Column' --%>
        <td class="misoicon"
            onclick="window.location.href='<c:url value="/miso/kitdescriptor/${sequencing.kitDescriptorId}"/>'"><span
            class="ui-icon ui-icon-pencil"/></td>
      </tr>
    </c:forEach>
    </tbody>
  </table>
  <script type="text/javascript">
    jQuery(document).ready(function () {
      jQuery("#table1").tablesorter({
        headers: {
          7: {
            sorter: false
          }
        }
      });
    });

    jQuery(function () {
      var theTable = jQuery("#table1");

      jQuery("#filter1").keyup(function () {
        jQuery.uiTableFilter(theTable, this.value);
        writeTotalNo();
      });

      jQuery('#filter-form1').submit(function () {
        theTable.find("tbody > tr:visible > td:eq(1)").mousedown();
        return false;
      }).focus(); //Give focus to input field
    });
  </script>
</div>

<div id="tab-2">
  <h1>
    <div id="EMPCRtotalCount">
    </div>
  </h1>
  <a href="<c:url value="/miso/kitdescriptor/new"/>" class="add">Create Kit Descriptor</a>

  <form id="filter-form2">Filter: <input name="filter" id="filter2" value="" maxlength="30" size="30" type="text">
  </form>
  <br/>
  <table class="list" id="table2">
    <thead>
    <tr>
      <th>Name</th>
      <th>Version</th>
      <th>Manufacturer</th>
      <th>Part Number</th>
      <th>Stock Level</th>
      <th>Type</th>
      <th>Platform</th>
      <%-- GLT-201: Comment to remove 'Edit Column' --%>
      <th class="fit">Edit</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${empcr}" var="empcr">
      <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
        <td class="misoicon"
            onclick="window.location.href='<c:url value="/miso/kitdescriptor/${sequencing.kitDescriptorId}"/>'">
            <b>${empcr.name}</b></td> 
        <td>${empcr.version}</td>
        <td>${empcr.manufacturer}</td>
        <td>${empcr.partNumber}</td>
        <td>${empcr.stockLevel}</td>
        <td>${empcr.kitType.key}</td>
        <td>${empcr.platformType.key}</td>
        <%-- GLT-201: Comment to remove 'Edit Column' --%>
        <td class="misoicon"
            onclick="window.location.href='<c:url value="/miso/kitdescriptor/${empcr.kitDescriptorId}"/>'"><span
            class="ui-icon ui-icon-pencil"/></td>
      </tr>
    </c:forEach>
    </tbody>
  </table>
  <script type="text/javascript">

    jQuery(document).ready(function () {
      jQuery("#table2").tablesorter({
        headers: {
          7: {
            sorter: false
          }
        }
      });
    });

    jQuery(function () {
      var theTable = jQuery("#table2");

      jQuery("#filter2").keyup(function () {
        jQuery.uiTableFilter(theTable, this.value);
        writeTotalNo();
      });

      jQuery('#filter-form2').submit(function () {
        theTable.find("tbody > tr:visible > td:eq(1)").mousedown();
        return false;
      }).focus(); //Give focus to input field
    });
  </script>
</div>

<div id="tab-3">
  <h1>
    <div id="LibrarytotalCount">
    </div>
  </h1>
  <a href="<c:url value="/miso/kitdescriptor/new"/>" class="add">Create Kit Descriptor</a>

  <form id="filter-form3">Filter: <input name="filter" id="filter3" value="" maxlength="30" size="30" type="text">
  </form>
  <br/>
  <table class="list" id="table3">
    <thead>
    <tr>
      <th>Name</th>
      <th>Version</th>
      <th>Manufacturer</th>
      <th>Part Number</th>
      <th>Stock Level</th>
      <th>Type</th>
      <th>Platform</th>
      <%-- GLT-201: Comment to remove 'Edit Column' --%>
      <th class="fit">Edit</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${library}" var="library">
      <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
        <td class="misoicon"
            onclick="window.location.href='<c:url value="/miso/kitdescriptor/${library.kitDescriptorId}"/>'">
          <b>${library.name}</b></td>
        <td>${library.version}</td>
        <td>${library.manufacturer}</td>
        <td>${library.partNumber}</td>
        <td>${library.stockLevel}</td>
        <td>${library.kitType.key}</td>
        <td>${library.platformType.key}</td>
        <%-- GLT-201: Comment to remove 'Edit Column' --%>
        <td class="misoicon"
            onclick="window.location.href='<c:url value="/miso/kitdescriptor/${library.kitDescriptorId}"/>'"><span
            class="ui-icon ui-icon-pencil"/></td>
      </tr>
    </c:forEach>
    </tbody>
  </table>
  <script type="text/javascript">

    jQuery(document).ready(function () {
      jQuery("#table3").tablesorter({
        headers: {
          7: {
            sorter: false
          }
        }
      });
    });

    jQuery(function () {
      var theTable = jQuery("#table3");

      jQuery("#filter3").keyup(function () {
        jQuery.uiTableFilter(theTable, this.value);
        writeTotalNo();
      });

      jQuery('#filter-form3').submit(function () {
        theTable.find("tbody > tr:visible > td:eq(1)").mousedown();
        return false;
      }).focus(); //Give focus to input field
    });
  </script>
</div>

<div id="tab-4">
  <h1>
    <div id="ClusteringtotalCount">
    </div>
  </h1>
  <a href="<c:url value="/miso/kitdescriptor/new"/>" class="add">Create Kit Descriptor</a>

  <form id="filter-form4">Filter: <input name="filter" id="filter4" value="" maxlength="30" size="30" type="text">
  </form>
  <br/>
  <table class="list" id="table4">
    <thead>
    <tr>
      <th>Name</th>
      <th>Version</th>
      <th>Manufacturer</th>
      <th>Part Number</th>
      <th>Stock Level</th>
      <th>Type</th>
      <th>Platform</th>
      <%-- GLT-201: Comment to remove 'Edit Column' --%>
      <th class="fit">Edit</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${clustering}" var="clustering">
      <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
        <td class="misoicon"
            onclick="window.location.href='<c:url value="/miso/kitdescriptor/${clustering.kitDescriptorId}"/>'">
          <b>${clustering.name}</b></td>
        <td>${clustering.version}</td>
        <td>${clustering.manufacturer}</td>
        <td>${clustering.partNumber}</td>
        <td>${clustering.stockLevel}</td>
        <td>${clustering.kitType.key}</td>
        <td>${clustering.platformType.key}</td>
        <%-- GLT-201: Comment to remove 'Edit Column' --%>
        <td class="misoicon"
            onclick="window.location.href='<c:url value="/miso/kitdescriptor/${clustering.kitDescriptorId}"/>'"><span
            class="ui-icon ui-icon-pencil"/></td>
      </tr>
    </c:forEach>
    </tbody>
  </table>
  <script type="text/javascript">
    jQuery(document).ready(function () {
      jQuery("#table4").tablesorter({
        headers: {
          7: {
            sorter: false
          }
        }
      });
    });

    jQuery(function () {
      var theTable = jQuery("#table4");

      jQuery("#filter4").keyup(function () {
        jQuery.uiTableFilter(theTable, this.value);
        writeTotalNo();
      });

      jQuery('#filter-form4').submit(function () {
        theTable.find("tbody > tr:visible > td:eq(1)").mousedown();
        return false;
      }).focus(); //Give focus to input field
    });
  </script>
</div>

<div id="tab-5">
  <h1>
    <div id="MultiplexingtotalCount">
    </div>
  </h1>
  <a href="<c:url value="/miso/kitdescriptor/new"/>" class="add">Create Kit Descriptor</a>

  <form id="filter-form5">Filter: <input name="filter" id="filter5" value="" maxlength="30" size="30" type="text">
  </form>
  <br/>
  <table class="list" id="table5">
    <thead>
    <tr>
      <th>Name</th>
      <th>Version</th>
      <th>Manufacturer</th>
      <th>Part Number</th>
      <th>Stock Level</th>
      <th>Type</th>
      <th>Platform</th>
      <%-- GLT-201: Comment to remove 'Edit Column' --%>
      <th class="fit">Edit</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${multiplexing}" var="multiplexing">
      <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
        <td class="misoicon"
            onclick="window.location.href='<c:url value="/miso/kitdescriptor/${multiplexing.kitDescriptorId}"/>'">
          <b>${multiplexing.name}</b></td>
        <td>${multiplexing.version}</td>
        <td>${multiplexing.manufacturer}</td>
        <td>${multiplexing.partNumber}</td>
        <td>${multiplexing.stockLevel}</td>
        <td>${multiplexing.kitType.key}</td>
        <td>${multiplexing.platformType.key}</td>
        <%-- GLT-201: Comment to remove 'Edit Column' --%>
        <td class="misoicon"
            onclick="window.location.href='<c:url value="/miso/kitdescriptor/${multiplexing.kitDescriptorId}"/>'"><span
            class="ui-icon ui-icon-pencil"/></td>
      </tr>
    </c:forEach>
    </tbody>
  </table>
  <script type="text/javascript">
    jQuery(document).ready(function () {
      writeTotalNo();
      jQuery("#table5").tablesorter({
        headers: {
          7: {
            sorter: false
          }
        }
      });
    });

    jQuery(function () {
      var theTable = jQuery("#table5");

      jQuery("#filter5").keyup(function () {
        jQuery.uiTableFilter(theTable, this.value);
        writeTotalNo();
      });

      jQuery('#filter-form5').submit(function () {
        theTable.find("tbody > tr:visible > td:eq(1)").mousedown();
        return false;
      }).focus(); //Give focus to input field
    });

    function writeTotalNo() {
      jQuery('#SequencingtotalCount').html(jQuery('#table1>tbody>tr:visible').length.toString() + " Sequencing Kit");
      jQuery('#EMPCRtotalCount').html(jQuery('#table2>tbody>tr:visible').length.toString() + " EMPCR Kit");
      jQuery('#LibrarytotalCount').html(jQuery('#table3>tbody>tr:visible').length.toString() + " Library Kit");
      jQuery('#ClusteringtotalCount').html(jQuery('#table4>tbody>tr:visible').length.toString() + " Clustering Kit");
      jQuery('#MultiplexingtotalCount').html(jQuery('#table5>tbody>tr:visible').length.toString() + " Multiplexing Kit");
    }
  </script>
</div>
</div>
</div>
</div>

<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>
