<div class="row">
 <nav class="navbar navbar-default navbar-static" role="navigation">
   <div class="container">
     <div class="navbar-header">
       <button class="navbar-toggle collapsed" type="button" data-toggle="collapse" data-target=".lg-navbar-collapse">
         <span class="sr-only">Toggle navigation</span>
         <span class="icon-bar"></span>
         <span class="icon-bar"></span>
         <span class="icon-bar"></span>
       </button>
         <div class="navbar-brand">
            <a href="/manage/league?name=${league.uname}">${league.name}</a>
         </div>
     </div>

     <div class="collapse navbar-collapse lg-navbar-collapse">
       <ul class="nav navbar-nav">
          <c:if test="${mgr_lg || mgr_gm}">
          <li role="presentation" class="${trs_active}"><a href="/manage/league/tours?name=${league.uname}"><spring:message code="tours"/></a></li>
          </c:if>  
          <c:if test="${mgr_tm}">
          <li role="presentation" class="${lg_active}"><a href="/manage/league?name=${league.uname}"><spring:message code="teams"/></a></li>
          </c:if>  
          <c:if test="${mgr_lg}">
          <li role="presentation" class="${info_active}"><a href="/manage/league/info?name=${league.uname}"><spring:message code="settings"/></a></li>
          </c:if>  
          <c:if test="${mgr_ppl}">
          <li role="presentation" class="${ppl_active}"><a href="/manage/league/people?name=${league.uname}"><spring:message code="people"/></a></li>
          </c:if>  
        </ul>
     </div><!-- /.nav-collapse -->
   </div><!-- /.container-fluid -->
 </nav> <!-- /navbar-example -->
  <div class="col-md-6 col-md-offset-3">
     <%@include file="show_err.jsp"%>        
 </div>
</div>