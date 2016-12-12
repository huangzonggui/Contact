package com.android.hzg.contact.entity;

import java.io.Serializable;

/**
 * Created by hzg on 2016/12/10.
 */
public class User implements Serializable {

    public int _id;
    public String userName;
    public String mobilePhone;
    public String officePhone;
    public String familyPhone;
    public String position;
    public String company;
    public String address;
    public String zipCode;
    public String email;
    public String otherContact;
    public String remark;
    public int imageId;
    public int privacy;//1代表隐私用户，0代表普通用户

}
