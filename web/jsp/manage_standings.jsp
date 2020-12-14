<%@include file="include.jsp"%>
<%@include file="header.jsp"%>

<%@include file="navbar.jsp"%>

<div class="container">
  <div class="row-fluid">
<c:set var="std_active" value="active"/>
<spring:message code="standings" var="pg_hdr"/>
    <%@include file="tour_hdr.jsp"%>
    <%@include file="sch_bar.jsp"%>
    </div>

<c:if test="${tr.main != null}">
    <c:set var="dv" value="${tr.main}"/>
    <div class="row-fluid">
    <%@include file="div_table.jsp"%>    
      <div class="text-center">
      <form action="/_update/table" method="POST" id="refresh-form">
          <input type="hidden" name="tour" value="${tr.uname}"/>
           <button class="btn btn-primary btn-sub" id="refresh-btn"><spring:message code="refresh"/></button>
       </form>
       </div>
    </div>
</c:if>


       <c:set var="embdiv" value="1"/>
       <c:forEach items="${tr.divList}" var="dv" varStatus="di">
  <div class="row-fluid">
        <form action="/_update/table" method="POST" id="form-${dv.id}">
            <input type="hidden" name="tour" value="${tr.uname}"/>
            <input type="hidden" name="div" value="${dv.uname}"/>
         </form>


        <%@include file="div_bar.jsp"%>    
  </div>
        <%@include file="div_table.jsp"%>    
       </c:forEach>
</div>
<script type="text/javascript">
    $(document).ready(function() {

       $(".btn-sub").click(function() {
           $("#form-" + this.id).submit();
       });

    });
</script>
<%@include file="footer.jsp"%>
