<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Редактирование компании - Трамплин</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
<jsp:include page="/jsp/header.jsp" />

<main class="container">
    <div class="form-card max-width-600">
        <h2 class="form-title">Редактирование компании</h2>
        <%
            String error = (String) session.getAttribute("error");
            if (error != null) {
                session.removeAttribute("error");
        %>
        <div class="error-message"><i class="fas fa-exclamation-circle"></i> <%= error %></div>
        <% } %>
        <form method="post" action="<%= request.getContextPath() %>/company" enctype="multipart/form-data">
            <input type="hidden" name="companyId" value="${company.id}">
            <div class="form-group">
                <label>Название компании *</label>
                <input type="text" name="name" class="form-control" value="${company.name}" required>
            </div>
            <div class="form-group">
                <label>Описание</label>
                <textarea name="description" class="form-control" rows="4">${company.description}</textarea>
            </div>
            <div class="form-group">
                <label>Email</label>
                <input type="email" name="email" class="form-control" value="${company.email}">
            </div>
            <div class="form-group">
                <label>Телефон</label>
                <input type="text" name="phone" class="form-control" value="${company.phone}">
            </div>
            <div class="form-group">
                <label>Сайт</label>
                <input type="url" name="website" class="form-control" value="${company.website}">
            </div>
            <div class="form-group">
                <label>Логотип компании</label>
                <input type="file" name="logoFile" accept="image/*" class="form-control">
                <small>Загрузите изображение (JPG, PNG, GIF). Максимум 5 МБ.</small>
                <c:if test="${not empty company.logoUrl}">
                    <div class="current-logo">
                        <p>Текущий логотип:</p>
                        <img class="logo-preview" src="<%= request.getContextPath() %>${company.logoUrl}" alt="Текущий логотип">
                    </div>
                </c:if>
            </div>
            <button type="submit" class="btn btn-primary">Сохранить</button>
            <a href="<%= request.getContextPath() %>/employer/dashboard" class="btn btn-outline">Отмена</a>
        </form>
    </div>
</main>
<jsp:include page="/jsp/footer.jsp" />
</body>
</html>