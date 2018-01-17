<%@ include file="/header.jsp" %>

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

<c:if test="${not empty param.login_error}"><p/>

  <div class="flasherror">Access denied. Please contact an administrator of this MISO instance.<c:if
          test="${not empty sessionScope.SPRING_SECURITY_LAST_EXCEPTION}"><br/><br/>${sessionScope.SPRING_SECURITY_LAST_EXCEPTION}</c:if>
  </div>
</c:if>

<div id="login-form">
    <form action="/login" method="POST">
    <div style="margin:0;padding:0;display:inline">
      <table>
        <tr>
          <td align="right"><label for="username">Username</label></td>
          <td align="left"><input type="text" name="username" id="username"/></td>
        </tr>
        <tr>    
          <td align="right"><label for="password">Password</label></td>
          <td align="left"><input type="password" name="password" id="password"/></td>
        </tr>

        <tr>
          <td></td>
          <td align="left">
            <small><input type='checkbox' name='_spring_security_remember_me'/> Stay logged in</small>
          </td>
        </tr>
        <tr>
          <td align="left">
            <img src="<c:url value='/styles/images/blue-cud-button.png'/>" alt="CUD logo"/>
          </td>
          <td align="right">
            <input type="submit" name="login" value="Login &#187;" tabindex="5"/>
          </td>
        </tr>
      </table>
    </div>
    <script type="text/javascript">
      jQuery('#username').focus();
    </script>
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
  </form>
</div>
<script type="text/javascript">
  jQuery(document).ready(function() {
    jQuery(':input:visible:enabled:first').focus();
  });
</script>

<%@ include file="/footer.jsp" %>