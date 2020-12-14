<div class="row">
  <div class="col-md-6 col-md-offset-3">
    <h2 class="text-center"><spring:message code="play.off"/></h2>

    <div class="well">
      <div class="dropdown">
      <spring:message code="stage"/>
        <button class="btn btn-default dropdown-toggle" type="button" id="stage" data-toggle="dropdown" aria-expanded="true">
          <c:if test="${sch.stage == 1}"><spring:message code="final"/></c:if>
          <c:if test="${sch.stage != 1}"> 1 / ${sch.stage}</c:if>
          <span class="caret"></span>
        </button>
        <ul class="dropdown-menu" role="menu" aria-labelledby="stage">

        <c:forEach items="${sch.stages}" var="st">            
          <li role="presentation"><a role="menuitem" href="/manage/tour/schedule?name=${tr.uname}&div=${dv.uname}&mode=1&stage=${st}"> 1 / ${st} </a></li>
        </c:forEach>
          <li role="presentation"><a role="menuitem" href="/manage/tour/schedule?name=${tr.uname}&div=${dv.uname}&mode=1&stage=1"> <spring:message code="final"/></a></li>
        </ul>
      </div>
    </div>
  </div>


</div>

<div class="row">
  <div class="col-md-6 col-md-offset-3">
  <div class="form-group">
    <form:form method="POST" action="/_create/schedule" role="form" class="form-horizontal" commandName="sch">
      <form:hidden path="leagueName" value="${league.uname}" />
      <form:hidden path="tourName" value="${tr.uname}" />
      <form:hidden path="mode" value="1" />
      <form:hidden path="stage" value="${stage}" />
      <form:hidden path="divName" value="${dv.uname}" />

      <table class="table">
      <th colspan="3"><spring:message code="teams"/></th>
      <th><spring:message code="delete"/></th>
      <c:forEach items="${sch.games}" var="pg" varStatus="g">
          <tr>
          <td><form:select class="select form-control" path="games[${g.index}].team1.id">
                  <form:option value="${pg.team1.id}">${pg.team1.name}</form:option>
                  <c:forEach items="${sch.table}" var="tt">
                    <c:if test="${tt.team.id != pg.team1.id}">
                      <form:option value="${tt.team.id}">${tt.team.name}</form:option>       
                    </c:if>
                  </c:forEach>
          </form:select></td>
          <td>-</td>
          <td><form:select class="select form-control" path="games[${g.index}].team2.id">
                  <form:option value="${pg.team2.id}">${pg.team2.name}</form:option>
                  <c:forEach items="${sch.table}" var="tt">
                    <c:if test="${tt.team.id != pg.team2.id}">
                      <form:option value="${tt.team.id}">${tt.team.name}</form:option>       
                    </c:if>
                  </c:forEach>
          </form:select></td>
          <td><form:checkbox path="games[${g.index}].removed"/></td>
      </c:forEach>
      </table>
    <div class="alert alert-info"><h5 class="text-center"><spring:message code="poff.all"/>&nbsp; <a href="/manage/tour/schedule?name=${tr.uname}&mode=1&all=1&stage=${sch.stage}">
        <spring:message code="here"/></a></h5></div>

      <button type="submit" class="btn btn-primary btn-lg center-block"><spring:message code="schedule"/></button>
     </form:form>
  </div>

  </div>
</div>

<div class="row">

  <div class="col-md-6 col-md-offset-3">
      <h3 class="text-center"><spring:message code="table"/></h3>
     <table class="table table-striped table-bordered">
       <th><spring:message code="team"/></th>
        <c:if test="${dv == null}">
       <th><spring:message code="div.short"/></th>
        </c:if>

       <th><spring:message code="points.short"/></th>
       <th><spring:message code="gdiff.short"/></th>

     <c:forEach items="${sch.table}" var="t">
     <tr>
       <td>${t.team.name}</td>
        <c:if test="${dv == null}">
       <td>${league.divs[t.divId].name}</td>
        </c:if>
       <td>${t.points}</td>
       <td>${t.gf} - ${t.ga}</td>
      </tr>
     </c:forEach>
     </table>
  </div>

</div>

