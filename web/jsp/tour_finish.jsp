<%@include file="include.jsp"%>
<%@include file="header.jsp"%>

<%@include file="navbar.jsp"%>


<div class="container">
<c:set var="fin_active" value="active"/>
<spring:message code="finish" var="pg_hdr"/>
<%@include file="tour_hdr.jsp"%>
  <div class="row">
     <div>
        <%@include file="show_err.jsp"%>        
    </div>

    <div class="col-md-8 col-md-offset-2 well">
      <h2 class="text-center"><spring:message code="tour.finish"/>
        <a href="/manage/tour?name=${tr.uname}">${tr.name}</a>
    </h2>

      <form method="POST" action="/_save/tour/finish" role="form">
          <input type="hidden" name="tour" value="${tr.uname}">
          <h5><spring:message code="tour.finish.warn"/></h5>
          <button type="submit" class="center-block btn btn-primary btn-lg"><spring:message code="finish"/></button>
      </form>
    </div>
  </div>
</div>
<%@include file="footer.jsp"%>


