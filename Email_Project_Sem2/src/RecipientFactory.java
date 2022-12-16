public class RecipientFactory {

    // Recipient factory method that returns different types of recipients
    public Recipient getRecipient(String type, String[] info){
        switch (type) {

            case "official":
                return new Official_Recipient(info[0], info[1], info[2]);
            case "office_friend":
                return new Office_Friend_Recipient(info[0], info[1], info[2], info[3]);
            case "personal":
                return new Personal_Recipient(info[0], info[1], info[2], info[3]);
            default:
                return null;

        }
    }


}
