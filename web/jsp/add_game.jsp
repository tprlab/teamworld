<%@include file="include.jsp"%>
<%@include file="header.jsp"%>
<link href="<c:url value="/css/bootstrap-datetimepicker.min.css"/>" type="text/css" rel="stylesheet" />
<%@include file="navbar.jsp"%>

<div class="container">
<%@include file="tour_hdr.jsp"%>
<%@include file="sch_bar.jsp"%>
<div class="row-fluid">
     <%@include file="show_err.jsp"%>        
</div>

  <div class="row">
    <h3 class="text-center"><spring:message code="game.add"/></h3>
  </div>
  <div class="well">
    <form method="POST" action="/_create/game">
    <input type="hidden" name="tour" value="${tr.uname}"/>
    <input type="hidden" name="div" value="${dv.uname}"/>
  <div class="row form-group">
    <div class="col-md-5">
    <select name="team1" class="select form-control">
       <c:forEach items="${dv.table}" var="t">
            <option value="${t.team.uname}">${t.team.name}</option>
       </c:forEach>
    </select>
    </div>
    <div class="col-md-2 text-center">
     <label class="control-label">VS</label>
    </div>
    <div class="col-md-5">
    <select name="team2" class="select form-control">
       <c:forEach items="${dv.table}" var="t">
            <option value="${t.team.uname}">${t.team.name}</option>
       </c:forEach>
    </select>
    </div>
  </div>
  <div class="row">
    <div class="col-md-2 col-md-offset-5">
    <button type="submit" class="btn btn-primary center-block"><spring:message code="add"/></button>
    </div>
  </div>
    </form>
  </div>
 </div>



<%@include file="footer.jsp"%>


