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
  Date: 15-Feb-2010
  Time: 15:08:52

--%>
<%@ include file="../header.jsp" %>
<script type="text/javascript" src="<c:url value='/scripts/run_ajax.js?ts=${timestamp.time}'/>"></script>
<script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.breadcrumbs.popup.js'/>"></script>

<div id="maincontent">
<div id="contentcolumn">
<form:form method="POST" commandName="experiment" autocomplete="off"
           onsubmit="return validate_experiment(this);">
<h1>
    <c:choose>
        <c:when test="${not empty experiment.experimentId}">Edit</c:when>
        <c:otherwise>Create</c:otherwise>
    </c:choose> Experiment
    <button type="submit" class="fg-button ui-state-default ui-corner-all">Save</button>
</h1>
<div class="breadcrumbs">
    <ul>
        <li>
            <a href="/">Home</a>
        </li>
        <li>
            <div class="breadcrumbsbubbleInfo">
                <div class="trigger">
                    <a href='<c:url value="/miso/project/${experiment.study.project.projectId}"/>'>${experiment.study.project.name}</a>
                </div>
                <div class="breadcrumbspopup">
                        ${experiment.study.project.alias}
                </div>
            </div>
        </li>
        <li>
            <div class="breadcrumbsbubbleInfo">
                <div class="trigger">
                    <a href='<c:url value="/miso/study/${experiment.study.studyId}"/>'>${experiment.study.name}</a>
                </div>
                <div class="breadcrumbspopup">
                        ${experiment.study.alias}
                </div>
            </div>
        </li>
    </ul>
</div>
<div class="sectionDivider" onclick="toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
    <div id="note_arrowclick" class="toggleLeft"></div>
</div>
<div id="notediv" class="note" style="display:none;">An experiment contains design information about the
    sequencing experiment. Experiments are associated with Runs which contain the actual sequencing results.
    A Pool is attached to an Experiment which is then assigned to an instrument partition (lane/chamber).
</div>
<h2>Experiment Information</h2>
<table class="in">
<tr>
    <td class="h">Experiment ID:</td>
    <td>
        <c:choose>
            <c:when test="${not empty experiment.experimentId}">${experiment.experimentId}</c:when>
            <c:otherwise><i>Unsaved</i></c:otherwise>
        </c:choose>
    </td>
</tr>
<tr>
    <td class="h">Study ID:</td>
    <td>${experiment.study.studyId}</td>
</tr>
<tr>
    <td class="h">Name:</td>
    <td>
        <c:choose>
            <c:when test="${not empty experiment.experimentId}">${experiment.name}</c:when>
            <c:otherwise><i>Unsaved</i></c:otherwise>
        </c:choose>
    </td>
</tr>
<tr>
    <td class="h">Title:</td>
    <td><form:input path="title"/><span id="titlecounter" class="counter"></span></td>
    <%--<td><a href="void(0);" onclick="popup('help/experimentTitle.html');">Help</a></td>--%>
</tr>
<tr>
    <td class="h">Alias:</td>
    <td><form:input path="alias" class="validateable"/><span id="aliascounter" class="counter"></span></td>
    <%--<td><a href="void(0);" onclick="popup('help/experimentAlias.html');">Help</a></td>--%>
</tr>
<tr>
    <td class="h">Description:</td>
    <td><form:input path="description" class="validateable"/><span id="descriptioncounter" class="counter"></span></td>
    <%--<td><a href="void(0);" onclick="popup('help/experimentDescription.html');">Help</a></td>--%>
</tr>
<c:if test="${not empty experiment.accession}">
    <tr>
        <td class="h">Accession:</td>
        <td><a href="http://www.ebi.ac.uk/ena/data/view/${experiment.accession}"
               target="_blank">${experiment.accession}</a>
        </td>
        <%--<td><a href="void(0);" onclick="popup('help/experimentAccession.html');">Help</a></td>--%>
    </tr>
</c:if>
<tr>
    <c:choose>
        <c:when test="${empty experiment.experimentId or empty experiment.platform}">
            <td>Platform:</td>
            <td>
                <form:select id="platforms" path="platform" items="${platforms}" itemLabel="nameAndModel"
                             itemValue="platformId"
                             onchange="editloadPoolsbyPlatform(this);"/>
            </td>
        </c:when>
        <c:otherwise>
            <td>Platform:</td>
            <td>${experiment.platform.platformType.key} - ${experiment.platform.instrumentModel}</td>
        </c:otherwise>
    </c:choose>
</tr>
<c:choose>
    <c:when test="${!empty experiment.study and experiment.securityProfile.profileId eq experiment.study.securityProfile.profileId}">
        <tr>
            <td>Permissions</td>
            <td><i>Inherited from study </i><a
                    href='<c:url value="/miso/study/${experiment.study.studyId}"/>'>${experiment.study.name}</a>
                <input type="hidden" value="${experiment.study.securityProfile.profileId}"
                       name="securityProfile" id="securityProfile"/>
            </td>
        </tr>
        </table>
    </c:when>
    <c:otherwise>
        </table>
        <%@ include file="permissions.jsp" %>
    </c:otherwise>
</c:choose>

<div id="tabs">
<ul>
    <li><a href="#tab-1"><span>Pool Selection</span></a></li>
    <li><a href="#tab-2"><span>Consumables</span></a></li>
</ul>
<div id="tab-1">
        <%--<c:choose>--%>
        <%--<c:when test="${empty experiment.pool and empty availablePools}">--%>
        <%--No ${experiment.platform.platformType.key} pools available. Would you like to--%>
        <%--<c:choose>--%>
        <%--<c:when test="${not empty experiment}">--%>
        <%--<a href='<c:url value="/miso/pool/${fn:toLowerCase(experiment.platform.platformType.key)}/new/${experiment.experimentId}"/>'>create--%>
        <%--one</a>?--%>
        <%--</c:when>--%>
        <%--<c:otherwise>--%>
        <%--<a href='<c:url value="/miso/pool/${fn:toLowerCase(experiment.platform.platformType.key)}/new"/>'>create--%>
        <%--one</a>?--%>
        <%--</c:otherwise>--%>
        <%--</c:choose>--%>
        <%--</c:when>--%>
        <%--<c:otherwise>--%>
        <%--<table class="in">--%>
        <%--<tr>--%>
        <%--<td>Available Pools:<br/></td>--%>
        <%--<td>--%>
        <%--<div class="draggable_box">--%>
        <%--<ul id="list_1" class="poolList elementListDroppable">--%>
        <%--<c:forEach items="${availablePools}" var="pool">--%>
        <%--<li id="pool_${pool.poolId}" poolId="${pool.poolId}" poolName="${pool.name}"--%>
        <%--style="cursor: pointer;"--%>
        <%--class="draggable">${pool.name}--%>
        <%--<input type="hidden" id="poolId${pool.poolId}" value="${pool.poolId}"/>--%>

        <%--<div class="toggleRight"--%>
        <%--onclick="toggleRightInfo(this, 'pooldiv_${pool.poolId}');"></div>--%>
        <%--<div id="pooldiv_${pool.poolId}" style="display:none;">--%>
        <%--<c:forEach items="${pool.dilutions}" var="dil">--%>
        <%--${dil.name} (${dil.identificationBarcode})<br/>--%>
        <%--</c:forEach>--%>
        <%--</div>--%>
        <%--</li>--%>
        <%--</c:forEach>--%>
        <%--</ul>--%>
        <%--</div>--%>
        <%--</td>--%>
        <%--</tr>--%>
        <%--<tr>--%>
        <%--<td>Selected Pool:<br/></td>--%>
        <%--<td>--%>
        <%--<div class="singledropbox">--%>
        <%--<ul id="list_2" class="droppable" bind="pool">--%>
        <%--<c:if test="${not empty experiment.pool}">--%>
        <%--<li id="pool_${experiment.pool.poolId}" poolId="${experiment.pool.poolId}"--%>
        <%--poolName="${experiment.pool.name}"--%>
        <%--style="cursor: pointer;"--%>
        <%--class="draggable">${experiment.pool.name}--%>

        <%--<input type="hidden" id="pool${experiment.pool.poolId}"--%>
        <%--value="${experiment.pool.poolId}"--%>
        <%--name="pool"/>--%>

        <%--<div class="toggleRight"--%>
        <%--onclick="toggleRightInfo(this, 'pooldiv_${experiment.pool.poolId}');"></div>--%>
        <%--<div id="pooldiv_${experiment.pool.poolId}" style="display:none;">--%>
        <%--<c:forEach items="${experiment.pool.dilutions}" var="dil">--%>
        <%--${dil.name} (${dil.identificationBarcode})<br/>--%>
        <%--</c:forEach>--%>
        <%--</div>--%>
        <%--</li>--%>
        <%--</c:if>--%>

        <%--</ul>--%>
        <%--<input type="hidden" value="on" name="_pool"/>--%>
        <%--</div>--%>
        <%--</td>--%>
        <%--</tr>--%>
        <%--</table>--%>
        <%--</c:otherwise>--%>
        <%--</c:choose>--%>

      <c:choose>
        <c:when test="${empty experiment.pool}">
          <c:choose>
            <c:when test="${empty availablePools}">
              No ${experiment.platform.platformType.key} pools available. Would you like to
              <c:choose>
                  <c:when test="${not empty experiment}">
                      <a href='<c:url value="/miso/pool/${fn:toLowerCase(experiment.platform.platformType.key)}/new/${experiment.experimentId}"/>'>create
                          pool</a>?
                  </c:when>
                  <c:otherwise>
                      <a href='<c:url value="/miso/pool/${fn:toLowerCase(experiment.platform.platformType.key)}/new"/>'>create
                          pool</a>?
                  </c:otherwise>
              </c:choose>
            </c:when>
            <c:otherwise>
              <div class="note">
              <h2>Selected pool:</h2>
              <div id="selPool" class="elementList ui-corner-all"></div>
              </div>
              Please select a Pool below to be associated with this Experiment
              <c:choose>
                   <c:when test="${empty experiment.experimentId or empty experiment.platform}">
                  <%--<c:when test="${not empty experiment}">--%>
                     <%--<a href='<c:url value="/miso/pool/${fn:toLowerCase(experiment.platform.platformType.key)}/new"/>'> create a new pool</a>?--%>
                  </c:when>
                  <c:otherwise>
                     , <b>OR</b> <a href='<c:url value="/miso/pool/${fn:toLowerCase(experiment.platform.platformType.key)}/new/${experiment.experimentId}"/>'> create a new pool</a>?
                  </c:otherwise>
              </c:choose>
              <div id='poolList' class="elementList ui-corner-all" style="height:500px">
                <c:forEach items="${availablePools}" var="p">
                    <div bind="${p.poolId}" onMouseOver="this.className='dashboardhighlight'" onMouseOut="this.className='dashboard'" class="dashboard" ondblclick="experimentSelectPool(this);">
                      <span style="float:left">
                      <b>${p.name}</b> (${fn:length(p.dilutions)} dilutions)
                      </span>
                      <span style='float: right; font-size: 24px; font-weight: bold; color:#BBBBBB'>${p.platformType.key}</span>
                    </div>
                </c:forEach>
              </div>
            </c:otherwise>
          </c:choose>
        </c:when>
        <c:otherwise>
          <div class="note">
            <h2>Selected pool:</h2>
            <div id="selPool" class="elementList ui-corner-all">
              <div onMouseOver="this.className='dashboardhighlight'" onMouseOut="this.className='dashboard'" class="dashboard">
                <span class='float-left'>
                <input type="hidden" id="pool${experiment.pool.poolId}" value="${experiment.pool.poolId}" name="pool"/>
                <b>Pool:</b> <a href='<c:url value="/miso/pool/${fn:toLowerCase(experiment.platform.platformType.key)}/${experiment.pool.poolId}"/>'>${experiment.pool.name}</a><br/>
                <b>Dilutions:</b><br/>
                <i>
                  <c:forEach items="${experiment.pool.dilutions}" var="dil">
                    ${dil.library.alias} (${dil.library.name})<br/>
                  </c:forEach>
                </i>
                </span>
                <span onclick='confirmRemove(jQuery(this).parent());' class='float-right ui-icon ui-icon-circle-close'></span>
              </div>
              <input type="hidden" value="on" name="_pool"/>
            </div>
          </div>
        </c:otherwise>
      </c:choose>
</div>

<div id="tab-2">
    <h1>Consumables</h1>

    <div id="addKitDialog" title="Assign a kit to this experiment"></div>

    <h2>Library Kit</h2>

    <a href='javascript:void(0);' class="add"
       onclick="showLibraryKitDialog(${experiment.experimentId},
               <c:choose>
                <c:when test="${fn:length(experiment.pool.dilutions) > 1}">
                    true
                </c:when>
                <c:otherwise>
                    false
                </c:otherwise>
               </c:choose>);
                   ">Add Library Kit</a><br/>

    <form id='addLibraryKitForm'>
        <table class="list" id="libraryKitTable">
            <thead>
            <tr>
                <th>Name</th>
                <th class="fit">Part Number</th>
                <th class="fit">Lot Number</th>
                <th class="fit">Kit Date</th>
            </tr>
            </thead>
            <tbody>
            <c:if test="${not empty libraryKits}">
                <c:forEach items="${libraryKits}" var="kit">
                    <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
                        <td>${kit.kitDescriptor.name}</td>
                        <td>${kit.kitDescriptor.partNumber}</td>
                        <td>${kit.lotNumber}</td>
                        <td>${kit.kitDate}</td>
                    </tr>
                </c:forEach>
            </c:if>
            </tbody>
        </table>
    </form>

    <c:choose>
        <c:when test="${experiment.platform.platformType.key ne 'Illumina'}">
            <h2>EmPCR Kit</h2>
            <a href='javascript:void(0);' class="add"
               onclick="showEmPcrKitDialog(${experiment.experimentId}); return false;">Add EmPCR Kit</a><br/>

            <form id='addEmPcrKitForm'>
                <table class="list" id="emPcrKitTable">
                    <thead>
                    <tr>
                        <th>Name</th>
                        <th class="fit">Part Number</th>
                        <th class="fit">Lot Number</th>
                        <th class="fit">Kit Date</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:if test="${not empty emPcrKits}">
                        <c:forEach items="${emPcrKits}" var="kit">
                            <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
                                <td class="fit">${kit.kitDescriptor.name}</td>
                                <td>${kit.kitDescriptor.partNumber}</td>
                                <td>${kit.lotNumber}</td>
                                <td>${kit.kitDate}</td>
                            </tr>
                        </c:forEach>
                    </c:if>
                    </tbody>
                </table>
            </form>
        </c:when>
        <c:otherwise>
            <h2>Clustering Kit</h2>
            <a href='javascript:void(0);' class="add"
               onclick="showClusteringKitDialog(${experiment.experimentId}); return false;">Add Clustering Kit</a><br/>

            <form id='addClusteringKitForm'>
                <table class="list" id="clusteringKitTable">
                    <thead>
                    <tr>
                        <th>Name</th>
                        <th class="fit">Part Number</th>
                        <th class="fit">Lot Number</th>
                        <th class="fit">Kit Date</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:if test="${not empty clusteringKits}">
                        <c:forEach items="${clusteringKits}" var="kit">
                            <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
                                <td class="fit">${kit.kitDescriptor.name}</td>
                                <td>${kit.kitDescriptor.partNumber}</td>
                                <td>${kit.lotNumber}</td>
                                <td>${kit.kitDate}</td>
                            </tr>
                        </c:forEach>
                    </c:if>
                    </tbody>
                </table>
            </form>
        </c:otherwise>
    </c:choose>

    <h2>Sequencing Kit</h2>
    <a href='javascript:void(0);' class="add"
       onclick="showSequencingKitDialog(${experiment.experimentId}); return false;">Add Sequencing Kit</a><br/>

    <form id='addSequencingKitForm'>
        <table class="list" id="sequencingKitTable">
            <thead>
            <tr>
                <th>Name</th>
                <th class="fit">Part Number</th>
                <th class="fit">Lot Number</th>
                <th class="fit">Kit Date</th>
            </tr>
            </thead>
            <tbody>
            <c:if test="${not empty sequencingKits}">
                <c:forEach items="${sequencingKits}" var="kit">
                    <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
                        <td class="fit">${kit.kitDescriptor.name}</td>
                        <td>${kit.kitDescriptor.partNumber}</td>
                        <td>${kit.lotNumber}</td>
                        <td>${kit.kitDate}</td>
                    </tr>
                </c:forEach>
            </c:if>
            </tbody>
        </table>
    </form>
</div>
</div>

</form:form>
</div>
</div>

<script type="text/javascript">
    addMaxDatePicker("creationDate", 0);

    var processLists = function() {
        // remove available li inputs
        jQuery('#list_1').find('li').find('input').each(function() {
            var jQueryitem = jQuery(this);
            jQueryitem.remove();
        });

        //add selected li inputs
        jQuery('#list_2').find('li').each(function() {
            var jQueryitem = jQuery(this);
            var h = jQueryitem.attr('poolName') + "<input type='hidden' id='pool" + jQueryitem.attr('poolId') + "' value='" + jQueryitem.attr('poolId') + "' name='pool'/>";
            jQueryitem.html(h);
        });
    }

    jQuery(function() {
        jQuery("#poolList").sortable({
            revert: true
        });
        jQuery(".draggable").draggable({
            connectToSortable: '#poolList',
            revert: true,
            scroll: false
        });
        jQuery("ul, li").disableSelection();

        updateDroppables("#list_2");

        jQuery(".elementListDroppable").droppable({
            accept: '.draggable',
            activeClass: 'ui-state-hover',
            hoverClass: 'ui-state-active',
            tolerance: 'pointer',
            drop: function(event, ui) {
                jQuery(ui.draggable).find('input').attr("name", "");
                jQuery(ui.draggable).appendTo(jQuery(this));
            }
        });

    });

    jQuery(document).ready(function() {
        jQuery("#tabs").tabs();

        jQuery('#title').simplyCountable({
            counter: '#titlecounter',
            countType: 'characters',
            maxCount: ${maxLengths['title']},
            countDirection: 'down'
        });

        jQuery('#alias').simplyCountable({
            counter: '#aliascounter',
            countType: 'characters',
            maxCount: ${maxLengths['alias']},
            countDirection: 'down'
        });

        jQuery('#description').simplyCountable({
            counter: '#descriptioncounter',
            countType: 'characters',
            maxCount: ${maxLengths['description']},
            countDirection: 'down'
        });
    });

</script>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>