package com.nrdc.policeHamrah.impl;

import com.nrdc.policeHamrah.helper.Constants;
import com.nrdc.policeHamrah.helper.PrivilegeNames;
import com.nrdc.policeHamrah.jsonModel.StandardResponse;
import com.nrdc.policeHamrah.jsonModel.jsonRequest.RequestAddRole;
import com.nrdc.policeHamrah.jsonModel.jsonRequest.RequestEditRole;
import com.nrdc.policeHamrah.jsonModel.jsonResponse.ResponseGetPrivileges;
import com.nrdc.policeHamrah.model.dao.RoleDao;
import com.nrdc.policeHamrah.model.dao.RolePrivilegeDao;
import com.nrdc.policeHamrah.model.dao.SystemDao;
import com.nrdc.policeHamrah.model.dao.UserDao;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import java.util.List;

public class RoleImpl {
    public StandardResponse addRole(String token, RequestAddRole requestAddRole) throws Exception {
        EntityManager entityManager = Database.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        entityManager.getEntityManagerFactory().getCache().evictAll();
        try {
            UserDao user = UserDao.validate(token);
            SystemDao systemDao = SystemDao.getSystem(requestAddRole.getFkSystemId());
            List<SystemDao> userSystems = user.systems();
            if (!userSystems.contains(systemDao))
                throw new Exception(Constants.USER_SYSTEM_ERROR);

            user.checkPrivilege(PrivilegeNames.ADD_ROLE, requestAddRole.getFkSystemId());
            if (requestAddRole.getRoleText().equals(Constants.SYS_ADMIN))
                throw new Exception(Constants.CAN_NOT_CREATE_SYSADMIN);
            List<Long> roleIdList = entityManager.createQuery("SELECT (r.id) FROM RoleDao r WHERE r.fkSystemId = :fkSystemId")
                    .setParameter("fkSystemId", requestAddRole.getFkSystemId())
                    .getResultList();
            roleIdList.removeAll(requestAddRole.getPrivileges());
            if (roleIdList.size() == 0)
                throw new Exception(Constants.CAN_NOT_CREATE_SYSADMIN);
            RoleDao role = new RoleDao(requestAddRole.getRoleText(), requestAddRole.getFkSystemId(), user.getId());
            Long roleId = (Long) (entityManager.createQuery("SELECT MAX (r.id) FROM RoleDao r")
                    .getSingleResult())
                    + 1;
            role.setId(roleId);
            if (!transaction.isActive())
                transaction.begin();

            entityManager.persist(role);
            for (Long privilegeId : requestAddRole.getPrivileges()) {
                RolePrivilegeDao rp = new RolePrivilegeDao();
                rp.setFkRoleId(roleId);
                rp.setFkPrivilegeId(privilegeId);
                entityManager.persist(rp);
            }
            if (transaction.isActive())
                transaction.commit();
            return new StandardResponse();
        } catch (Exception ex) {
            if (transaction != null && transaction.isActive())
                transaction.rollback();
            throw ex;
        } finally {
            if (entityManager.isOpen())
                entityManager.close();
        }
    }

    public StandardResponse editRole(String token, RequestEditRole requestEditRole) throws Exception {
        EntityManager entityManager = Database.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        entityManager.getEntityManagerFactory().getCache().evictAll();
        try {
            try {
                RoleDao role = (RoleDao) entityManager.createQuery("SELECT r FROM RoleDao r WHERE r.id = :roleId")
                        .setParameter("roleId", requestEditRole.getFkRoleId())
                        .getSingleResult();
                if (role.getRole().equals(Constants.SYS_ADMIN))
                    throw new Exception(Constants.CAN_NOT_EDIT_SYSADMIN);
            } catch (NonUniqueResultException | NoResultException ex) {
                throw new Exception(Constants.NOT_VALID_ROLE);
            }
            UserDao user = UserDao.validate(token);
            Long fkSystemId = (Long) entityManager.createQuery("SELECT r.fkSystemId FROM RoleDao r WHERE r.id = :fkRoleId ")
                    .setParameter("fkRoleId", requestEditRole.getFkRoleId())
                    .getSingleResult();
            user.checkPrivilege(PrivilegeNames.EDIT_ROLE, fkSystemId);
            if (!transaction.isActive())
                transaction.begin();
            //delete all privileges of roll and add new privileges
            entityManager.createQuery("DELETE FROM RolePrivilegeDao rp WHERE rp.fkRoleId = :fkRoleId")
                    .setParameter("fkRoleId", requestEditRole.getFkRoleId())
                    .executeUpdate();
            RolePrivilegeDao rp;
            for (Long privilegeId : requestEditRole.getPrivileges()) {
                rp = new RolePrivilegeDao();
                rp.setFkRoleId(requestEditRole.getFkRoleId());
                rp.setFkPrivilegeId(privilegeId);
                entityManager.persist(rp);
            }
            if (transaction.isActive())
                transaction.commit();
            return new StandardResponse<>();
        } catch (Exception ex) {
            if (transaction != null && transaction.isActive())
                transaction.rollback();
            throw ex;
        } finally {
            if (entityManager.isOpen())
                entityManager.close();
        }
    }

    public StandardResponse removeRole(String token, Long fkRoleId) throws Exception {
        EntityManager entityManager = Database.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        entityManager.getEntityManagerFactory().getCache().evictAll();
        try {
            UserDao user = UserDao.validate(token);
            Long fkSystemId = (Long) entityManager.createQuery("SELECT r.fkSystemId FROM RoleDao r WHERE r.id = :fkRoleId")
                    .setParameter("fkRoleId", fkRoleId)
                    .getSingleResult();
            user.checkPrivilege(PrivilegeNames.REMOVE_ROLE, fkSystemId);
            if (!transaction.isActive())
                transaction.begin();
            Long size = (Long) entityManager.createQuery("SELECT COUNT (r) FROM RoleDao r WHERE r.id = :roleId AND r.role = :role")
                    .setParameter("role", Constants.SYS_ADMIN)
                    .setParameter("roleId", fkRoleId)
                    .getSingleResult();
            if (size.equals(1L))
                throw new Exception(Constants.CAN_NOT_REMOVE_SYSADMIN);

            entityManager.createQuery("DELETE FROM RoleDao r WHERE r.id = :roleId")
                    .setParameter("roleId", fkRoleId)
                    .executeUpdate();
            entityManager.createQuery("DELETE FROM RolePrivilegeDao rp WHERE rp.fkRoleId = :fkRoleId")
                    .setParameter("fkRoleId", fkRoleId)
                    .executeUpdate();

            if (transaction.isActive())
                transaction.commit();
            return new StandardResponse<>();
        } catch (Exception ex) {
            if (transaction != null && transaction.isActive())
                transaction.rollback();
            throw ex;
        } finally {
            if (entityManager.isOpen())
                entityManager.close();
        }
    }


    public StandardResponse getRolePrivileges(String token, Long fkRoleId) throws Exception {
        EntityManager entityManager = Database.getEntityManager();
        entityManager.getEntityManagerFactory().getCache().evictAll();
        try {
            UserDao.validate(token);
            List privileges = entityManager.createQuery("SELECT p FROM PrivilegeDao p JOIN RolePrivilegeDao rp ON p.id = rp.fkPrivilegeId WHERE rp.fkRoleId = :fkRoleId")
                    .setParameter("fkRoleId", fkRoleId)
                    .getResultList();
            ResponseGetPrivileges responseGetPrivileges = new ResponseGetPrivileges();
            responseGetPrivileges.setPrivileges(privileges);
            StandardResponse<ResponseGetPrivileges> response = new StandardResponse<>();
            response.setResponse(responseGetPrivileges);
            return response;
        } finally {
            if (entityManager.isOpen())
                entityManager.close();
        }
    }
}
