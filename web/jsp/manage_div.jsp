<%@include file="include.jsp"%>
<%@include file="header.jsp"%>
<link href="<c:url value="/css/bootstrap-datetimepicker.min.css"/>" type="text/css" rel="stylesheet" />
<%@include file="navbar.jsp"%>

<div class="container">
    <%@include file="tour_hdr.jsp"%>

  <div class="row">
        <%@include file="div_bar.jsp"%>
  </div>


<c:if test="${dv.getTableSize() > 0}">
  <div class="row">
    <div class="col-md-4 col-md-offset-4 well">
        <h3>
        <form action="/_update/table" method="POST">
            <spring:message code="table"/>
            <input type="hidden" name="tour" value="${tr.uname}"/>
            <input type="hidden" name="div" value="${dv.uname}"/>
            <input type="hidden" name="ret" value="div"/>
            <button class="btn btn-primary right-block"><spring:message code="refresh"/></button>
        </form>
        </h3>
    </div>
  </div>
<%@include file="div_table.jsp"%>    
</c:if>


      <c:if test="${dv.getGamesSize() > 0}">
        <c:set var="divName" value="${dv.uname}"/>
            <%@include file="game_table.jsp"%>
      </c:if>
</div>

<script type="text/javascript">
    $(function () {

        $("#b_clrsch").click(function() {
           $("#f_clrsch").submit();
        });

        $('.datepicker').datetimepicker({pickTime: false});
        $('.timepicker').datetimepicker({pickDate: false, useSeconds: false, minuteStepping:15, autoClose:true});

        $('.sc-btn-1').click(function() {
            var btn_id = $(this).attr('id')
            var g_id = btn_id.substring(5);
            var sc_id = '#scin1-' + g_id
            var sc = $(sc_id).val()
            var isc = parseInt(sc) || 0;
            isc += 1;
            $(sc_id).val(isc)
        });

        $('.sc-btn-2').click(function() {
            var btn_id = $(this).attr('id')
            var g_id = btn_id.substring(5);
            var sc_id = '#scin2-' + g_id
            var sc = $(sc_id).val()
            var isc = parseInt(sc) || 0;
            isc += 1;

            $(sc_id).val(isc)
        });

    });
</script>

<script type="text/javascript" src="/js/moment.js"></script>
<script type="text/javascript" src="/js/bootstrap-datetimepicker.min.js"></script>
<%@include file="footer.jsp"%>


