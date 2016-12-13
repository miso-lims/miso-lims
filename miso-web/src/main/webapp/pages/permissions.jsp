<%--
  ~ Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
  ~ MISO project contacts: Robert Davey @ TGAC
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

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#permissions_arrowclick'), 'permissions');">
  Permissions
  <div id="permissions_arrowclick" class="toggleLeft"></div>
</div>
<div id="permissions" class="note" style="display:none">
  <h2>Permissions</h2>
  <table class="in">
    <tr>
      <td class="h">Owner:</td>
      <td>
        <c:choose>
          <c:when test="${(formObj.securityProfile.owner.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                    or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
            <form:select path="securityProfile.owner" id="securityProfile_owner">
              <form:option value="" label="Select..."/>
              <form:options items="${owners}" itemLabel="fullName" itemValue="userId"/>
            </form:select>
          </c:when>
          <c:otherwise>
            <c:choose>
              <c:when test="${not empty formObj.securityProfile and not empty formObj.securityProfile.owner}">
                ${formObj.securityProfile.owner.fullName}
              </c:when>
              <c:otherwise>
                <%-- ${SPRING_SECURITY_CONTEXT.authentication.principal.fullName} --%>
                <i>None</i>
              </c:otherwise>
            </c:choose>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <c:if test="${empty formObj.securityProfile or
                    (formObj.securityProfile.owner.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username) or
                    fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
      <tr>
        <td class="h">Allow all internal users access?:</td>
        <td><form:checkbox path="securityProfile.allowAllInternal"/></td>
      </tr>

      <tr>
        <td class="h">Users (read):<br/>
          <a href="javascript:void(0);"
             onclick="Utils.ui.checkAllConfirm('securityProfile.readUsers', 'Are you sure you want to select all users to read?'); return false;">All</a>
          / <a href="javascript:void(0);"
               onclick="Utils.ui.uncheckAll('securityProfile.readUsers'); return false;">None</a></td>
        <td>
          <div id="readUsers" class="checklist">
            <form:checkboxes items="${accessibleUsers}" path="securityProfile.readUsers"
                             itemLabel="fullName"
                             itemValue="userId"/>
          </div>
        </td>
      </tr>
      <tr>
        <td class="h">Users (write):<br/>
          <a href="javascript:void(0);"
             onclick="Utils.ui.checkAllConfirm('securityProfile.writeUsers', 'Are you sure you want to select all users to write?'); return false;">All</a>
          / <a href="javascript:void(0);"
               onclick="Utils.ui.uncheckAll('securityProfile.writeUsers'); return false;">None</a>
        </td>
        <td>
          <div id="writeUsers" class="checklist">
            <form:checkboxes items="${accessibleUsers}" path="securityProfile.writeUsers"
                             itemLabel="fullName"
                             itemValue="userId"/>
          </div>
        </td>
      </tr>
      <tr>
        <td class="h">Groups (read):<br/>
          <a href="javascript:void(0);"
             onclick="Utils.ui.checkAll('securityProfile.readGroups'); return false;">All</a>
          / <a href="javascript:void(0);"
               onclick="Utils.ui.uncheckAll('securityProfile.readGroups'); return false;">None</a>
        </td>
        <td>
          <div id="readGroups" class="checklist">
            <form:checkboxes items="${accessibleGroups}" path="securityProfile.readGroups"
                             itemLabel="name"
                             itemValue="groupId"/>
          </div>
        </td>
      </tr>
      <tr>
        <td class="h">Groups (write):<br/>
          <a href="javascript:void(0);"
             onclick="Utils.ui.checkAll('securityProfile.writeGroups'); return false;">All</a>
          / <a href="javascript:void(0);"
               onclick="Utils.ui.uncheckAll('securityProfile.writeGroups'); return false;">None</a>
        </td>
        <td>
          <div id="writeGroups" class="checklist">
            <form:checkboxes items="${accessibleGroups}" path="securityProfile.writeGroups"
                             itemLabel="name"
                             itemValue="groupId"/>
          </div>
        </td>
      </tr>
    </c:if>
  </table>
</div>