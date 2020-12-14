<%@include file="include.jsp"%>
<%@include file="header.jsp"%>

<%@include file="navbar.jsp"%>
<div class="container">
  <div class="row">
    <div class="col-md-6 col-md-offset-3 well">
      <h2 class="text-center"><spring:message code="contact.support"/></h2>
      <c:if test="${not empty error}">
        <div class="alert alert-danger" role="alert"><spring:message code="error.unexpected"/></div>
      </c:if>
        <c:if test="${done}">
            <div class="alert alert-success" role="alert"><spring:message code="msg.done"/></div>
            <h5 class="text-center"><spring:message code="return.to"/> <a href="/"><spring:message code="main.page"/></a></h5>
        </c:if>
        <c:if test="${!done}">
          <spring:message code="subj" var="subj_str"/>
          <spring:message code="email" var="email_str"/>
          <spring:message code="msg_text" var="text_str"/>

          <form method="POST" action="/_create/contact" role="form">
              <h5><input type="text" name="subj" class="form-control input-block-level" placeholder="${subj_str}" value="${subj}"/></h5>
              <h5><input type="text" name="email" class="form-control input-block-level" placeholder="${email_str}" value="${email}"/></h5>
              <h5><textarea name="text" class="input-block-level form-control" rows="4" placeholder="${text_str}">${text}</textarea></h5>
              <h5><button type="submit" class="center-block btn btn-primary"><spring:message code="send"/></button></h5>
          </form>
        </c:if>
    </div>
  </div>
</div>



<%@include file="footer.jsp"%>


