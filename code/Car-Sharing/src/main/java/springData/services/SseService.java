package springData.services;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Service emitting server event with ID and data value
 *
 * @author mia17
 */
@Service
public class SseService {

   final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("hh:mm:ss a");
   final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

   public void addEmitter(final SseEmitter emitter) {
      emitters.add(emitter);
   }

   public void removeEmitter(final SseEmitter emitter) {
      emitters.remove(emitter);
   }

   /**
    * Emits SSE every second while requests are In Progress
    *
    * @throws IOException
    */
   @Async
   @Scheduled(fixedRate = 1000)
   public void notifyActiveRequest() throws IOException {
      System.err.println("Active Request Notifier Service");
      final List<SseEmitter> deadEmitters = new ArrayList<>();
      emitters.forEach(emitter -> {
         try {
            // Send message
            emitter.send(SseEmitter.event()
                    .id("active")
                    .data(DATE_FORMATTER.format(LocalDateTime.now())));
         } catch (final Exception e) {
            deadEmitters.add(emitter);
         }
      });
      emitters.removeAll(deadEmitters);
   }

   /**
    * Emits SSE for requests that have not begun
    *
    * @throws IOException
    */
   @Async
   public void notifyUnstartedRequest() throws IOException {
      System.err.println("Unstarted Request Notifier Service");

      final List<SseEmitter> deadEmitters = new ArrayList<>();
      emitters.forEach(emitter -> {
         try {
            // Send message
            emitter.send(SseEmitter.event()
                    .id("unstarted")
                    .data(DATE_FORMATTER.format(LocalDateTime.now())));
         } catch (final Exception e) {
            deadEmitters.add(emitter);
         }
      });
      emitters.removeAll(deadEmitters);
   }

   /**
    * Emits SSE for completed requests
    * Sends ID & data value ended
    *
    * @throws IOException
    */
   @Async
   public void notifyCompletedRequest() throws IOException {
      System.err.println("Completed Request Notifier Service");

      final List<SseEmitter> deadEmitters = new ArrayList<>();
      emitters.forEach(emitter -> {
         try {
            // Send message
            emitter.send(SseEmitter.event()
                    .id("ended")
                    .data("ended"));
         } catch (final Exception e) {
            deadEmitters.add(emitter);
         }
      });
      emitters.removeAll(deadEmitters);
   }

   /*
   emitters.forEach(emitter -> {
        try {
           // Before Request Start
           if (request.getStartTime().isBefore(LocalDateTime.now())) {
              emitter.send("Unstarted");
              //Thread.sleep(100);
              emitter.send(DATE_FORMATTER.format(LocalDateTime.now()));
           }
           // Request Ended
           else if (request.getEndTime().isBefore(LocalDateTime.now())) {
              emitter.send("Ended");
              //Thread.sleep(100);
              emitter.send(DATE_FORMATTER.format(LocalDateTime.now()));
           }
           else {
              // Send message
              emitter.send(DATE_FORMATTER.format(LocalDateTime.now()));
           }
        } catch (Exception e) {
           deadEmitters.add(emitter);
        }
     });

     @Async
  @Scheduled(fixedRate = 1000)
  public void notifyUnstartedRequest() throws IOException {
     System.err.println("Notifier Service");
     List<SseEmitter> deadEmitters = new ArrayList<>();

     LocalDateTime currentTime = LocalDateTime.now();

     // Update expired requests
     List<Request> activeRequests = requestRepo.findAllByEndTime(currentTime, 
           Constants.REQUEST_STATUS_IN_PROGRESS);

     for (Request request: activeRequests) {

        emitters.forEach(emitter -> {
           try {
              // Before Request Start
              if (currentTime.isBefore(request.getStartTime())) {
                 //emitter.send(DATE_FORMATTER.format(LocalDateTime.now()));
                 emitter.send(SseEmitter.event()
                       .id("unstarted")
                       .data(DATE_FORMATTER.format(LocalDateTime.now()))); 
              }
              // Request Ended
              else if (currentTime.isAfter(request.getEndTime())) {
                 //Thread.sleep(100);
                 emitter.send(DATE_FORMATTER.format(LocalDateTime.now()));
                 /*emitter.send(SseEmitter.event()
                       .id("ended")
                       .data("ended")); 
              }
              else if (currentTime.isAfter(request.getStartTime()) && currentTime.isBefore(request.getEndTime())) {
                 // Send message
                 emitter.send(DATE_FORMATTER.format(LocalDateTime.now()));
                 /* emitter.send(SseEmitter.event(); 
                       .id("active")
                       .data(DATE_FORMATTER.format(LocalDateTime.now())));
              }
              //emitter.send(DATE_FORMATTER.format(LocalDateTime.now()));
           } catch (Exception e) {
              deadEmitters.add(emitter);
           }
        });
     }

     emitters.removeAll(deadEmitters);
  }

    */

}
// SseService
