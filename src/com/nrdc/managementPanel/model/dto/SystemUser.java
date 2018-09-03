package com.nrdc.managementPanel.model.dto;

import com.nrdc.managementPanel.helper.Constants;
import com.nrdc.managementPanel.model.dao.SystemUserDAO;

import javax.persistence.*;

@Entity
@Table(name = "USER_SYSTEM", schema = Constants.SCHEMA)
public class SystemUser extends SystemUserDAO {
    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_USER_SYSTEM")
    public Long getId() {
        return super.getId();
    }

    @Override
    @Basic
    @Column(name = "FK_USER_ID")
    public Long getFkUserId() {
        return super.getFkUserId();
    }

    @Override
    @Basic
    @Column(name = "FK_SYSTEM_ID")
    public Long getFkSystemId() {
        return super.getFkSystemId();
    }
}
