/**
 * map-coord.js – интерактивная карта для выбора координат вакансии.
 * Используется на странице создания/редактирования вакансии.
 * При клике на карту устанавливается маркер и заполняются скрытые поля lat/lng.
 */
document.addEventListener('DOMContentLoaded', function() {
    var map = L.map('map-coord').setView([55.751244, 37.618423], 10);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    }).addTo(map);

    var marker;
    var latField = document.getElementById('lat');
    var lngField = document.getElementById('lng');

    if (latField.value && lngField.value) {
        var lat = parseFloat(latField.value);
        var lng = parseFloat(lngField.value);
        marker = L.marker([lat, lng]).addTo(map);
        map.setView([lat, lng], 14);
    }

    map.on('click', function(e) {
        var lat = e.latlng.lat;
        var lng = e.latlng.lng;
        latField.value = lat;
        lngField.value = lng;
        if (marker) map.removeLayer(marker);
        marker = L.marker([lat, lng]).addTo(map);
    });
});