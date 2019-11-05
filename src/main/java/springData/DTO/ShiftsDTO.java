package springData.DTO;

import java.util.ArrayList;
import java.util.List;

import springData.domain.Shift;

public class ShiftsDTO {

   private int timesheetId = -1;

   private List<Shift> shifts;

   // default and parameterized constructor
   public ShiftsDTO() {
      shifts = new ArrayList<Shift>();
   }

   public ShiftsDTO(List<Shift> shifts) {
      this.shifts = shifts;
   }

   public void addShift(Shift shift) {
      this.shifts.add(shift);
   }

   public List<Shift> getShifts() {
      return shifts;
   }

   public void setShifts(List<Shift> shifts) {
      this.shifts = shifts;
   }

   public int getTimesheetId() {
      return timesheetId;
   }

   public void setTimesheetId(int timesheetId) {
      this.timesheetId = timesheetId;
   }

}
