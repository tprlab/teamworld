<%@include file="include.jsp"%>
<%@include file="header.jsp"%>

<%@include file="navbar.jsp"%>


<div class="container">
<c:set var="stg_active" value="active"/>
<spring:message code="settings" var="pg_hdr"/>
<%@include file="tour_hdr.jsp"%>
  <div class="row">

    <div class="col-md-8 col-md-offset-2 well">
      <h3 class="text-center">
        <a href="/manage/tour?name=${tr.uname}">${tr.name}</a>
     </h3>

        <p>
        <form method="POST" action="/_save/tour">
            <input type="hidden" name="name" value="${tr.uname}"/>
            <label class="control-label"><spring:message code="tour.rename"/>:&nbsp;</label>
            <table>
            <tr><td class="col-md-8">
                <input class="form-control" type="text" name="newname" value="${tr.name}"/>
            </td><td class="col-md-4">
                <button type="submit" class="btn btn-primary form-control"><spring:message code="save"/></button>
            </td></tr></table>
        </form>
        </p>

    <p class="text-center"><a href="/manage/tour/finish?name=${tr.uname}" class="btn btn-success"><spring:message code="tour.finish"/></a></p>
    <p class="text-center"><a href="/manage/tour/cancel?name=${tr.uname}" class="btn btn-danger"><spring:message code="tour.cancel"/></a></p>

    </div>
  </div>
</div>
<%@include file="footer.jsp"%>


