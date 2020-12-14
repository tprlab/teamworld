  <div class="row">
    <div class="col-md-6 col-md-offset-3">      
        <h3 ><spring:message code="history"/></h3>
<c:if test="${league.getHistorySize() > 0}">
        <table class="table table-striped">
        <th><spring:message code="tour"/></th>
        <th><spring:message code="started"/></th>
        <th><spring:message code="finished"/></th>
       <c:forEach items="${league.history}" var="tr">
         <tr><td><a href="/tour?name=${tr.uname}">${tr.name}</a></td>
         <td>${tr.strStart}</td>
         <td>${tr.strEnd}</td>
        </tr>
       </c:forEach>
        </table>
</c:if>
<c:if test="${league.getHistorySize() < 1}">
    <div class="alert alert-info">
    <spring:message code="no.history"/>
    </div>
</c:if>
    </div>
  </div>

