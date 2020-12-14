<h3 class="text-center"><spring:message code="round.robin"/></h3>
<div class="col-md-8 col-md-offset-2 well">
<p><spring:message code="rr.text"/></p>
<form:form method="POST" action="/_create/schedule" role="form" commandName="sch">
  <div class="form-group form-inline">
      <label class="control-label col-sm-4"><spring:message code="rounds_or_games"/></label>
        <form:select class="form-control" path="extmode">
            <form:option value="0"><spring:message code="rounds"/></form:option>
            <form:option value="1"><spring:message code="games"/></form:option>
        </form:select>
  </div>

  <div class="form-group form-inline">
      <label class="control-label col-sm-4"><spring:message code="number_of_them"/></label>
       <form:input type="number" path="rounds" class="form-control" value="1"/>

  </div>


  <form:hidden path="leagueName" value="${league.uname}" />
  <form:hidden path="tourName" value="${tr.uname}" />
  <form:hidden path="divName" value="${dv.uname}" />
  <form:hidden path="mode" value="0" />

  <div class="form-group form-inline">
  <label class="control-label col-sm-4"><spring:message code="day"/></label>
   
  <form:select class="form-control" path="day" >
      <form:option value="0">Not applied</form:option>
      <form:option value="7"><spring:message code="sunday"/></form:option>
      <form:option value="1"><spring:message code="monday"/></form:option>
      <form:option value="2"><spring:message code="tuesday"/></form:option>
      <form:option value="3"><spring:message code="wednesday"/></form:option>
      <form:option value="4"><spring:message code="thursday"/></form:option>
      <form:option value="5"><spring:message code="friday"/></form:option>
      <form:option value="6"><spring:message code="saturday"/></form:option>
  </form:select>
  </div>

  <div class="form-group form-inline">
      <label class="control-label col-sm-4"><spring:message code="start.from"/></label>
    <div class='input-group date datepicker'>
      <form:input path="startDate" type="text" class="form-control" data-date-format="DD MMM YY"/>
       <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span>
    </div>
  </div>
  <div class="form-group form-inline">
     <label class="control-label col-sm-4"><spring:message code="start.time"/></label>

        <table class="table"><tr><td class="col-md-4">
        <select name="hour" class="select form-control defTime">
            <c:forEach var="i" begin="1" end="12">
                <option value="${i}">${i}</option>
            </c:forEach>
        </select></td>

        <td class="col-md-4">
        <select name="minute" class="select form-control defTime">
            <option value="0">00</option>
            <option value="15">15</option>
            <option value="30">30</option>
            <option value="45">45</option>
        </select></td>
        <td class="col-md-4">
        <select name="ampm" class="select form-control defTime">
            <option value="1">PM</option>
            <option value="0">AM</option>
        </select>
        </td></tr></table>
  </div>
   
  <div class="form-group form-inline">
        <label class="control-label col-sm-4"><spring:message code="end.time"/></label>

        <table class="table"><tr><td class="col-md-4">
        <select name="ehour" class="select form-control defTime">
            <c:forEach var="i" begin="1" end="12">
                <option value="${i}">${i}</option>
            </c:forEach>
        </select></td>

        <td class="col-md-4">        
        <select name="eminute" class="select form-control defTime">
            <option value="0">00</option>
            <option value="15">15</option>
            <option value="30">30</option>
            <option value="45">45</option>
        </select>
        </td>

        <td class="col-md-4">
        <select name="eampm" class="select form-control defTime">
            <option value="1">PM</option>
            <option value="0">AM</option>
        </select>
        </td></tr></table>
  </div>

  <div class="form-group form-inline">
      <label class="control-label col-sm-4"><spring:message code="game.len"/></label>
       <form:input type="number" path="gameLength" class="form-control" value="60"/>
  </div>

  <div class="form-group form-inline">
      <label class="control-label col-sm-4"><spring:message code="paral.games"/></label>
       <form:input type="number" path="gameNum" class="form-control" value="2"/>
  </div>




  <h5><button type="submit" class="btn btn-primary btn-lg center-block"><spring:message code="schedule"/></button></h5>
 </form:form>
</div>
</div>

<script type="text/javascript" src="/js/moment.js"></script>
<script type="text/javascript" src="/js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript">
    $(function () {
        $('.datepicker').datetimepicker({pickTime: false});
        $('.timepicker').datetimepicker({pickDate: false, useSeconds: false, minuteStepping:15, autoClose:true});
    });
</script>
