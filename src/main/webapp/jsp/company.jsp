<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
  <title>${company.name} - Трамплин</title>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
  <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
<jsp:include page="/jsp/header.jsp" />

<main class="container">
  <div class="company-view">
    <div class="cover-container" style="margin-bottom: 1rem;">
      <img class="cover-image" src="${pageContext.request.contextPath}${not empty company.coverUrl ? company.coverUrl : '/images/covers/default/default_company_cover.jpg'}" alt="Обложка компании">
    </div>
    <c:if test="${not empty company.logoUrl}">
      <img class="company-logo" src="${pageContext.request.contextPath}${company.logoUrl}" alt="Логотип ${company.name}">
    </c:if>
    <h1>${company.name}</h1>
    <c:if test="${not empty company.description}">
      <div class="company-description">
        <h2>Описание</h2>
        <p>${company.description}</p>
      </div>
    </c:if>
    <div class="company-contacts">
      <h2>Контакты</h2>
      <c:if test="${not empty company.email}">
        <p><strong>Email:</strong> ${company.email}</p>
      </c:if>
      <c:if test="${not empty company.phone}">
        <p><strong>Телефон:</strong> ${company.phone}</p>
      </c:if>
      <c:if test="${not empty company.website}">
        <p><strong>Сайт:</strong> <a href="${company.website}" target="_blank">${company.website}</a></p>
      </c:if>
      <c:if test="${empty company.email and empty company.phone and empty company.website}">
        <p>Контактные данные не указаны.</p>
      </c:if>
    </div>

    <div class="company-vacancies">
      <h2>Вакансии компании</h2>
      <c:if test="${empty opportunities}">
        <p>У компании пока нет активных вакансий.</p>
      </c:if>
      <div class="opportunities-grid">
        <c:forEach var="opp" items="${opportunities}">
          <div class="opportunity-card">
            <h3 class="card-title">${opp.title}</h3>
            <div class="card-meta">
              <i class="fas fa-map-marker-alt"></i> ${opp.location}
              <i class="fas fa-briefcase"></i>
              <c:choose>
                <c:when test="${opp.type == 'job'}">Вакансия</c:when>
                <c:when test="${opp.type == 'internship'}">Стажировка</c:when>
                <c:when test="${opp.type == 'event'}">Мероприятие</c:when>
              </c:choose>
            </div>
            <div class="card-actions">
              <a href="<%= request.getContextPath() %>/opportunity?id=${opp.id}" class="btn btn-outline btn-small">Подробнее</a>
            </div>
          </div>
        </c:forEach>
      </div>
    </div>
  </div>
</main>
<jsp:include page="/jsp/footer.jsp" />
</body>
</html>