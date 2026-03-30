package dao;

import models.Company;
import models.Opportunity;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO для работы с таблицей companies.
 * Обеспечивает CRUD операции, поиск по владельцу, модерацию и удаление.
 */
public class CompanyDAO {

    // Сохранение новой компании
    public boolean saveCompany(Company company) {
        String sql = "INSERT INTO companies (name, description, owner_id, status, email, phone, website, logo_url, moderation_status, rejection_reason, cover_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, company.getName());
            ps.setString(2, company.getDescription());
            ps.setInt(3, company.getOwnerId());
            ps.setString(4, company.getStatus());
            ps.setString(5, company.getEmail());
            ps.setString(6, company.getPhone());
            ps.setString(7, company.getWebsite());
            ps.setString(8, company.getLogoUrl());
            ps.setString(9, company.getModerationStatus());
            ps.setString(10, company.getRejectionReason());
            ps.setString(11, company.getCoverUrl());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    company.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Поиск компании по ID
    public Company findById(int id) {
        String sql = "SELECT * FROM companies WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapCompany(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Поиск компании по ID владельца (работодателя)
    public Company findByOwnerId(int ownerId) {
        String sql = "SELECT * FROM companies WHERE owner_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapCompany(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Обновление информации о компании
    public boolean updateCompany(Company company) {
        String sql = "UPDATE companies SET name=?, description=?, email=?, phone=?, website=?, logo_url=?, status=?, moderation_status=?, rejection_reason=?, cover_url=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, company.getName());
            ps.setString(2, company.getDescription());
            ps.setString(3, company.getEmail());
            ps.setString(4, company.getPhone());
            ps.setString(5, company.getWebsite());
            ps.setString(6, company.getLogoUrl());
            ps.setString(7, company.getStatus());
            ps.setString(8, company.getModerationStatus());
            ps.setString(9, company.getRejectionReason());
            ps.setString(10, company.getCoverUrl());
            ps.setInt(11, company.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Получить все компании (для админа)
    public List<Company> getAllCompanies() {
        List<Company> list = new ArrayList<>();
        String sql = "SELECT * FROM companies";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapCompany(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Обновить статус компании (устаревший метод, лучше использовать approve/reject)
    public boolean updateCompanyStatus(int companyId, String status) {
        String sql = "UPDATE companies SET status = ?, moderation_status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, status);
            ps.setInt(3, companyId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Одобрить компанию (модерация)
    public boolean approveCompany(int companyId) {
        String sql = "UPDATE companies SET status = 'approved', moderation_status = 'approved', rejection_reason = NULL WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, companyId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Отклонить компанию с указанием причины
    public boolean rejectCompany(int companyId, String reason) {
        String sql = "UPDATE companies SET status = 'rejected', moderation_status = 'rejected', rejection_reason = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reason);
            ps.setInt(2, companyId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Список компаний, ожидающих модерации
    public List<Company> getPendingCompanies() {
        List<Company> list = new ArrayList<>();
        String sql = "SELECT * FROM companies WHERE moderation_status IS NULL OR moderation_status = 'pending'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapCompany(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Удалить компанию и все связанные с ней данные (вакансии, теги, отклики)
    public boolean deleteCompany(int companyId) {
        // Сначала удаляем все вакансии компании (и связанные с ними данные)
        OpportunityDAO oppDAO = new OpportunityDAO();
        List<Opportunity> opportunities = oppDAO.findByCompanyId(companyId);
        for (Opportunity opp : opportunities) {
            oppDAO.deleteOpportunity(opp.getId());
        }

        // Затем удаляем саму компанию
        String sql = "DELETE FROM companies WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, companyId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Маппинг ResultSet в объект Company
    private Company mapCompany(ResultSet rs) throws SQLException {
        Company company = new Company();
        company.setId(rs.getInt("id"));
        company.setName(rs.getString("name"));
        company.setDescription(rs.getString("description"));
        company.setEmail(rs.getString("email"));
        company.setPhone(rs.getString("phone"));
        company.setWebsite(rs.getString("website"));
        company.setLogoUrl(rs.getString("logo_url"));
        company.setOwnerId(rs.getInt("owner_id"));
        company.setStatus(rs.getString("status"));
        company.setModerationStatus(rs.getString("moderation_status"));
        company.setRejectionReason(rs.getString("rejection_reason"));
        company.setCoverUrl(rs.getString("cover_url"));
        return company;
    }
}