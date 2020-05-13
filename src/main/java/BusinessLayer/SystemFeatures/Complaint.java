package BusinessLayer.SystemFeatures;

import BusinessLayer.Enum.ComplaintStatus;
import BusinessLayer.Users.Fan;
import BusinessLayer.Users.SystemManager;
import DB.SystemController;

import java.util.HashSet;

public class Complaint {
    private String fanID;
    private String description;
    private long reportDate;
    private HashSet <Comment> comments;
    private ComplaintStatus status;
    private String _id;


    public Complaint(Fan fan, String description) {
        this.fanID = fan.get_id();
        this.description = description;
        comments = new HashSet<>();
        reportDate = System.currentTimeMillis();
        status = ComplaintStatus.New;
        //ComplaintSystemController.addComplaint(this);
    }

    // ========== Comments ================

    private static class Comment {
        private static int idCounter = 0;
        private String systemManagerID;
        private String comment;
        private long CommentDate;
        private int commentID;

        public Comment(SystemManager systemManager, String comment) {
            this.systemManagerID = systemManager.get_id();
            this.comment = comment;
            CommentDate = System.currentTimeMillis();
            commentID = idCounter++;
        }

        public int getCommentID() {
            return commentID;
        }
    }

    public boolean addComment(SystemManager systemManager, String comment) throws Exception {
        if (this.status == ComplaintStatus.Closed) {
            throw new Exception("This Complaint status is already Closed");
        }
        Comment complaintComment = new Comment(systemManager, comment);
        comments.add(complaintComment);
        this.status = ComplaintStatus.Closed;
        SystemController.logger.info("New comment to complaint have been created; Complaint ID: " + _id +
                "; Comment ID: " + complaintComment.getCommentID() + "; System Manger ID:" + systemManager.get_id());

        return true;
    }

    //========== Getters and Setters ================
    public ComplaintStatus getStatus() {
        return status;
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
    }

    public String getFanID() {
        return fanID;
    }

    public void setFanID(Fan fan) {
        this.fanID = fan.get_id();
    }

    public long getReportDate() {
        return reportDate;
    }

    public String getDescription() {
        return description;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
