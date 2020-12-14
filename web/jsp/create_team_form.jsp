    <form:form method="POST" action="/_create/team" commandName="team">
      <spring:message code="name" var="name"/>
    <p>
      <form:input type="text" path="name" id="tmname" class="form-control" placeholder="${name}"/>
    </p>
      <form:hidden path="leagueName" value="${league.uname}" />
    <p>
      <c:if test="${league.getDivsSize() > 0 }">
          <form:select path="divId" class="form-control">
              <form:option value="0"><spring:message code="div.select"/></form:option>
            <c:forEach items="${league.divs}" var="dv">
              <form:option value="${dv.key}">${dv.value.name}</form:option>
            </c:forEach>
          </form:select>
      </c:if>
    </p>
      <button type="submit" class="btn btn-primary center-block"><spring:message code="create"/></button>
   </form:form>
