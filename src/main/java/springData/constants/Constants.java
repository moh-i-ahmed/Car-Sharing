package springData.constants;

public class Constants {

   // RegEex patterns
   public static final String REGEX_PHONE_NUMBER = "^(?:0|\\+?44)(?:\\d\\s?){9,10}$";
   /*public static final String REGEX_PHONE_NUMBER = "^(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?(?:\\(\\s*"
         + "([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)|([2-9]1[02-9]|[2-9][02-8]"
         + "1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]"
         + "{2})\\s*(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?$"; */
   public static final String REGEX_LICENSE = "^[A-Z9]{5}\\d{6}[A-Z9]{2}\\d[A-Z]{2}$";

   // Registration Email template
   public static final String REGISTRATIONSUBJECT = "Verify Email Address";
   public static final String REGISTRATIONTEMPLATE = "registration.ftl";

   // Password Reset Email template
   public static final String PASSWORDSUBJECT = "Password Reset";
   public static final String PASSWORDTEMPLATE = "passwordReset.ftl";

   // Profile update Email template
   public static final String USERNAME_UPDATE_SUBJECT = "Profile Updated";
   public static final String USERNAME_UPDATE_TEMPLATE = "profile-update.ftl";

   // Request Status Messages
   public static final String REQUEST_STATUS_COMPLETE = "Completed";
   public static final String REQUEST_STATUS_IN_PROGRESS = "In Progress";
   public static final String REQUEST_STATUS_UNSTARTED = "Unstarted";
   public static final String REQUEST_STATUS_CANCELLED = "Cancelled";

   // Invoice Status Messages
   public static final String INVOICE_STATUS_PAID = "Paid";
   public static final String INVOICE_STATUS_UNPAID = "Unpaid";

   // Payment Prices In Pence/Cents
   public static final double PRICE_BASE_CHARGE = 5 * 100;
   public static final double PRICE_PER_KILOMETER = 0.3 * 100;
   public static final double PRICE_PER_MINUTE = 0.2 * 100;
   public static final double PRICE_CANCELLATION = 8 * 100;

   // Notification Status Codes
   public static final String NOTIFICATION_SUCCESS = "Success";
   public static final String NOTIFICATION_WARNING = "Warning";
   public static final String NOTIFICATION_ERROR = "Error";

   //public static final String SERVER_URL = "://localhost:8080/";
   public static final String SERVER_URL = "://heroku-carsharing.herokuapp.com/";

}
//Constants
