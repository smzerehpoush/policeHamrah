package com.nrdc.managementPanel.model;

import com.nrdc.managementPanel.helper.Constants;
import com.nrdc.managementPanel.impl.Database;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "OPERATION", schema = Constants.SCHEMA)
public class Operation {
    private Long id;
    private Long fkUserId;
    private Long fkPrivilegeId;
    private Date time;
    private String description;
    private Long statusCode;

    public Operation() {
    }

    public Operation(Long fkUserId, Long fkPrivilegeId) {
        this.fkUserId = fkUserId;
        this.fkPrivilegeId = fkPrivilegeId;
    }

    public Operation(User user, Privilege privilege, Long statusCode) {
        this.fkUserId = user.getId();
        this.fkPrivilegeId = privilege.getId();
        this.statusCode = statusCode;
    }

    public void persist() {
        EntityManager entityManager = Database.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            if (!transaction.isActive())
                transaction.begin();
                entityManager.persist(this);
            if (transaction.isActive())
                transaction.commit();
        } catch (Exception ex) {
            if (transaction != null && transaction.isActive())
                transaction.rollback();
        } finally {
            if (entityManager.isOpen())
                entityManager.close();
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_OPERATION")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "FK_USER_ID")
    public Long getFkUserId() {
        return fkUserId;
    }

    public void setFkUserId(Long fkUserId) {
        this.fkUserId = fkUserId;
    }

    @Basic
    @Column(name = "FK_PRIVILEGE_ID")
    public Long getFkPrivilegeId() {
        return fkPrivilegeId;
    }

    public void setFkPrivilegeId(Long fkPrivilegeId) {
        this.fkPrivilegeId = fkPrivilegeId;
    }

    @Basic
    @Temporal(TemporalType.DATE)
    @Column(name = "TIME")
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Basic
    @Column(name = "DESCRIPTION")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "STATUS_CODE")
    public Long getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Long statusCode) {
        this.statusCode = statusCode;
    }
}
