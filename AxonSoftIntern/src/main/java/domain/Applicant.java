package domain;

import java.util.ArrayList;
import java.util.List;
//clasa aplicantului, blueprintul care defineste natura viitorului obiect creat
public abstract class Applicant {
    //primul nume al aplicantului
    private final String firstNameOfApplicant;
    //am facut o lista astfel daca aplicantul are 100+ de nume mijlocii vor fii stocate toate in acea lista
    private final List<String> middleNameOfApplicant;
    //ultimul nume al aplicantului
    private final String lastNameOfApplicant;
    //emailul aplicantului
    private final String emailOfApplicant;

    //aici am facut un constructor in cazul in care aplicantul nu are un nume mijlociu
    public Applicant(String firstNameOfApplicant, String lastNameOfApplicant, String emailOfApplicant) {
        this(firstNameOfApplicant, null, lastNameOfApplicant, emailOfApplicant);
    }
    public Applicant(String firstNameOfApplicant, List<String> middleNameOfApplicant,
                     String lastNameOfApplicant, String emailOfApplicant) {
        //daca aplicantul nu a introdus primul nume, ultimul nume sau emailul aruncam o exceptie.
        if (firstNameOfApplicant == null || lastNameOfApplicant == null || emailOfApplicant == null) {
            throw new IllegalArgumentException("First or last name or the email of the applicant cannot be null");
        }
        this.firstNameOfApplicant = firstNameOfApplicant;
        if(middleNameOfApplicant == null){this.middleNameOfApplicant = new ArrayList<String>();}
        else{this.middleNameOfApplicant = middleNameOfApplicant;}
        this.lastNameOfApplicant = lastNameOfApplicant;
        this.emailOfApplicant = emailOfApplicant;
    }
    //getter pentru ultimul nume al aplicantului
    public String getLastNameOfApplicant() {
        return lastNameOfApplicant;
    }
    //getter pentru emailul aplicantului
    public String getEmailOfApplicant() {
        return emailOfApplicant;
    }
    //metoda pentru a returna tot numele aplicantului
    public String getFullName() {
        //initializam un stringBuilder care incepe cu primul nume al aplicantului
        //defapt aici as fii putut folosi un simplu String si sa concatenez cu +, dar nu stiu cate nume mijlocii
        //are un aplicant iar pentru fiecare String nou se creaza un obiect nou care face programul mai putin eficient
        //din punct de vedere al memoriei.
        StringBuilder sb = new StringBuilder(firstNameOfApplicant);
        sb.append(" ");
        //itereaza prin toate numele mijlocii ale aplicantului si le adauga
        for (String middleNames : middleNameOfApplicant) {sb.append(middleNames).append(" ");}
        //la final adauga ultimul nume al aplicantului
        sb.append(lastNameOfApplicant);
        //returneaz numele aplicantului ca un String normal iar medoda trim elimina spatiile extra / taburile /
        // sau liniile libere de la inceputul si finalul stringului
        return sb.toString().trim();
    }
}
