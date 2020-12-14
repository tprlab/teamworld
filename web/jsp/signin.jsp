<%@include file="include.jsp"%>
<%@include file="header.jsp"%>

<%@include file="navbar.jsp"%>
<div class="container">
    <%@include file="show_err.jsp"%>
  <div class="row">
    <div class="col-md-6 col-md-offset-3 well">
      <h2><spring:message code="reg"/></h2>

       <form id="fb_signin" action="<c:url value="/signin/facebook"/>" method="POST">
         <button type="submit">
           Facebook
         </button>
       </form>

       <form id="g_signin" action="<c:url value="/signin/google"/>" method="POST">
         <button type="submit">
           Google
         </button>
       </form>


    </div>
  </div>
</div>
<%@include file="footer.jsp"%>