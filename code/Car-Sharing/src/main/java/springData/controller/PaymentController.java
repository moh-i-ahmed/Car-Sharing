package springData.controller;

import java.security.Principal;
import java.util.List;

import com.stripe.exception.StripeException;
import com.stripe.model.Card;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import springData.domain.Payment;
import springData.domain.User;
import springData.repository.UserRepository;
import springData.services.StripeService;

@Controller
@RequestMapping("/payment")
public class PaymentController {

   private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

   @Autowired private StripeService stripeService;
   @Autowired UserRepository userRepo;

   @Value("${stripe.publicKey}")
   private String stripePublicKey;

   @GetMapping("/wallet")
   public String wallet(Model model, Principal principal) throws Exception {
      //Get Logged in User
      User user = userRepo.findByUsername(principal.getName());

      List<Card> getCards = stripeService.getCards(user);
      model.addAttribute("cards", getCards);
      model.addAttribute("stripePublicKey", stripePublicKey);
      model.addAttribute("username", user.getFirstName() + " " + user.getLastName());

      return "/user/wallet";
   }

   @PostMapping("/add-card")
   public String addCard(Model model, Principal principal, HttpServletRequest request,
         RedirectAttributes redirectAttributes) throws StripeException {

      //Get Logged in User
      User user = userRepo.findByUsername(principal.getName());

      //Retrieve generated token
      String stripeToken = request.getParameter("stripeToken");
      
      System.out.println("Fisher " + stripeToken);

      //Add Card & get response message
      String message = stripeService.addCard(user, stripeToken);
      userRepo.save(user);

      redirectAttributes.addFlashAttribute("message", message);

      logger.info(message + " for: " + user.getUsername());

      return "redirect:/payment/wallet";
   }

   @PostMapping("/charge")
   public String chargeCard(HttpServletRequest request) throws Exception {
      String token = request.getParameter("stripeToken");
      Double amount = Double.parseDouble(request.getParameter("amount"));
      Payment payment = new Payment();
      payment.setPaymentAmount(2000);
     // stripeService.chargeCard(token, payment);
      return "redirect:/dashboard";
   }

   @GetMapping("/remove-card/{cardId}")
   public String removeCard(@PathVariable String cardId, Model model, Principal principal,
         RedirectAttributes redirectAttributes) throws StripeException {

      //Get Logged in User
      User user = userRepo.findByUsername(principal.getName());

      //Retrieve generated token
      stripeService.removeCard(user, cardId);
      userRepo.save(user);

      //Notification message
      String message = "Success: Card Removed";
      redirectAttributes.addFlashAttribute("message", message);

      logger.info("Card removed by " + user.getUsername());

      return "redirect:/payment/wallet";
   }

   @RequestMapping("/checkout")
   public String checkout(Model model) {
      model.addAttribute("amount", 50 * 100); // in cents
      model.addAttribute("stripePublicKey", stripePublicKey);
      model.addAttribute("currency", "GBP");
      return "checkout";
   }

}
//PaymentController
