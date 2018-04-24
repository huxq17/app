package com.aiqing.kaiheiba.bean;

import android.text.TextUtils;

import java.util.List;

public class AccountBean {
    /**
     * AccountId : 1
     * Avatar :
     * Born : 2000-10-10 08:00:00
     * City :
     * CreateAt : 2018-03-13 10:43:47
     * FirstIp : 127.0.0.1
     * Gender : 0
     * ID : 1
     * InviteCode : 123456
     * InviterId : 0
     * LastIp : 127.0.0.1
     * LastLoginAt : 2018-03-13 10:43:46
     * Mobile : 1234567896
     * Nickname : jjj
     * Province : 上海
     * "IsFollowed": "1", //是否关注了该粉丝 0未关注 1已关注
     * Sign : hahahaha
     */
    private String Accid;
    private String AccountId;
    private String Avatar;
    private String Born;
    private String City;
    private String CreateAt;
    private String FirstIp;
    private String Gender;
    private String ID;
    private String InviteCode;
    private String InviterId;
    private String LastIp;
    private String LastLoginAt;
    private String Mobile;
    private String Nickname;
    private String Province;
    private String Sign;
    private String IsFollowed;
    private String money;

    public String getMoney() {
        return money;
    }

    public String getAccid() {
        return Accid;
    }

    public void setAccid(String accid) {
        Accid = accid;
    }

    public String getIsFollowed() {
        return IsFollowed;
    }

    public void setIsFollowed(String isFollowed) {
        IsFollowed = isFollowed;
    }

    public String getAccountId() {
        return AccountId;
    }

    public void setAccountId(String AccountId) {
        this.AccountId = AccountId;
    }

    public String getAvatar() {
        return Avatar;
    }

    public void setAvatar(String Avatar) {
        this.Avatar = Avatar;
    }

    public String getBorn() {
        return Born;
    }

    public void setBorn(String Born) {
        this.Born = Born;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String City) {
        this.City = City;
    }

    public String getCreateAt() {
        return CreateAt;
    }

    public void setCreateAt(String CreateAt) {
        this.CreateAt = CreateAt;
    }

    public String getFirstIp() {
        return FirstIp;
    }

    public void setFirstIp(String FirstIp) {
        this.FirstIp = FirstIp;
    }

    public int getGender() {
        int gender =0;
        if (!TextUtils.isEmpty(Gender) && TextUtils.isDigitsOnly(Gender)) {
            gender = Integer.parseInt(Gender);
        }
        return gender;
    }

    public void setGender(String Gender) {
        this.Gender = Gender;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getInviteCode() {
        return InviteCode;
    }

    public void setInviteCode(String InviteCode) {
        this.InviteCode = InviteCode;
    }

    public String getInviterId() {
        return InviterId;
    }

    public void setInviterId(String InviterId) {
        this.InviterId = InviterId;
    }

    public String getLastIp() {
        return LastIp;
    }

    public void setLastIp(String LastIp) {
        this.LastIp = LastIp;
    }

    public String getLastLoginAt() {
        return LastLoginAt;
    }

    public void setLastLoginAt(String LastLoginAt) {
        this.LastLoginAt = LastLoginAt;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String Mobile) {
        this.Mobile = Mobile;
    }

    public String getNickname() {
        return Nickname;
    }

    public void setNickname(String Nickname) {
        this.Nickname = Nickname;
    }

    public String getProvince() {
        return Province;
    }

    public void setProvince(String Province) {
        this.Province = Province;
    }

    public String getSign() {
        return Sign;
    }

    public void setSign(String Sign) {
        this.Sign = Sign;
    }

    public List<InviteBean> invitors;

    public static class InviteBean {
        public String createAt;
        public String mobile;
    }
}
