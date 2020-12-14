<%@include file="include.jsp"%>
<%@include file="header.jsp"%>

<%@include file="navbar.jsp"%>

<div class="container">
<c:set var="trs_active" value="active"/>
    <%@include file="lg_bar.jsp"%>        

  <div class="row">
    <div class="col-md-6 col-md-offset-3">      
        <h2><spring:message code="tours"/>
        <c:if test="${mgr_lg}">
        <%@include file="create_tour_btn.jsp"%></h2>
        </c:if>
    </div>
  </div>
    <label class="hidden" id="label-add"><spring:message code="add"/></label>
    <label class="hidden" id="label-back"><spring:message code="back"/></label>

  <div class="row">
    <div class="col-md-6 col-md-offset-3 ">      

       <c:forEach items="${league.tours}" var="tr">
         <h4><a href="/manage/tour?name=${tr.uname}">${tr.name}</a></h4>
       </c:forEach>
    </div>
  </div>
    <%@include file="tour_hist.jsp"%>

</div>
<%@include file="footer.jsp"%>


