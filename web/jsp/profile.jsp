<%@include file="include.jsp"%>
<%@include file="header.jsp"%>

<%@include file="navbar.jsp"%>
<div class="container">
    <h2 class="text-center"><spring:message code="welcome"/>, ${usr.name}</h2>

  <div class="row">
    <div class="col-md-6 col-md-offset-3 well">
      <h3 class="text-center"><spring:message code="your.leagues"/>
       <a href="/create/league" class="btn btn-primary" role="button"><spring:message code="create"/></a>
     </h3>
        <table class="table">
         <c:forEach items="${leagues}" var="lg">
           <tr><td><a href="/manage/league?name=${lg.uname}">${lg.name}</a></td></tr>
         </c:forEach>

         <c:forEach items="${fleagues}" var="lg">
           <tr><td><a href="/league?name=${lg.uname}">${lg.name}</a></td>
            <td></td>
            </tr>
         </c:forEach>
        </table>
    </div>
  </div>


  <div class="row">
    <div class="col-md-6 col-md-offset-3 well">
      <h3 class="text-center"><spring:message code="teams"/></h3>
        <table class="table">
         <c:forEach items="${pteams}" var="t">
           <tr><td><a href="/team?name=${t.uname}">${t.name}</a></td></tr>
         </c:forEach>
        </table>
    </div>
  </div>

</div>



<%@include file="footer.jsp"%>


