package models;

import java.sql.Timestamp;

/**
 * Модель для отображения отклика с дополнительной информацией о пользователе.
 * Используется в панели работодателя для показа списка откликов с данными соискателя.
 */
public class ApplicationWithUser {
    private int id;
    private int userId;
    private int opportunityId;
    private String status;
    private Timestamp createdAt;

    // Данные пользователя
    private String userName;
    private String userEmail;
    private String userPhotoUrl;

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getOpportunityId() { return opportunityId; }
    public void setOpportunityId(int opportunityId) { this.opportunityId = opportunityId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getUserPhotoUrl() { return userPhotoUrl; }
    public void setUserPhotoUrl(String userPhotoUrl) { this.userPhotoUrl = userPhotoUrl; }
}