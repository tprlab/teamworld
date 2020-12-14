<%@include file="include.jsp"%>
<%@include file="header.jsp"%>

<%@include file="navbar.jsp"%>

<div class="container">
    <c:set var="lg_active" value="active"/>
    <%@include file="lg_view_bar.jsp"%>

    <div class="col-md-8 col-md-offset-4">
     <h3><spring:message code="league"/>&nbsp;<strong>${league.name}</strong></h3>
        <h4><spring:message code="sport"/>&nbsp;:<strong>${league.sport.name}</strong></h4>
        <h4><spring:message code="location"/>&nbsp;: <em>${league.location}</em></h4>
        <form class="form" method="POST" action="/_follow">
            <input type="hidden" name="subj" value="${league.uname}"/>
            <input type="hidden" name="ftype" value="0"/>
            <c:if test="${follow == 1}">
                <button class="btn btn-primary"><spring:message code="follow"/>&nbsp;${league.name}</button>
            </c:if>
            <c:if test="${follow == -1}">
                <input type="hidden" name="undo" value="true"/>
                <button class="btn btn-danger btn-xs"><spring:message code="unfollow"/>&nbsp;${league.name}</button>
            </c:if>

        </form>

    </div>

  </div>
   <c:if test="${tr != null}">
       <h2 class="text-center"><a href="/tour?name=${tr.uname}">${tr.name}</a></h2>
        <%@include file="tour.jsp"%>
   </c:if>
   <c:if test="${tr == null}">
   </c:if>

</div>
<%@include file="footer.jsp"%>


