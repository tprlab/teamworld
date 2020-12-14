<%@include file="include.jsp"%>
<%@include file="header.jsp"%>

<%@include file="navbar.jsp"%>

<div class="container">
<c:set var="tms_active" value="active"/>
<spring:message code="teams" var="pg_hdr"/>
    <%@include file="tour_hdr.jsp"%>
  <div class="row">
    <div class="col-md-6 col-md-offset-3">
     <c:if test="${mgr_lg}">
      <h4><spring:message code="need.more.teams"/>
       <a href="/manage/league?name=${league.uname}"><spring:message code="here"/></a>
     </h4>
     </c:if>
    <p><h4>Ready to start? Let's <a href="/manage/tour/schedule?name=${tr.uname}">schedule</a> some games</h4></p>
  </div>
  </div>

  <div class="row">
    <div class="col-md-6 col-md-offset-3">
      <h3><spring:message code="teams"/></h3>

      <table class="table table-striped table-bordered">
      <c:forEach items="${teams}" var="tm">
      <tr><td class="col-md-2">${tm.name}</td>

        <c:if test="${tr.getDivListSize() > 0 && mgr_lg}">
        <td class="col-md-2">
        <form:form method="POST" action="/_save/divteamtr" role="form" class="form-inline" commandName="new_team" id="fortm-${tm.id}">
              <form:hidden path="uname" value="${tm.uname}" />
              <form:hidden path="tourName" value="${tr.uname}" />
              <form:hidden path="leagueName" value="${league.uname}" />
              <form:select path="divId" class="form-control tmselect" id="select-${tm.id}">
                  <form:option value="0"><spring:message code="div.select"/></form:option>
                <c:forEach items="${divs}" var="dv">
                  <form:option value="${dv.id}">${dv.name}</form:option>
                </c:forEach>
              </form:select>
         </form:form>
         </c:if>
         <c:if test="${tr.getDivListSize() == 0 && mgr_lg}">
            <td class="col-md-1">
            <input class="btn pull-right" type="checkbox" checked> 
         </c:if>
         </td>

      </tr>
      </c:forEach>
      </table>
  </div>
</div>


<c:if test="${tr.getDivListSize() > 0} && mgr_lg">
  <div class="row">
    <div class="col-md-6 col-md-offset-3">      
    <form method="POST" action="/_save/copy">
      <input type="hidden" name="tour" value="${tr.uname}"/>
    
      <h3><spring:message code="divs"/>
            <spring:message code="copy.tooltip" var="copy.tt"/>
            <button class="btn btn-primary" data-toggle="tooltip" data-placement="right" title="${copy.tt}"><spring:message code="copy.tms"/></button>
      </h3>

    </form>
       <p>
       <c:forEach items="${tr.divList}" var="dv">
         <h3><a href="/manage/div?name=${dv.uname}&tour=${tr.uname}">${dv.name}</a></h3>
         <table class="table table-striped table-bordered">
           <c:forEach items="${dv.table}" var="tm">
           <tr>
            <td class="col-md-2">${tm.team.name}</td>
            <td class="col-md-2">
             <form:form method="POST" action="/_save/divteamtr" role="form" class="form-inline" commandName="new_team" id="fortm-${tm.team.id}">
              <form:hidden path="uname" value="${tm.team.uname}" />
              <form:hidden path="tourName" value="${tr.uname}" />
              <form:hidden path="leagueName" value="${league.uname}" />
              <form:select path="divId" class="form-control tmselect" id="select-${tm.team.id}">
                  <form:option value="0"><spring:message code="no.div"/></form:option>
                  <form:option value="${dv.id}" selected="selected">${dv.name}</form:option>

                   <c:forEach items="${divs}" var="dd">
                        <c:if test="${dd.id != dv.id}">
                          <form:option value="${dd.id}">${dd.name}</form:option>
                        </c:if>
                   </c:forEach>

              </form:select>
            </form:form>
            </td>
           </tr>
           </c:forEach>
         </table>
       </c:forEach>
        </p>
    </div>
 </div>


<script type="text/javascript">
    $(document).ready(function() {
       $(".tmselect").change(function() {
            var sel_id = $(this).attr('id')
            var tm_id = sel_id.substring(7)
            var form_id = "#fortm-"+ tm_id
            $(form_id).submit();
       });
      $('[data-toggle="tooltip"]').tooltip()
    });
</script>
</c:if>
</div>
<%@include file="footer.jsp"%>


