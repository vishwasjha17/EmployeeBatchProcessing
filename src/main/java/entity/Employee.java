package entity;


import java.util.regex.Pattern;
import static constants.EmployeeValidatorRegex.*;

public class Employee {
       private String  offSet;
       private  String  empId;
       private  String  empName;
       private  String  empPhoneNumber;
       private  String  empEmailId;
       public Employee(){
              this.offSet = null;
              this.empId = null;
              this.empName = null;
              this.empPhoneNumber = null;
              this.empEmailId = null;
       }

       public Employee(String offSet,String empId, String empName, String empEmailId, String empPhoneNumber){
              this.offSet = offSet;
              this.empName = empName;
              this.empId = empId;
              this.empEmailId = empEmailId;
              this.empPhoneNumber = empPhoneNumber;
       }

       private Boolean validId(){
              return (this.empId == null? false :Pattern.compile(ID_REGEX).matcher(empId).matches());
       }
       private Boolean validEmail() {
              return (this.empEmailId == null? false :Pattern.compile(MAIL_REGEX).matcher(empEmailId).matches());
       }

       private Boolean validName(){
              return (this.empName == null ? false :Pattern.compile(NAME_REGEX).matcher(empName).matches());
       }

       private Boolean validPhoneNumber(){
              return (this.empPhoneNumber == null ? false :Pattern.compile(PHONE_REGEX).matcher(empPhoneNumber).matches());
       }

       public Boolean isValidEmployee(){
              return validId() && validEmail() && validName() && validPhoneNumber();
       }

       public String getEmpEmailId() {
              return empEmailId;
       }

       public String getEmpId() {
              return empId;
       }

       public String getEmpName() {
              return empName;
       }

       public String getEmpPhoneNumber() {
              return empPhoneNumber;
       }

       public String getOffSet(){ return offSet;}
       public void setEmpEmailId(String empEmailId) {
              this.empEmailId = empEmailId;
       }

       public void setEmpId(String empId) {
              this.empId = empId;
       }

       public void setEmpName(String empName) {
              this.empName = empName;
       }

       public void setEmpPhoneNumber(String empPhoneNumber) {
              this.empPhoneNumber = empPhoneNumber;
       }

}
