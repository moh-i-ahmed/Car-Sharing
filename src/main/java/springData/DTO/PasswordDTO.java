package springData.DTO;

public class PasswordDTO {

   private String password;

   private String confirmPassword;


   // default and parameterized constructors
   public PasswordDTO() {
   }

   // getter and setter
   public String getPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public String getConfirmPassword() {
      return confirmPassword;
   }

   public void setConfirmPassword(String password) {
      this.confirmPassword = password;
   }

}
//UserFormDTO
