<%@include file="include.jsp"%>
<%@include file="header.jsp"%>
<link href="<c:url value="/css/bootstrap-datetimepicker.min.css"/>" type="text/css" rel="stylesheet" />
<%@include file="navbar.jsp"%>


<div class="container">
<c:set var="gms_active" value="active"/>
<spring:message code="games" var="pg_hdr"/>
<%@include file="tour_hdr.jsp"%>

<%@include file="sch_bar.jsp"%>

<div class="row">
  <div class="col-md-6 col-md-offset-3">
     <%@include file="show_err.jsp"%>        
 </div>
</div>


    <c:if test="${tr.main != null}">
      <c:set var="dv" value="${tr.main}"/>
      <%@include file="game_table.jsp"%>
    </c:if>

        <c:set var="embdiv" value="1"/>
     <c:forEach items="${tr.divList}" var="dv" varStatus="di">
      <div class="row-fluid">

            <%@include file="div_bar.jsp"%>
       </div>

        <c:if test="${dv.getGamesSize() > 0}">
            <%@include file="game_table.jsp"%>
        </c:if>
      </c:forEach>
</div>
<script type="text/javascript" src="/js/moment.js"></script>
<script type="text/javascript" src="/js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript">
    $(function () {
        $('.datepicker').datetimepicker({pickTime: false, showTodayButton: true});
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

<%@include file="footer.jsp"%>
