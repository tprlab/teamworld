<%@include file="include.jsp"%>
<%@include file="header.jsp"%>
<link href="<c:url value="/css/bootstrap-datetimepicker.min.css"/>" type="text/css" rel="stylesheet" />
<%@include file="navbar.jsp"%>

<div class="container">
<%@include file="tour_hdr.jsp"%>

<%@include file="sch_bar.jsp"%>
  <div class="row-fluid">
      <c:if test="${param.mode == null || param.mode == 0}">
        <%@include file="round_robin.jsp"%>
      </c:if>
      <c:if test="${param.mode == 1}">
        <c:if test="${not empty sch}">
            <%@include file="play_off.jsp"%>
         </c:if>
      </c:if>
</div>

</div>
<%@include file="footer.jsp"%>


