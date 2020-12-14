<form:form class="form-horizontal" action="/_save/game" commandName="ng" method="POST">
    <c:if test="${not empty divName}">
        <input type="hidden" name="return" value="div"/>
        <input type="hidden" name="divName" value="${divName}"/>
    </c:if>
    <input type="hidden" name="tourName" value="${tr.uname}"/>
    <form:hidden path="id" value="${g.id}"/>
 <div class="form-group">
  <label for="dt-${g.id}" class="control-label col-md-3"><spring:message code="date"/></label>
  <div class="col-md-9">
    <div class='input-group date datepicker' id="dt-${g.id}">
      <form:input path="strDate" type="text" class="form-control" data-date-format="DD MMM YY ddd" value="${g.strDate}"/>
       <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span>
    </div>
  </div>
 </div>

 <div class="form-group">
  <label for="tm-${g.id}" class="control-label col-md-3"><spring:message code="time"/></label>
  <div class="col-md-9">
        <c:set var="hh" value="${fn:substring(g.strTime, 0, 2)}"/> 
        <fmt:parseNumber var="hi" type="number" value="${hh}" />

        <c:set var="mm" value="${fn:substring(g.strTime, 3, 4)}"/> 
        <c:set var="ap" value="${fn:substring(g.strTime, 6, 7)}"/> 
        
         <c:choose>
           <c:when test="${mm == '0'}">
               <c:set var="sel0" value="selected"/>
           </c:when>
           <c:when test="${mm == '1'}">
               <c:set var="sel15" value="selected"/>
           </c:when>
           <c:when test="${mm == '3'}">
               <c:set var="sel30" value="selected"/>
           </c:when>
           <c:when test="${mm == '4'}">
               <c:set var="sel45" value="selected"/>
           </c:when>
         </c:choose>

         <c:choose>
           <c:when test="${ap == 'A'}">
               <c:set var="sel_a" value="selected"/>
           </c:when>
           <c:otherwise>
               <c:set var="sel_p" value="selected"/>
           </c:otherwise>
         </c:choose>




        <table><tr>
        <td class="col-md-4"><select name="hour" class="select form-control">
            <c:forEach var="i" begin="1" end="12">
                <c:choose>
                <c:when test="${i == hi}">
                    <c:set var="sel" value="selected"/>
                </c:when>
                <c:otherwise>
                    <c:set var="sel" value=""/>
                </c:otherwise>
                </c:choose>
                <option value="${i}" ${sel}>${i}</option>
            </c:forEach>
        </select></td>
        
        <td class="col-md-4"><select name="minute" class="select form-control">
            <option value="0" ${sel0}>00</option>
            <option value="15" ${sel15}>15</option>
            <option value="30" ${sel30}>30</option>
            <option value="45" ${sel45}>45</option>
        </select></td>

        <td class="col-md-4"><select name="ampm" class="select form-control">
            <option value="1" ${sel_p}>PM</option>
            <option value="0" ${sel_a}>AM</option>
        </select></td>
        </tr></table>
  </div>
 </div>

 <div class="form-group">
  <label class="control-label col-md-3"><spring:message code="score"/></label>
  <table>
  <tr><td>
     <div class="input-group">
        <span class="input-group-btn">
          <button class="btn btn-primary sc-btn-1" type="button" id="btn1-${g.id}"><strong>+</strong></button>
        </span>
        <form:input path="strScore1" class="form-control" value="${g.strScore1}" id="scin1-${g.id}" type="text"/>
      </div>
  </td>
  <td>&nbsp;:&nbsp;</td>
  <td>
     <div class="input-group">
        <form:input path="strScore2" type="text" class="form-control" value="${g.strScore2}" id="scin2-${g.id}"/>
        <span class="input-group-btn">
          <button class="btn btn-primary sc-btn-2" type="button" id="btn2-${g.id}"><strong>+</strong></button>
        </span>
      </div></td>
  </div>
   </tr></table>
 </div>
 

 <div class="form-group">
  <label for="det-${g.id}" class="control-label col-md-3"><spring:message code="${tr.league.sport.fieldName}"/></label>
  <div class="col-md-4"><form:input path="court" id="det-${g.id}" type="number" class="form-control" value="${g.court}"/></div>
 </div>

 <div class="form-group">
  <label for="det-${g.id}" class="control-label col-md-3"><spring:message code="delete"/></label>
  <div class="col-md-9"><form:checkbox path="removed" id="del-${g.id}"/></div>
 </div>


 <div class="form-group">
       &nbsp;<button type="submit" class="btn btn-primary center-block"><spring:message code="save"/></button>
 </div>
</form:form>
