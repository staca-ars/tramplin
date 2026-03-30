package servlets;

import dao.FavoriteDAO;
import models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Сервлет для управления избранным (только для авторизованных пользователей).
 * Обрабатывает AJAX-запросы на добавление и удаление вакансий из избранного.
 */
@WebServlet("/favorite")
public class FavoriteServlet extends HttpServlet {
    private FavoriteDAO favoriteDAO = new FavoriteDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Необходима авторизация");
            return;
        }
        User user = (User) session.getAttribute("user");
        String action = req.getParameter("action");
        int oppId = Integer.parseInt(req.getParameter("opportunityId"));

        if ("add".equals(action)) {
            favoriteDAO.addFavorite(user.getId(), oppId);
        } else if ("remove".equals(action)) {
            favoriteDAO.removeFavorite(user.getId(), oppId);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Неизвестное действие");
            return;
        }
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}