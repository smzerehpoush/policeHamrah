package com.nrdc.managementPanel.impl;

import com.nrdc.managementPanel.helper.PrivilegeNames;
import com.nrdc.managementPanel.helper.SystemNames;
import com.nrdc.managementPanel.jsonModel.StandardResponse;
import com.nrdc.managementPanel.jsonModel.customizedModel.RoleWithPrivileges;
import com.nrdc.managementPanel.jsonModel.jsonResponse.ResponseGetPrivileges;
import com.nrdc.managementPanel.model.Privilege;
import com.nrdc.managementPanel.model.Role;
import com.nrdc.managementPanel.model.Token;
import com.nrdc.managementPanel.model.User;

import javax.persistence.EntityManager;
import java.util.LinkedList;
import java.util.List;

public class PrivilegeImpl {
    public StandardResponse getPrivileges(String token) throws Exception {
        EntityManager entityManager = Database.getEntityManager();
        try {
            Token.validateToken(token, SystemNames.MANAGEMENT_PANEL);
            User user = User.getUser(token, SystemNames.MANAGEMENT_PANEL);
            user.checkPrivilege(PrivilegeNames.GET_PRIVILEGES);
            List<Privilege> privileges = entityManager.createQuery("SELECT p FROM Privilege p")
                    .getResultList();
            ResponseGetPrivileges responseGetPrivileges = new ResponseGetPrivileges();
            responseGetPrivileges.setPrivileges(privileges);
            StandardResponse response = new StandardResponse<>();
            response.setResultCode(1);
            response.setResultMessage("OK");
            response.setResponse(responseGetPrivileges);
            return response;
        } finally {
            if (entityManager.isOpen())
                entityManager.close();
        }
    }

    public StandardResponse getPrivileges(String token, Long fkRoleId) throws Exception {
        EntityManager entityManager = Database.getEntityManager();
        try {
            Token.validateToken(token, SystemNames.MANAGEMENT_PANEL);
            User user = User.getUser(token, SystemNames.MANAGEMENT_PANEL);
            user.checkPrivilege(PrivilegeNames.GET_PRIVILEGES);
            List privileges = entityManager.createQuery("SELECT p FROM Privilege p JOIN RolePrivilege rp ON p.id = rp.fkPrivilegeId WHERE rp.fkRoleId = :fkRoleId")
                    .setParameter("fkRoleId", fkRoleId)
                    .getResultList();
            ResponseGetPrivileges responseGetPrivileges = new ResponseGetPrivileges();
            responseGetPrivileges.setPrivileges(privileges);
            StandardResponse response = new StandardResponse<>();
            response.setResultCode(1);
            response.setResultMessage("OK");
            response.setResponse(responseGetPrivileges);
            return response;
        } finally {
            if (entityManager.isOpen())
                entityManager.close();
        }
    }


}