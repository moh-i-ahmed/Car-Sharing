package springData.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import springData.domain.Request;
import springData.repository.RequestRepository;
import springData.services.SseService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/sse")
public class SseRestController {

   private static final Logger logger = LoggerFactory.getLogger(SseRestController.class);

   @Autowired SseService sseService;
   @Autowired private RequestRepository requestRepo;

   final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

   /**
    * RestController listening for SSE requests
    *
    * @param requestID - request's ID
    * @throws InterruptedException
    * @throws IOException
    * @return emitter & HttpStatus
    */
   @GetMapping("/notification/{requestID}")
   public ResponseEntity<SseEmitter> doNotify(@PathVariable int requestID) throws InterruptedException, IOException {
      logger.info("Sse Notifier Rest Controller");

      // Find Request by ID
      Request request = requestRepo.findById(requestID);

      // Get Current Timestamp
      LocalDateTime currentTime = LocalDateTime.now();

      final SseEmitter emitter = new SseEmitter();
      sseService.addEmitter(emitter);

      // Before Request
      if (currentTime.isBefore(request.getStartTime())) {
         // Send request hasn't started
         sseService.notifyUnstartedRequest();
      } else if (currentTime.isAfter(request.getEndTime())) {
         // Request Ended
         // Send request has ended
         sseService.notifyCompletedRequest();
         //Thread.sleep(1000);
      } else if ((currentTime.isAfter(request.getStartTime())) && (currentTime.isBefore(request.getEndTime()))) {
         // Active Request
         // Send timer
         sseService.notifyActiveRequest();
      }

      emitter.onCompletion(() -> sseService.removeEmitter(emitter));
      emitter.onTimeout(() -> sseService.removeEmitter(emitter));
      return new ResponseEntity<>(emitter, HttpStatus.OK);
   }

}
// SseRestController
