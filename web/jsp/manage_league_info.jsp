<%@include file="include.jsp"%>
<%@include file="header.jsp"%>

<%@include file="navbar.jsp"%>

<div class="container">
<c:set var="info_active" value="active"/>
    <%@include file="lg_bar.jsp"%>        

  <div class="row">
    <div class="col-md-6 col-md-offset-3 well">      
      <c:if test="${not empty error}">
        <div class="alert alert-danger" role="alert"><spring:message code="${error}"/></div>
      </c:if>


      <form:form method="POST" action="/_save/league" role="form" commandName="league">
          <form:hidden path="id" value="${league.id}"/>
          <form:hidden path="uname" value="${league.uname}"/>

          <h5><label><spring:message code="name"/></label></h5>
          <h5><form:input type="text" path="name" class="form-control input-block-level" data-provide="typeahead"/></h5>              
          <h5><label><spring:message code="location"/></label></h5>
          <h5><form:input type="text" path="location" class="form-control input-block-level" data-provide="typeahead" placeholder="${league.location}"/></h5>
          <h5><label><spring:message code="sport"/></label></h5>
          <h5>
          <form:select path="sportId" class="form-control">
              <form:option value="${league.sport.id}">${league.sport.name}</form:option>
            <c:forEach items="${sportList}" var="sp">
             <c:if test="${sp.id != league.sport.id}">
              <form:option value="${sp.id}">${sp.name}</form:option>
             </c:if>
            </c:forEach>
          </form:select>
          </h5>
          <h5><button type="submit" class="center-block btn btn-primary"><spring:message code="save"/></button></h5>
      </form:form>
    </div>

  </div>
</div>
<%@include file="footer.jsp"%>


