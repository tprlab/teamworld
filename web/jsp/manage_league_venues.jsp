<%@include file="include.jsp"%>
<%@include file="header.jsp"%>

<%@include file="navbar.jsp"%>

<div class="container">
<c:set var="vn_active" value="active"/>
<%@include file="lg_bar.jsp"%>        

  <div class="row-fluid">
        <h2><spring:message code="venues"/>
        <%@include file="create_venue_btn.jsp"%></h2>
  </div>

        <table><tr>
        <th class="col-md-4"><spring:message code="name"/></div>
        <th class="col-md-6"><spring:message code="addr"/></div>
        <th class="col-md-2"><spring:message code="court.num"/></div>
        </tr>

     <c:forEach items="${vens}" var="vn">
        <tr>
          <td class="col-md-4">
            <h4>
            <button id="btn-vn-${vn.id}" class="btn-vn btn btn-default" data-toggle="collapse" data-target="#vn-${vn.id}" aria-expanded="true" aria-controls="vn-${vn.id}">
                <span class="glyphicon glyphicon-menu-down" aria-hidden="true"></span> </button>

            ${vn.name}</h4></td>
            <td class="col-md-6"><h5><em>${vn.addr}</em></h5></td>
            <td class="col-md-2"><h5>${vn.courts}</h5></td>
            </tr>
            <tr><td colspan="3">
           <div id="vn-${vn.id}" class="collapse well">
                <%@include file="venue_form.jsp"%>
            </div>
            </td>
            </tr>
        
     </c:forEach>
</table>            



</div>
<%@include file="footer.jsp"%>


