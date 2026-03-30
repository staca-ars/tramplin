package models;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Модель вакансии, стажировки, мероприятия или менторской программы.
 * Содержит всю информацию о возможности, включая координаты для карты,
 * статус модерации, теги, обложку, срок действия и список откликов.
 */
public class Opportunity {
    // Основные поля
    private int id;
    private String title;
    private String description;
    private int companyId;
    private String type;       // internship / job / event / mentorship
    private String format;     // remote / office / hybrid
    private String location;
    private Timestamp createdAt;
    private Timestamp expiresAt;   // срок действия (NULL = бессрочно)
    private double lat;            // координаты для карты
    private double lng;

    // Модерация
    private String moderationStatus; // pending / approved / rejected
    private String rejectionReason;

    // Визуальные элементы
    private String coverUrl;

    // Дополнительные поля (не хранятся в БД, заполняются при запросах)
    private transient List<Application> applications = new ArrayList<>();
    private String companyName;      // название компании (JOIN)
    private String tags;             // список тегов через запятую

    // Конструкторы
    public Opportunity() {}

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getCompanyId() { return companyId; }
    public void setCompanyId(int companyId) { this.companyId = companyId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Timestamp expiresAt) { this.expiresAt = expiresAt; }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLng() { return lng; }
    public void setLng(double lng) { this.lng = lng; }

    public String getModerationStatus() { return moderationStatus; }
    public void setModerationStatus(String moderationStatus) { this.moderationStatus = moderationStatus; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }

    public List<Application> getApplications() { return applications; }
    public void setApplications(List<Application> applications) { this.applications = applications; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
}