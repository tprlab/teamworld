<table class="table table-striped table-bordered">
  <c:forEach items="${dvtm.teams}" var="tm">
    <tr><td class="col-md-6"><a href="/manage/league/team?name=${tm.uname}">${tm.name}</a></td>

   <form:form method="POST" action="/_save/divteam" role="form" class="form-inline" commandName="team" id="fortm-${tm.id}">
     <form:hidden path="uname" value="${tm.uname}" />
     <form:hidden path="leagueName" value="${league.uname}" />
   <td class="col-md-6">
     <form:select path="divId" class="form-control tmselect select" id="select-${tm.id}">
       <option value="0"><spring:message code="no.div"/></option>
         <form:option value="${dvtm.id}" selected="selected">${dvtm.name}</form:option>
       <c:forEach items="${league.divs}" var="dd">
           <c:if test="${dd.key != dvtm.id}">
             <form:option value="${dd.key}">${dd.value.name}</form:option>
           </c:if>
       </c:forEach>
     </form:select>
   </td>
</form:form>

  </tr>
  </c:forEach>
</table>
