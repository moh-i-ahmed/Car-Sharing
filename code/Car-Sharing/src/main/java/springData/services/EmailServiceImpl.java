package springData.services;

import javax.mail.internet.InternetAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import it.ozimov.springboot.mail.model.Email;
import it.ozimov.springboot.mail.model.defaultimpl.DefaultEmail;
import it.ozimov.springboot.mail.service.EmailService;

@Service
public class EmailServiceImpl {
  
   private JavaMailSender mailSender;
   
   @Autowired
   public EmailService emailService;

  /* public void sendEmailWithoutTemplating(){
      final Email email = DefaultEmail.builder()
           .from(new InternetAddress("cicero@mala-tempora.currunt", "Marco Tullio Cicerone "))
           .to(Lists.newArrayList(new InternetAddress("titus@de-rerum.natura", "Pomponius Attĭcus")))
           .subject("Laelius de amicitia")
           .body("Firmamentum autem stabilitatis constantiaeque eius, quam in amicitia quaerimus, fides est.")
           .encoding("UTF-8").build();

      emailService.send(email);
   }*/
}