<%@include file="include.jsp"%>
<%@include file="header.jsp"%>

<%@include file="navbar.jsp"%>

<div class="container">
    <c:set var="league" value="${tr.league}"/>
    <%@include file="lg_view_bar.jsp"%>
  <div class="row">
      <h2 class="text-center">${tr.name}</h2>
  </div>  

    <%@include file="tour.jsp"%>  

</div>
<%@include file="footer.jsp"%>
