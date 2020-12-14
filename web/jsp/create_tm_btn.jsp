  <button class="btn btn-primary pull-right" data-toggle="collapse" data-target="#add-tm-form" aria-expanded="true" aria-controls="add-tm-form">
    <span id="btn-tm"><spring:message code="add"/></span>
  </button>
<div class="row">
  <div id="add-tm-form" class="collapse well">
        <%@include file="create_team_form.jsp"%>
  </div>
</div>
<script type="text/javascript">
    $(document).ready(function() {
        $('#add-tm-form').on('shown.bs.collapse', function () {
            $('#tmname').focus();
            $('#btn-tm').text($('#label-back').text());
        })

        $('#add-tm-form').on('hidden.bs.collapse', function () {
            $('#btn-tm').text($('#label-add').text());
        })

    });
</script>