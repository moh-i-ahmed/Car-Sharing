package springData.exception;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

   private static final Logger logger = LoggerFactory.getLogger(CustomAsyncExceptionHandler.class);

   @Override
   public void handleUncaughtException(Throwable throwable, Method method, Object... obj) {
      logger.info("Async Exception message - " + throwable.getMessage());
      logger.info("Method name - " + method.getName());

      for (Object param : obj) {
         logger.info("Parameter value - " + param);
      }
   }

}
// CustomAsyncExceptionHandler
