<%@include file="include.jsp"%>
<%@include file="header.jsp"%>

<%@include file="navbar.jsp"%>

<div class="container">
<c:set var="ppl_active" value="active"/>
    <%@include file="lg_bar.jsp"%>        

  <div class="row">
    <div class="col-md-6 col-md-offset-3">      
        <h2><spring:message code="fans"/></h2>
    </div>
  </div>
  <div class="row">
    <div class="col-md-6 col-md-offset-3 ">      

        <table class="table">
       <c:forEach items="${fans}" var="f">
        <tr><td>
         <h4><a href="/assign?league=${league.uname}&user=${f.id}">${f.name}</a></h4>
        </td>
        <td>
        </td>
        </tr>
       </c:forEach>
        </table>
    </div>
  </div>
</div>
<%@include file="footer.jsp"%>


