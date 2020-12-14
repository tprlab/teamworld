<table class="table table-striped table-bordered">
<c:forEach items="${teams}" var="tm">
<tr><td>
<a href="/team?name=${tm.uname}">${tm.name}</a>
</td></tr>
</c:forEach>
</table>
