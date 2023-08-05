package constants;

public class EmployeeValidatorRegex {
    public static final String MAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    public static final String ID_REGEX = "^\\d{6}$";
    public static final String PHONE_REGEX = "^\\d{10}$";
    public static final String NAME_REGEX = "^[a-zA-Z\\s]{1,50}$";
}
