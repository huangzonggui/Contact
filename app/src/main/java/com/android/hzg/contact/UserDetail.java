package com.android.hzg.contact;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.*;
import com.android.hzg.contact.db.DBHelper;
import com.android.hzg.contact.entity.User;

import java.util.HashMap;

/**
 * Created by hzg on 2016/12/11.
 */
public class UserDetail extends Activity {

    EditText et_name;
    EditText et_mobilePhone;
    EditText et_officePhone;
    EditText et_familyPhone;
    EditText et_position;
    EditText et_company;
    EditText et_address;
    EditText et_zipCode;
    EditText et_otherContact;
    EditText et_email;
    EditText et_remark;

    Button btn_modify;
    Button btn_return;
    Button btn_delete;

    User user;
    //头像的按钮
    ImageButton imageButton;
    //用flag来判断按钮的状态 flase表示查看点击“修改”状态，true表示点击“保存”状态
    boolean flag = false;
    //判断是否修改了信息，为了点击返回的时候判断是否修改了信息，进而设置resultCode,进而返回主页面的时候判断是否需要刷新数据
    boolean isDataChanged = false;

    Gallery gallery;
    ImageSwitcher imageSwitcher;

    AlertDialog imageChooseDialog;
    //所有图片
    private int[] images = {R.drawable.icon
            , R.drawable.image1, R.drawable.image2, R.drawable.image3
            , R.drawable.image4, R.drawable.image5, R.drawable.image6
            , R.drawable.image7, R.drawable.image8, R.drawable.image9
            , R.drawable.image10, R.drawable.image11, R.drawable.image12
            , R.drawable.image13, R.drawable.image14, R.drawable.image15
            , R.drawable.image16, R.drawable.image17, R.drawable.image18
            , R.drawable.image19, R.drawable.image20, R.drawable.image21
            , R.drawable.image22, R.drawable.image23, R.drawable.image24
            , R.drawable.image25, R.drawable.image26, R.drawable.image27
            , R.drawable.image28, R.drawable.image29, R.drawable.image30};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_detail);

        //获得意图
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");//从intent中拿到装有user
        loadUserData();
        setEditTextDisable();

        btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    setTitle("修改信息");
                    modify();
                    setEditTextDisable();
                    btn_modify.setText("修改");
                    flag = false;
                } else {
                    setEditTextAble();
                    btn_modify.setText("保存修改");
                    flag = true;
                }
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(UserDetail.this).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delete();
                        setResult(4);
                        finish();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setTitle("是否要删除？").create().show();
            }
        });

        btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDataChanged) {
                    setResult(4);
                } else {
                    setResult(5);
                }
                finish();
            }
        });

    }

    //修改数据:获得最新数据，创建DBHelper对象，更新数据库
    private void modify() {
        user.userName = et_name.getText().toString();
        user.address = et_address.getText().toString();
        user.company = et_company.getText().toString();
        user.email = et_email.getText().toString();
        user.familyPhone = et_familyPhone.getText().toString();
        user.mobilePhone = et_mobilePhone.getText().toString();
        user.officePhone = et_officePhone.getText().toString();
        user.otherContact = et_otherContact.getText().toString();
        user.position = et_position.getText().toString();
        user.remark = et_remark.getText().toString();
        user.zipCode = et_zipCode.getText().toString();
        DBHelper helper = new DBHelper(this);
        helper.openDataBase();
        helper.modify(user);
        isDataChanged = true;
    }

    //将user的细节数据显示出来
    public void loadUserData() {
        //初始化控件
        et_name = (EditText) findViewById(R.id.username);
        et_mobilePhone = (EditText) findViewById(R.id.mobilephone);
        et_officePhone = (EditText) findViewById(R.id.officephone);
        et_familyPhone = (EditText) findViewById(R.id.familyphone);
        et_position = (EditText) findViewById(R.id.position);
        et_company = (EditText) findViewById(R.id.company);
        et_address = (EditText) findViewById(R.id.address);
        et_zipCode = (EditText) findViewById(R.id.zipcode);
        et_otherContact = (EditText) findViewById(R.id.othercontact);
        et_email = (EditText) findViewById(R.id.email);
        et_remark = (EditText) findViewById(R.id.remark);

        //获得Button控件
        btn_modify = (Button) findViewById(R.id.btn_modify);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        btn_return = (Button) findViewById(R.id.btn_return);

        imageButton = (ImageButton) findViewById(R.id.image_button);

        //为控件赋值
        et_name.setText(user.userName);
        et_mobilePhone.setText(user.mobilePhone);
        et_officePhone.setText(user.officePhone);
        et_familyPhone.setText(user.familyPhone);
        et_position.setText(user.position);
        et_company.setText(user.company);
        et_address.setText(user.address);
        et_zipCode.setText(user.zipCode);
        et_otherContact.setText(user.otherContact);
        et_email.setText(user.email);
        et_remark.setText(user.remark);
        imageButton.setImageResource(user.imageId);

    }

    //设置不可修改,顺便将颜色改为黑色
    public void setEditTextDisable() {
        et_name.setEnabled(false);
        et_mobilePhone.setEnabled(false);
        et_officePhone.setEnabled(false);
        et_familyPhone.setEnabled(false);
        et_position.setEnabled(false);
        et_company.setEnabled(false);
        et_address.setEnabled(false);
        et_zipCode.setEnabled(false);
        et_otherContact.setEnabled(false);
        et_email.setEnabled(false);
        et_remark.setEnabled(false);
        imageButton.setEnabled(false);

        //设置显示的字体颜色为灰色
        et_name.setTextColor(Color.GRAY);
        et_mobilePhone.setTextColor(Color.GRAY);
        et_officePhone.setTextColor(Color.GRAY);
        et_familyPhone.setTextColor(Color.GRAY);
        et_position.setTextColor(Color.GRAY);
        et_company.setTextColor(Color.GRAY);
        et_address.setTextColor(Color.GRAY);
        et_zipCode.setTextColor(Color.GRAY);
        et_otherContact.setTextColor(Color.GRAY);
        et_email.setTextColor(Color.GRAY);
        et_remark.setTextColor(Color.GRAY);

    }

    //设置可修改,顺便将颜色改了
    public void setEditTextAble() {
        et_name.setEnabled(true);
        et_mobilePhone.setEnabled(true);
        et_officePhone.setEnabled(true);
        et_familyPhone.setEnabled(true);
        et_position.setEnabled(true);
        et_company.setEnabled(true);
        et_address.setEnabled(true);
        et_zipCode.setEnabled(true);
        et_otherContact.setEnabled(true);
        et_email.setEnabled(true);
        et_remark.setEnabled(true);
        imageButton.setEnabled(true);


        //设置显示的字体颜色为黑色
        et_name.setTextColor(Color.WHITE);
        et_mobilePhone.setTextColor(Color.WHITE);
        et_officePhone.setTextColor(Color.WHITE);
        et_familyPhone.setTextColor(Color.WHITE);
        et_position.setTextColor(Color.WHITE);
        et_company.setTextColor(Color.WHITE);
        et_address.setTextColor(Color.WHITE);
        et_zipCode.setTextColor(Color.WHITE);
        et_otherContact.setTextColor(Color.WHITE);
        et_email.setTextColor(Color.WHITE);
        et_remark.setTextColor(Color.WHITE);

    }

    public void delete() {
        DBHelper helper = new DBHelper(this);
        helper.openDataBase();
        helper.delete(user._id);
    }


}
