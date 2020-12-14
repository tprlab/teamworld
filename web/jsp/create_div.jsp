<%@include file="include.jsp"%>
<%@include file="header.jsp"%>

<%@include file="navbar.jsp"%>

<div class="container-fluid">
  <div class="row-fluid">
    <%@include file="lg_bar.jsp"%>        
    <div class="col-md-6 col-md-offset-3 well">
      <h2 class="text-center"><spring:message code="div.create"/></h2>
        <%@include file="create_div_form.jsp"%>
   </div>
  </div>
</div>

<script type="text/javascript">
    $(document).ready(function() {
        $('#dname').focus();
    });
</script>

<%@include file="footer.jsp"%>