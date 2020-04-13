package springData.algorithm;

public class Haversine {

   public static final double R = 6372.8; // In kilometers

   // Returns distance (in kilometers) between pair of coordinates 
   public static double haversine(double startLat, double startLon, double endLat, double endLon) {
      double dLat = Math.toRadians(endLat - startLat);
      double dLon = Math.toRadians(endLon - startLon);
      startLat = Math.toRadians(startLat);
      endLat = Math.toRadians(endLat);

      double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(startLat) * Math.cos(endLat);
      double c = 2 * Math.asin(Math.sqrt(a));
      return R * c;
   }
}
//Haversine
// Extract from https://rosettacode.org/wiki/Haversine_formula under Java
