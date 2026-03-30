package servlets;

import dao.FavoriteDAO;
import dao.OpportunityDAO;
import models.Opportunity;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервлет для главной страницы. Обрабатывает корневой URL "/".
 * Загружает список активных вакансий, поддерживает поиск и фильтрацию по тегам,
 * а также передаёт данные для карты и избранного.
 */
@WebServlet("")
public class IndexServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String searchQuery = req.getParameter("q");
        String tag = req.getParameter("tag");

        OpportunityDAO oppDAO = new OpportunityDAO();
        List<Opportunity> opportunities;

        // Если задан поисковый запрос или тег – используем фильтрацию
        if ((searchQuery != null && !searchQuery.trim().isEmpty()) || (tag != null && !tag.trim().isEmpty())) {
            opportunities = oppDAO.findFiltered(searchQuery, tag);
        } else {
            opportunities = oppDAO.findAll(); // только одобренные и не просроченные
        }

        // Передаём данные в JSP
        req.setAttribute("opportunities", opportunities);
        req.setAttribute("opportunitiesJson", new Gson().toJson(opportunities));
        req.setAttribute("searchQuery", searchQuery);
        req.setAttribute("tag", tag);

        // Загружаем избранное для авторизованных соискателей
        List<Integer> favoriteIds = new ArrayList<>();
        User currentUser = (User) req.getSession().getAttribute("user");
        if (currentUser != null && "seeker".equals(currentUser.getRole())) {
            FavoriteDAO favDAO = new FavoriteDAO();
            favoriteIds = favDAO.findFavoriteOpportunityIds(currentUser.getId());
        }
        req.setAttribute("favoriteIdsJson", new Gson().toJson(favoriteIds));

        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}