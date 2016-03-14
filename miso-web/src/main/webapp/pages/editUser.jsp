<%@ include file="../header.jsp" %>

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
<script type="text/javascript" src="<c:url value='/scripts/parsley/parsley.min.js'/>"></script>

<div id="maincontent">
  <div id="contentcolumn">
    <c:choose>
      <c:when test="${fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
        <form:form id="user-form" data-parsley-validation="" action="/miso/admin/user" method="POST" commandName="user" autocomplete="off">
          <sessionConversation:insertSessionConversationId attributeName="user"/>
          <h1><c:choose><c:when
              test="${not empty user.userId}">Edit</c:when><c:otherwise>Create</c:otherwise></c:choose>
            User
            <button onclick="return User.validateUser();" class="fg-button ui-state-default ui-corner-all">Save</button>
          </h1>

          <div class="bs-callout bs-callout-warning hidden">
            <h2>Oh snap!</h2>
            <p>This form seems to be invalid!</p>
          </div>

          <table class="in">
            <tr>
              <td class="h">User ID:</td>
              <td>${user.userId}</td>
            </tr>
            <tr>
              <td>Full name:</td>
              <td>
                <sec:authorize access="hasRole('ROLE_ADMIN')">
                  <form:input id="fullName" path="fullName"/><span id="fullNameCounter" class="counter"></span>
                </sec:authorize>

                <sec:authorize access="hasRole('ROLE_TECH')">
                  ${user.fullName}
                </sec:authorize>
              </td>
            </tr>
            <tr>
              <td>Login name:</td>
              <td>
                <sec:authorize access="hasRole('ROLE_ADMIN')">
                  <form:input id="loginName" path="loginName"/><span id="loginNameCounter" class="counter"></span>
                </sec:authorize>

                <sec:authorize access="hasRole('ROLE_TECH')">
                  ${user.loginName}
                </sec:authorize>
              </td>
            </tr>
            <tr>
              <td>Email Address</td>
              <td>
                <c:choose>
                  <c:when test="${(user.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                                        or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
                    <form:input id="email" path="email"/><span id="emailCounter" class="counter"></span>
                  </c:when>
                  <c:otherwise>
                    <sec:authorize access="hasRole('ROLE_TECH')">
                      ${user.email}
                    </sec:authorize>
                  </c:otherwise>
                </c:choose>
              </td>
            </tr>
            <sec:authorize access="hasRole('ROLE_ADMIN')">
              <c:choose>
                <c:when test="${securityMethod eq 'jdbc'}">
                  <tr>
                    <td>Current Password:</td>
                    <td><form:password path="password"/></td>
                  </tr>
                  <tr>
                    <td>New Password:</td>
                    <td><input type="password" name="newpassword" id="newpassword"/></td>
                  </tr>
                  <tr>
                    <td>Confirm new Password:</td>
                    <td><input type="password" name="confirmpassword" id="confirmpassword"/></td>
                  </tr>
                </c:when>
                <c:otherwise>
                  <tr>
                    <td>Password:</td>
                    <td><i>Password change support only available for the 'jdbc' security method. If using LDAP, please change
                      the user password in your LDAP server.</i></td>
                  </tr>
                </c:otherwise>
              </c:choose>
              <tr>
                <td>Admin?:</td>
                <td><form:checkbox path="admin"/></td>
              </tr>
              <tr>
                <td>Internal?:</td>
                <td><form:checkbox path="internal"/></td>
              </tr>
              <tr>
                <td>External?:</td>
                <td><form:checkbox path="external"/></td>
              </tr>
              <tr>
                <td>Active?:</td>
                <td><form:checkbox path="active"/></td>
              </tr>
            </sec:authorize>
            <tr>
              <td>Groups:</td>
              <td>
                <div id="groups" class="checklist">
                  <form:checkboxes items="${groups}" path="groups"
                                   itemLabel="name"
                                   itemValue="groupId"/>
                </div>
              </td>
            </tr>
            <tr>
              <td>Supplemental Roles:</td>
              <td>
                <div id="roles" class="checklist">
                  <form:checkboxes items="${roles}" path="roles"/>
                </div>
              </td>
            </tr>
          </table>
        </form:form>
      </c:when>
      <c:otherwise>
        <form:form action="/miso/user" method="POST" commandName="user" autocomplete="off">
          <sessionConversation:insertSessionConversationId attributeName="user"/>
          <h1>Edit Your Account
            <button onclick="return User.validateUser()" class="fg-button ui-state-default ui-corner-all">Save</button>
          </h1>
          <table class="in">
            <tr>
              <td class="h">User ID:</td>
              <td>${user.userId}</td>
            </tr>
            <tr>
              <td>Full name:</td>
              <td>
                ${user.fullName}
              </td>
            </tr>
            <tr>
              <td>Login name:</td>
              <td>
                ${user.loginName}
              </td>
            </tr>
            <tr>
              <td>Email Address</td>
              <td>
                <c:choose>
                  <c:when test="${user.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username}">
                    <form:input path="email"/>
                  </c:when>
                  <c:otherwise>
                    ${user.email}
                  </c:otherwise>
                </c:choose>
              </td>
            </tr>
            <c:choose>
              <c:when test="${securityMethod eq 'jdbc'}">
                <tr>
                  <td>Current Password:</td>
                  <td><form:password path="password"/></td>
                </tr>
                <tr>
                  <td>New Password:</td>
                  <td><input type="password" name="newpassword" id="newpassword"/></td>
                </tr>
                <tr>
                  <td>Confirm new Password:</td>
                  <td><input type="password" name="confirmpassword" id="confirmpassword"/></td>
                </tr>
              </c:when>
              <c:otherwise>
                <tr>
                  <td>Password:</td>
                  <td><i>Password change support only available for the 'jdbc' security method. If using LDAP, please change
                    the user password in your LDAP server.</i></td>
                </tr>
              </c:otherwise>
            </c:choose>
          </table>
        </form:form>
      </c:otherwise>
    </c:choose>
  </div>
</div>

<script type="text/javascript">
  jQuery(document).ready(function () {
    Validate.attachParsley('#user-form');
    
    jQuery('#fullName').simplyCountable({
      counter: '#fullNameCounter',
      countType: 'characters',
      maxCount: ${maxLengths['fullName']},
      countDirection: 'down'
    });

    jQuery('#loginName').simplyCountable({
      counter: '#loginNameCounter',
      countType: 'characters',
      maxCount: ${maxLengths['loginName']},
      countDirection: 'down'
    });
    
    jQuery('#email').simplyCountable({
      counter: '#emailCounter',
      countType: 'characters',
      maxCount: ${maxLengths['email']},
      countDirection: 'down'
    });
  });
</script>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
