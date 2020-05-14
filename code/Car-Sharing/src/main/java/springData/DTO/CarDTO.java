package springData.DTO;

public class CarDTO {

   private String registrationNumber;

   private String carName;

   private String carMake;

   private String carColor;

   private String latitude;

   private String longitude;

   private boolean isActive;

   private int fuelLevel;

   // default and parameterized constructors
   public CarDTO() {
   }

   public String getRegistrationNumber() {
      return registrationNumber;
   }

   public void setRegistrationNumber(String registrationNumber) {
      this.registrationNumber = registrationNumber;
   }

   public String getCarName() {
      return carName;
   }

   public void setCarName(String carName) {
      this.carName = carName;
   }

   public String getCarMake() {
      return carMake;
   }

   public void setCarMake(String carMake) {
      this.carMake = carMake;
   }

   public String getCarColor() {
      return carColor;
   }

   public void setCarColor(String carColor) {
      this.carColor = carColor;
   }

   public String getLatitude() {
      return latitude;
   }

   public void setLatitude(String latitude) {
      this.latitude = latitude;
   }

   public String getLongitude() {
      return longitude;
   }

   public void setLongitude(String longitude) {
      this.longitude = longitude;
   }

   public boolean getIsActive() {
      return isActive;
   }

   public void setActive(boolean isActive) {
      this.isActive = isActive;
   }

   public int getFuelLevel() {
      return fuelLevel;
   }

   public void setFuelLevel(int fuelLevel) {
      this.fuelLevel = fuelLevel;
   }

}
// CarDTO
