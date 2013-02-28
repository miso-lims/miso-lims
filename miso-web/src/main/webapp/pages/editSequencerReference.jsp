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
        <form:form  action="/miso/sequencer" method="POST" commandName="sequencerReference" autocomplete="off">
          <sessionConversation:insertSessionConversationId attributeName="sequencerReference"/>
            <h1>
                Edit Sequencer Reference
                <button type="submit" class="fg-button ui-state-default ui-corner-all">Save</button>
            </h1>
            <div class="breadcrumbs">
                <ul>
                    <li>
                        <a href="<c:url value='/miso/'/>">Home</a>
                    </li>
                    <li>
                        <div class="breadcrumbsbubbleInfo">
                            <div class="trigger">
                                <a href='<c:url value="/miso/stats"/>'>Sequencer References</a>
                            </div>
                            <div class="breadcrumbspopup">
                                All Sequencer References
                            </div>
                        </div>
                    </li>
                </ul>
            </div>
            <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
                <div id="note_arrowclick" class="toggleLeft"></div>
            </div>
            <div id="notediv" class="note" style="display:none;">A Sequencer Reference represents a sequencer. This may be
                a machine physically attached to a sequencer itself, or more commonly, a cluster or storage machine that
                holds the run directories.
            </div>
            <h2>Sequencer Information</h2>

            <table class="in">
                <tr>
                    <td class="h">Sequencer Reference ID:</td>
                    <td>
                        <c:choose>
                            <c:when test="${sequencerReference.id != 0}">${sequencerReference.id}</c:when>
                            <c:otherwise><i>Unsaved</i></c:otherwise>
                        </c:choose>
                    </td>
                </tr>
                <tr>
                    <td>Platform:</td>
                    <td>${sequencerReference.platform.nameAndModel}</td>
                </tr>
                <tr>
                    <td class="h">Name:</td>
                    <td><form:input path="name"/><span id="namecounter" class="counter"></span></td>
                </tr>
                <tr>
                    <td>IP Address:</td>
                    <td>
                        <input type="text" id="ipAddress" name="ipAddress" value="${trimmedIpAddress}"/>
                        <input type="hidden" value="on" name="_ipAddress"/>
                    </td>
                </tr>
            </table>

            <br/>
        </form:form>
    </div>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>