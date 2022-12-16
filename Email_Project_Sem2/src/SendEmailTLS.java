import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class SendEmailTLS {

    private static Session session;
    static final String username = "Your email address";
    final String password = "Your email password";

    public static String getUsername() {
        return username;
    }

    public void send(Email email) {



        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); //TLS

        session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("Your email address"));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(email.getRecipients())
            );
            message.setSubject(email.getSubject());
            message.setText(email.getBody());

            Transport.send(message);

            System.out.println("Email Sent");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}