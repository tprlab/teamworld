<%@include file="include.jsp"%>
<%@include file="header.jsp"%>

<%@include file="navbar.jsp"%>

<div class="container">
    <c:set var="lg_active" value="active"/>
    <%@include file="lg_bar.jsp"%>
    <div class="row">

      <div class="col-md-6 col-md-offset-3"> 
    <label class="hidden" id="label-add"><spring:message code="add"/></label>
    <label class="hidden" id="label-back"><spring:message code="back"/></label>

        <h4>
        <spring:message code="teams.done"/>
        <a href="/manage/league/tours?name=${league.uname}"><spring:message code="here"/></a>
        </h4>
      </div>
    </div>

    <div class="row">
      <div class="col-md-6 col-md-offset-3"> 
          <h3><spring:message code="divs"/>
            <%@include file="create_div_btn.jsp"%>
          </h3>
      </div>
    </div>

    <div class="row">
      <div class="col-md-6 col-md-offset-3"> 

           <c:forEach items="${league.divList}" var="dv">
                <h4><a href="#div-${dv.uname}">${dv.name}</a></h4>
           </c:forEach>

      </div>
    </div>


    <div class="row">
      <div class="col-md-6 col-md-offset-3">     
      <h3><spring:message code="teams"/>
        <%@include file="create_tm_btn.jsp"%>
      </h3>
      </div>
    </div>


    <div class="row">
      <div class="col-md-6 col-md-offset-3">     

      <table class="table table-striped table-bordered">
      <c:forEach items="${league.teams}" var="tm">
      <tr><td class="col-md-6"><a href="/manage/league/team?name=${tm.uname}">${tm.name}</a></td>

      <c:if test="${league.getDivsSize() > 0 }">
        <form:form method="POST" action="/_save/divteam" role="form" class="form-inline" commandName="team" id="fortm-${tm.id}">
              <form:hidden path="uname" value="${tm.uname}" />
              <form:hidden path="leagueName" value="${league.uname}" />
       <td class="col-md-6">
              <select name="divId" class="form-control tmselect select" id="select-${tm.id}">
                <c:if test="${tm.divId != 0}">
                    <c:set var="tdv" value="${league.divs[tm.divId]}"/>
                    <option value="${tdv.id}">${tdv.name}</option>
                </c:if>
                <option value="0"><spring:message code="no.div"/></option>

                <c:forEach items="${league.divs}" var="dv">
                    <c:if test="${dv.value.id != tm.divId}">
                      <option value="${dv.key}">${dv.value.name}</option>
                    </c:if>
                </c:forEach>
              </select>
            </td>
         </form:form>
      </c:if>

      </tr>
      </c:forEach>
      </table>
    </div>
  </div>
<c:forEach items="${league.divList}" var="dv">
    
  <div class="row">
    <div class="col-md-6 col-md-offset-3">

        <form method="POST" id="rank-form-${dv.uname}" action="/_save/rank">
            <input type="hidden" name="name" value="${dv.uname}"/>
            <input type="hidden" name="league" value="${league.uname}"/>
        </form>
         <h4 id="div-${dv.uname}"><a href="/manage/league/div?name=${dv.uname}">${dv.name}</a>
            <c:if test="${dv.rank > 0}">
            <button id="btn-rank-${dv.uname}" class="btn-rank btn btn-default"><span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span> </button>
            </c:if>
        </h4>
            <c:if test="${dv.getTeamsSize() < 1}">
                <div class="alert alert-info">
                    <spring:message code="no.teams.div"/>
                </div>
            </c:if>
            <c:set var="dvtm" value="${dv}"/>
            <%@include file="tm_div_table.jsp"%>
        </div>
    </div>
</c:forEach>


</div>

<script type="text/javascript">
    $(document).ready(function() {
       $(".tmselect").change(function() {
            var sel_id = $(this).attr('id')
            var tm_id = sel_id.substring(7)
            var btn_id = "#btntm-"+ tm_id
            var form_id = "#fortm-"+ tm_id
            $(form_id).submit();
       });

        $(".btn-rank").click(function() {
            var btn_id = $(this).attr('id')
            var d_id = btn_id.substring(9);
            var form_id = "#rank-form-" + d_id;
            $(form_id).submit();
        });

    });
</script>

<%@include file="footer.jsp"%>
