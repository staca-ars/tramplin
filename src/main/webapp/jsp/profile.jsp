<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="models.User" %>
<html>
<head>
    <title>Профиль пользователя - Трамплин</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
<jsp:include page="/jsp/header.jsp" />

<main class="container">
    <div class="profile-view">
        <!-- Обложка (background-image динамический, оставляем inline) -->
        <div class="profile-cover" style="background-image: url('${pageContext.request.contextPath}${not empty profile.coverUrl ? profile.coverUrl : '/images/covers/default/default_user_cover.png'}');">
        </div>
        <div class="profile-main">
            <div class="profile-avatar">
                <c:choose>
                    <c:when test="${not empty profile.photoUrl}">
                        <img src="${pageContext.request.contextPath}${profile.photoUrl}" alt="Фото">
                    </c:when>
                    <c:otherwise>
                        <i class="fas fa-user"></i>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="profile-info">
                <div class="profile-name">${profile.fullName != null ? profile.fullName : profileUser.name}</div>
                <div class="profile-email">${profileUser.email}</div>
                <c:if test="${sessionScope.user.id == profileUser.id}">
                    <a href="<%= request.getContextPath() %>/jsp/seeker/dashboard" class="btn btn-outline btn-sm">Редактировать профиль</a>
                </c:if>
            </div>
        </div>

        <div class="profile-details">
            <div class="profile-detail">
                <div class="detail-label"><i class="fas fa-university"></i> ВУЗ</div>
                <div class="detail-value">${profile.university != null ? profile.university : 'Не указано'}</div>
            </div>
            <div class="profile-detail">
                <div class="detail-label"><i class="fas fa-calendar-alt"></i> Курс / Год выпуска</div>
                <div class="detail-value">
                    <c:if test="${profile.course > 0}">${profile.course} курс</c:if>
                    <c:if test="${profile.graduationYear > 0}"><c:if test="${profile.course > 0}">, </c:if>${profile.graduationYear} г.</c:if>
                    <c:if test="${profile.course == null && profile.graduationYear == null}">Не указано</c:if>
                </div>
            </div>
            <div class="profile-detail">
                <div class="detail-label"><i class="fas fa-code"></i> Навыки</div>
                <div class="detail-value">${profile.skills != null ? profile.skills : 'Не указано'}</div>
            </div>
            <div class="profile-detail">
                <div class="detail-label"><i class="fas fa-folder-open"></i> Проекты</div>
                <div class="detail-value">${profile.projects != null ? profile.projects : 'Не указано'}</div>
            </div>
            <div class="profile-detail">
                <div class="detail-label"><i class="fab fa-github"></i> GitHub</div>
                <div class="detail-value">
                    <c:choose>
                        <c:when test="${not empty profile.github}">
                            <a href="${profile.github}" target="_blank">${profile.github}</a>
                        </c:when>
                        <c:otherwise>Не указано</c:otherwise>
                    </c:choose>
                </div>
            </div>
            <div class="profile-detail">
                <div class="detail-label"><i class="fas fa-briefcase"></i> Портфолио</div>
                <div class="detail-value">
                    <c:choose>
                        <c:when test="${not empty profile.portfolio}">
                            <a href="${profile.portfolio}" target="_blank">${profile.portfolio}</a>
                        </c:when>
                        <c:otherwise>Не указано</c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <!-- Только для владельца профиля -->
        <c:if test="${isOwner}">
            <!-- Блок откликов (исправлено название: было "Мои отклики") -->
            <div class="profile-section">
                <h2 class="section-title">Мои отклики</h2>
                <c:forEach var="app" items="${applications}">
                    <c:choose>
                        <c:when test="${not empty app.opportunity and app.opportunity.moderationStatus == 'approved'}">
                            <div class="opportunity-card clickable-card" data-url="<%= request.getContextPath() %>/opportunity?id=${app.opportunity.id}">
                                <h3 class="card-title">${app.opportunity.title}</h3>
                                <div class="card-meta">
                                    <i class="fas fa-building"></i>
                                    <a href="<%= request.getContextPath() %>/company/${app.opportunity.companyId}" class="stop-propagation">${app.opportunity.companyName}</a>
                                    <i class="fas fa-calendar-alt"></i> ${app.createdAt}
                                </div>
                            </div>
                        </c:when>
                        <c:when test="${not empty app.opportunity}">
                            <div class="opportunity-card moderation-card">
                                <h3 class="card-title">${app.opportunity.title}</h3>
                                <div class="card-meta">
                                    <i class="fas fa-building"></i> ${app.opportunity.companyName}
                                    <i class="fas fa-calendar-alt"></i> ${app.createdAt}
                                </div>
                                <p class="text-warning">Вакансия на модерации и пока недоступна.</p>
                                <form method="post" action="<%= request.getContextPath() %>/profile" class="d-inline">
                                    <input type="hidden" name="action" value="deleteResponse">
                                    <input type="hidden" name="opportunityId" value="${app.opportunityId}">
                                    <button type="submit" class="btn btn-outline btn-small">Удалить отклик</button>
                                </form>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="opportunity-card deleted-card">
                                <h3 class="card-title">Вакансия удалена</h3>
                                <div class="card-meta">
                                    <i class="fas fa-building"></i> Недоступно
                                    <i class="fas fa-calendar-alt"></i> ${app.createdAt}
                                </div>
                                <form method="post" action="<%= request.getContextPath() %>/profile" class="d-inline">
                                    <input type="hidden" name="action" value="deleteResponse">
                                    <input type="hidden" name="opportunityId" value="${app.opportunityId}">
                                    <button type="submit" class="btn btn-outline btn-small">Удалить отклик</button>
                                </form>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
                <c:if test="${empty applications}">
                    <p>Вы ещё не откликались на вакансии</p>
                </c:if>
            </div>

            <!-- Блок контактов -->
            <div class="profile-section">
                <h2 class="section-title">Мои контакты</h2>
                <div>
                    <c:forEach var="contact" items="${connections}">
                        <div class="connection-item">
                            <span>${contact.name} (${contact.email})</span>
                            <form method="post" action="<%= request.getContextPath() %>/profile" class="d-inline">
                                <input type="hidden" name="action" value="removeConnection">
                                <input type="hidden" name="userId" value="${contact.id}">
                                <button type="submit" class="btn btn-outline btn-small confirm-delete">Удалить</button>
                            </form>
                        </div>
                    </c:forEach>
                    <c:if test="${empty connections}">
                        <p>У вас пока нет контактов</p>
                    </c:if>
                </div>
                <form method="post" action="<%= request.getContextPath() %>/profile" class="mt-1">
                    <input type="hidden" name="action" value="addConnection">
                    <div class="flex-row gap-05">
                        <input type="email" name="email" placeholder="Email пользователя" class="form-control" required>
                        <button type="submit" class="btn btn-primary btn-small">Добавить</button>
                    </div>
                </form>
            </div>
        </c:if>
    </div>
</main>

<footer class="footer"></footer>

<script>
    // Обработчик клика по карточкам с классом .clickable-card
    document.querySelectorAll('.clickable-card').forEach(card => {
        card.addEventListener('click', function(e) {
            // Если клик был по ссылке или внутри неё – не переходим
            if (e.target.closest('.stop-propagation')) return;
            const url = this.getAttribute('data-url');
            if (url) location.href = url;
        });
    });
</script>
</body>
</html>