package ServiceLayer.Controllers;

import BusinessLayer.Enum.ComplaintStatus;
import BusinessLayer.SystemFeatures.Complaint;
import DB.ComplaintDao;
import DB.SystemController;

public class ComplaintSystemController {
    private ComplaintDao complaintDao;

    public ComplaintSystemController() {
        this.complaintDao = ComplaintDao.getInstance();
    }

    public boolean addComplaint(Complaint complaint) {
        complaintDao.save(complaint);
        //Logger
        SystemController.logger.info("Creation | New Complaint have been add to system; complaint ID: " + complaint.get_id() +
                "; Fan ID: " + complaint.getFanID());
        return true;
    }

    public boolean moveToArchive(Complaint complaint) {
        complaint.setStatus(ComplaintStatus.Archive);
        complaintDao.delete(complaint);
        SystemController.logger.info("Deletion | Complaint have been move to archive; complaint ID: " + complaint.get_id() +
                "; Fan ID: " + complaint.getFanID());
        return true;
    }

    public boolean moveToClose(Complaint complaint) {
        complaintDao.delete(complaint);
        return true;
    }
}
