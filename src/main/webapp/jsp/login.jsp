<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Вход - Трамплин</title>
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
        <h2 class="form-title">Вход в систему</h2>
        <% if(request.getAttribute("error") != null) { %>
        <div class="error-message"><i class="fas fa-exclamation-circle"></i> <%= request.getAttribute("error") %></div>
        <% } %>
        <form method="post" action="<%= request.getContextPath() %>/auth">
            <input type="hidden" name="action" value="login">
            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" class="form-control" id="email" name="email" required>
            </div>
            <div class="form-group">
                <label for="password">Пароль</label>
                <input type="password" class="form-control" id="password" name="password" required>
            </div>
            <button type="submit" class="btn btn-primary btn-block">Войти</button>
        </form>
        <div class="form-footer">
            Нет аккаунта? <a href="<%= request.getContextPath() %>/jsp/register.jsp">Зарегистрироваться</a>
        </div>
    </div>
</main>
<jsp:include page="/jsp/footer.jsp" />
</body>
</html>