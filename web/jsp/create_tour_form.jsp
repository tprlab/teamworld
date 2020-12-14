    <form:form method="POST" action="/_create/tour" role="form" commandName="tr">
      <spring:message code="name" var="name"/>
      <form:input type="text" path="name" class="form-control" placeholder="${name}" id="trname"/>
      <form:hidden path="leagueName" value="${league.uname}" />
      <h5><button type="submit" class="btn btn-primary center-block"><spring:message code="create"/></button></h5>
     </form:form>
