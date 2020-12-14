  <button class="btn btn-primary pull-right" data-toggle="collapse" data-target="#add-dv-form" aria-expanded="true" aria-controls="add-dv-form">
    <span id="btn-div"><spring:message code="add"/></span>
  </button>
  <div id="add-dv-form" class="collapse well">
        <%@include file="create_div_form.jsp"%>
   </div>
<script type="text/javascript">
    $(document).ready(function() {
        $('#add-dv-form').on('shown.bs.collapse', function () {
            $('#dname').focus();
            $('#btn-div').text($('#label-back').text());
        })

        $('#add-dv-form').on('hidden.bs.collapse', function () {
            $('#btn-div').text($('#label-add').text());
        })

    });
</script>