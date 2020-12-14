<c:if test="${dv.getGamesSize() > 0}">
  <c:forEach items="${dv.games}" var="g" varStatus="i">
    <div class="row-fluid">
        <h4>
        <button class="btn btn-block btn-default" data-toggle="collapse" data-target="#game-${g.id}" aria-expanded="true" aria-controls="game-${g.id}">
        <table>
         <tr>
          <c:if test="${g.stage != 0}">
            <td class="col-sm-1">
            <c:if test="${g.stage != 1}">
              1&nbsp;/&nbsp;${g.stage}
            </c:if>     
            <c:if test="${g.stage == 1}">
              <spring:message code="final"/>
            </c:if>     
            </td><td class="col-sm-2">
            </c:if>
            <c:if test="${g.stage == 0}">
              <td colspan="2" class="col-sm-6 text-center">
            </c:if>
                <h5><span><strong>${g.team1.name} - ${g.team2.name}</strong></span></h5>
                </td>
                <td class="col-sm-3"><h5>${g.strDate}</h5></td>
                <td class="col-sm-3"><h5>${g.strTime}</h5></td>
           </tr>
            <tr>
            <td colspan="2" class="col-sm-6 text-center">
                    <c:if test="${g.status != 0}">
                        <h4><strong>${g.strScore1}&nbsp;:&nbsp;${g.strScore2}</strong></h4>
                    </c:if>
                </td>
                <td class="col-sm-3">${g.details}</td>
            </tr>
        </table>
        </button>
            </h4>
           <div id="game-${g.id}" class="collapse well">
               <%@include file="game_form.jsp"%>
            </div>
      </div>
    </c:forEach>
</c:if>