<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<footer class="footer">
    <div class="container">
        <div class="footer-content">
            <div class="footer-section">
                <h4 class="footer-title">Трамплин</h4>
                <p class="footer-text">Карьерная платформа для студентов, выпускников и IT-компаний.</p>
            </div>
            <div class="footer-section">
                <h4 class="footer-title">Навигация</h4>
                <ul class="footer-links">
                    <li><a href="<%= request.getContextPath() %>/">Главная</a></li>
                </ul>
            </div>
            <div class="footer-section">
                <h4 class="footer-title">Социальные сети</h4>
                <div class="social-links">
                    <a href="#" aria-label="Telegram"><i class="fab fa-telegram"></i></a>
                    <a href="#" aria-label="VK"><i class="fab fa-vk"></i></a>
                    <a href="#" aria-label="GitHub"><i class="fab fa-github"></i></a>
                </div>
            </div>
        </div>
        <div class="footer-bottom">
            <p>&copy; 2026 Трамплин. Все права защищены.</p>
        </div>
    </div>
</footer>