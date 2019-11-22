package springData.utils;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AccessCodeGenerator {

   private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
   
   public static String generateAccessCode(int count) {
      StringBuilder builder = new StringBuilder();
      while (count-- != 0) {
         int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
         builder.append(ALPHA_NUMERIC_STRING.charAt(character));
      }
      return builder.toString();
   }

}
//AccessCodeGenerator
//Adapted from example on -- https://dzone.com/articles/generate-random-alpha-numeric

