package springData.DTO;

public class UserDTO {

   private int userID;

   private String firstName;

   private String lastName;

   private String username;

   private String password;

   private String matchingPassword;

   private String phoneNumber;

   private String roleName;

   private String driverLicense;

   // default and parameterized constructors
   public UserDTO() {
   }

   // getter and setter
   public String getPassword() {
      return password;
   }

   public int getUserID() {
      return userID;
   }

   public void setUserID(int userID) {
      this.userID = userID;
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

   public String getPhoneNumber() {
      return phoneNumber;
   }

   public void setPhoneNumber(String phoneNumber) {
      this.phoneNumber = phoneNumber;
   }

   public String getDriverLicense() {
      return driverLicense;
   }

   public void setDriverLicense(String driverLicense) {
      this.driverLicense = driverLicense;
   }

   public String getRoleName() {
      return roleName;
   }

   public void setRoleName(String roleName) {
      this.roleName = roleName;
   }


}
// UserDTO
