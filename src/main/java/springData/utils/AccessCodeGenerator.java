package springData.utils;

public class AccessCodeGenerator {

   private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
   
   public static String generateAccessCode() {
      // Code size
      int size = 6;

      StringBuilder builder = new StringBuilder();
      while (size-- != 0) {
         int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
         builder.append(ALPHA_NUMERIC_STRING.charAt(character));
      }
      return builder.toString();
   }

}
//AccessCodeGenerator
//Adapted from example on -- https://dzone.com/articles/generate-random-alpha-numeric

