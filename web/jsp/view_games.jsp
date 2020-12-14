 <table class="table table-striped">
  <th colspan="2"><spring:message code="game"/></th>
    <c:if test="${show_lg}">
  <th><spring:message code="league"/></th>
    </c:if>

    <c:if test="${show_div}">
  <th><spring:message code="div.short"/></th>
    </c:if>
  <th><spring:message code="date"/></th>
    <c:if test="${!hide_score}">
  <th ><spring:message code="score"/></th>
    </c:if>

   <c:forEach items="${games}" var="g">
   <tr>
            <c:if test="${g.stage != 0}">
                <td>
                <c:if test="${g.stage != 1}">

                    1&nbsp;/&nbsp;${g.stage}
                </c:if>     
                <c:if test="${g.stage == 1}">
                    <spring:message code="final"/>
                </c:if>
                </td><td>
            </c:if>
            <c:if test="${g.stage == 0}">
                <td colspan="2">
            </c:if>
        ${g.team1.name} - ${g.team2.name}</td>

    <c:if test="${show_lg}">
          <td>
          <c:if test="${g.league != null}">
              <a href="/league?name=${g.league.uname}">${g.league.name}</a>
          </c:if>
          </td>
    </c:if>

        <c:if test="${show_div}">
          <td>
          <c:if test="${g.divId > 0}">
              <a href="/div?name=${g.division.uname}&tour=${tr.uname}">${g.division.name}</a>
          </c:if>
          </td>
        </c:if>

        <td>${g.strDate}</td>
    <c:if test="${!hide_score}">
        <td>
            <c:if test="${g.status == 1}">
                ${g.score1}&nbsp;:&nbsp;${g.score2}
            </c:if>
        </td>
    </c:if>
   </tr>

   </c:forEach>
 </table>
