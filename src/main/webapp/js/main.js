/**
 * main.js – общие интерактивные улучшения
 * Включает:
 * - плавное появление карточек (IntersectionObserver)
 * - подтверждение удаления для элементов с классом .confirm-delete
 * - управление панелью поиска (открытие/закрытие)
 * - управление избранным (звёздочки, localStorage, синхронизация с сервером)
 * - боковую панель избранного (отображение списка избранных вакансий)
 */

document.addEventListener('DOMContentLoaded', function() {
    // ========== 1. Плавное появление карточек ==========
    const cards = document.querySelectorAll('.opportunity-card, .form-card');
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '1';
                entry.target.style.transform = 'translateY(0)';
            }
        });
    }, { threshold: 0.1 });
    cards.forEach(card => {
        card.style.opacity = '0';
        card.style.transform = 'translateY(20px)';
        card.style.transition = 'opacity 0.5s ease, transform 0.5s ease';
        observer.observe(card);
    });

    // ========== 2. Подтверждение удаления ==========
    const deleteLinks = document.querySelectorAll('.confirm-delete');
    deleteLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            if (!confirm('Вы уверены, что хотите удалить?')) {
                e.preventDefault();
            }
        });
    });

    // ========== 3. Поисковая панель ==========
    const searchToggleBtn = document.getElementById('searchToggleBtn');
    const searchPanel = document.getElementById('searchPanel');
    if (searchToggleBtn && searchPanel) {
        searchToggleBtn.addEventListener('click', function(e) {
            e.preventDefault();
            searchPanel.classList.toggle('visible');
        });
        // Если есть параметры поиска в URL – открыть панель
        if (window.location.search.includes('q=') || window.location.search.includes('tag=')) {
            searchPanel.classList.add('visible');
        }
    }

    // ========== 4. Управление избранным ==========
    // Глобальные переменные (должны быть определены в JSP):
    // - isLoggedIn (boolean)
    // - favoriteIdsFromServer (массив ID избранных для авторизованных)
    // - contextPath (строка)
    let favoriteIds = isLoggedIn ? (favoriteIdsFromServer || []) : (JSON.parse(localStorage.getItem('favorites')) || []);

    // Функция обновления иконки звезды
    function updateStarIcon(star, isFav) {
        star.innerHTML = isFav ? '<i class="fas fa-star"></i>' : '<i class="far fa-star"></i>';
    }

    // Функция добавления/удаления из избранного
    function toggleFavorite(oppId, starElement) {
        const index = favoriteIds.indexOf(oppId);
        if (index === -1) {
            favoriteIds.push(oppId);
            updateStarIcon(starElement, true);
            if (isLoggedIn) {
                fetch(contextPath + '/favorite', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: 'action=add&opportunityId=' + oppId
                }).catch(err => console.error('Ошибка добавления:', err));
            } else {
                localStorage.setItem('favorites', JSON.stringify(favoriteIds));
            }
        } else {
            favoriteIds.splice(index, 1);
            updateStarIcon(starElement, false);
            if (isLoggedIn) {
                fetch(contextPath + '/favorite', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: 'action=remove&opportunityId=' + oppId
                }).catch(err => console.error('Ошибка удаления:', err));
            } else {
                localStorage.setItem('favorites', JSON.stringify(favoriteIds));
            }
        }
        updateFavoritesCount();
        // Обновляем маркер на карте, если функция доступна
        if (window.updateMarkerIcon) {
            window.updateMarkerIcon(oppId, favoriteIds.includes(oppId));
        }
        // Перерисовываем боковую панель, если она открыта
        if (document.getElementById('favoritesDrawer')?.classList.contains('open')) {
            renderFavoritesList();
        }
    }

    // Инициализация звёздочек на главной странице
    document.querySelectorAll('.favorite-star').forEach(star => {
        const oppId = parseInt(star.dataset.id);
        const isFav = favoriteIds.includes(oppId);
        updateStarIcon(star, isFav);
        star.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            toggleFavorite(oppId, star);
        });
    });

    // ========== 5. Боковая панель избранного ==========
    const favoritesBtn = document.getElementById('favoritesDrawerBtn');
    const drawer = document.getElementById('favoritesDrawer');
    const drawerOverlay = document.getElementById('drawerOverlay');
    const closeDrawerBtn = document.getElementById('closeDrawerBtn');
    const favoritesList = document.getElementById('favoritesList');
    const favoritesCountSpan = document.getElementById('favoritesCount');

    function openDrawer() {
        if (drawer && drawerOverlay) {
            drawer.classList.add('open');
            drawerOverlay.classList.add('active');
            renderFavoritesList();
        }
    }
    function closeDrawer() {
        if (drawer && drawerOverlay) {
            drawer.classList.remove('open');
            drawerOverlay.classList.remove('active');
        }
    }
    if (favoritesBtn) favoritesBtn.addEventListener('click', openDrawer);
    if (closeDrawerBtn) closeDrawerBtn.addEventListener('click', closeDrawer);
    if (drawerOverlay) drawerOverlay.addEventListener('click', closeDrawer);

    function updateFavoritesCount() {
        if (favoritesCountSpan) {
            favoritesCountSpan.textContent = favoriteIds.length;
        }
    }

    function renderFavoritesList() {
        if (!window.opportunitiesData) {
            favoritesList.innerHTML = '<p>Данные загружаются...</p>';
            return;
        }
        if (!favoritesList) return;
        if (favoriteIds.length === 0) {
            favoritesList.innerHTML = '<p>Нет избранных вакансий.</p>';
            return;
        }
        // Для получения данных о вакансиях используем window.opportunitiesData (только на главной)
        let allOpps = window.opportunitiesData || [];
        let favOpps = allOpps.filter(opp => favoriteIds.includes(opp.id));
        let html = '';
        favOpps.forEach(opp => {
            html += `
                <div class="favorite-drawer-item" data-id="${opp.id}">
                    <div class="item-info">
                        <div class="item-title"><a href="${contextPath}/opportunity?id=${opp.id}">${escapeHtml(opp.title)}</a></div>
                        <div class="item-company">${escapeHtml(opp.companyName || 'Компания #' + opp.companyId)}</div>
                    </div>
                    <button class="remove-fav" data-id="${opp.id}">&times;</button>
                </div>
            `;
        });
        favoritesList.innerHTML = html;
        // Обработчики удаления из боковой панели
        document.querySelectorAll('.remove-fav').forEach(btn => {
            btn.addEventListener('click', function(e) {
                e.stopPropagation();
                const oppId = parseInt(this.dataset.id);
                const star = document.querySelector(`.favorite-star[data-id="${oppId}"]`);
                if (star) {
                    star.click(); // эмулируем клик по звезде
                } else {
                    // Если звезды нет (например, страница не главная), удаляем вручную
                    const index = favoriteIds.indexOf(oppId);
                    if (index !== -1) {
                        favoriteIds.splice(index, 1);
                        if (isLoggedIn) {
                            fetch(contextPath + '/favorite', {
                                method: 'POST',
                                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                                body: 'action=remove&opportunityId=' + oppId
                            }).catch(err => console.error(err));
                        } else {
                            localStorage.setItem('favorites', JSON.stringify(favoriteIds));
                        }
                        updateFavoritesCount();
                        renderFavoritesList();
                        if (window.updateMarkerIcon) window.updateMarkerIcon(oppId, false);
                    }
                }
                updateFavoritesCount();
                renderFavoritesList();
            });
        });
    }

    // Простая функция экранирования HTML для безопасности
    function escapeHtml(str) {
        if (!str) return '';
        return str.replace(/[&<>]/g, function(m) {
            if (m === '&') return '&amp;';
            if (m === '<') return '&lt;';
            if (m === '>') return '&gt;';
            return m;
        });
    }

    // Инициализация счётчика и панели
    updateFavoritesCount();
    // Если панель уже открыта (например, после перезагрузки с параметром), можно перерисовать, но по умолчанию закрыта.
});