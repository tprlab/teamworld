<%@include file="include.jsp"%>
<%@include file="header.jsp"%>

<%@include file="navbar.jsp"%>
<div class="container-fluid">
  <div class="row-fluid">
    <div class="col-md-6 col-md-offset-3 well">
      <h2><spring:message code="login.title"/></h2>

        <c:if test="${not empty param.error}">
            <label><spring:message code="login.failed"/></label>
        </c:if>
        <c:if test="${not empty msg}">
            <label>${msg}</label>
        </c:if>
        <form method="POST" name="loginCmd" action="<c:url value="j_spring_security_check"/>" class="form-signin">
        <h5><spring:message code="login.name" var="name"/>
        <input type="text" name="name" class="input-block-level form-control" placeholder="${name}"></h5>
        <h5><spring:message code="login.pwd" var="pwd"/>
        <input type="password" name="pwd" class="input-block-level form-control" placeholder="${pwd}"></h5>
        <button class="btn btn-large btn-primary" type="submit"><spring:message code="login.action"/></button>
        </form>

    </div>
  </div>
</div>
<%@include file="footer.jsp"%>