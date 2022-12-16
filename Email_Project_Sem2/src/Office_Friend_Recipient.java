public class Office_Friend_Recipient extends Official_Recipient implements Greetable {
    private String birthday;

    public Office_Friend_Recipient(String name, String email, String designation, String birthday) {
        super(name, email, designation);
        this.birthday = birthday;
    }
    @Override
    public String getBirthday() {
        return birthday;
    }

    public String getGreeting() {
        return ("Wish you a Happy Birthday " + this.getName() + ".");
    }
    public boolean isBirthday(String date) {
        String today = date.substring(5);
        String birth_day = birthday.substring(5);
        return today.equals(birth_day);
    }
}
