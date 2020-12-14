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
        <input type="hidden" name="oid" value="${team.id}"/>
        <input type="hidden" name="type" value="team"/>
        <input type="hidden" name="uid" value="${player.id}"/>
        <input type="hidden" name="league" value="${league.id}"/>
   <table class="table">
    <c:forEach items="${perms}" var="p">
      <c:set var="ch" value=""/>
      <c:if test="${p.state != 0}">
        <c:set var="ch" value="checked"/>
      </c:if>
      <tr>
        <td><spring:message code="${p.desc}"/></td>
        <td><input type="checkbox" name="tperm${p.perm}" ${ch}/>
       </tr>
    </c:forEach>
   </table>
        <button class="btn btn-primary center-block"><spring:message code="save"/></button>
    </form>
    </div>

</div>


<%@include file="footer.jsp"%>
