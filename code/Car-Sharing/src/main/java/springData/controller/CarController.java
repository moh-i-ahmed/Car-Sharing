package springData.controller;

import java.security.Principal;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import springData.DTO.CarDTO;
import springData.constants.Constants;
import springData.domain.Car;
import springData.domain.Location;
import springData.repository.CarRepository;
import springData.repository.LocationRepository;
import springData.validator.CarDTOValidator;
import springData.validator.PasswordDTOValidator;

@Controller
@RequestMapping("/car")
public class CarController {

   @PersistenceContext
   EntityManager entityManager;

   private static final Logger LOGGER = LoggerFactory.getLogger(CarController.class);

   BCryptPasswordEncoder pe = new  BCryptPasswordEncoder();

   @Autowired private CarRepository carRepo;
   @Autowired private LocationRepository locationRepo;

   @InitBinder("carDTO")
   protected void initCarDTOBinder(WebDataBinder binder) {
      binder.addValidators(new CarDTOValidator());
   }

   @InitBinder("passwordDTO")
   protected void initPasswordDTOBinder(WebDataBinder binder) {
      binder.addValidators(new PasswordDTOValidator());
   }

   /* ----------------------------------------------------------
    * CAR MANAGEMENT CONTROLLERS
    * -----------------------------------------------------------*/

   @GetMapping("/add-car")
   public String addCar(Model model, Principal principal) {
      CarDTO carDTO = new CarDTO();

      model.addAttribute("true", true);
      model.addAttribute("false", false);
      model.addAttribute("carDTO", carDTO);
      model.addAttribute("username", principal.getName());

      return "admin/car/add-car";
   }

   @PostMapping("/add-car")
   public String addCar(@Valid @ModelAttribute("carDTO") CarDTO carDTO, BindingResult result, Model model,
           Principal principal) {

      Car carExists = carRepo.findByRegistrationNumber(carDTO.getRegistrationNumber());

      // Registration Number is already in use
      if (carExists != null) {
         result.rejectValue("registrationNumber", "", "Registration Number is already in use.");
         model.addAttribute("carDTO", carDTO);

         return "admin/car/add-car";
      }
      // Validation Errors
      if (result.hasErrors()) {
         model.addAttribute("carDTO", carDTO);
         model.addAttribute("username", principal.getName());

         System.err.println(result);

         return "admin/car/add-car";
      } else {
         // Create new Car using CarDTO details
         Car newCar = new Car();
         newCar.setRegistrationNumber(carDTO.getRegistrationNumber().toUpperCase());
         newCar.setCarName(carDTO.getCarName());
         newCar.setCarMake(carDTO.getCarMake());
         newCar.setCarColor(carDTO.getCarColor());
         newCar.setIsActive(carDTO.getIsActive());
         newCar.setFuelLevel(carDTO.getFuelLevel());

         Location requestLocation = new Location(carDTO.getLatitude(), carDTO.getLongitude());
         locationRepo.save(requestLocation);

         newCar.setLocation(requestLocation);

         // Save Car
         carRepo.save(newCar);

         LOGGER.info("\n Admin Log: Car added with " + carDTO.getRegistrationNumber().toUpperCase());

         return "redirect:/car/view-all-cars";
      }
   }

   @GetMapping("/edit-car/{registrationNumber}")
   public String editCar(@PathVariable String registrationNumber, Model model, Principal principal) {
      // Find Car by registration number
      Car car = carRepo.findByRegistrationNumber(registrationNumber);

      // Add Car details to DTO
      CarDTO carDTO = new CarDTO();
      carDTO.setRegistrationNumber(car.getRegistrationNumber().toUpperCase());
      carDTO.setCarName(car.getCarName());
      carDTO.setCarMake(car.getCarMake());
      carDTO.setCarName(car.getCarName());
      carDTO.setCarColor(car.getCarColor());
      carDTO.setActive(car.isIsActive());
      carDTO.setFuelLevel(car.getFuelLevel());
     // carDTO.setLatitude(car.getLocation().getLatitude());
     // carDTO.setLongitude(car.getLocation().getLongitude());

      model.addAttribute("true", true);
      model.addAttribute("false", false);
      model.addAttribute("regNumber", registrationNumber);
      model.addAttribute("carDTO", carDTO);
      model.addAttribute("username", principal.getName());

      return "admin/car/edit-car";
   }

   @GetMapping("/view-car/{registrationNumber}")
   public String viewCar(@PathVariable String registrationNumber, Model model, Principal principal) {
      // Find Car by @PathVariable
      Car car = carRepo.findByRegistrationNumber(registrationNumber);

      model.addAttribute("car", car);
      model.addAttribute("username", principal.getName());

      return "admin/car/view-car";
   }

   @PostMapping("/update-car/{registrationNumber}")
   public String updateCar(@Valid @ModelAttribute("carDTO") CarDTO carDTO, BindingResult result,
           @PathVariable String registrationNumber, Model model, RedirectAttributes redirectAttributes) {

      // Find Car by @PathVariable
      Car car = carRepo.findByRegistrationNumber(registrationNumber);

      Car carExists = carRepo.findByRegistrationNumber(carDTO.getRegistrationNumber());

      // Registration Number is already in use
      if (!(carDTO.getRegistrationNumber().equalsIgnoreCase(registrationNumber)) && (carExists != null)) {
         result.rejectValue("registrationNumber", "", "Registration Number is already in use.");
         model.addAttribute("carDTO", carDTO);

         return "admin/car/edit-car";
      }
      // Validation Errors
      if (result.hasErrors()) {
         model.addAttribute("carDTO", carDTO);

         System.err.println(result);
         return "admin/car/edit-car";
      } else {
         // Update Car using CarDTO details
         car.setCarName(carDTO.getCarName());
         car.setCarMake(carDTO.getCarMake());
         car.setCarColor(carDTO.getCarColor());
         car.setIsActive(carDTO.getIsActive());
         car.setFuelLevel(carDTO.getFuelLevel());

         // Update Location
         if (!carDTO.getLatitude().isEmpty() || carDTO.getLatitude() != null) {
            Location carLocation = car.getLocation();

            if (carLocation == null) {
               carLocation = new Location();
            }
            carLocation.setLatitude(carDTO.getLatitude());
            carLocation.setLongitude(carDTO.getLongitude());
            locationRepo.save(carLocation);

            car.setLocation(carLocation);
         }
         // Save Car
         carRepo.save(car);

         // Notification message
         String messageCode = Constants.NOTIFICATION_SUCCESS;
         String message = "Car details updated";

         redirectAttributes.addFlashAttribute("messageCode", messageCode);
         redirectAttributes.addFlashAttribute("message", message);

         LOGGER.info("\n Admin Log: Car details updated: " + car.getRegistrationNumber());

         return "redirect:/car/view-all-cars";
      }
   }

   @GetMapping("/view-all-cars")
   public String viewAllCars(Model model, Principal principal) {
      // List of Users
      List<Car> cars = (List<Car>) carRepo.findAll();

      model.addAttribute("cars", cars);
      model.addAttribute("username", principal.getName());

      return "admin/car/view-all-cars";
   }

   @Transactional
   @GetMapping("/delete-car/{registrationNumber}")
   public String deleteCar(@PathVariable String registrationNumber, RedirectAttributes redirectAttributes) {
      // Find Car by @PathVariable
      Car car = carRepo.findByRegistrationNumber(registrationNumber);

      // Drop Car from database
      entityManager.remove(car);

      LOGGER.info("\n Admin Log: Car deleted: " + car.getRegistrationNumber());

      // Notification message
      String messageCode = Constants.NOTIFICATION_SUCCESS;
      String message = "Car deleted from database";

      redirectAttributes.addFlashAttribute("messageCode", messageCode);
      redirectAttributes.addFlashAttribute("message", message);

      return "redirect:/car/view-all-cars";
   }

}
// CarController
