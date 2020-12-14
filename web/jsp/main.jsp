<%@include file="include.jsp"%>
<%@include file="header.jsp"%>

<%@include file="navbar.jsp"%>

<spring:message code="search" var="search_word"/>


<div class="container">
  <div class="row">

      <h1 class="text-center"><spring:message code="brand.full"/></h1>
      <h4 class="text-center"><em><spring:message code="brand.slogan"/></em></h4>

    <div class="col-md-8 col-md-offset-2">
      <h5><spring:message code="brand.desc"/></h5>
    </div>
   </div>

  <div class="row">
    <div class="col-md-8 col-md-offset-2">
      <h3 class="text-center"><spring:message code="leagues"/></h3>
        <%@include file="leagues.jsp"%>
    </div>
  </div>

  <div class="row">
    <div class="col-md-8 col-md-offset-2">
      <h3 class="text-center"><spring:message code="last.results"/></h3>
        <c:set var="games" value="${results}"/>
        <c:set var="show_lg" value="true"/>
        <c:set var="hide_score" value="true"/>
        <%@include file="view_games.jsp"%>
    </div>
  </div>  

  <div class="row">
    <div class="col-md-8 col-md-offset-2">
      <h3 class="text-center"><spring:message code="coming.games"/></h3>
        <c:set var="games" value="${cgames}"/>
        <c:set var="show_lg" value="true"/>
        <c:set var="hide_score" value="true"/>
        <%@include file="view_games.jsp"%>
    </div>
  </div>  



</div>



<%@include file="footer.jsp"%>


