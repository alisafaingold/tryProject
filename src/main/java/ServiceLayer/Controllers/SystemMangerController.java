package ServiceLayer.Controllers;

import BusinessLayer.Enum.ComplaintStatus;
import BusinessLayer.Enum.TeamState;
import BusinessLayer.Football.Team;
import BusinessLayer.SystemFeatures.Complaint;
import BusinessLayer.Users.ManagementUser;
import BusinessLayer.Users.Owner;
import BusinessLayer.Users.SignedUser;
import BusinessLayer.Users.SystemManager;
import CrossCutting.Utils;
import DB.ComplaintDao;
import DB.TeamDao;
import DB.UserDao;
import org.apache.commons.validator.routines.EmailValidator;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SystemMangerController {
    private UserDao userDao;
    private TeamDao teamDao;
    private ComplaintDao complaintDao;

    public SystemMangerController() {
        userDao = UserDao.getInstance();
        teamDao = TeamDao.getInstance();
        complaintDao = ComplaintDao.getInstance();
    }

    //Wasn't in UC
    public Owner signUpNewOwner(String userId, String teamOwnerID, String firstName, String lastName, String email) throws Exception {
        Optional optional = userDao.get(userId);
        Optional optional1 = userDao.get(teamOwnerID);
        if(optional1.isPresent()){
            ManagementUser teamOwner = (ManagementUser) optional1.get();
            if(optional.isPresent()) {
                SignedUser systemManager = (SignedUser) optional.get();
                if(systemManager instanceof SystemManager) {
                    boolean valid = EmailValidator.getInstance().isValid(email);
                    if (!valid)
                        throw new Exception("Not valid Email");

                    String password = lastName + "_" + firstName + "_123";

                    SignedUser byEmail = userDao.getByEmail(email);
                    if (byEmail != null)
                        throw new Exception("This user name already exist in the system");

                    //TODO Send Email

                    String hashPassword = Utils.sha256(password);

                    Owner owner = new Owner(email, hashPassword, firstName, lastName, email);
                    userDao.save(owner);
//                SystemController.userNameUser.put(email, owner);
                    return owner;
                }
                else{
                    throw new Exception("The user with the following id is not system manager: " + userId);
                }
            }
            else{
                throw new Exception("There is no user with the following id: " + userId);
            }
        } else {
            throw new Exception("Wrong IDS");
        }

    }

    //UC 8.1
    public boolean permanentlyCloseTeam(String teamID) throws Exception {
        Optional optional = teamDao.get(teamID);
        if(optional.isPresent()){
            Team team = (Team) optional.get();
            if (team.getState() == TeamState.active || team.getState() == TeamState.notActive) {
                //Todo send alerts
                team.setStatus(TeamState.permanentlyClosed);
                teamDao.delete(team);
            } else {
                throw new Exception("This team is already permanently closed");
            }
            return true;
        } else {
            throw new Exception("Wrong IDS");
        }
    }

    //UC 8.2
    //maybe shouldn't do this UC
//    public boolean removeUserFromSystem(SignedUser signedUser) throws Exception {
//        //todo send alerts
//        signedUser.deleteUser();
//        return true;
//    }

    //UC 8.3.1
    public List<Complaint> getAllComplaints() {
        return new ArrayList<>(complaintDao.getAll());
    }

    //UC 8.3.2
    public boolean addCommentToComplaint(String systemManagerID, String complaintID, String comment) throws Exception {
        Optional optional = userDao.get(systemManagerID);
        Optional optional1 = complaintDao.get(complaintID);
        if( optional.isPresent() && optional1.isPresent()){
            Complaint complaint = (Complaint) optional1.get();
            SystemManager systemManager = (SystemManager) optional.get();
            boolean b = complaint.addComment(systemManager, comment);
            complaintDao.update(complaint);
            return b;
            //TODO send notification to the fan
        } else {
            throw new Exception("Wrong IDS");
        }


    }

    public boolean closeComplaint(String systemManagerID, String complaintID) throws Exception {
        Optional optional = userDao.get(systemManagerID);
        Optional optional1 = complaintDao.get(complaintID);
        if( optional.isPresent() && optional1.isPresent()) {
            Complaint complaint = (Complaint) optional1.get();
            SystemManager systemManager = (SystemManager) optional.get();
            complaint.setStatus(ComplaintStatus.Closed);
            complaintDao.delete(complaint);
            return true;
        } else {
            throw new Exception("Wrong IDS");

        }
    }

    //UC 8.4
    public List<List<String>> getSystemEventsLog(long fromDate, long toDate) throws Exception {
        if (fromDate < toDate) {
            List<List<String>> lists = readFromLog();
            return lists.stream().filter(strings -> {
                long dataEpoch = Long.parseLong(strings.get(0));
                return dataEpoch > fromDate && dataEpoch < toDate;
            }).collect(Collectors.toList());
        } else {
            throw new Exception("Wrong Dates");
        }
    }

    public static List<List<String>> readFromLog() {
        List<List<String>> logs = new ArrayList<>();
        try {
            FileInputStream fstream = new FileInputStream("logs/logger.log");
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;
            /* read log line by line */
            while ((strLine = br.readLine()) != null) {
                String[] values = strLine.split("\\|");
                logs.add(Arrays.asList(values));
            }
            fstream.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        return logs;
    }

}
