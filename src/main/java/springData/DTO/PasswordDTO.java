package springData.DTO;

public class PasswordDTO {

   private String currentPassword;

   private String newPassword;

   private String confirmPassword;


   // default and parameterized constructors
   public PasswordDTO() {
   }

   public String getCurrentPassword() {
      return currentPassword;
   }

   public void setCurrentPassword(String currentPassword) {
      this.currentPassword = currentPassword;
   }

   public String getNewPassword() {
      return newPassword;
   }

   public void setNewPassword(String newPassword) {
      this.newPassword = newPassword;
   }

   public String getConfirmPassword() {
      return confirmPassword;
   }

   public void setConfirmPassword(String password) {
      this.confirmPassword = password;
   }

}
// PasswordDTO
