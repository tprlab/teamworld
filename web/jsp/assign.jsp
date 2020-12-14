<%@include file="include.jsp"%>
<%@include file="header.jsp"%>

<%@include file="navbar.jsp"%>

<div class="container">
    <c:set var="lg_active" value="active"/>
    <%@include file="lg_bar.jsp"%>
    <div class="row">
        <h2><spring:message code="assign.perms"/>&nbsp;<strong>${player.name}</strong></h2>
    </div>

    <div class="row">
    <form action="/_assign" method="POST">
        <input type="hidden" name="oid" value="${league.id}"/>
        <input type="hidden" name="type" value="league"/>
        <input type="hidden" name="uid" value="${player.id}"/>
   <table class="table">
    <c:forEach items="${lperms}" var="p">
      <c:set var="ch" value=""/>
      <c:if test="${p.state != 0}">
        <c:set var="ch" value="checked"/>
      </c:if>
      <tr>
        <td><spring:message code="${p.desc}"/></td>
        <td><input type="checkbox" name="lperm${p.perm}" ${ch}/>
       </tr>
    </c:forEach>
   </table>
        <button class="btn btn-primary center-block"><spring:message code="save"/></button>
    </form>
    </div>

    <div class="row">
        <h4><spring:message code="assign.tperms"/>

      <div class="dropdown">
        <button class="btn btn-default dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
          <spring:message code="sel_team"/>
          <span class="caret"></span>
        </button>
        <ul class="dropdown-menu" aria-labelledby="dropdownMenu1">
             <c:forEach items="${teams}" var="t">
                 <li><a href="/team/assign?team=${t.uname}&user=${player.id}">${t.name}</a></li>
             </c:forEach>
        </ul>
      </div>
    </h4>
    </div>
</div>


<%@include file="footer.jsp"%>
