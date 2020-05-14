package springData.algorithm;

public class Haversine {

   public static final double R = 6372.8; // In kilometers

   // Returns distance (in kilometers) between pair of coordinates
   public static double haversine(double startLat, final double startLon, double endLat, final double endLon) {
      final double dLat = Math.toRadians(endLat - startLat);
      final double dLon = Math.toRadians(endLon - startLon);
      startLat = Math.toRadians(startLat);
      endLat = Math.toRadians(endLat);

      final double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(startLat) *
              Math.cos(endLat);
      final double c = 2 * Math.asin(Math.sqrt(a));
      return R * c;
   }
}
// Haversine
