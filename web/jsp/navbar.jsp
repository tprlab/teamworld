<nav class="navbar navbar-inverse navbar-static" role="navigation">
      <div class="container">
        <div class="navbar-header">
          <button class="navbar-toggle collapsed" type="button" data-toggle="collapse" data-target=".js-navbar-collapse">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
            <a class="navbar-brand" href="/"><spring:message code="brand"/></a>
        </div>
        <div class="collapse navbar-collapse js-navbar-collapse">
          <ul class="nav navbar-nav">
            <sec:authorize ifNotGranted="ROLE_ANONYMOUS">
                <li><a href="/contact"><spring:message code="support"/></a></li>
            </sec:authorize>
          </ul>
          <ul class="nav navbar-nav navbar-right">
            <sec:authorize ifNotGranted="ROLE_ANONYMOUS">
             <li><a href="/profile"><spring:message code="profile"/>
            ( <sec:authentication property="principal.username"></sec:authentication>)</a></li>
             <li role="presentation">
                <form method="POST" action="<c:url value='j_spring_security_logout'/>" class="navbar-form">
                  <button type="submit" class="btn btn-link"><spring:message code="logout.action"/></button>
                </form>
             </li>
            </sec:authorize>
            <sec:authorize ifAnyGranted="ROLE_ANONYMOUS">
                <a href="/login" class="btn btn-success navbar-btn"><spring:message code="login.action"/></a>
            </sec:authorize>

          </ul>
        </div><!-- /.nav-collapse -->
      </div><!-- /.container-fluid -->
    </nav> <!-- /navbar-example -->