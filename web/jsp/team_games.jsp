<%@include file="include.jsp"%>
<%@include file="header.jsp"%>

<%@include file="navbar.jsp"%>

<div class="container">
    <%@include file="tour_hdr.jsp"%>
  <div class="row">
    <h2 class="text-center"><a href="/tour?name=${tr.uname}">${tr.name}</a></h2>
    <div class="col-md-8 col-md-offset-2">
      <c:set var="show_div" value="false"/>
        <%@include file="view_games.jsp"%>
    </div>

  </div>
</div>
<%@include file="footer.jsp"%>


