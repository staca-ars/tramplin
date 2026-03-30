package dao;

import models.Opportunity;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO для работы с избранными вакансиями (favorites).
 * Поддерживает добавление, удаление, проверку и получение списка избранного.
 */
public class FavoriteDAO {

    public boolean addFavorite(int userId, int oppId) {
        String sql = "INSERT INTO favorites (user_id, opportunity_id) VALUES (?, ?)";
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

    public boolean removeFavorite(int userId, int oppId) {
        String sql = "DELETE FROM favorites WHERE user_id = ? AND opportunity_id = ?";
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

    public List<Integer> findFavoriteOpportunityIds(int userId) {
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT opportunity_id FROM favorites WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getInt("opportunity_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Opportunity> findFavoriteOpportunities(int userId) {
        List<Opportunity> list = new ArrayList<>();
        String sql = "SELECT o.* FROM opportunities o JOIN favorites f ON o.id = f.opportunity_id WHERE f.user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            OpportunityDAO oppDAO = new OpportunityDAO();
            while (rs.next()) {
                list.add(oppDAO.mapOpportunity(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean hasFavorite(int userId, int oppId) {
        String sql = "SELECT id FROM favorites WHERE user_id = ? AND opportunity_id = ?";
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

    public void deleteAllFavorites(int userId) {
        String sql = "DELETE FROM favorites WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}