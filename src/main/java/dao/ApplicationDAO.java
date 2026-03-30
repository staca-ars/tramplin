package dao;

import models.Application;
import models.ApplicationWithUser;
import models.Opportunity;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO для работы с откликами (applications).
 * Обеспечивает создание, обновление статуса, поиск по пользователю/вакансии,
 * удаление откликов, проверку существования отклика, а также получение расширенных данных с информацией о пользователе.
 */
public class ApplicationDAO {

    public boolean apply(int userId, int oppId) {
        String sql = "INSERT INTO applications (user_id, opportunity_id, status) VALUES (?, ?, 'pending')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, oppId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateStatus(int applicationId, String status) {
        String sql = "UPDATE applications SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, applicationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Application> findByUserId(int userId) {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT id, user_id, opportunity_id, status, created_at FROM applications WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Application app = new Application();
                app.setId(rs.getInt("id"));
                app.setUserId(rs.getInt("user_id"));
                app.setOpportunityId(rs.getInt("opportunity_id"));
                app.setStatus(rs.getString("status"));
                app.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(app);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Application> findByOpportunityId(int oppId) {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT * FROM applications WHERE opportunity_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, oppId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Application app = new Application();
                app.setId(rs.getInt("id"));
                app.setUserId(rs.getInt("user_id"));
                app.setOpportunityId(rs.getInt("opportunity_id"));
                app.setStatus(rs.getString("status"));
                list.add(app);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void deleteByOpportunityId(int oppId) {
        String sql = "DELETE FROM applications WHERE opportunity_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, oppId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasApplied(int userId, int oppId) {
        String sql = "SELECT id FROM applications WHERE user_id = ? AND opportunity_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, oppId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<ApplicationWithUser> findApplicationsWithUserByOpportunityId(int oppId) {
        List<ApplicationWithUser> list = new ArrayList<>();
        String sql = "SELECT a.*, u.name as user_name, u.email as user_email, sp.photo_url as user_photo_url " +
                "FROM applications a " +
                "JOIN users u ON a.user_id = u.id " +
                "LEFT JOIN seeker_profiles sp ON u.id = sp.user_id " +
                "WHERE a.opportunity_id = ? " +
                "ORDER BY a.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, oppId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ApplicationWithUser app = new ApplicationWithUser();
                app.setId(rs.getInt("id"));
                app.setUserId(rs.getInt("user_id"));
                app.setOpportunityId(rs.getInt("opportunity_id"));
                app.setStatus(rs.getString("status"));
                app.setCreatedAt(rs.getTimestamp("created_at"));
                app.setUserName(rs.getString("user_name"));
                app.setUserEmail(rs.getString("user_email"));
                app.setUserPhotoUrl(rs.getString("user_photo_url"));
                list.add(app);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean cancelApplication(int userId, int oppId) {
        String sql = "DELETE FROM applications WHERE user_id = ? AND opportunity_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, oppId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}