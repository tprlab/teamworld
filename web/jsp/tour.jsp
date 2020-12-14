<c:if test="${tr.main != null}">
  <c:set var="dv" value="${tr.main}"/>
   <%@include file="div_table.jsp"%>
    <c:if test="${dv.getGamesSize() > 0}">
      <c:set var="games" value="${dv.games}"/>
      <c:set var="show_div" value="${tr.getDivListSize() > 1}"/>
       <div class="row">
         <div class="col-md-8 col-md-offset-2">
         <%@include file="view_games.jsp"%>    
         </div>    
       </div>
    </c:if>
</c:if>

<c:if test="${tr.getDivListSize() > 0}">
  <div class="row">
      <div class="col-md-4 col-md-offset-4 well">
        <h3><spring:message code="divs"/></h3>
        <table class="table">
       <c:forEach items="${tr.divList}" var="dv">
        <tr><td><a href="#${dv.uname}">${dv.name}</a></td></tr>
       </c:forEach>
        </table>
      </div>
 </div>
</c:if>

  <c:set var="show_div" value="false"/>
  <c:forEach items="${tr.divList}" var="dv">
    <h3 class="text-center"><a name="${dv.uname}" href="/div?name=${dv.uname}&tour=${tr.uname}">${dv.name}</a></h3>
   <%@include file="div_table.jsp"%>    
<c:if test="${dv.getGamesSize() > 0}">
 <div class="row">
   <div class="col-md-8 col-md-offset-2">
   <c:set var="games" value="${dv.games}"/>
   <%@include file="view_games.jsp"%>    
   </div>
 </div>
</c:if>
  </c:forEach>
