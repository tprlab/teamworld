    <form:form method="POST" action="/_create/div" commandName="new_div">
      <spring:message code="name" var="name"/>
    <p>
      <form:input type="text" path="name" id="dname" class="form-control" placeholder="${name}"/>
    </p>
      <form:hidden path="leagueName" value="${league.uname}" />
      <button type="submit" class="btn btn-primary center-block"><spring:message code="create"/></button>
   </form:form>
