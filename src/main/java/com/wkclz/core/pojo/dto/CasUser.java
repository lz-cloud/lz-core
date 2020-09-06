package com.wkclz.core.pojo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

/**
 * Description: Create by lz-gen
 * @author: wangkaicun
 * @table: cas_user (用户表，存储用户的基本信息) 重新生成代码会覆盖
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CasUser {

    /**
     * 用户编码
     */
    private String userCode;

    /**
     * 父ID
     */
    private Long pid;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 性别(0女1男2未知)
     */
    private Integer sex;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 用户类型
     */
    private String userType;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 国家，如中国为CN
     */
    private String country;

    /**
     * 用户特权信息
     */
    private String privilege;

    /**
     * 生日
     */
    private Date birthday;

    /**
     * 国籍
     */
    private String nationality;

    /**
     * 民族
     */
    private String nation;

    /**
     * 手机号（验证）
     */
    private String mobile;

    /**
     * 邮箱（验证）
     */
    private String email;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 工号
     */
    private String workNumber;

    /**
     * 学号
     */
    private String studentNumber;

    /**
     * 卡号
     */
    private String carNumber;

    /**
     * QQ号
     */
    private String qq;

    /**
     * 学院
     */
    private String academy;

    /**
     * 专业
     */
    private String major;

    /**
     * 身份证号码
     */
    private String idCard;

    /**
     * 住址
     */
    private String address;

    /**
     * 创建者IP
     */
    private String createIp;

    /**
     * 登录成功次数
     */
    private Integer loginTimes;

    /**
     * 最后更新人IP
     */
    private String lastUpdateIp;

    /**
     * 最后登录时间
     */
    private Date lastLoginTime;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;

    /**
     * 最后登录失败时间
     */
    private Date lastFiledTime;

    /**
     * 最后登录失败IP
     */
    private String lastFiledIp;


    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPrivilege() {
        return privilege;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getWorkNumber() {
        return workNumber;
    }

    public void setWorkNumber(String workNumber) {
        this.workNumber = workNumber;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getAcademy() {
        return academy;
    }

    public void setAcademy(String academy) {
        this.academy = academy;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreateIp() {
        return createIp;
    }

    public void setCreateIp(String createIp) {
        this.createIp = createIp;
    }

    public Integer getLoginTimes() {
        return loginTimes;
    }

    public void setLoginTimes(Integer loginTimes) {
        this.loginTimes = loginTimes;
    }

    public String getLastUpdateIp() {
        return lastUpdateIp;
    }

    public void setLastUpdateIp(String lastUpdateIp) {
        this.lastUpdateIp = lastUpdateIp;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public Date getLastFiledTime() {
        return lastFiledTime;
    }

    public void setLastFiledTime(Date lastFiledTime) {
        this.lastFiledTime = lastFiledTime;
    }

    public String getLastFiledIp() {
        return lastFiledIp;
    }

    public void setLastFiledIp(String lastFiledIp) {
        this.lastFiledIp = lastFiledIp;
    }
}

