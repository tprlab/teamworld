<form:form method="POST" action="/_save/venue" role="form" commandName="nv">
  <h5>
  <label><spring:message code="name"/></label>
  <form:input type="text" path="name" class="form-control" id="vname" value="${vn.name}"/>
  </h5>
  <h5>
  <label><spring:message code="addr"/></label>
  <form:input type="text" path="addr" class="form-control" value="${vn.addr}"/>
  <h/5>
  <h5>
  <label><spring:message code="desc"/></label>
  <form:input type="text" path="desc" class="form-control" value="${vn.desc}"/>
  <h/5>
  <h5>
  <label><spring:message code="court.num"/></label>
  <form:input type="text" path="courts" class="form-control" value="${vn.courts}"/>
  </h5>
   
  <table class="table">
  <tr>
  <td class="col-md-3"><label><spring:message code="available"/></label></td>
  <td class="col-md-9">
   <c:if test="${vn.active}">
     <c:set var="act_ch" value="checked"/>
  </c:if>
  <input type="checkbox" name="active" ${act_ch}/></td>
  </tr>
    <c:if test="${vn.id > 0}">
      <tr><td><label><spring:message code="delete"/></label></td>
          <td><input type="checkbox" name="delete"/></td>
      </tr>
    </c:if>
</table>


  <form:hidden path="leagueId" value="${league.id}" />
  <form:hidden path="id" value="${vn.id}"/>
  <input type="hidden" name="league" value="${league.uname}" />
  <h5><button type="submit" class="btn btn-primary center-block">
    <c:if test="${vn.id > 0}">
    <spring:message code="save"/>
    </c:if>
    <c:if test="${empty vn}">
    <spring:message code="create"/>
    </c:if>
    </button></h5>
 </form:form>
