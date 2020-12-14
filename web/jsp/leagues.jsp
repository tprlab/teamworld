<table class="table">
<th><spring:message code="name"/></th>
<th><spring:message code="sport"/></th>
<th><spring:message code="location"/></th>
<c:forEach items="${leagues}" var="lg">
<tr>
<td><a href="/league?name=${lg.uname}">${lg.name}</a></td>
<td>${lg.sport.name}</td>
<td>${lg.location}</td>
</tr>
</c:forEach>
</table>
