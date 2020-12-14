<c:if test="${dv.getGamesSize() > 0}">
  <c:forEach items="${dv.games}" var="g" varStatus="i">
    <div class="row">
      <div class="col-md-6 col-md-offset-3">
        <h4>
        <table class="table">
         <tr>
          <c:if test="${g.stage != 0}">
            <td class="col-md-1">
            <c:if test="${g.stage != 1}">
              1&nbsp;/&nbsp;${g.stage}
            </c:if>     
            <c:if test="${g.stage == 1}">
              <spring:message code="final"/>
            </c:if>     
            </td><td class="col-md-2">
            </c:if>
            <c:if test="${g.stage == 0}">
              <td colspan="2" class="col-md-3">
            </c:if>
                <h4><span class="label label-default">${g.team1.name} - ${g.team2.name}</span></h4>
                </td>
                <td class="col-md-1">
                    <c:if test="${g.status != 0}">
                        ${g.strScore1}&nbsp;:&nbsp;${g.strScore2}
                    </c:if>
                </td>
                <td class="col-md-2">${g.strDate}</td>
                <td class="col-md-2">${g.strTime}</td>
                <td class="col-md-1"><button class="btn" data-toggle="collapse" data-target="#game-${g.id}" aria-expanded="true" aria-controls="game-${g.id}">
                    <span class="glyphicon glyphicon-chevron-down"/></button>
                </td>
           </tr><table>
            </h4>
           <div id="game-${g.id}" class="collapse well">
               <%@include file="game_form.jsp"%>
            </div>
        </div>
      </div>
    </c:forEach>
</c:if>