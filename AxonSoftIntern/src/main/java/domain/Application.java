package domain;

import java.util.List;
//clasa aplicatie care extinde aplicantul si implementeaza assignmentul
public class Application extends Applicant{
    //data cand aplicantul a trimis documentele/rezolvarile pentru intrenship
    private final String dateOfDelivery;
    //ora cand aplicantul a trimis documentele/rezolvarile pentru internship
    private final String timeOfDelivery;
    //scorul aplicantului
    private final double scoreOfApplication;
    //punctele bonus oferite aplicantului
    private int bonusPoints;

    //constructorul cu numele mijlociu/mijlocii
    public Application(String firstNameOfApplicant, List<String> middleNameOfApplicant, String lastNameOfApplicant,
                       String emailOfApplicant, String dateOfDelivery, String timeOfDelivery, double scoreOfApplication,
                       int bonusPoints) {
        super(firstNameOfApplicant, middleNameOfApplicant, lastNameOfApplicant, emailOfApplicant);
        this.dateOfDelivery = dateOfDelivery;
        this.timeOfDelivery = timeOfDelivery;
        this.scoreOfApplication = scoreOfApplication;
        this.bonusPoints = bonusPoints;
    }
    //constructorul fara numele mijlociu/mijlocii
    public Application(String firstNameOfApplicant, String lastNameOfApplicant, String emailOfApplicant,
                       String dateOfDelivery, String timeOfDelivery, double scoreOfApplication, int bonusPoints) {
        super(firstNameOfApplicant,null, lastNameOfApplicant, emailOfApplicant);
        this.dateOfDelivery = dateOfDelivery;
        this.timeOfDelivery = timeOfDelivery;
        this.scoreOfApplication = scoreOfApplication;
        this.bonusPoints = bonusPoints;
    }
    //getter pentru data trimiterii
    public String getDateOfDelivery() {
        return dateOfDelivery;
    }
    //getter pentru ora trimiterii
    public String getTimeOfDelivery() {
        return timeOfDelivery;
    }
    //getter pentru scorul aplicantului
    public double getScoreOfApplication() {
        return scoreOfApplication;
    }
    //getter pentru punctele bonus
    public int getBonusPoints() {
        return bonusPoints;
    }
    //setter pentru punctele bonus
    public void setBonusPoints(int bonusPoints) {
        this.bonusPoints = bonusPoints;
    }
}
