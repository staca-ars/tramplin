/**
 * map-main.js – карта Leaflet для главной страницы.
 * Отображает маркеры вакансий с разным цветом для избранных.
 * При наведении – всплывающая подсказка, при клике – детальное окно.
 */
document.addEventListener('DOMContentLoaded', function() {
    var contextPath = document.querySelector('meta[name="contextPath"]').getAttribute('content');
    var map = L.map('map').setView([55.751244, 37.618423], 10);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(map);

    var opportunities = window.opportunitiesData || [];
    var favoriteIds = window.favoriteIdsFromServer || [];
    window.markerMap = new Map();

    // Иконки: обычная и золотая для избранных
    var defaultIcon = L.icon({
        iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
        iconSize: [25, 41],
        iconAnchor: [12, 41],
        popupAnchor: [1, -34]
    });
    var favoriteIcon = L.icon({
        iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-gold.png',
        iconSize: [25, 41],
        iconAnchor: [12, 41],
        popupAnchor: [1, -34]
    });

    // Функция обновления иконки маркера (вызывается из main.js при изменении избранного)
    window.updateMarkerIcon = function(oppId, isFavorite) {
        var marker = window.markerMap.get(oppId);
        if (marker) {
            marker.setIcon(isFavorite ? favoriteIcon : defaultIcon);
        }
    };

    opportunities.forEach(function(opp) {
        if (opp.lat && opp.lng) {
            var companyLink = contextPath + '/company/' + opp.companyId;
            var companyHtml = opp.companyName ? '<a href="' + companyLink + '" target="_blank">' + opp.companyName + '</a>' : 'Компания #' + opp.companyId;

            var tagsHtml = '';
            if (opp.tags) {
                var tagList = opp.tags.split(', ');
                var displayTags = tagList.slice(0, 3).join(', ');
                tagsHtml = '<div class="popup-tags"><i class="fas fa-tags"></i> ' + displayTags + (tagList.length > 3 ? '…' : '') + '</div>';
            }

            var popupContent = '<div class="popup-card">' +
                '<h3>' + opp.title + '</h3>' +
                '<div class="popup-company">' + companyHtml + '</div>' +
                '<div class="popup-location"><i class="fas fa-map-marker-alt"></i> ' + opp.location + '</div>' +
                tagsHtml +
                '<a href="' + contextPath + '/opportunity?id=' + opp.id + '" class="popup-link">Подробнее</a>' +
                '</div>';

            var tooltipContent = '<b>' + opp.title + '</b><br>' +
                (opp.companyName ? opp.companyName : 'Компания #' + opp.companyId) + '<br>' +
                '📍 ' + opp.location;

            var isFav = favoriteIds.includes(opp.id);
            var marker = L.marker([opp.lat, opp.lng], { icon: isFav ? favoriteIcon : defaultIcon })
                .addTo(map)
                .bindPopup(popupContent)
                .bindTooltip(tooltipContent, { sticky: false, offset: [0, -20], direction: 'top' });
            window.markerMap.set(opp.id, marker);
        }
    });

    if (window.markerMap.size === 0) {
        map.setView([55.751244, 37.618423], 5);
        L.popup()
            .setLatLng([55.751244, 37.618423])
            .setContent("Нет вакансий с указанными координатами. Добавьте их через админку.")
            .openOn(map);
    }

    // Клик по карточке вакансии в списке – центрируем карту и открываем popup
    document.querySelectorAll('.opportunity-card').forEach(function(card) {
        var lat = card.getAttribute('data-lat');
        var lng = card.getAttribute('data-lng');
        if (lat && lng) {
            card.style.cursor = 'pointer';
            card.addEventListener('click', function() {
                map.setView([parseFloat(lat), parseFloat(lng)], 14);
                var marker;
                for (let [id, m] of window.markerMap.entries()) {
                    if (m.getLatLng().lat == lat && m.getLatLng().lng == lng) {
                        marker = m;
                        break;
                    }
                }
                if (marker) marker.openPopup();
            });
        }
    });
});