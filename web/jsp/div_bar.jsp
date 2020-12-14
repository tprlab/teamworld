 <nav class="navbar navbar-default navbar-static" role="navigation">
   <div class="container-fluid">
     <div class="navbar-header">
       <button class="navbar-toggle collapsed" type="button" data-toggle="collapse" data-target=".dv-navbar-collapse-${dv.id}">
         <span class="sr-only">Toggle navigation</span>
         <span class="icon-bar"></span>
         <span class="icon-bar"></span>
         <span class="icon-bar"></span>
       </button>
         <div class="navbar-brand">
        <c:if test="${embdiv == 1}">
            <a href="/manage/div?name=${dv.uname}&tour=${tr.uname}">${dv.name}</a>
        </c:if>
        <c:if test="${embdiv != 1}">
            <ol class="breadcrumb">
            <li>${dv.name}</li>
            <li><a href="/manage/tour?name=${tr.uname}">${tr.name}</a></li>
            </ol>
        </c:if>
         </div>
     </div>

     <div class="collapse navbar-collapse dv-navbar-collapse-${dv.id}">
       <ul class="nav navbar-nav">
            <li role="presentation"><a href="/schedule/game?tour=${tr.uname}&div=${dv.uname}" ><spring:message code="game.add"/></a></li>
            <li role="presentation"><a href="/manage/tour/schedule?name=${tr.uname}&div=${dv.uname}"><spring:message code="round.robin"/>&nbsp;${dv.name}</a></li>
            <li role="presentation"><a href="/manage/tour/schedule?name=${tr.uname}&mode=1&div=${dv.uname}"><spring:message code="play.off"/>&nbsp;${dv.name}</a></li>
            <li role="presentation"><a href="/manage/tour/schedule/clear?tour=${tr.uname}&div=${dv.uname}"><spring:message code="clear"/>&nbsp;${dv.name}</a></li>
        </ul>
     </div><!-- /.nav-collapse -->
   </div><!-- /.container-fluid -->
 </nav> <!-- /navbar-example -->
<form action="/_remove/schedule" method="POST" id="f_clrsch-${dv.id}"/>
 <input type="hidden" name="tour" value="${tr.uname}"/>
 <input type="hidden" name="div" value="${dv.uname}"/>
 <input type="hidden" name="ret" value="div"/>
</form>
