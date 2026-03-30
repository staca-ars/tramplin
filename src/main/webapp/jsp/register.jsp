<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Регистрация - Трамплин</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <script src="<%= request.getContextPath() %>/js/main.js" defer></script>
</head>
<body>
<header class="header">
    <div class="container">
        <a href="<%= request.getContextPath() %>/" class="logo">Трамплин</a>
    </div>
</header>

<main class="container">
    <a href="<%= request.getContextPath() %>/" class="back-link"><i class="fas fa-arrow-left"></i> На главную</a>

    <div class="form-card">
        <h2 class="form-title">Регистрация</h2>
        <% if(request.getAttribute("error") != null) { %>
        <div class="error-message"><i class="fas fa-exclamation-circle"></i> <%= request.getAttribute("error") %></div>
        <% } %>
        <form method="post" action="<%= request.getContextPath() %>/auth">
            <input type="hidden" name="action" value="register">
            <div class="form-group">
                <label for="name">Имя</label>
                <input type="text" class="form-control" id="name" name="name" required>
            </div>
            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" class="form-control" id="email" name="email" required>
            </div>
            <div class="form-group">
                <label for="password">Пароль</label>
                <input type="password" class="form-control" id="password" name="password" required>
            </div>
            <div class="form-group">
                <label for="role">Вы</label>
                <select class="form-control" id="role" name="role">
                    <option value="seeker">Соискатель</option>
                    <option value="employer">Работодатель</option>
                </select>
            </div>
            <button type="submit" class="btn btn-primary btn-block">Зарегистрироваться</button>
        </form>
        <div class="form-footer">
            Уже есть аккаунт? <a href="<%= request.getContextPath() %>/jsp/login.jsp">Войти</a>
        </div>
    </div>
</main>
<jsp:include page="/jsp/footer.jsp" />
</body>
</html>