<%--
  seekers Dashboard
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Личный кабинет соискателя - Трамплин</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <script src="<%= request.getContextPath() %>/js/main.js" defer></script>
</head>
<body>
<jsp:include page="/jsp/header.jsp" />

<main class="container">
    <h1 class="section-title">Личный кабинет соискателя</h1>
    <p>Добро пожаловать, ${sessionScope.user.name}!</p>

    <%
        String error = (String) session.getAttribute("error");
        if (error != null) {
            session.removeAttribute("error");
    %>
    <div class="error-message"><i class="fas fa-exclamation-circle"></i> <%= error %></div>
    <% } %>

    <div class="dashboard-grid">
        <!-- Левая колонка: профиль и настройки -->
        <div class="profile-card">
            <h2>Мой профиль</h2>
            <form id="profileForm" method="post" action="<%= request.getContextPath() %>/jsp/seeker/dashboard" enctype="multipart/form-data">
                <input type="hidden" name="action" value="updateProfile">
                <div class="form-group">
                    <label>ФИО</label>
                    <input type="text" name="fullName" class="form-control" value="${profile.fullName}">
                </div>
                <div class="form-group">
                    <label>ВУЗ</label>
                    <input type="text" name="university" class="form-control" value="${profile.university}">
                </div>
                <div class="form-group">
                    <label>Курс</label>
                    <input type="number" name="course" class="form-control" value="${profile.course}">
                </div>
                <div class="form-group">
                    <label>Год выпуска</label>
                    <input type="number" name="graduationYear" class="form-control" value="${profile.graduationYear}">
                </div>
                <div class="form-group">
                    <label>Навыки (через запятую)</label>
                    <textarea name="skills" class="form-control">${profile.skills}</textarea>
                </div>
                <div class="form-group">
                    <label>Проекты</label>
                    <textarea name="projects" class="form-control">${profile.projects}</textarea>
                </div>
                <div class="form-group">
                    <label>GitHub</label>
                    <input type="url" name="github" class="form-control" value="${profile.github}">
                </div>
                <div class="form-group">
                    <label>Портфолио</label>
                    <input type="url" name="portfolio" class="form-control" value="${profile.portfolio}">
                </div>
                <div class="form-group">
                    <label>Фото профиля</label>
                    <input type="file" name="photo" accept="image/*" class="form-control">
                    <c:if test="${not empty profile.photoUrl}">
                        <div class="current-photo">
                            <img src="${pageContext.request.contextPath}${profile.photoUrl}" alt="Фото" class="profile-photo-preview">
                        </div>
                    </c:if>
                </div>
                <button type="submit" class="btn btn-primary">Сохранить</button>
            </form>

            <hr>

            <h2>Приватность</h2>
            <form method="post" action="<%= request.getContextPath() %>/jsp/seeker/dashboard">
                <input type="hidden" name="action" value="updateVisibility">
                <div class="privacy-toggle">
                    <label>Кто может видеть мой профиль?</label>
                    <select name="visibility" class="form-control w-auto">
                        <option value="public" ${sessionScope.user.visibility == 'public' ? 'selected' : ''}>Все пользователи</option>
                        <option value="private" ${sessionScope.user.visibility == 'private' ? 'selected' : ''}>Только я и мои контакты</option>
                    </select>
                    <button type="submit" class="btn btn-primary btn-small">Применить</button>
                </div>
            </form>
        </div>
    </div>
</main>

<footer class="footer">
    <!-- как в index.jsp -->
</footer>
</body>
</html>