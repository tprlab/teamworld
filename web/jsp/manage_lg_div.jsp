<%@include file="include.jsp"%>
<%@include file="header.jsp"%>

<%@include file="navbar.jsp"%>

<div class="container">
    <spring:message code="div" var="lg_ext"/>
    <%@include file="lg_bar.jsp"%>
    <div class="row">
      <div class="col-md-6 col-md-offset-3 well"> 
        <h3 class="text-center">${dv.name}</h3>
        <p>
        <form method="POST" action="/_save/div">
            <input type="hidden" name="name" value="${dv.uname}"/>
            <label><spring:message code="div.rename"/>:&nbsp;</label>
            <input type="text" name="newname" value="${dv.name}"/>
            <button type="submit" class="btn btn-primary"><spring:message code="save"/></button>
        </form>
        </p>

        <c:if test="${dv.teams < 1 && dv.tours < 1}">
        <p>
        <label><spring:message code="div.delete"/>:&nbsp;</label>
        <form method="POST" action="/_remove/div">
            <input type="hidden" name="div" value="${dv.uname}"/>
            <input type="hidden" name="league" value="${dv.league.uname}"/>
            <button type="submit" class="btn btn-danger"><spring:message code="delete"/></button>
        </form>
        </p>
        </c:if>
      </div>
    </div>

<div>

<%@include file="footer.jsp"%>
