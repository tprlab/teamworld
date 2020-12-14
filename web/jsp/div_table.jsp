<div class="row">
  <div class="col-md-6 col-md-offset-3">
   <c:if test="${dv.getTableSize() > 0}">
    <table class="table table-striped table-bordered">
      <th><spring:message code="team"/></th>
      <th><spring:message code="games.short"/></th>
      <c:if test="${counting.resultOnly}">
        <th><spring:message code="wins.short"/></th>
        <th><spring:message code="loss.short"/></th>
      </c:if>
      <c:if test="${counting.tieAllowed}">
        <th><spring:message code="ties.short"/></th>
      </c:if>
      <th><spring:message code="points.short"/></th>
      <th><spring:message code="gdiff.short"/></th>

    <c:forEach items="${dv.table}" var="t">
    <tr>
      <td><a href="/team?name=${t.team.uname}&tour=${tr.uname}">${t.team.name}</a></td>
      <td>${t.games}</td>
      <c:if test="${counting.resultOnly}">
        <td>${t.wins}</td>
        <td>${t.losses}</td>
      </c:if>
      <c:if test="${counting.tieAllowed}">
       <td>${t.ties}</td>
      </c:if>
      <td>${t.points}</td>
      <td>${t.gf} - ${t.ga}</td>
     </tr>
    </c:forEach>
    </table>
  </c:if>
  </div>
</div>
