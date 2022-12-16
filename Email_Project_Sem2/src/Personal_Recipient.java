public class Personal_Recipient extends Recipient implements Greetable {
    private String nickname;
    private String birthday;

    public Personal_Recipient(String name, String nickname, String email, String birthday) {
        super(name, email);
        this.nickname = nickname;
        this.birthday = birthday;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getGreeting() {
        return ("Hugs and Love on your birthday " + this.getName() + ".");
    }

    public boolean isBirthday(String date) {
        String today = date.substring(5);//consider only date and month for birthday
        String birth_day = birthday.substring(5);
        return today.equals(birth_day);
    }
}
