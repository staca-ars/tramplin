package models;

/**
 * Модель компании-работодателя.
 * Хранит информацию о компании, включая контактные данные, логотип, обложку,
 * а также статусы модерации (pending/approved/rejected) и причину отказа.
 */
public class Company {
    // Основные поля
    private int id;
    private String name;
    private String description;
    private String email;
    private String phone;
    private String website;
    private String logoUrl;
    private int ownerId;            // ID пользователя-владельца компании

    // Поля модерации
    private String status;           // pending / approved / rejected
    private String moderationStatus; // дублирует status, оставлено для совместимости
    private String rejectionReason;

    // Визуальные элементы
    private String coverUrl;

    // Конструкторы
    public Company() {}

    public Company(String name, String description, int ownerId, String status) {
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
        this.status = status;
        this.moderationStatus = status;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public String getStatus() { return status; }
    public void setStatus(String status) {
        this.status = status;
        this.moderationStatus = status; // синхронизация
    }

    public String getModerationStatus() { return moderationStatus; }
    public void setModerationStatus(String moderationStatus) {
        this.moderationStatus = moderationStatus;
        this.status = moderationStatus; // синхронизация
    }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
}