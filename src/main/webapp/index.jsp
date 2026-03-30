<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="models.User" %>
<html>
<head>
    <title>Трамплин — карьерная платформа</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
    <script src="<%= request.getContextPath() %>/js/main.js" defer></script>
    <script>
        var isLoggedIn = ${sessionScope.user != null};
        var favoriteIdsFromServer = ${favoriteIdsJson};
        var contextPath = "<%= request.getContextPath() %>";
    </script>
    <meta name="contextPath" content="<%= request.getContextPath() %>">
</head>
<body>
<jsp:include page="/jsp/header.jsp" />

<main class="container">
    <!-- Форма поиска -->
    <div id="searchPanel" class="search-panel">
        <form method="get" action="<%= request.getContextPath() %>/">
            <div class="flex-row gap-05">
                <input type="text" name="q" class="form-control" placeholder="Поиск по названию, описанию, компании..."
                       value="${not empty searchQuery ? searchQuery : ''}">
                <input type="text" name="tag" class="form-control" placeholder="Тег (например, Java)"
                       value="${not empty tag ? tag : ''}">
                <button type="submit" class="btn btn-primary">Найти</button>
                <a href="<%= request.getContextPath() %>/" class="btn btn-outline">Сбросить</a>
            </div>
        </form>
    </div>

    <!-- Карта -->
    <div id="map"></div>

    <!-- Список возможностей -->
    <h2 class="section-title">Последние возможности</h2>
    <div class="opportunities-grid" id="opportunities-list">
        <c:forEach var="opp" items="${opportunities}">
            <div class="opportunity-card" data-lat="${opp.lat}" data-lng="${opp.lng}" data-id="${opp.id}">
                <button class="favorite-star" data-id="${opp.id}">
                    <i class="far fa-star"></i>
                </button>
                <h3 class="card-title">${opp.title}</h3>
                <div class="card-meta">
                    <i class="fas fa-building"></i>
                    <a href="${pageContext.request.contextPath}/company/${opp.companyId}" class="company-link">${opp.companyName != null ? opp.companyName : 'Компания #' + opp.companyId}</a>
                    <i class="fas fa-map-marker-alt location-icon"></i> ${opp.location}
                </div>
                <div class="card-tags">
                    <c:if test="${not empty opp.tags}">
                        <c:forEach var="tag" items="${opp.tags.split(', ')}">
                            <a href="<%= request.getContextPath() %>/?tag=${tag}" class="tag">${tag}</a>
                        </c:forEach>
                    </c:if>
                </div>
                <div class="card-actions">
                    <a href="<%= request.getContextPath() %>/opportunity?id=${opp.id}" class="btn btn-outline btn-small">Подробнее</a>
                </div>
            </div>
        </c:forEach>
        <c:if test="${empty opportunities}">
            <p>Пока нет активных вакансий. Зайдите позже.</p>
        </c:if>
    </div>

    <!-- Блок для работодателя (только если роль employer) -->
    <% if (session.getAttribute("user") != null) {
        User user = (User) session.getAttribute("user");
        if ("employer".equals(user.getRole())) { %>
    <div class="employer-panel">
        <h3>Панель работодателя</h3>
        <p>Управляйте вакансиями, просматривайте отклики и редактируйте информацию о компании.</p>
        <a href="<%= request.getContextPath() %>/employer/dashboard" class="btn btn-primary">Перейти в дашборд</a>
        <a href="<%= request.getContextPath() %>/company" class="btn btn-outline">Редактировать компанию</a>
    </div>
    <% }
    } %>
</main>

<jsp:include page="/jsp/footer.jsp" />

<script>
    window.opportunitiesData = ${opportunitiesJson};
    window.favoriteIds = favoriteIdsFromServer;
</script>
<script src="<%= request.getContextPath() %>/js/map-main.js" defer></script>

</body>
</html>