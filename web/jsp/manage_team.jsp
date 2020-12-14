<%@include file="include.jsp"%>
<%@include file="header.jsp"%>

<%@include file="navbar.jsp"%>

<div class="container">
    <spring:message code="team" var="lg_ext"/>
    <%@include file="lg_bar.jsp"%>
    <div class="row">
      <div class="col-md-6 col-md-offset-3 well"> 
        <h3 class="text-center">${team.name}</h3>
        <p>
        <form method="POST" action="/_save/team">
            <input type="hidden" name="name" value="${team.uname}"/>
            <label><spring:message code="team.rename"/>:&nbsp;</label>
            <input type="text" name="newname" value="${team.name}"/>
            <button type="submit" class="btn btn-primary"><spring:message code="save"/></button>
        </form>
        </p>

        <c:if test="${team.games < 1}"> 
        <p>
        <label><spring:message code="team.delete"/>:&nbsp;</label>
        <form method="POST" action="/_remove/team">
            <input type="hidden" name="name" value="${team.uname}"/>
            <button type="submit" class="btn btn-danger"><spring:message code="delete"/></button>
        </form>
        </p>
        </c:if>
      </div>
    </div>

<div>

<%@include file="footer.jsp"%>
