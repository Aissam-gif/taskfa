package com.example.taskfa.modelDao;

import com.example.taskfa.controllers.project.InvitationModelTable;
import com.example.taskfa.model.Project;
import com.example.taskfa.model.User;
import com.example.taskfa.utils.DBConfig;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.*;
import java.sql.*;
import java.util.Arrays;

public class UserDAO {
    /*
    MD5 method to Encrypt User password For signUp
     */
    private static String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
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

        String selectStm = "SELECT * FROM user WHERE email = '"+email
                +"' AND password = '"+ MD5(password) +"';";

            try {
                ResultSet rsUser = DBConfig.dbExecuteQuery(selectStm);
                System.out.println("wsel");
                User user =  getUserFromResultSet(rsUser);
                return user;
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
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
    public static void createUser(String firstName, String lastName, String status, File selectedFile, String email, String password) throws ClassNotFoundException {

        String insertStmtprepared = "INSERT INTO user" +
                "(firstName, lastName, status, image, email, password) " +
                "VALUES " +
                "(?,?,?,?,?,?)";
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


    }
}