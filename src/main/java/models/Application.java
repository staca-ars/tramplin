package models;

import java.sql.Timestamp;

/**
 * Модель "Отклик" (Application).
 * Представляет заявку соискателя на конкретную вакансию.
 * Содержит информацию о пользователе, вакансии, статусе отклика и дате создания.
 * Используется в личном кабинете соискателя и работодателя.
 */
public class Application {
    private int id;
    private int userId;
    private int opportunityId;
    private String status; // pending, accepted, rejected, reserved
    private Opportunity opportunity; // объект вакансии для удобства (не хранится в БД)
    private Timestamp createdAt;

    // Конструктор по умолчанию (неявный, можно явно не писать)

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getOpportunityId() { return opportunityId; }
    public void setOpportunityId(int opportunityId) { this.opportunityId = opportunityId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Opportunity getOpportunity() { return opportunity; }
    public void setOpportunity(Opportunity opportunity) { this.opportunity = opportunity; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}