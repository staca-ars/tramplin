package servlets;

import dao.*;
import models.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервлет для управления вакансиями работодателя.
 * Поддерживает создание, редактирование, удаление вакансий,
 * а также повторную отправку на модерацию.
 */
@WebServlet("/employer/opportunity")
public class EmployerOpportunityServlet extends HttpServlet {

    private OpportunityDAO oppDAO = new OpportunityDAO();
    private CompanyDAO companyDAO = new CompanyDAO();
    private TagDAO tagDAO = new TagDAO();
    private ApplicationDAO appDAO = new ApplicationDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/jsp/login.jsp");
            return;
        }
        User user = (User) session.getAttribute("user");
        if (!"employer".equals(user.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String action = req.getParameter("action");
        Company company = companyDAO.findByOwnerId(user.getId());
        if (company == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Компания не найдена");
            return;
        }

        if ("create".equals(action)) {
            req.setAttribute("company", company);
            req.getRequestDispatcher("/jsp/employer/opportunity_form.jsp").forward(req, resp);
        } else if ("edit".equals(action)) {
            int oppId = Integer.parseInt(req.getParameter("id"));
            Opportunity opp = oppDAO.findById(oppId);
            if (opp == null || opp.getCompanyId() != company.getId()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            List<Tag> tags = tagDAO.findTagsByOpportunityId(oppId);
            String tagsString = tags.stream().map(Tag::getName).collect(Collectors.joining(", "));
            req.setAttribute("opportunity", opp);
            req.setAttribute("company", company);
            req.setAttribute("tagsString", tagsString);
            req.getRequestDispatcher("/jsp/employer/opportunity_form.jsp").forward(req, resp);
        } else if ("delete".equals(action)) {
            int oppId = Integer.parseInt(req.getParameter("id"));
            Opportunity opp = oppDAO.findById(oppId);
            if (opp != null && opp.getCompanyId() == company.getId()) {
                appDAO.deleteByOpportunityId(oppId);
                tagDAO.deleteTagsForOpportunity(oppId);
                oppDAO.deleteOpportunity(oppId);
            }
            resp.sendRedirect(req.getContextPath() + "/employer/dashboard");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/jsp/login.jsp");
            return;
        }
        User user = (User) session.getAttribute("user");
        if (!"employer".equals(user.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Company company = companyDAO.findByOwnerId(user.getId());
        if (company == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String action = req.getParameter("action");

        // Повторная отправка на модерацию
        if ("resubmit".equals(action)) {
            int oppId = Integer.parseInt(req.getParameter("opportunityId"));
            Opportunity opp = oppDAO.findById(oppId);
            if (opp != null && opp.getCompanyId() == company.getId()) {
                opp.setModerationStatus("pending");
                opp.setRejectionReason(null);
                oppDAO.updateOpportunity(opp);
            }
            resp.sendRedirect(req.getContextPath() + "/employer/dashboard");
            return;
        }

        // Обычное сохранение/обновление вакансии
        String oppIdParam = req.getParameter("opportunityId");
        Opportunity opp;
        if (oppIdParam != null && !oppIdParam.isEmpty()) {
            opp = oppDAO.findById(Integer.parseInt(oppIdParam));
            if (opp == null || opp.getCompanyId() != company.getId()) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        } else {
            opp = new Opportunity();
            opp.setCompanyId(company.getId());
        }

        opp.setTitle(req.getParameter("title"));
        opp.setDescription(req.getParameter("description"));
        opp.setType(req.getParameter("type"));
        opp.setFormat(req.getParameter("format"));
        opp.setLocation(req.getParameter("location"));

        String latStr = req.getParameter("lat");
        String lngStr = req.getParameter("lng");
        if (latStr != null && !latStr.trim().isEmpty()) {
            try {
                opp.setLat(Double.parseDouble(latStr));
            } catch (NumberFormatException e) { /* ignore */ }
        }
        if (lngStr != null && !lngStr.trim().isEmpty()) {
            try {
                opp.setLng(Double.parseDouble(lngStr));
            } catch (NumberFormatException e) { /* ignore */ }
        }

        // Устанавливаем статус модерации и причину отказа
        opp.setModerationStatus("pending");
        opp.setRejectionReason(null);

        // Обработка срока действия
        String expiresAtStr = req.getParameter("expiresAt");
        if (expiresAtStr != null && !expiresAtStr.trim().isEmpty()) {
            try {
                LocalDateTime localDateTime = LocalDateTime.parse(expiresAtStr);
                opp.setExpiresAt(Timestamp.valueOf(localDateTime));
            } catch (Exception e) {
                e.printStackTrace();
                opp.setExpiresAt(null);
            }
        } else {
            opp.setExpiresAt(null);
        }

        // Сохраняем или обновляем вакансию (теперь с expiresAt)
        if (opp.getId() == 0) {
            oppDAO.saveOpportunity(opp);
        } else {
            oppDAO.updateOpportunity(opp);
        }

        // Обновляем теги
        tagDAO.deleteTagsForOpportunity(opp.getId());
        String tagsStr = req.getParameter("tags");
        if (tagsStr != null && !tagsStr.trim().isEmpty()) {
            String[] tagNames = tagsStr.split(",");
            for (String tagName : tagNames) {
                tagName = tagName.trim();
                if (tagName.isEmpty()) continue;
                Tag tag = tagDAO.findOrCreateByName(tagName);
                tagDAO.linkTagToOpportunity(tag.getId(), opp.getId());
            }
        }

        resp.sendRedirect(req.getContextPath() + "/employer/dashboard");
    }
}