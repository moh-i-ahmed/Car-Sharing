package springData.controller;

import java.security.Principal;
import java.util.List;

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
import springData.DTO.UserDTO;
import springData.domain.Car;
import springData.domain.Request;
import springData.domain.Role;
import springData.domain.User;
import springData.repository.AddressRepository;
import springData.repository.RoleRepository;
import springData.repository.CarRepository;
import springData.repository.RequestRepository;
import springData.repository.UserRepository;
import springData.services.EmailServiceImpl;
import springData.validator.CarDTOValidator;
import springData.validator.PasswordDTOValidator;
import springData.validator.UserDTOValidator;

@Controller
@RequestMapping("/car")
public class CarController {

   private final Logger logger = LoggerFactory.getLogger(CarController.class);

   BCryptPasswordEncoder pe = new  BCryptPasswordEncoder();

   @Autowired UserRepository userRepo;
   @Autowired RoleRepository roleRepo;
   @Autowired CarRepository carRepo;
   @Autowired RequestRepository requestRepo;
   @Autowired AddressRepository addressRepo;
   @Autowired private EmailServiceImpl emailService;

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

      model.addAttribute("carDTO", carDTO);
      model.addAttribute("username", principal.getName());

      return "admin/car/add-Car";
   }

   @PostMapping("/add-car")//create")
   public String addCar(@Valid @ModelAttribute("carDTO") CarDTO carDTO, BindingResult result, Model model,
         Principal principal) {

      Car carExists = carRepo.findByRegistrationNumber(carDTO.getRegistrationNumber());

      //Registration Number is already in use
      if (carExists != null) {
         result.rejectValue("registrationNumber", "", "Registration Number is already in use.");
         model.addAttribute("carDTO", carDTO);

         return "admin/car/add-car";
      }

      if (result.hasErrors()) {
         //List of Roles
         model.addAttribute("carDTO", carDTO);
         model.addAttribute("username", principal.getName());

         System.err.println(result);

         return "admin/car/add-car";
      } else {
         //Create new Car using CarDTO details
         Car newCar = new Car();
         newCar.setCarName(carDTO.getCarName());
         newCar.setCarMake(carDTO.getCarMake());
         newCar.setCarColor(carDTO.getCarColor());
         newCar.setIsActive(carDTO.getIsActive());
         newCar.setFuelLevel(carDTO.getFuelLevel());
         newCar.setLatitude(carDTO.getLatitude());
         newCar.setLongitude(carDTO.getLongitude());

         //Save Car
         carRepo.save(newCar);

         logger.info("\n Admin Log: Car added with " + carDTO.getRegistrationNumber());

         return "redirect:/admin/car/view-all-cars";
      }
   }

   @GetMapping("/edit-car/{registrationNumber}")
   public String editCar(@PathVariable String registrationNumber, Model model, Principal principal) {
      //Find Car by @PathVariable
      Car car = carRepo.findByRegistrationNumber(registrationNumber);

      //Add Car details to DTO
      CarDTO carDTO = new CarDTO();
      carDTO.setRegistrationNumber(car.getRegistrationNumber());
      carDTO.setCarName(car.getCarName());
      carDTO.setCarMake(car.getCarMake());
      carDTO.setCarName(car.getCarName());
      carDTO.setCarColor(car.getCarColor());
      carDTO.setActive(car.isIsActive());
      carDTO.setFuelLevel(car.getFuelLevel());
      carDTO.setLatitude(car.getLatitude());
      carDTO.setLongitude(car.getLongitude());

      model.addAttribute("regNumber", registrationNumber);
      model.addAttribute("carDTO", carDTO);
      model.addAttribute("username", principal.getName());

      return "admin/car/edit-car";
   }

   @GetMapping("/view-car/{registrationNumber}")
   public String viewCar(@PathVariable String registrationNumber, Model model, Principal principal) {
      //Find Car by @PathVariable
      Car car = carRepo.findByRegistrationNumber(registrationNumber);

      model.addAttribute("car", car);
      model.addAttribute("username", principal.getName());

      return "admin/car/view-car";
   }

   @GetMapping("/view-request/{requestID}")
   public String viewRequestDetails(@PathVariable int requestID, Model model) {
      //Find Request by ID
      Request request = requestRepo.findById(requestID);

      model.addAttribute("request", request);
      model.addAttribute("username", request.getUser().getFirstName() + " " + request.getUser().getLastName());

      return "/user/view-request";
   }

   @PostMapping("/update-car/{registrationNumber}")
   public String updateCar(@Valid @ModelAttribute("carDTO") CarDTO carDTO, BindingResult result,
         @PathVariable String registrationNumber, Model model, RedirectAttributes redirectAttributes) {

      //Find Car by @PathVariable
      Car car = carRepo.findByRegistrationNumber(registrationNumber);

      if (result.hasErrors()) {
         model.addAttribute("carDTO", carDTO);

         System.err.println(result);
         return "admin/car/edit-car";
      } 
      else {
         //Update Car using CarDTO details
         car.setCarName(carDTO.getCarName());
         car.setCarMake(carDTO.getCarMake());
         car.setCarColor(carDTO.getCarColor());
         car.setIsActive(carDTO.getIsActive());
         car.setFuelLevel(carDTO.getFuelLevel());
         car.setLatitude(carDTO.getLatitude());
         car.setLongitude(carDTO.getLongitude());

         //Save Car
         carRepo.save(car);

         //Notification message
         String message = "Success: Car details updated";
         redirectAttributes.addFlashAttribute("message", message);

         return "redirect:/car/view-all-cars";
      }
   }

   @GetMapping("/delete-car/{registrationNumber}")
   public String deleteCar(@PathVariable String registrationNumber, RedirectAttributes redirectAttributes) {
      //Find Car by @PathVariable
      Car car = carRepo.findByRegistrationNumber(registrationNumber);

      /*if (!(addressRepo.findAllByUser(user) == null)) {
         //Delete the user's addresses
         addressRepo.deleteAll(addressRepo.findAllByUser(user));
      } */
      //Drop Car from database
      carRepo.delete(car);

      logger.info("\n Car deleted: " + car.getRegistrationNumber() +
            "\n by Admin.");

      String message = "Success: Car deleted from database";
      redirectAttributes.addFlashAttribute("message", message);

      return "redirect:/car/view-all-cars";
   }

   @GetMapping("/view-all-cars")
   public String viewAllCars(Model model, Principal principal) {
      //List of Users
      List<Car> cars = (List<Car>) carRepo.findAll();

      model.addAttribute("cars", cars);
      model.addAttribute("username", principal.getName());

      return "admin/car/view-all-cars";
   }
 
}
//CarController
