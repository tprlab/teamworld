<%@include file="include.jsp"%>
<%@include file="header.jsp"%>

<%@include file="navbar.jsp"%>

<div class="container">
    <c:set var="dvs_active" value="active"/>
   <%@include file="lg_bar.jsp"%>        

  <div class="row">
    <div class="col-md-6 col-md-offset-3">
      <h2><spring:message code="divs"/></h2>
      <h5>
        <%@include file="create_div_btn.jsp"%>
      </h5>
    </div>
 </div>

       <c:forEach items="${league.divs}" var="dv">
    
  <div class="row">
    <div class="col-md-6 col-md-offset-3">

         <h4><a href="#" id="div-${dv.value.uname}">${dv.value.name}</a></h4>
            <c:if test="${dv.value.getTeamsSize() < 1}">
                <div class="alert alert-info">
                    <spring:message code="no.teams.div"/>
                </div>
            </c:if>
         <table class="table table-striped table-bordered">
           <c:forEach items="${dv.value.teams}" var="tm">
           <tr><td>
           ${tm.name}</td>
            <td>
            <form:form method="POST" action="/_save/divteam" role="form" class="form-inline" commandName="new_team">
              <form:hidden path="uname" value="${tm.uname}" />
              <form:hidden path="leagueName" value="${league.uname}" />
              <input type="hidden" name="ret" value="div"/>
            <td>
              <form:select path="divId" class="form-control">
                  <form:option value="0">No div</form:option>
                  <form:option value="${dv.key}" selected="selected">${dv.value.name}</form:option>
                <c:forEach items="${league.divs}" var="dd">
                    <c:if test="${dd.key != dv.key}">
                      <form:option value="${dd.key}">${dd.value.name}</form:option>
                    </c:if>
                </c:forEach>
              </form:select>
            </td>
            <td><button type="submit" class="btn btn-primary"><spring:message code="set"/></button></td>
         </form:form>

           </td></tr>
           </c:forEach>
         </table>
        </div>
    </div>
       </c:forEach>


<%@include file="footer.jsp"%>


