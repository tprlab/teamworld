 <nav class="navbar navbar-default navbar-static" role="navigation">
   <div class="container">
     <div class="navbar-header">
       <button class="navbar-toggle collapsed" type="button" data-toggle="collapse" data-target=".tr-navbar-collapse">
         <span class="sr-only">Toggle navigation</span>
         <span class="icon-bar"></span>
         <span class="icon-bar"></span>
         <span class="icon-bar"></span>
       </button>
         <div class="navbar-brand">
              <a href="/manage/tour?name=${tr.uname}">${tr.name}</a>
         </div>
    
     </div>
     <div class="collapse navbar-collapse tr-navbar-collapse">
       <ul class="nav navbar-nav">
          <li role="presentation" class="${lg_active}"><a href="/manage/league?name=${tr.league.uname}"><spring:message code="league"/></a></li>
         <c:if test="${mgr_tm}">
          <li role="presentation" class="${tms_active}"><a href="/manage/tour?name=${tr.uname}"><spring:message code="teams"/></a></li>
         </c:if>
          <li role="presentation" class="${gms_active}"><a href="/manage/tour/games?name=${tr.uname}"><spring:message code="games"/></a></li>
          <li role="presentation" class="${std_active}"><a href="/manage/tour/standings?name=${tr.uname}"><spring:message code="standings"/></a></li>
         <c:if test="${mgr_lg}">
          <li role="presentation" class="${stg_active}"><a href="/manage/tour/settings?name=${tr.uname}"><spring:message code="settings"/></a></li>
         </c:if>
        </ul>
     </div><!-- /.nav-collapse -->
   </div><!-- /.container-fluid -->
 </nav> <!-- /navbar-example -->
