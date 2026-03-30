<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Панель работодателя - Трамплин</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
<jsp:include page="/jsp/header.jsp" />

<main class="container">
    <h1>Панель работодателя</h1>

    <!-- Информация о компании -->
    <div class="company-info">
        <div class="cover-container">
            <img class="cover-image" src="${pageContext.request.contextPath}${not empty company.coverUrl ? company.coverUrl : '/images/covers/default/default_company_cover.jpg'}" alt="Обложка компании">
        </div>
        <c:if test="${not empty company.logoUrl}">
            <img class="company-logo" src="${pageContext.request.contextPath}${company.logoUrl}" alt="Логотип ${company.name}">
        </c:if>
        <p><strong>Компания:</strong> ${company.name}</p>
        <p><strong>Статус:</strong>
            <c:choose>
                <c:when test="${company.moderationStatus == 'pending'}">
                    <span class="status-badge status-pending">На модерации</span>
                </c:when>
                <c:when test="${company.moderationStatus == 'approved'}">
                    <span class="status-badge status-accepted">Подтверждена</span>
                </c:when>
                <c:when test="${company.moderationStatus == 'rejected'}">
                    <span class="status-badge status-rejected">Отклонена</span>
                    <c:if test="${not empty company.rejectionReason}">
                        <br><strong>Причина:</strong> ${company.rejectionReason}
                    </c:if>
                </c:when>
                <c:otherwise>
                    <span class="status-badge">${company.moderationStatus}</span>
                </c:otherwise>
            </c:choose>
        </p>
        <c:if test="${company.moderationStatus == 'rejected'}">
            <form action="<%= request.getContextPath() %>/company" method="post" class="inline-form">
                <input type="hidden" name="action" value="resubmit">
                <input type="hidden" name="companyId" value="${company.id}">
                <button type="submit" class="btn btn-primary btn-sm">Отправить на повторную модерацию</button>
            </form>
        </c:if>
        <c:if test="${not empty company.description}">
            <p><strong>Описание:</strong> ${company.description}</p>
        </c:if>
        <a href="<%= request.getContextPath() %>/company" class="btn btn-outline btn-sm">Редактировать компанию</a>
    </div>

    <!-- Список вакансий -->
    <div class="dashboard-section">
        <div class="section-header">
            <h2>Мои вакансии</h2>
            <a href="<%= request.getContextPath() %>/employer/opportunity?action=create" class="btn btn-primary">+ Создать вакансию</a>
        </div>
        <c:forEach var="opp" items="${opportunities}">
            <div class="opportunity-item clickable-card" data-url="<%= request.getContextPath() %>/opportunity?id=${opp.id}">
                <input type="hidden" class="opp-id" value="${opp.id}">
                <h3>${opp.title}</h3>
                <p>Тип: ${opp.type}, формат: ${opp.format}, местоположение: ${opp.location}</p>
                <p>Статус модерации:
                    <c:choose>
                        <c:when test="${opp.moderationStatus == 'pending'}">
                            <span class="status-badge status-pending">На модерации</span>
                        </c:when>
                        <c:when test="${opp.moderationStatus == 'approved'}">
                            <span class="status-badge status-accepted">Одобрена</span>
                        </c:when>
                        <c:when test="${opp.moderationStatus == 'rejected'}">
                            <span class="status-badge status-rejected">Отклонена</span>
                            <c:if test="${not empty opp.rejectionReason}">
                                <br><strong>Причина:</strong> ${opp.rejectionReason}
                            </c:if>
                        </c:when>
                    </c:choose>
                </p>
                <div class="action-buttons">
                    <a href="<%= request.getContextPath() %>/employer/opportunity?action=edit&id=${opp.id}" class="btn btn-outline btn-sm">Редактировать</a>
                    <a href="<%= request.getContextPath() %>/employer/opportunity?action=delete&id=${opp.id}" class="btn btn-outline btn-sm confirm-delete">Удалить</a>
                    <c:if test="${opp.moderationStatus == 'rejected'}">
                        <form action="<%= request.getContextPath() %>/employer/opportunity" method="post" class="inline-form">
                            <input type="hidden" name="action" value="resubmit">
                            <input type="hidden" name="opportunityId" value="${opp.id}">
                            <button type="submit" class="btn btn-primary btn-sm">Отправить на повторную модерацию</button>
                        </form>
                    </c:if>
                </div>
                <div class="applications">
                    <a href="<%= request.getContextPath() %>/employer/applications?opportunityId=${opp.id}" class="btn btn-outline btn-sm">
                        Отклики (${opp.applications.size()})
                    </a>
                </div>
                <p>Срок действия: <c:choose>
                    <c:when test="${not empty opp.expiresAt}">${opp.expiresAt}</c:when>
                    <c:otherwise>Бессрочно</c:otherwise>
                </c:choose></p>
            </div>
        </c:forEach>
        <c:if test="${empty opportunities}">
            <p>У вас пока нет вакансий.</p>
        </c:if>
    </div>
</main>
<jsp:include page="/jsp/footer.jsp" />
</body>
</html>