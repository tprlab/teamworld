<%@include file="include.jsp"%>
<%@include file="header.jsp"%>
<%@include file="navbar.jsp"%>

<div class="container">
  <div class="row">
    <div class="col-md-6 col-md-offset-3 well">
      <h2><spring:message code="league.create"/></h2>
      <c:if test="${not empty error}">
        <div class="alert alert-danger" role="alert"><spring:message code="${error}"/></div>
      </c:if>

      <form:form method="POST" action="/_create/league" role="form" commandName="league" id="form-lg">
          <h5><spring:message code="name" var="name"/>
          <form:hidden path="timezone"/>
          <form:input type="text" path="name" class="form-control input-block-level" placeholder="${name}"/>
          </h5>
          <h5><spring:message code="location" var="location"/>
          <form:input type="text" path="location" class="form-control input-block-level" name="location" data-provide="typeahead" placeholder="${location}"/>
          </h5>
          <h5>
          <form:select path="sportId" class="form-control">
              <form:option value="0"><spring:message code="sport.select"/></form:option>
            <c:forEach items="${sportList}" var="sp">
              <form:option value="${sp.id}">${sp.name}</form:option>
            </c:forEach>
          </form:select>
          </h5>
          <h5><button type="submit" class="center-block btn btn-primary"><spring:message code="create"/></button></h5>
      </form:form>
    <p><spring:message code="more.sport"/>&nbsp;<a href="/contact"><spring:message code="here"/></a></p>

    </div>
  </div>
</div>
<script type="text/javascript">
$( document ).ready(function() {
        var tz = jstz.determine();
        $('#timezone').val(tz.name());
    });
</script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jstimezonedetect/1.0.4/jstz.min.js"></script>
<%@include file="footer.jsp"%>


