<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>${opportunity.title} - Трамплин</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
<jsp:include page="/jsp/header.jsp" />

<main class="container">
    <c:if test="${param.applied != null}">
        <div class="success-message">Вы успешно откликнулись на вакансию!</div>
    </c:if>
    <c:if test="${param.cancelled != null}">
        <div class="success-message">Вы отозвали свой отклик.</div>
    </c:if>
    <c:if test="${param.error != null}">
        <div class="error-message">Произошла ошибка. Попробуйте позже.</div>
    </c:if>
    <div class="opportunity-detail max-width-800 mx-auto">
        <div class="cover-container mb-1">
            <img class="cover-image" src="${pageContext.request.contextPath}${not empty opportunity.coverUrl ? opportunity.coverUrl : '/images/covers/default/default_opportunity_cover.jpg'}" alt="Обложка вакансии">
        </div>
        <div class="company-info-row">
            <c:if test="${not empty company.logoUrl}">
                <img class="company-logo-small" src="${pageContext.request.contextPath}${company.logoUrl}" alt="Логотип">
            </c:if>
            <p><strong>Компания:</strong> <a href="${pageContext.request.contextPath}/company/${company.id}" class="company-link">${company.name}</a></p>
        </div>
        <h1>${opportunity.title}</h1>
        <div class="meta">
            <p><strong>Тип:</strong>
                <c:choose>
                    <c:when test="${opportunity.type == 'job'}">Вакансия</c:when>
                    <c:when test="${opportunity.type == 'internship'}">Стажировка</c:when>
                    <c:when test="${opportunity.type == 'event'}">Мероприятие</c:when>
                    <c:otherwise>${opportunity.type}</c:otherwise>
                </c:choose>
            </p>
            <p><strong>Формат:</strong>
                <c:choose>
                    <c:when test="${opportunity.format == 'remote'}">Удалённо</c:when>
                    <c:when test="${opportunity.format == 'office'}">В офисе</c:when>
                    <c:when test="${opportunity.format == 'hybrid'}">Гибрид</c:when>
                    <c:otherwise>${opportunity.format}</c:otherwise>
                </c:choose>
            </p>
            <p><strong>Место:</strong> ${opportunity.location}</p>
            <p><strong>Дата публикации:</strong> ${opportunity.createdAt}</p>
            <c:if test="${not empty opportunity.expiresAt}">
                <p><strong>Срок действия до:</strong> ${opportunity.expiresAt}</p>
            </c:if>
        </div>
        <div class="description">
            <h2>Описание</h2>
            <p>${opportunity.description}</p>
        </div>
        <div class="contacts">
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
        <div class="tags">
            <h2>Теги</h2>
            <div class="card-tags">
                <c:forEach var="tag" items="${tags}">
                    <a href="<%= request.getContextPath() %>/?tag=${tag.name}" class="tag">${tag.name}</a>
                </c:forEach>
                <c:if test="${empty tags}">
                    <p>Теги не указаны</p>
                </c:if>
            </div>
        </div>
        <!-- Кнопка отклика/отзыва для авторизованных соискателей -->
        <c:if test="${sessionScope.user != null and sessionScope.user.role == 'seeker'}">
            <c:choose>
                <c:when test="${hasApplied}">
                    <form action="<%= request.getContextPath() %>/application" method="post">
                        <input type="hidden" name="action" value="cancel">
                        <input type="hidden" name="opportunityId" value="${opportunity.id}">
                        <button type="submit" class="btn btn-outline">Отозвать отклик</button>
                    </form>
                </c:when>
                <c:otherwise>
                    <form action="<%= request.getContextPath() %>/application" method="post">
                        <input type="hidden" name="action" value="apply">
                        <input type="hidden" name="opportunityId" value="${opportunity.id}">
                        <button type="submit" class="btn btn-primary">Откликнуться</button>
                    </form>
                </c:otherwise>
            </c:choose>
        </c:if>
    </div>
</main>
<jsp:include page="/jsp/footer.jsp" />
</body>
</html>