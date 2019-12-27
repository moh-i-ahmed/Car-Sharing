package springData.services;

import it.ozimov.springboot.mail.model.Email;
import it.ozimov.springboot.mail.model.defaultimpl.DefaultEmail;
import it.ozimov.springboot.mail.service.EmailService;
import it.ozimov.springboot.mail.service.exception.CannotSendEmailException;
import springData.DTO.UserDTO;
import springData.constants.Constants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;

import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

@Service
public class EmailServiceImpl {

   @Autowired
   public EmailService emailService;

   public void sendMimeEmailWithFreemarker(String subject, String template, UserDTO userDTO, Map<String, Object> modelObject)
         throws UnsupportedEncodingException, CannotSendEmailException, URISyntaxException {

      final Email email = DefaultEmail.builder()
            .from(new InternetAddress("web.carsharing@gmail.com", "Car Sharing"))
            .to(newArrayList(new InternetAddress(userDTO.getUsername(), userDTO.getFirstName())))
            .subject(subject)
            .body("")//this will be overridden by the template, anyway
            .encoding("UTF-8").build();

      emailService.send(email, template, modelObject);
   }

   //Function processing registration email requests
   public void sendRegistrationEmail(UserDTO userDTO) {
      try {
         String subject = "";
         String template = "";
         Map<String, Object> modelObject = null;

         subject = Constants.REGISTRATIONSUBJECT;
         template = Constants.REGISTRATIONTEMPLATE;
         modelObject = ImmutableMap.of(
               "name", userDTO.getFirstName()
               );

         //Send email with required info
         sendMimeEmailWithFreemarker(subject, template, userDTO, modelObject);
      } catch (UnsupportedEncodingException | CannotSendEmailException | URISyntaxException e) {
         e.printStackTrace();
      }
   }

   //Function processing password reset email requests
   public void sendResetEmail(UserDTO userDTO) {
      try {
         Map<String, Object> modelObject = null;

         modelObject = ImmutableMap.of(
               "password", userDTO.getPassword(),
               "name", userDTO.getFirstName()
               );

         //Send email with required info
         sendMimeEmailWithFreemarker(Constants.PASSWORDSUBJECT, Constants.PASSWORDTEMPLATE,
               userDTO, modelObject);
      } catch (UnsupportedEncodingException | CannotSendEmailException | URISyntaxException e) {
         e.printStackTrace();
      }
   }

}
//EmailServiceImpl
