<%@include file="include.jsp"%>
<%@include file="header.jsp"%>

<%@include file="navbar.jsp"%>

<div class="container">
    <%@include file="lg_bar.jsp"%>        
  <div class="row">
    <div class="col-md-4 col-md-offset-4 well">
      <h2 class="text-center"><spring:message code="team.create"/></h2>
        <%@include file="create_team_form.jsp"%>
   </div>
  </div>
</div>

<script type="text/javascript">
    $(document).ready(function() {
        $('#tmname').focus();
    });
</script>

<%@include file="footer.jsp"%>