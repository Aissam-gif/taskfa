package com.example.taskfa.modelDao;

import com.example.taskfa.controllers.project.InvitationModelTable;
import com.example.taskfa.model.User;
import com.example.taskfa.utils.DBConfig;
import com.example.taskfa.utils.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.*;
import java.sql.*;

public class UserDAO {

    public static boolean verifyQuestion(String email, String answer) {
        String selectStatement = "SELECT answer FROM user where email = '"+email+"';";
        String result = null;
        try {
            ResultSet rsUser = DBConfig.dbExecuteQuery(selectStatement);
            while (rsUser.next()){
                result = rsUser.getString("answer");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return answer.equals(result);
    }

    public static void resetPassword(String email,String password) {
        String updateStatement = "UPDATE user SET password = '"+MD5(password)+"' WHERE email = '"+email+"';";
        try {
            DBConfig.dbExecuteUpdate(updateStatement);
         } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*
    MD5 method to Encrypt User password For signUp
     */
    private static String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100), 1, 3);
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    /*
    SELECT USER INFORMATIONS FOR LOGIN
     */
    public static User searchUser(String email, String password) throws NoSuchAlgorithmException {
        User user = null;
        String selectStm = "SELECT * FROM user WHERE email = '"+email
                +"' AND password = '"+ MD5(password) +"';";

            try {
                ResultSet rsUser = DBConfig.dbExecuteQuery(selectStm);
                user =  getUserFromResultSet(rsUser);
                return user;
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        UserSession.setCurrentUser(user);
        return null;
    }

    private static User getUserFromResultSet(ResultSet rs) throws SQLException {
        User user = null;
        if (rs.next()) {
            user = new User();
            user.setIdUser(rs.getInt("iduser"));
            user.setFirstName(rs.getString("firstName"));
            user.setLastName(rs.getString("lastName"));
            user.setStatus(rs.getString("status"));
            user.setAdmin(false);
            Blob blob = rs.getBlob("image");
            InputStream inputStream = blob.getBinaryStream();
            Image image = new Image(inputStream);
            user.setImage(image);
        }
        return user;
    }

    /*
    CREATE USER ROW IN DATABASE FOR SIGN UP
     */
    public static void createUser(String firstName, String lastName, String status, File selectedFile, String email, String password,String question) throws ClassNotFoundException {

        String insertStmtprepared = "INSERT INTO user" +
                "(firstName, lastName, status, image, email, password,answer) " +
                "VALUES " +
                "(?,?,?,?,?,?,?)";

        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(selectedFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            DBConfig.dbConnect();
            Connection conn = DBConfig.getConn();
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(insertStmtprepared);
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, status);
            ps.setBinaryStream(4, fileInputStream, (int) selectedFile.length());
            ps.setString(5, email);
            ps.setString(6, MD5(password));
            ps.setString(7,question);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        /*
        try {
            DBConfig.dbExecuteUpdate(insertStmt);
        } catch(SQLException e){
            System.out.println("Error Occurs when Creating new user");
        }
         */
    }

    /*
       Get Invitations for the user to join projects
     */
    public static ObservableList<InvitationModelTable> getInvitations(int userid) throws ClassNotFoundException, SQLException {
        String selectStatement = "SELECT iduser,projectid,title,usersNumber,project.status,firstName,lastName FROM user_has_invitation " +
                "INNER JOIN project ON  user_has_invitation.project_projectid = projectid " +
                "INNER JOIN user_has_project ON user_has_project.project_projectid = user_has_invitation.project_projectid " +
                "INNER JOIN user ON user_has_project.user_iduser = user.iduser " +
                "WHERE user_has_invitation.user_iduser = "+userid+" AND invitation_status = 0;";

        try {
            ResultSet rs = DBConfig.dbExecuteQuery(selectStatement);
            ObservableList<InvitationModelTable> invitationList = getInvitationList(rs);
            return invitationList;
        } catch (SQLException e) {
            throw e;
        }
    }
    private static ObservableList<InvitationModelTable> getInvitationList(ResultSet rs) throws SQLException {
        ObservableList<InvitationModelTable> invitationList = FXCollections.observableArrayList();
        while (rs.next()) {
            InvitationModelTable invitationModel = new InvitationModelTable();
            invitationModel.setUserId(rs.getInt("iduser"));
            invitationModel.setProjectId(rs.getInt("projectid"));
            invitationModel.setTitle(rs.getString("title"));
            invitationModel.setMembersNum(rs.getInt("usersNumber"));
            invitationModel.setStatus(rs.getString("status"));
            invitationModel.setProjectOwner(rs.getString("firstName")+" "+rs.getString("lastName"));
            System.out.println(invitationModel);
            invitationList.add(invitationModel);
        }
        return invitationList;
    }

    public static void updateInvitation(int projectId, int userId, int status) throws SQLException, ClassNotFoundException {
        String preparedStatement = "UPDATE user_has_invitation" +
                " SET invitation_status = ? WHERE project_projectid = ? AND user_iduser = ?;";

            DBConfig.dbConnect();
            Connection conn = DBConfig.getConn();
            PreparedStatement ps = conn.prepareStatement(preparedStatement);
            ps.setInt(2, projectId);
            ps.setInt(3, userId);
            ps.setInt(1, status);
            ps.executeUpdate();



            if (status == 1) {
                preparedStatement = "INSERT INTO user_has_project" +
                        " VALUES (?,?,?);";
                ps = conn.prepareStatement(preparedStatement);
                ps.setInt(2, projectId);
                ps.setInt(3, 0);
                ps.setInt(1, userId);
                ps.executeUpdate();
                preparedStatement = "UPDATE project " +
                        "SET usersNumber = usersNumber+1 WHERE projectid = ?;";
                ps = conn.prepareStatement(preparedStatement);
                ps.setInt(1,projectId);
                ps.executeUpdate();
            } else {
                preparedStatement = "DELETE FROM user_has_invitation" +
                        " WHERE project_projectid = ? AND user_iduser = ?;";
                ps = conn.prepareStatement(preparedStatement);
                ps.setInt(1, projectId);
                ps.setInt(2, userId);
                ps.executeUpdate();
            }
    }


    public static boolean isAdminInProject(int userId, int projectId) throws SQLException, ClassNotFoundException {
        String selectStm = "SELECT role FROM user_has_project " +
                "WHERE user_iduser = "+userId+" AND project_projectid = "+projectId+";";
        try {
            ResultSet rsTasks = DBConfig.dbExecuteQuery(selectStm);
            rsTasks.next();
            return rsTasks.getInt("role") == 1 ?  true : false;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("SQL select operation has been failed: " + e);
            throw e;
        }
    }



}
