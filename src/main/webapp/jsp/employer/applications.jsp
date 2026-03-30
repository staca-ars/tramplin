<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Отклики на вакансию ${opportunity.title} - Трамплин</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
<jsp:include page="/jsp/header.jsp" />

<main class="container">
    <h1>Отклики на вакансию: ${opportunity.title}</h1>
    <a href="<%= request.getContextPath() %>/employer/dashboard" class="back-link">&larr; Назад к дашборду</a>

    <div class="dashboard-section">
        <c:forEach var="app" items="${applications}">
            <div class="application-item">
                <div class="applicant-info">
                    <div class="applicant-avatar">
                        <c:choose>
                            <c:when test="${not empty app.userPhotoUrl}">
                                <img src="${pageContext.request.contextPath}${app.userPhotoUrl}" alt="Аватар">
                            </c:when>
                            <c:otherwise>
                                <i class="fas fa-user"></i>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="applicant-details">
                        <div class="applicant-name">
                            <a href="<%= request.getContextPath() %>/profile?id=${app.userId}" target="_blank">${app.userName != null ? app.userName : app.userEmail}</a>
                        </div>
                        <div class="applicant-email">${app.userEmail}</div>
                        <div class="applicant-date">Отклик от ${app.createdAt}</div>
                    </div>
                </div>
                <div class="application-status">
                    <p><strong>Статус:</strong>
                        <span class="status-badge status-${app.status}">${app.status}</span>
                    </p>
                    <form action="<%= request.getContextPath() %>/application" method="post" class="inline-form">
                        <input type="hidden" name="action" value="updateStatus">
                        <input type="hidden" name="applicationId" value="${app.id}">
                        <input type="hidden" name="opportunityId" value="${opportunity.id}">
                        <select name="status">
                            <option value="pending" ${app.status == 'pending' ? 'selected' : ''}>В ожидании</option>
                            <option value="accepted" ${app.status == 'accepted' ? 'selected' : ''}>Принять</option>
                            <option value="rejected" ${app.status == 'rejected' ? 'selected' : ''}>Отклонить</option>
                        </select>
                        <button type="submit" class="btn btn-primary btn-sm">Изменить</button>
                    </form>
                </div>
            </div>
        </c:forEach>
        <c:if test="${empty applications}">
            <p>На эту вакансию пока нет откликов.</p>
        </c:if>
    </div>
</main>

<footer class="footer"></footer>
</body>
</html>