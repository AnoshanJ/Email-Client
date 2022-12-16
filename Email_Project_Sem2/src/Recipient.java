public abstract class Recipient {
    private String name;
    private String email;
    private static int count=0; //count to maintain the number of recipient objects

    public Recipient(String name, String email){
        this.name = name;
        this.email = email;
        count+=1;
    }
    //static method that returns number of recipient objects
    public static int getCount() {
        return count;
    }

    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }

}
