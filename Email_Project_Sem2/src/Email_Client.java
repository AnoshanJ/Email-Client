// index number : 200040B

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;


public class Email_Client {

    private static  List<Greetable> birthday_list = new ArrayList<>(); //list to store those who have to be sent birthday email
    private static  List<Recipient> recipient_list = new ArrayList<>(); //list to store recipient objects
    private static String day;  //today's date
    private static ObjectOutputStream os; //common output stream that can be accessed when required

    public static Recipient createRecipient(String type, String[] info) {
        RecipientFactory f = new RecipientFactory(); //Factory method to create recipient objects
        Recipient Rec = f.getRecipient(type, info);
        if (Rec instanceof Greetable) {
            birthday_list.add((Greetable) Rec); //add Greetable recipients to the recipient list
        }
        return Rec;
    }

    public static void initialize() {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        day = formatter.format(date);
//        day = "2022/07/22";
//        day = "2000/01/12";

        System.out.println("Welcome to Easy Mail!");
        System.out.println("Today is " + day);
        System.out.println("You are signed in as: " + SendEmailTLS.getUsername());
        System.out.println();
        System.out.println("Initializing...");

        serializer(); // initialize serialization
        recipientLoader(); //load all recipients from the clientlist file

    }
    public static void serializer(){
        //this function initializes the serialized file
        try {
            File file = new File("saved_emails.ser");
            //if the file exists we will copy the objects from it, delete the saved_emails.ser and save it again , this is to avoid corruption
            if (file.exists()) {
                List<Email> emails_stored = new ArrayList<>(); //we'll store the email objects serialized so that we can write it again
                FileInputStream fileStream=null;
                ObjectInputStream is=null;
                try {
                    //read the objects already serialized
                    fileStream = new FileInputStream("saved_emails.ser");
                    is = new ObjectInputStream(fileStream);
                    while (true) {
                        try {
                            //cast the serialized objects into email type
                            Email email1 = (Email) is.readObject();
                            emails_stored.add(email1);

                        }
                        catch (Exception ex) {
                            break;
                        }

                    }
                    //close these input streams
                    is.close();
                    fileStream.close();
                    file.delete(); //delete the old serialized file, so that we can rewrite to that as required

                    FileOutputStream fs = new FileOutputStream("saved_emails.ser", true); //turn on append mode
                    os = new ObjectOutputStream(fs);

                    for (Email email1 : emails_stored) {
                        os.writeObject(email1); //rewrite all the serialized emails
                    }
                    emails_stored.clear(); // delete the stored emails since we don't need it anymore


                } catch (IOException e) {
                    System.out.println("IO Exception occurred!");
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
                finally{
                    if (is!=null){
                        try{
                            is.close();
                            fileStream =null;
                        }
                        catch(Exception ignoreMe){}

                    }
                    if(fileStream!=null){
                        try{
                            fileStream.close();
                        }
                        catch(Exception ignoreMe){}

                    }
                }

            }
            else { //if no serialized file was saved, create a new one
                FileOutputStream fs = new FileOutputStream("saved_emails.ser", true);
                os = new ObjectOutputStream(fs);
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public static void recipientLoader(){
            Scanner sc = null;
        //reads the clientList.txt and creates recipient objects
        try {
            File file = new File("clientList.txt");
            sc = new Scanner(file);
            String type;
            String[] info;
            int length;
            while (sc.hasNextLine()) {
                // get type and other info(as list)
                type = sc.next().toLowerCase();
                length = type.length();
                type = type.substring(0, length - 1);
                info = sc.nextLine().strip().split(",");
                recipient_list.add(createRecipient(type, info));


            }
            //look for those whose birthday greeting have to be sent today
            if (!birthday_list.isEmpty())
            {

                for (Greetable recipient_g : birthday_list)
                {
                    Recipient recipient = (Recipient) recipient_g;
                    birthdayGreeter(recipient, recipient_g);
                }
            }

        } catch (FileNotFoundException fe) {
            System.out.println("Saved Recipients not found.");
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
        finally{
            if (sc!=null) {sc.close();}
        }
    }
    public static void birthdayGreeter(Recipient recipient, Greetable recipient_g) {

        String[] line = new String[3];
        //check if today is the birthday
        if (recipient_g.isBirthday(day)){
            System.out.println("Sending birthday greeting to : "+recipient.getName());
            line[0] = recipient.getEmail();
            line[1] = "Happy Birthday " + recipient.getName() + "!";
            line[2] = recipient_g.getGreeting();
            //send this list to send_mail function to send email
            sendMail(line);
        }
    }

    public static void addContact(String input_string) {
        //extract information from the string and reformat it
        input_string = input_string.replace(" ", "");
        input_string = input_string.replace(":", ": ");
        String type = input_string.substring(0, input_string.indexOf(":")).toLowerCase();
        int length = type.length();
        String[] info = input_string.substring(length + 2).split(",");


        try {
            //file that points to where clientList is saved
            File file = new File("clientList.txt");

            if (!file.exists()) {
                //create a new file if not exists
                file.createNewFile();
            }

            //We will use random access file so that we can move the pointer
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            //set the pointer to the end
            raf.seek(raf.length());
            raf.writeBytes(input_string);
            raf.writeBytes(System.lineSeparator());
            raf.close();

            //create recipient from the method
            Recipient recipient = createRecipient(type, info);
            System.out.println("New Contact Added.");
            //add Greetable recipients to birthday greeter list
            if (recipient instanceof Greetable) {
                birthdayGreeter(recipient, (Greetable) recipient);
            }


        }
        catch (IOException ioe) {
            ioe.printStackTrace();

        }
    }

    public static void retrieveEmail(String date) {
        //this function checks for emails send on a particular date
        FileInputStream fileStream = null;
        ObjectInputStream is = null;
        try {

            fileStream = new FileInputStream("saved_emails.ser");
            is = new ObjectInputStream(fileStream);
            boolean found = false;
            while (true) {
                try {
                    Email email1 = (Email) is.readObject();//explicitly cast object into email type
                    //check if the day the email was sent is same
                    if (email1.getSent_date().equals(date)) {
                        found = true;
                        System.out.println("Recipient: " + email1.getRecipients());
                        System.out.println("Subject: " + email1.getSubject());
                        System.out.println();
                    }

                } catch (Exception ex) {
                    break;
                }

            }
            if (!found) {
                //if no such emails were sent on that particular day
                System.out.println("No emails were sent on : " + date);
            }

        }
        catch (FileNotFoundException f) {
            //no serialized file was found
            System.out.println("No emails were saved...");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            //close the input streams
            if(is!=null){
                try{
                is.close();
                fileStream =null;}
                catch (Exception ignoreMe){
                }


            }
            if (fileStream!=null){
                try{
                fileStream.close();}
                catch (Exception ignoreMe){}
            }
        }


    }

    public static void sendMail(String[] line) {

        //start the TLS connection
        SendEmailTLS send_email = new SendEmailTLS();
        Email email1 = new Email(day);
        email1.setRecipients(line[0]);
        email1.setSubject(line[1]);
        email1.setBody(line[2]);
        //send the email
        send_email.send(email1);

        try {
            //serialize the new email
            os.writeObject(email1);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {

        //initialize the client by calling other necessary functions
        initialize();
        System.out.println();
        Scanner scanner = new Scanner(System.in);
        System.out.println("""
                Enter option type:\s
                1 - Adding a new recipient
                2 - Sending an email
                3 - Printing out all the recipients who have birthdays
                4 - Printing out details of all the emails sent
                5 - Printing out the number of recipient objects in the application
                -1 - Exit the Application""");

        //named loop so that it can be exited with -1
        outerloop:
        while (true) {
            try {
                int option = scanner.nextInt();
                scanner.nextLine();
                switch (option) {
                    case 1:
                        // input format - Official: nimal,nimal@gmail.com,ceo
                        //Office_friend: kamal,kamal@gmail.com,clerk,2000/12/12
                        //Personal: sunil,<nick-name>,sunil@gmail.com,2000/10/10
                        System.out.println("Type the new recipient details");
                        System.out.println("Format: <Type>: <name>, <nickname>(Only P), <email>, <title>(Only O/F), <birthday>(Only P/F)");
                        String input_string = scanner.nextLine();
                        addContact(input_string);
                        break;

                    case 2:
                        // input format - email, subject, content
                        // code to send an email

                        System.out.println("Type the email, input format : email, subject, content");
                        String[] line = scanner.nextLine().split(",", 3);
                        sendMail(line);
                        break;

                    case 3:
                        // input format - yyyy/MM/dd (ex: 2018/09/17)
                        // code to print recipients who have birthdays on the given date
                        System.out.println("Type the birthday in yyyy/MM/dd format");
                        String in_birthdate = scanner.nextLine();
                        for (Greetable recipient : birthday_list) {
                            Recipient recipient1 = (Recipient) recipient;
                            String birthdate = recipient.getBirthday();
                            if (birthdate.equals(in_birthdate)) {
                                System.out.println("Name : " + recipient1.getName());
                            }
                        }
                        break;
                    case 4:
                        // input format - yyyy/MM/dd (ex: 2018/09/17)
                        // code to print the details of all the emails sent on the input date
                        System.out.println("Type the sent date in yyyy/MM/dd format");
                        String email_date = scanner.nextLine();
                        retrieveEmail(email_date);

                        break;

                    case 5:
                        // code to print the number of recipient objects in the application
                        System.out.println(Recipient.getCount());
                        break;
                    case -1:
                        try {
                            //close the output streams
                            os.flush();
                            os.close();


                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        break outerloop;
                    default:
                        System.out.println("Invalid Input");
                }
            }
            catch (InputMismatchException ex){
                System.out.println("Invalid Input. Type the correct operation.");
                scanner.nextLine();
            }
            catch (Exception ex){
                ex.printStackTrace();
            }

        }

    }
}


