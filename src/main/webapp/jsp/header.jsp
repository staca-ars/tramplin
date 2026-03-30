<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="models.User" %>
<%
    User currentUser = (User) session.getAttribute("user");
    String uri = request.getRequestURI();
    String contextPath = request.getContextPath();
    boolean isMainPage = uri.equals(contextPath) || uri.equals(contextPath + "/") || uri.equals(contextPath + "/index.jsp");
%>
<header class="header">
    <div class="container">
        <a href="<%= request.getContextPath() %>/" class="logo">Трамплин</a>
        <nav class="nav">
            <% if (currentUser == null) { %>
            <a href="<%= request.getContextPath() %>/jsp/login.jsp">Вход</a>
            <a href="<%= request.getContextPath() %>/jsp/register.jsp">Регистрация</a>
            <% } else {
                String role = currentUser.getRole();
                if ("seeker".equals(role)) { %>
            <a href="<%= request.getContextPath() %>/profile">Профиль</a>
            <% } else if ("employer".equals(role)) { %>
            <a href="<%= request.getContextPath() %>/employer/dashboard">Моя компания</a>
            <% } else if ("admin".equals(role)) { %>
            <a href="<%= request.getContextPath() %>/admin/dashboard">Админ-панель</a>
            <% } %>
            <a href="<%= request.getContextPath() %>/auth?action=logout">Выход</a>
            <% } %>
            <% if (isMainPage) { %>
            <a href="#" id="searchToggleBtn" class="search-toggle">Поиск</a>
            <% } %>
            <a href="#" id="favoritesDrawerBtn" class="favorites-toggle">
                <i class="fas fa-heart"></i>
                <span id="favoritesCount" class="favorites-count">0</span>
            </a>
        </nav>
    </div>
</header>

<!-- Боковая панель избранного -->
<div id="favoritesDrawer" class="drawer">
    <div class="drawer-header">
        <h3>Избранное</h3>
        <button id="closeDrawerBtn" class="close-drawer">&times;</button>
    </div>
    <div id="favoritesList" class="drawer-content">
        <!-- сюда динамически загружаются вакансии -->
    </div>
</div>
<div id="drawerOverlay" class="drawer-overlay"></div>