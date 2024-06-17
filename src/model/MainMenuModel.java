package model;

import other.Department;

import java.sql.Connection;
import java.util.ArrayList;

public class MainMenuModel {

    public int getDepartmentIndex(AuthorizationModel authorizationModel) {
        try {
            return authorizationModel.getDepartmentIndex();
        }
        catch (Exception e){
            return -1;
        }
    }

    public int getTeacherId(AuthorizationModel authorizationModel) {
        try {
            return authorizationModel.getSelectedTeacherId();
        }
        catch (Exception e){
            return -1;
        }
    }

    public ArrayList<Department> getDepartmentList(AuthorizationModel authorizationModel) {
        try {
            return authorizationModel.getDepartmentListFromDb();
        }
        catch (Exception e){
            return null;
        }
    }

     public Connection checkConnection(AuthorizationModel authorizationModel){
         try {
             return authorizationModel.getConnection();
         } catch (Exception ex) {
             return null;
         }
     }

     public boolean checkAccess(AuthorizationModel authorizationModel){
         try {
             return authorizationModel.isAuthorizationComplete();
         } catch (Exception ex) {
             return false;
         }
     }

}
