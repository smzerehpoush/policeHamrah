package com.nrdc.policeHamrah.service;

import com.nrdc.policeHamrah.exceptions.ServerException;
import com.nrdc.policeHamrah.helper.Constants;
import com.nrdc.policeHamrah.helper.Encryption;
import com.nrdc.policeHamrah.impl.RoleImpl;
import com.nrdc.policeHamrah.jsonModel.EncryptedRequest;
import com.nrdc.policeHamrah.jsonModel.EncryptedResponse;
import com.nrdc.policeHamrah.jsonModel.StandardResponse;
import com.nrdc.policeHamrah.jsonModel.jsonRequest.RequestAddRole;
import com.nrdc.policeHamrah.jsonModel.jsonRequest.RequestEditRole;
import com.nrdc.policeHamrah.model.dao.UserDao;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/role")
public class RoleServices {
    private static Logger logger = Logger.getLogger(RoleServices.class.getName());

    /**
     * 05
     * add a role
     *
     * @param encryptedRequest RequestAddRole
     * @return simple StandardResponse to handle state
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addRole(EncryptedRequest encryptedRequest) throws Exception {
        logger.info("++================== addRole SERVICE : START ==================++");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            RequestAddRole requestAddRole = objectMapper.readValue(Encryption.decryptRequest(encryptedRequest), RequestAddRole.class);
            StandardResponse response = new RoleImpl().addRole(encryptedRequest.getToken(), requestAddRole);
            String key = UserDao.getKey(encryptedRequest.getToken()).getKey();
            EncryptedResponse encryptedResponse = Encryption.encryptResponse(key, response);
            Response finalResponse = Response.status(200).entity(encryptedResponse).build();
            logger.info("++================== addRole SERVICE : END ==================++");
            return finalResponse;
        } catch (Exception ex) {
            return ServerException.create("++================== addRoles SERVICE : EXCEPTION ==================++", ex, encryptedRequest.getToken());
        }
    }

    /**
     * 06
     * edit a role
     *
     * @param encryptedRequest RequestEditRole
     * @return simple StandardResponse to handle state
     */
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response editRole(EncryptedRequest encryptedRequest) throws Exception {
        logger.info("++================== editRole SERVICE : START ==================++");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            RequestEditRole requestEditRole = objectMapper.readValue(Encryption.decryptRequest(encryptedRequest), RequestEditRole.class);
            StandardResponse response = new RoleImpl().editRole(encryptedRequest.getToken(), requestEditRole);
            String key = UserDao.getKey(encryptedRequest.getToken()).getKey();
            EncryptedResponse encryptedResponse = Encryption.encryptResponse(key, response);
            Response finalResponse = Response.status(200).entity(encryptedResponse).build();
            logger.info("++================== editRole SERVICE : END ==================++");
            return finalResponse;
        } catch (Exception ex) {
            return ServerException.create("++================== editRoles SERVICE : EXCEPTION ==================++", ex, encryptedRequest.getToken());
        }
    }

    /**
     * 07
     * remove role of user
     *
     * @param token    user token
     * @param fkRoleId id of role
     * @return simple StandardResponse to handle state
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeRole(@QueryParam("token") String token, @QueryParam("fkRoleId") Long fkRoleId) throws Exception {
        logger.info("++================== removeRole SERVICE : START ==================++");
        try {
            if (token == null || fkRoleId == null) {
                throw new Exception(Constants.NOT_VALID_REQUEST);
            }
            StandardResponse response = new RoleImpl().removeRole(token, fkRoleId);
            String key = UserDao.getKey(token).getKey();
            EncryptedResponse encryptedResponse = Encryption.encryptResponse(key, response);
            Response finalResponse = Response.status(200).entity(encryptedResponse).build();
            logger.info("++================== removeRole SERVICE : END ==================++");
            return finalResponse;
        } catch (Exception ex) {
            return ServerException.create("++================== removeRoles SERVICE : EXCEPTION ==================++", ex, token);
        }
    }

    /**
     * 08
     * get privileges of a role
     *
     * @param token    user token
     * @param fkRoleId id of role
     * @return list of privileges of a role
     */
    @Path("/privileges")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRolePrivileges(@QueryParam("token") String token, @QueryParam("fkRoleId") Long fkRoleId) throws Exception {
        logger.info("++================== getRolePrivileges SERVICE : START ==================++");
        try {
            if (token == null || fkRoleId == null) {
                throw new Exception(Constants.NOT_VALID_REQUEST);
            }
            StandardResponse response = new RoleImpl().getRolePrivileges(token, fkRoleId);
            String key = UserDao.getKey(token).getKey();
            EncryptedResponse encryptedResponse = Encryption.encryptResponse(key, response);
            Response finalResponse = Response.status(200).entity(encryptedResponse).build();
            logger.info("++================== getRolePrivileges SERVICE : END ==================++");
            return finalResponse;
        } catch (Exception ex) {
            return ServerException.create("++================== getRolePrivileges SERVICE : EXCEPTION ==================++", ex, token);
        }
    }
}

