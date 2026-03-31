<%@ include file="/WEB-INF/header.jsp" %>

<c:if test="${not empty param.login_error}"><p/>

  <div class="flasherror">Access denied. Please contact an administrator of this MISO instance.<c:if
          test="${not empty sessionScope.SPRING_SECURITY_LAST_EXCEPTION}"><br/><br/>${sessionScope.SPRING_SECURITY_LAST_EXCEPTION}</c:if>
  </div>
</c:if>

<sec:authorize access="isAuthenticated()">
  <script>
  window.location = '/mainMenu';
  </script>
</sec:authorize>

<div id="login-form" style="text-align:center; padding-top:16px;">
  <c:choose>
    <c:when test="${samlLoginEnabled}">
      <form action="/saml2/authenticate/${samlRegistrationId}" method="GET">
        <table>
            <tr>
              <td align="center" style="padding-bottom:12px;">
                Please log in to continue.
              </td>
            </tr>
            <tr>
              <td align="center">
                <input type="submit" name="login" value="Login with SSO &#187;" tabindex="5"/>
              </td>
            </tr>
          </table>
      </form>
    </c:when>
    <c:otherwise>
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
                <small><label><input type='checkbox' name='_spring_security_remember_me'/> Stay logged in</label></small>
              </td>
            </tr>
            <tr>
              <td align="left">
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
    </c:otherwise>
  </c:choose>
</div>

<script type="text/javascript">
  jQuery(document).ready(function() {
    jQuery(':input:visible:enabled:first').focus();
  });
</script>

<%@ include file="/WEB-INF/footer.jsp" %>