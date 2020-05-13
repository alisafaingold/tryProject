package ExternalServices;

import DB.SystemController;
import BusinessLayer.Football.Team;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.concurrent.TimeUnit;

public class ExternalServices {

    public static boolean getNewTeamAuthorization(Team teamToAdd){
//        Random rand = new Random();
//        if(rand.nextInt() > 0.9)
//            return false;
        return true;
    }

    public static boolean sendInviteToTheSystem(String email, String userName, String password, String invited){
        boolean valid = EmailValidator.getInstance().isValid(email);
        if(!valid || userName.length()==0 || password.length()==0)
            return false;
        String content = "Hello! "+ invited + " Have been invited you to the most amazing football system. Your initial user name is: " +
                userName + " and your password is: "+ password;

        System.out.println("This email have been sent to the mail: "+email +"\n The content: "+ content);
        return true;
    }

    public static boolean establishConnectionToTaxSystem() throws InterruptedException {
        SystemController.logger.info("ExternalServices | Establishing connection to the tax system of the country");
        TimeUnit.SECONDS.sleep(2);
        SystemController.logger.info("ExternalServices | Connection to the tax system of the country has been established");
        return true;
    }

    public static boolean establishConnectionToAssociationAccountingSystem() throws InterruptedException {
        SystemController.logger.info("ExternalServices | Establishing connection to the association accounting system");
        TimeUnit.SECONDS.sleep(2);
        SystemController.logger.info("ExternalServices | Connection to the association accounting system has been established");
        return true;
    }

}
