import java.io.Serializable; //email objects are serialized

public class Email implements Serializable {
    private String recipients;
    private String subject;
    private String body;
    private final String sent_date;

    public Email(String date){
        this.sent_date = date;
    }

    public String getSent_date() {
        return sent_date;
    }

    public void setRecipients(String recipients) {
        this.recipients = recipients;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getRecipients() {
        return recipients;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }
}

