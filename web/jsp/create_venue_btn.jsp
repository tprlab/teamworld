<label class="hidden" id="label-add"><spring:message code="add"/></label>
<label class="hidden" id="label-back"><spring:message code="back"/></label>

<button class="btn btn-primary" data-toggle="collapse" data-target="#add-vn-form" aria-expanded="true" aria-controls="add-vn-form">
    <span id="btn-vn"><spring:message code="add"/></span>
</button>
 <div id="add-vn-form" class="collapse well">
        <%@include file="venue_form.jsp"%>
 </div>
<script type="text/javascript">
    $(document).ready(function() {
        $('#add-vn-form').on('shown.bs.collapse', function () {
            $('#vname').focus();
            $('#btn-vn').text($('#label-back').text());
        })

        $('#add-vn-form').on('hidden.bs.collapse', function () {
            $('#btn-vn').text($('#label-add').text());
        })

    });
</script>