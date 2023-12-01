public class PasswordValidate {
    public static boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$";
        return email.matches(regex);
    }
    
    public static int checkPasswordStrength(String password) {
        int strengthPercentage = 0;

        // Check length of password
        if (password.length() >= 8 && password.length() <= 14) {
            strengthPercentage += 30;
        } else if (password.length() > 14) {
            strengthPercentage += 40;
        }

        // Check if password contains uppercase letters
        if (password.matches("(?=.*[A-Z]).*")) {
            strengthPercentage += 20;
        }

        // Check if password contains lowercase letters
        if (password.matches("(?=.*[a-z]).*")) {
            strengthPercentage += 20;
        }

        // Check if password contains numbers
        if (password.matches("(?=.*[0-9]).*")) {
            strengthPercentage += 10;
        }

        // Check if password contains special characters
        if (password.matches("(?=.*[~!@#$%^&*()_-]).*")) {
            strengthPercentage += 10;
        }

        return strengthPercentage;
    }
}


