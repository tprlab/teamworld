<button class="btn btn-primary" data-toggle="collapse" data-target="#add-tr-form" aria-expanded="true" aria-controls="add-tr-form">
    <span id="btn-tr"><spring:message code="add"/></span>
</button>
<div id="add-tr-form" class="collapse well">
        <%@include file="create_tour_form.jsp"%>
  </div>
<script type="text/javascript">
    $(document).ready(function() {
        $('#add-tr-form').on('shown.bs.collapse', function () {
            $('#trname').focus();
            $('#btn-tr').text($('#label-back').text());
        })

        $('#add-tr-form').on('hidden.bs.collapse', function () {
            $('#btn-tr').text($('#label-add').text());
        })

    });
</script>