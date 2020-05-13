package BusinessLayer.Football;

import BusinessLayer.Enum.RefereeTraining;
import BusinessLayer.Users.AssociationRepresentative;

import java.util.HashMap;

public class League {
    private String _id;
    String leagueName;
    long openDate;
    AssociationRepresentative responsibleAssociationRepresentative;
    RefereeTraining minRefereeTrainingRequired;

    public League(String leagueName, AssociationRepresentative associationRepresentative, RefereeTraining refereeTraining) {
        this.leagueName = leagueName;
        this.responsibleAssociationRepresentative = associationRepresentative;
        minRefereeTrainingRequired = refereeTraining;
        openDate = System.currentTimeMillis();
    }


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public RefereeTraining getMinRefereeTrainingRequired() {
        return minRefereeTrainingRequired;
    }

    @Override
    public String toString() {
        String string = leagueName+ " ";

        return string;
    }
}

