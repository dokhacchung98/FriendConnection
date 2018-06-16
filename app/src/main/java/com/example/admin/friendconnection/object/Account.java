package com.example.admin.friendconnection.object;

/**
 * Created by Admin on 4/11/2018.
 */

public class Account {
    private String userName;
    private String passWord;
    private String name;
    private String linkAvatar;
    private String id;
    private String sex;
    private String phone;
    private String mail;
    private String birthDay;

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public Account() {
    }

    public Account(String userName, String passWord, String name, String linkAvatar, String id, String mail, String phone, String sex, String birthDay) {
        this.userName = userName;
        this.passWord = passWord;
        this.name = name;
        this.linkAvatar = linkAvatar;
        this.id = id;
        this.mail = mail;
        this.phone = phone;
        this.sex = sex;
        this.birthDay = birthDay;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLinkAvatar() {
        return linkAvatar;
    }

    public void setLinkAvatar(String linkAvatar) {
        this.linkAvatar = linkAvatar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
