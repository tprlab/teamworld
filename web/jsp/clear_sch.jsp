<%@include file="include.jsp"%>
<%@include file="header.jsp"%>

<%@include file="navbar.jsp"%>

<div class="container-fluid">
    <%@include file="tour_hdr.jsp"%>
  <div class="row-fluid">
     <div>
        <%@include file="show_err.jsp"%>        
    </div>

    <div class="col-md-6 col-md-offset-3 well">
      <h2 class="text-center"><spring:message code="sch.clear"/></h2>
      <h4><spring:message code="clear.warn"/></h4>
        <form action="/_remove/schedule" method="POST"/>
         <input type="hidden" name="tour" value="${param.tour}"/>
         <input type="hidden" name="div" value="${divName}"/>
         <input type="hidden" name="ret" value="${ret}"/>

          <button type="submit" class="center-block btn btn-primary btn-lg"><spring:message code="clear"/></button>
        </form>

    </h2>

    </div>
  </div>
</div>
<%@include file="footer.jsp"%>


