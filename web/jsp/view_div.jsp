<%@include file="include.jsp"%>
<%@include file="header.jsp"%>

<%@include file="navbar.jsp"%>

<div class="container">
    <c:set var="league" value="${tr.league}"/>
    <%@include file="lg_view_bar.jsp"%>    
  <div class="row">
      <h2 class="text-center">${dv.name}
        <c:if test="${tr != null}">
            (<a href="/tour?name=${tr.uname}">${tr.name}</a>)
        </c:if>
    </h2>
  </div>

  <div class="row">
    <div class="col-md-8 col-md-offset-2">
        <%@include file="div_table.jsp"%>
        <c:if test="${dv.getGamesSize() > 0}">
            <c:set var="games" value="${dv.games}"/>
            <%@include file="view_games.jsp"%>    
        </c:if>
    </div>
  </div>

</div>
<%@include file="footer.jsp"%>


