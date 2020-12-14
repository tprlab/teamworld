<c:if test="${not empty error}">
  <div class="alert alert-danger" role="alert">
      <button type="button" class="close" data-dismiss="alert" aria-label="Close">
        <span aria-hidden="true">&times;</span>
      </button>
      <spring:message code="${error}"/>
      <c:if test="${errext != null}">
          (${errext})
      </c:if>
  </div>
</c:if>

<c:if test="${not empty umsg}">
  <div class="alert alert-success" role="alert">
      <button type="button" class="close" data-dismiss="alert" aria-label="Close">
        <span aria-hidden="true">&times;</span>
      </button>
      <spring:message code="${umsg}"/>
  </div>
</c:if>

