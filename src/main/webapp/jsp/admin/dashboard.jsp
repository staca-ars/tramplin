<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="dao.CompanyDAO" %>
<%@ page import="models.Company" %>
<%@ page import="models.User" %>
<html>
<head>
    <title>Панель администратора - Трамплин</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
<jsp:include page="/jsp/header.jsp" />

<main class="container">
    <h1>Панель администратора</h1>

    <div class="dashboard-grid">
        <!-- Левая колонка -->
        <div>
            <!-- Компании на модерацию -->
            <div class="section">
                <h2 class="section-title">Компании на модерацию</h2>
                <div class="scrollable-list">
                    <c:forEach var="company" items="${pendingCompanies}">
                        <div class="opportunity-item">
                            <h3>
                                <a href="<%= request.getContextPath() %>/company/${company.id}" target="_blank">${company.name}</a>
                            </h3>
                            <p>Владелец ID: ${company.ownerId}</p>
                            <p>Описание: ${company.description}</p>
                            <div class="action-buttons">
                                <form action="<%= request.getContextPath() %>/admin/dashboard" method="post" class="inline-form">
                                    <input type="hidden" name="action" value="approve">
                                    <input type="hidden" name="entity" value="company">
                                    <input type="hidden" name="id" value="${company.id}">
                                    <button type="submit" class="btn btn-primary btn-sm">Одобрить</button>
                                </form>
                                <form action="<%= request.getContextPath() %>/admin/dashboard" method="post" class="inline-form">
                                    <input type="hidden" name="action" value="reject">
                                    <input type="hidden" name="entity" value="company">
                                    <input type="hidden" name="id" value="${company.id}">
                                    <input type="text" name="reason" placeholder="Причина отказа" class="form-control reason-input" required>
                                    <button type="submit" class="btn btn-outline btn-sm">Отклонить</button>
                                </form>
                                <form action="<%= request.getContextPath() %>/admin/dashboard" method="post" class="inline-form">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="entity" value="company">
                                    <input type="hidden" name="id" value="${company.id}">
                                    <button type="submit" class="btn btn-outline btn-sm confirm-delete">Удалить</button>
                                </form>
                            </div>
                        </div>
                    </c:forEach>
                    <c:if test="${empty pendingCompanies}">
                        <p>Нет компаний на модерацию.</p>
                    </c:if>
                </div>
            </div>

            <!-- Вакансии на модерацию -->
            <div class="section">
                <h2 class="section-title">Вакансии на модерацию</h2>
                <div class="scrollable-list">
                    <c:forEach var="opp" items="${pendingOpportunities}">
                        <div class="opportunity-item">
                            <h3>
                                <a href="<%= request.getContextPath() %>/opportunity?id=${opp.id}" target="_blank">${opp.title}</a>
                            </h3>
                            <p>Тип: ${opp.type}, формат: ${opp.format}, местоположение: ${opp.location}</p>
                            <p>Статус модерации: <span class="status-badge status-pending">На модерации</span></p>
                            <div class="action-buttons">
                                <form action="<%= request.getContextPath() %>/admin/dashboard" method="post" class="inline-form">
                                    <input type="hidden" name="action" value="approve">
                                    <input type="hidden" name="entity" value="opportunity">
                                    <input type="hidden" name="id" value="${opp.id}">
                                    <button type="submit" class="btn btn-primary btn-sm">Одобрить</button>
                                </form>
                                <form action="<%= request.getContextPath() %>/admin/dashboard" method="post" class="inline-form">
                                    <input type="hidden" name="action" value="reject">
                                    <input type="hidden" name="entity" value="opportunity">
                                    <input type="hidden" name="id" value="${opp.id}">
                                    <input type="text" name="reason" placeholder="Причина отказа" class="form-control reason-input" required>
                                    <button type="submit" class="btn btn-outline btn-sm">Отклонить</button>
                                </form>
                                <form action="<%= request.getContextPath() %>/admin/dashboard" method="post" class="inline-form">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="entity" value="opportunity">
                                    <input type="hidden" name="id" value="${opp.id}">
                                    <button type="submit" class="btn btn-outline btn-sm confirm-delete">Удалить</button>
                                </form>
                            </div>
                        </div>
                    </c:forEach>
                    <c:if test="${empty pendingOpportunities}">
                        <p>Нет вакансий на модерацию.</p>
                    </c:if>
                </div>
            </div>

            <!-- Все вакансии -->
            <div class="section">
                <h2 class="section-title">Все вакансии</h2>
                <div class="scrollable-list">
                    <c:forEach var="opp" items="${allOpportunities}">
                        <div class="opportunity-item">
                            <h3>
                                <a href="<%= request.getContextPath() %>/opportunity?id=${opp.id}" target="_blank">${opp.title}</a>
                            </h3>
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
                                    </c:when>
                                </c:choose>
                            </p>
                            <div class="action-buttons">
                                <form action="<%= request.getContextPath() %>/admin/dashboard" method="post" class="inline-form">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="entity" value="opportunity">
                                    <input type="hidden" name="id" value="${opp.id}">
                                    <button type="submit" class="btn btn-outline btn-sm confirm-delete">Удалить</button>
                                </form>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>

        <!-- Правая колонка: управление пользователями -->
        <div>
            <div class="section">
                <h2 class="section-title">Управление пользователями</h2>
                <div class="scrollable-list">
                    <c:forEach var="u" items="${allUsers}">
                        <div class="connection-item">
                            <div>
                                <c:choose>
                                    <c:when test="${u.role == 'seeker'}">
                                        <strong><a href="<%= request.getContextPath() %>/profile?id=${u.id}" target="_blank">${u.name}</a></strong>
                                    </c:when>
                                    <c:when test="${u.role == 'employer'}">
                                        <%
                                            User empUser = (User) pageContext.getAttribute("u");
                                            CompanyDAO compDAO = new CompanyDAO();
                                            Company empCompany = compDAO.findByOwnerId(empUser.getId());
                                            if (empCompany != null) {
                                        %>
                                        <strong><a href="<%= request.getContextPath() %>/company/<%= empCompany.getId() %>" target="_blank">${u.name}</a></strong>
                                        <% } else { %>
                                        <strong>${u.name}</strong>
                                        <% } %>
                                    </c:when>
                                    <c:otherwise>
                                        <strong>${u.name}</strong>
                                    </c:otherwise>
                                </c:choose>
                                (${u.email}) - ${u.role}
                                <c:if test="${u.blocked}">
                                    <span class="status-badge status-pending">Заблокирован</span>
                                </c:if>
                            </div>
                            <div class="action-buttons">
                                <c:choose>
                                    <c:when test="${u.blocked}">
                                        <form action="<%= request.getContextPath() %>/admin/dashboard" method="post" class="inline-form">
                                            <input type="hidden" name="action" value="unblock">
                                            <input type="hidden" name="entity" value="user">
                                            <input type="hidden" name="id" value="${u.id}">
                                            <button type="submit" class="btn btn-primary btn-sm">Разблокировать</button>
                                        </form>
                                    </c:when>
                                    <c:otherwise>
                                        <form action="<%= request.getContextPath() %>/admin/dashboard" method="post" class="inline-form">
                                            <input type="hidden" name="action" value="block">
                                            <input type="hidden" name="entity" value="user">
                                            <input type="hidden" name="id" value="${u.id}">
                                            <button type="submit" class="btn btn-outline btn-sm">Заблокировать</button>
                                        </form>
                                    </c:otherwise>
                                </c:choose>
                                <form action="<%= request.getContextPath() %>/admin/dashboard" method="post" class="inline-form">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="entity" value="user">
                                    <input type="hidden" name="id" value="${u.id}">
                                    <button type="submit" class="btn btn-outline btn-sm confirm-delete">Удалить</button>
                                </form>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>
</main>

<footer class="footer"></footer>
</body>
</html>