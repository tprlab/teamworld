<div class="row">
 <nav class="navbar navbar-default navbar-static" role="navigation">
   <div class="container-fluid">
     <div class="navbar-header">
       <button class="navbar-toggle collapsed" type="button" data-toggle="collapse" data-target=".lg-navbar-collapse">
         <span class="sr-only">Toggle navigation</span>
         <span class="icon-bar"></span>
         <span class="icon-bar"></span>
         <span class="icon-bar"></span>
       </button>
         <div class="navbar-brand">
            <a href="/league?name=${league.uname}">${league.name}</a>
         </div>
     </div>

     <div class="collapse navbar-collapse lg-navbar-collapse">
       <ul class="nav navbar-nav">
          <li role="presentation" class="${lg_active}"><a href="/league?name=${league.uname}"><spring:message code="standings"/></a></li>
          <li role="presentation" class="${gms_active}"><a href="/league/games?name=${league.uname}"><spring:message code="games"/></a></li>
          <li role="presentation" class="${hist_active}"><a href="/league/history?name=${league.uname}"><spring:message code="history"/></a></li>
        </ul>
     </div><!-- /.nav-collapse -->
   </div><!-- /.container-fluid -->
 </nav> <!-- /navbar-example -->
</div>