<%@ include file="../header.jsp" %>

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

<div id="maincontent">
  <div id="contentcolumn">
    <h1><c:choose><c:when test="${not empty user.id}">Edit</c:when><c:otherwise>Create</c:otherwise></c:choose> User
      <button id="save" type="button" class="fg-button ui-state-default ui-corner-all">Save</button>
    </h1>
    
    <form:form id="userForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8"></form:form>
    
    <script>
      jQuery(document).ready(function () {
        FormUtils.createForm('userForm', 'save', ${userDto}, 'user', {
          isAdmin: ${miso:isAdmin()},
          isSelf: ${miso:isCurrentUser(user.loginName)},
          usersEditable: ${usersEditable}
        });
        Utils.ui.updateHelpLink(FormTarget.user.getUserManualUrl());
      });
    </script>
    
    <c:if test="${user.id != 0}">
      <c:if test="${usersEditable}">
        <form:form id="passwordForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8"></form:form>
        <table class="in">
          <tr>
            <td class="h"></td>
            <td><button id="savePassword" type="button" class="ui-state-default">Submit</button></td>
          </tr>
        </table>
        
        <script>
          jQuery(document).ready(function () {
            FormUtils.createForm('passwordForm', 'savePassword', {}, 'passwordreset', {
              userId: ${user.id},
              isAdmin: ${miso:isAdmin()},
              isSelf: ${miso:isCurrentUser(user.loginName)}
            });
          });
        </script>
      </c:if>
      
      <miso:list-section id="list_groups" name="Groups" target="group" alwaysShow="true" items="${groups}" config="{userId: ${user.id}, isAdmin: ${miso:isAdmin()}}"/>
    </c:if>
    
  </div>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
