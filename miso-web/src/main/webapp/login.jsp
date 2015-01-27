<%@ include file="/header.jsp" %>

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

<c:if test="${not empty param.login_error}"><p/>

  <div class="flasherror">Access denied. Please contact an administrator of this MISO instance.<c:if
          test="${not empty sessionScope.SPRING_SECURITY_LAST_EXCEPTION}"><br/><br/>${sessionScope.SPRING_SECURITY_LAST_EXCEPTION}</c:if>
  </div>
</c:if>

<div id="login-form">
  <form action="j_spring_security_check" method="POST">
    <%--
    <div style="margin:0;padding:0;display:inline">
      <table>
        <tr>
          <td align="right"><label for="j_username">Username</label></td>
          <td align="left"><input type="text" name="j_username" id="j_username"/></td>
        </tr>
        <tr>
          <td align="right"><label for="j_password">Password</label></td>
          <td align="left"><input type="password" name="j_password" id="j_password"/></td>
        </tr>

        <tr>
          <td></td>
          <td align="left">
            <small><input type='checkbox' name='_spring_security_remember_me'/> Stay logged in</small>
          </td>
        </tr>
        <tr>
          <td align="left">
            <!--<small><a href="registerUser">New user</a></small>-->
          </td>
          <td align="right">
            <input type="submit" name="login" value="Login &#187;" tabindex="5"/>
          </td>
        </tr>
      </table>
    </div>
    --%>
      <div class="panel panel-primary panel-login span7">
        <div class="panel-heading">
          <h3 class="panel-title">Login</h3>
        </div>
        <div class="panel-body">
          <ul class="list-group">
            <li class="list-group-item" style="border:0;height:45px;"><div class="input-group"><span class="input-group-addon fa fa-user fa-fw login-addon"></span>
              <%--<label for="j_username" class="float-left" style="margin-top:5px;">Username</label>--%><input type="text" name="j_username" id="j_username" class="form-control" placeholder="Username"/></div>
            </li>
            <li class="list-group-item" style="border:0;height:45px;"><div class="input-group"><span class="input-group-addon fa fa-key fa-fw login-addon"></span>
              <%--<label for="j_password" class="float-left" style="margin-top:5px;">Password</label>--%><input type="password" name="j_password" id="j_password" class="form-control" placeholder="Password"/></div>
            </li>
            <li class="list-group-item" style="border:0;height:45px;"><small><span style="width:100px" class="float-left"><input type='checkbox' name='_spring_security_remember_me'/> Stay logged in</small></span><input type="submit" name="login" value="Login &#187;" tabindex="5" class="float-right"/></li>
          </ul>
        </div>
      </div>
      <script type="text/javascript">
        Form.Element.focus('j_username');
      </script>
  </form>
</div>
<script type="text/javascript">
  jQuery(document).ready(function() {
    jQuery(':input:visible:enabled:first').focus();
  });
</script>

<%@ include file="/footer.jsp" %>