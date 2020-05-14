package springData.services;

import it.ozimov.springboot.mail.model.Email;
import it.ozimov.springboot.mail.model.defaultimpl.DefaultEmail;
import it.ozimov.springboot.mail.service.EmailService;
import it.ozimov.springboot.mail.service.exception.CannotSendEmailException;
import springData.DTO.UserDTO;
import springData.constants.Constants;
import springData.domain.User;
import springData.domain.VerificationToken;
import springData.repository.VerificationTokenRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;

import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

@Service
public class EmailServiceImpl {

   @Autowired public EmailService emailService;
   @Autowired private VerificationTokenRepository verificationTokenRepo;

   @Async
   public void sendMimeEmailWithFreemarker(String subject, String template, UserDTO userDTO,
         Map<String, Object> modelObject) throws UnsupportedEncodingException,
   CannotSendEmailException, URISyntaxException {

      final Email email = DefaultEmail.builder()
            .from(new InternetAddress("web.carsharing@gmail.com", "Car Sharing"))
            .to(newArrayList(new InternetAddress(userDTO.getUsername(), userDTO.getFirstName())))
            .subject(subject)
            .body("")//this will be overridden by the template, anyway
            .encoding("UTF-8").build();

      emailService.send(email, template, modelObject);
   }

   /**
    * Builds & send registration email with verification token
    *
    * @param user - email recipient
    * @param request - HttpServletRequest
    */
   public void sendRegistrationEmail(User user, HttpServletRequest request) {
      try {
         VerificationToken token = new VerificationToken(user);
         verificationTokenRepo.save(token);

         String appUrl = request.getScheme() + Constants.SERVER_URL + "register/confirm/" + token.getToken();

         UserDTO userDTO = new UserDTO();
         userDTO.setFirstName(user.getFirstName());
         userDTO.setUsername(user.getUsername());

         // Build message body
         String subject = "";
         String template = "";
         Map<String, Object> modelObject = null;

         subject = Constants.REGISTRATIONSUBJECT;
         template = Constants.REGISTRATIONTEMPLATE;
         modelObject = ImmutableMap.of(
               "name", userDTO.getFirstName(),
               "appUrl", appUrl
               );

         // Send email with required info
         sendMimeEmailWithFreemarker(subject, template, userDTO, modelObject);
      } catch (UnsupportedEncodingException | CannotSendEmailException | URISyntaxException e) {
         e.printStackTrace();
      }
   }

   /**
    * Build & send password reset email
    * @param user
    * @param request
    */
   public void sendResetEmail(User user, HttpServletRequest request) {
      try {
         VerificationToken token = new VerificationToken(user);
         verificationTokenRepo.save(token);

         String appUrl = request.getScheme() + Constants.SERVER_URL + "register/reset-password/" + token.getToken();

         UserDTO userDTO = new UserDTO();
         userDTO.setFirstName(user.getFirstName());
         userDTO.setUsername(user.getUsername());

         // Build message body
         Map<String, Object> modelObject = null;
         modelObject = ImmutableMap.of(
               "name", userDTO.getFirstName(),
               "appUrl", appUrl
               );

         // Send email with required info
         sendMimeEmailWithFreemarker(Constants.PASSWORDSUBJECT, Constants.PASSWORDTEMPLATE,
               userDTO, modelObject);
      } catch (UnsupportedEncodingException | CannotSendEmailException | URISyntaxException e) {
         e.printStackTrace();
      }
   }

   //Function processing username update email requests
   public void sendUsernameChangeEmail(UserDTO userDTO) {
      try {
         Map<String, Object> modelObject = null;

         modelObject = ImmutableMap.of(
               "name", userDTO.getFirstName()
               );

         // Send email with required info
         sendMimeEmailWithFreemarker(Constants.USERNAME_UPDATE_SUBJECT, Constants.USERNAME_UPDATE_TEMPLATE,
               userDTO, modelObject);
      } catch (UnsupportedEncodingException | CannotSendEmailException | URISyntaxException e) {
         e.printStackTrace();
      }
   }
}
// EmailServiceImpl
