package springData.DTO;

public class UserDTO {

   private String firstName;

   private String lastName;

   private String username;

   private String password;

   private String matchingPassword;

   private String roleName;

   private String organizationName;

   // default and parameterized constructors
   public UserDTO() {
   }

   // getter and setter
   public String getPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public String getMatchingPassword() {
      return matchingPassword;
   }

   public void setMatchingPassword(String matchingPassword) {
      this.matchingPassword = matchingPassword;
   }

   public void setFirstName(String firstName) {
      this.firstName = firstName;
   }

   public String getFirstName() {
      return firstName;
   }

   public void setLastName(String lastName) {
      this.lastName = lastName;
   }

   public String getLastName() {
      return lastName;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getUsername() {
      return username;
   }

   public String getRoleName() {
      return roleName;
   }

   public void setRoleName(String role) {
      this.roleName = role;
   }

   public String getOrganizationName() {
      return organizationName;
   }

   public void setOrganizationName(String organizationName) {
      this.organizationName = organizationName;
   }

}
//UserFormDTO
