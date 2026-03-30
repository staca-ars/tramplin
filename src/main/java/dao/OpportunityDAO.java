package dao;

import models.Opportunity;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO для работы с вакансиями (opportunities).
 * Предоставляет методы для CRUD операций, поиска, фильтрации и модерации.
 */
public class OpportunityDAO {

    public boolean saveOpportunity(Opportunity opp) {
        String sql = "INSERT INTO opportunities (title, description, company_id, type, format, location, lat, lng, moderation_status, rejection_reason, cover_url, expires_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, opp.getTitle());
            ps.setString(2, opp.getDescription());
            ps.setInt(3, opp.getCompanyId());
            ps.setString(4, opp.getType());
            ps.setString(5, opp.getFormat());
            ps.setString(6, opp.getLocation());
            ps.setDouble(7, opp.getLat());
            ps.setDouble(8, opp.getLng());
            ps.setString(9, opp.getModerationStatus());
            ps.setString(10, opp.getRejectionReason());
            ps.setString(11, opp.getCoverUrl());
            ps.setTimestamp(12, opp.getExpiresAt());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) opp.setId(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateOpportunity(Opportunity opp) {
        String sql = "UPDATE opportunities SET title=?, description=?, type=?, format=?, location=?, lat=?, lng=?, moderation_status=?, rejection_reason=?, cover_url=?, expires_at=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, opp.getTitle());
            ps.setString(2, opp.getDescription());
            ps.setString(3, opp.getType());
            ps.setString(4, opp.getFormat());
            ps.setString(5, opp.getLocation());
            ps.setDouble(6, opp.getLat());
            ps.setDouble(7, opp.getLng());
            ps.setString(8, opp.getModerationStatus());
            ps.setString(9, opp.getRejectionReason());
            ps.setString(10, opp.getCoverUrl());
            ps.setTimestamp(11, opp.getExpiresAt());
            ps.setInt(12, opp.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Opportunity> findAll() {
        List<Opportunity> list = new ArrayList<>();
        String sql = "SELECT o.*, c.name AS company_name, " +
                "GROUP_CONCAT(DISTINCT t.name ORDER BY t.name SEPARATOR ', ') AS tags " +
                "FROM opportunities o " +
                "JOIN companies c ON o.company_id = c.id " +
                "LEFT JOIN opportunity_tags ot ON o.id = ot.opportunity_id " +
                "LEFT JOIN tags t ON ot.tag_id = t.id " +
                "WHERE o.moderation_status = 'approved' " +
                "AND (o.expires_at IS NULL OR o.expires_at > NOW()) " +
                "GROUP BY o.id " +
                "ORDER BY o.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Opportunity opp = mapOpportunity(rs);
                opp.setCompanyName(rs.getString("company_name"));
                opp.setTags(rs.getString("tags"));
                list.add(opp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Opportunity findById(int id) {
        String sql = "SELECT * FROM opportunities WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapOpportunity(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Opportunity> findByCompanyId(int companyId) {
        List<Opportunity> list = new ArrayList<>();
        String sql = "SELECT * FROM opportunities WHERE company_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, companyId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapOpportunity(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Opportunity mapOpportunity(ResultSet rs) throws SQLException {
        Opportunity opp = new Opportunity();
        opp.setId(rs.getInt("id"));
        opp.setTitle(rs.getString("title"));
        opp.setDescription(rs.getString("description"));
        opp.setCompanyId(rs.getInt("company_id"));
        opp.setType(rs.getString("type"));
        opp.setFormat(rs.getString("format"));
        opp.setLocation(rs.getString("location"));
        opp.setCreatedAt(rs.getTimestamp("created_at"));
        opp.setExpiresAt(rs.getTimestamp("expires_at"));
        opp.setLat(rs.getDouble("lat"));
        opp.setLng(rs.getDouble("lng"));
        opp.setModerationStatus(rs.getString("moderation_status"));
        opp.setRejectionReason(rs.getString("rejection_reason"));
        opp.setCoverUrl(rs.getString("cover_url"));
        return opp;
    }

    public boolean approveOpportunity(int oppId) {
        String sql = "UPDATE opportunities SET moderation_status = 'approved', rejection_reason = NULL WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, oppId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean rejectOpportunity(int oppId, String reason) {
        String sql = "UPDATE opportunities SET moderation_status = 'rejected', rejection_reason = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reason);
            ps.setInt(2, oppId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<Opportunity> getPendingOpportunities() {
        List<Opportunity> list = new ArrayList<>();
        String sql = "SELECT * FROM opportunities WHERE moderation_status IS NULL OR moderation_status = 'pending'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapOpportunity(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean deleteOpportunity(int id) {
        // Удаляем связанные теги
        TagDAO tagDAO = new TagDAO();
        tagDAO.deleteTagsForOpportunity(id);
        // Удаляем связанные отклики
        ApplicationDAO appDAO = new ApplicationDAO();
        appDAO.deleteByOpportunityId(id);
        // Удаляем саму вакансию
        String sql = "DELETE FROM opportunities WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Opportunity> findAllForAdmin() {
        List<Opportunity> list = new ArrayList<>();
        String sql = "SELECT * FROM opportunities ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapOpportunity(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Opportunity> findFiltered(String searchText, String tag) {
        List<Opportunity> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT o.*, c.name AS company_name, " +
                        "GROUP_CONCAT(DISTINCT t.name ORDER BY t.name SEPARATOR ', ') AS tags " +
                        "FROM opportunities o " +
                        "JOIN companies c ON o.company_id = c.id " +
                        "LEFT JOIN opportunity_tags ot ON o.id = ot.opportunity_id " +
                        "LEFT JOIN tags t ON ot.tag_id = t.id " +
                        "WHERE o.moderation_status = 'approved' "
        );

        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        if (searchText != null && !searchText.trim().isEmpty()) {
            conditions.add("(o.title LIKE ? OR o.description LIKE ? OR c.name LIKE ?)");
            String like = "%" + searchText.trim() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }

        if (tag != null && !tag.trim().isEmpty()) {
            conditions.add("EXISTS (SELECT 1 FROM opportunity_tags ot2 " +
                    "JOIN tags t2 ON ot2.tag_id = t2.id " +
                    "WHERE ot2.opportunity_id = o.id AND t2.name = ?)");
            params.add(tag.trim());
        }

        if (!conditions.isEmpty()) {
            sql.append(" AND ").append(String.join(" AND ", conditions));
        }

        sql.append(" GROUP BY o.id ORDER BY o.created_at DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Opportunity opp = mapOpportunity(rs);
                opp.setCompanyName(rs.getString("company_name"));
                opp.setTags(rs.getString("tags"));
                list.add(opp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}