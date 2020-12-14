<%@include file="include.jsp"%>
<%@include file="header.jsp"%>

<%@include file="navbar.jsp"%>
<div class="container">
  <div class="row">
    <div class="col-md-6 col-md-offset-3 well">
      <h2><spring:message code="reg"/> ${prov}</h2>

       <form id="ssignup" action="<c:url value="/signup"/>" method="POST">
         <input type="hidden" name="email" value="${email}"/>
         <input type="text" name="name" value="${uname}"/>
         <button type="submit">
            <spring:message code="reg"/>           
         </button>
       </form>

    </div>
  </div>
</div>
<%@include file="footer.jsp"%>