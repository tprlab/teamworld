 <nav class="navbar navbar-default navbar-static" role="navigation">
   <div class="container-fluid">
     <div class="navbar-header">
       <button class="navbar-toggle collapsed" type="button" data-toggle="collapse" data-target=".sch-navbar-collapse">
         <span class="sr-only">Toggle navigation</span>
         <span class="icon-bar"></span>
         <span class="icon-bar"></span>
         <span class="icon-bar"></span>
       </button>
         <div class="navbar-brand">
            <spring:message code="schedule"/>
         </div>
     </div>

     <div class="collapse navbar-collapse sch-navbar-collapse">
       <ul class="nav navbar-nav">
        <c:if test="${tr.main != null}">
        <li role="presentation"><a href="/schedule/game?tour=${tr.uname}" ><spring:message code="game.add"/></a></li>
        </c:if>
        <li role="presentation"><a href="/manage/tour/schedule?name=${tr.uname}" ><spring:message code="round.robin"/></a></li>
        <li role="presentation"><a href="/manage/tour/schedule?name=${tr.uname}&mode=1"><spring:message code="play.off"/></a></li>
        <li role="presentation"><a href="/manage/tour/schedule/clear?tour=${tr.uname}"><spring:message code="clear.all"/></a></li>
        </ul>
     </div><!-- /.nav-collapse -->
   </div><!-- /.container-fluid -->
 </nav> <!-- /navbar-example -->
