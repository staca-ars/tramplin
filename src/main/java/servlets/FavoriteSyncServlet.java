package servlets;

import dao.FavoriteDAO;
import models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import java.io.IOException;

/**
 * Сервлет для синхронизации избранного из localStorage в базу данных.
 * Используется после входа пользователя: передаёт массив ID вакансий,
 * которые были добавлены в избранное в гостевом режиме.
 */
@WebServlet("/syncFavorites")
public class FavoriteSyncServlet extends HttpServlet {
    private FavoriteDAO favoriteDAO = new FavoriteDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Необходима авторизация");
            return;
        }
        User user = (User) session.getAttribute("user");
        Gson gson = new Gson();
        JsonArray arr = gson.fromJson(req.getReader(), JsonArray.class);

        // Очищаем старые избранные пользователя и добавляем новые из localStorage
        favoriteDAO.deleteAllFavorites(user.getId());
        for (int i = 0; i < arr.size(); i++) {
            int oppId = arr.get(i).getAsInt();
            favoriteDAO.addFavorite(user.getId(), oppId);
        }
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}