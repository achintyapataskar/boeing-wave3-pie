package com.stackroute.pie.model;


//import org.hibernate.annotations.NaturalId;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.data.mongodb.core.mapping.Document;

//import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document(collection="insurersname")

public class Insurer {

    private Long insurerId;

    @NotBlank
    @Size(min=3, max = 50)
    private String insurerName;

    @NotBlank
    @UniqueElements
    @Size(min=3, max = 50)
    private String insurerLicense; //username

    @UniqueElements
    @NotBlank
    @Size(max = 50)
    @Email
    private String insurerEmail;

//    @Transactional
    @NotBlank
    @Size(min=6, max = 100)
    private String password;

    @NotBlank
    @Size(min=6, max = 100)
    private String insurerAddress;

    @NotBlank
    @Size(min=6, max = 40)
    private String securityQuestion;



    @NotBlank
    @Size(min=6, max = 40)
    private String securityAnswer;



    //document inside the doucmnet
    private List<Policy> policies;

    private Set<Role> roles = new HashSet<>();

    public Insurer() {}

    public Insurer(String insurerName, String insurerLicense, String insurerEmail, String password, String insurerAddress, String securityQuestion, String securityAnswer) {
        this.insurerName = insurerName;
        this.insurerLicense = insurerLicense;
        this.insurerEmail = insurerEmail;
        this.password = password;
        this.securityAnswer = securityAnswer;
        this.securityQuestion = securityQuestion;
        this.insurerAddress = insurerAddress;
    }


    public Long getInsurerId() {
        return insurerId;
    }

    public void setInsurerId(Long insurerId) {
        this.insurerId = insurerId;
    }

    public String getInsurerName() {
        return insurerName;
    }

    public void setInsurerName(String insurerName) {
        this.insurerName = insurerName;
    }
    public String getInsurerLicense() {
        return insurerLicense;
    }

    public void setInsurerLicense(String insurerLicense) {
        this.insurerLicense = insurerLicense;
    }


    public String getInsurerEmail() {
        return insurerEmail;
    }

    public void setInsurerEmail(String insurerEmail) {
        this.insurerEmail = insurerEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public List<Policy> getPolicies() {
        return policies;
    }

    public void setPolicies(List<Policy> policies) {
        this.policies = policies;
    }

    public String getInsurerAddress() {
        return insurerAddress;
    }

    public void setInsurerAddress(String insurerAddress) {
        this.insurerAddress = insurerAddress;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }


}






