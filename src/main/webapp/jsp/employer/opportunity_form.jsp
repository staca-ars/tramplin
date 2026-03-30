<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
  <title>${opportunity == null ? 'Создание' : 'Редактирование'} вакансии - Трамплин</title>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
  <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
  <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
  <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
</head>
<body>
<jsp:include page="/jsp/header.jsp" />

<main class="container">
  <div class="form-card max-width-600">
    <h2 class="form-title">${opportunity == null ? 'Новая вакансия' : 'Редактирование вакансии'}</h2>
    <form method="post" action="<%= request.getContextPath() %>/employer/opportunity">
      <input type="hidden" name="opportunityId" value="${opportunity.id}">
      <div class="form-group">
        <label>Название *</label>
        <input type="text" name="title" class="form-control" value="${opportunity.title}" required>
      </div>
      <div class="form-group">
        <label>Описание</label>
        <textarea name="description" class="form-control" rows="5">${opportunity.description}</textarea>
      </div>
      <div class="form-group">
        <label>Тип</label>
        <select name="type" class="form-control">
          <option value="job" ${opportunity.type == 'job' ? 'selected' : ''}>Вакансия</option>
          <option value="internship" ${opportunity.type == 'internship' ? 'selected' : ''}>Стажировка</option>
          <option value="event" ${opportunity.type == 'event' ? 'selected' : ''}>Мероприятие</option>
        </select>
      </div>
      <div class="form-group">
        <label>Формат работы</label>
        <select name="format" class="form-control">
          <option value="office" ${opportunity.format == 'office' ? 'selected' : ''}>В офисе</option>
          <option value="remote" ${opportunity.format == 'remote' ? 'selected' : ''}>Удалённо</option>
          <option value="hybrid" ${opportunity.format == 'hybrid' ? 'selected' : ''}>Гибрид</option>
        </select>
      </div>
      <div class="form-group">
        <label>Местоположение (город или адрес)</label>
        <input type="text" name="location" class="form-control" value="${opportunity.location}">
      </div>
      <div class="form-group">
        <label>Срок действия (если не указан, то бессрочно)</label>
        <input type="datetime-local" name="expiresAt" class="form-control"
               value="${opportunity.expiresAt != null ? opportunity.expiresAt.toLocalDateTime().toString().substring(0, 16) : ''}">
        <small>Формат: ГГГГ-ММ-ДДТЧЧ:ММ (например, 2026-12-31T23:59)</small>
      </div>
      <div class="form-group">
        <label>Местоположение на карте</label>
        <div id="map-coord"></div>
        <small>Кликните на карту, чтобы установить маркер вакансии.</small>
        <input type="hidden" name="lat" id="lat" value="${opportunity.lat}">
        <input type="hidden" name="lng" id="lng" value="${opportunity.lng}">
      </div>
      <script src="<%= request.getContextPath() %>/js/map-coord.js" defer></script>
      <div class="form-group">
        <label>Теги (через запятую)</label>
        <input type="text" name="tags" class="form-control" value="${tagsString}" placeholder="Java, Spring, SQL">
        <small>Теги будут добавлены в базу и связаны с вакансией</small>
      </div>
      <button type="submit" class="btn btn-primary">Сохранить</button>
      <a href="<%= request.getContextPath() %>/employer/dashboard" class="btn btn-outline">Отмена</a>
    </form>
  </div>
</main>
<jsp:include page="/jsp/footer.jsp" />
</body>
</html>