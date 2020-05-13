package ServiceLayer.Controllers;

import DB.ComplaintDao;
import DB.SystemController;
import BusinessLayer.Enum.ComplaintStatus;
import BusinessLayer.SystemFeatures.Complaint;


import java.util.HashMap;
import java.util.HashSet;

public class ComplaintSystemController {
    public static HashSet <Complaint> newComplaint = new HashSet<>();
    public static HashSet <Complaint> closedComplaint = new HashSet<>();
    public static HashMap<Complaint, String> archiveComplaint = new HashMap<>();


    public static boolean addComplaint(Complaint complaint) {
        ComplaintDao complaintDao = new ComplaintDao();
        complaintDao.save(complaint);
        //Logger
        SystemController.logger.info("Creation | New Complaint have been add to system; complaint ID: " + complaint.get_id() +
                "; Fan ID: " + complaint.getFanID());
        return true;
    }

    public static boolean moveToArchive(Complaint complaint) {
        complaint.setStatus(ComplaintStatus.Archive);
        ComplaintDao complaintDao  = new ComplaintDao();
        complaintDao.delete(complaint);
        SystemController.logger.info("Deletion | Complaint have been move to archive; complaint ID: " + complaint.get_id() +
                "; Fan ID: " + complaint.getFanID());
        return true;
    }

    public static boolean moveToClose(Complaint complaint) {
        ComplaintDao complaintDao = new ComplaintDao();
        complaintDao.delete(complaint);
        return true;
    }
}
