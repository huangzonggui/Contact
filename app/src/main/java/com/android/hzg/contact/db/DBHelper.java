package com.android.hzg.contact.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.android.hzg.contact.entity.User;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by hzg on 2016/12/10.
 */
public class DBHelper {

    public static final String DB_DBNAME = "contact";//数据库名
    public static final String DB_TABLENAME = "user";//表名
    public static final int VERSION = 1;//数据库版本
    private SQLiteDatabase dbIntance;
    private MyDBHelper myDBHelper;
    StringBuffer table;//StringBuffer来装着创建表的sql语句
    private Context context;

    //构造方法
    public DBHelper(Context context) {
        this.context = context;
    }

    //打开数据库
    public void openDataBase() {
        if (dbIntance == null) {
            myDBHelper = new MyDBHelper(context, DB_DBNAME, VERSION);
            dbIntance = myDBHelper.getWritableDatabase();
        }
    }

    /**
     * 往数据库里面的user表插入一条数据，若失败返回-1
     *
     * @param user
     * @return 失败返回-1
     */
    public long insert(User user) {
        ContentValues values = new ContentValues();
        values.put("name", user.userName);
        values.put("mobilephone", user.mobilePhone);
        values.put("officephone", user.officePhone);
        values.put("familyphone", user.familyPhone);
        values.put("address", user.address);
        values.put("othercontact", user.otherContact);
        values.put("email", user.email);
        values.put("position", user.position);
        values.put("company", user.company);
        values.put("zipcode", user.zipCode);
        values.put("remark", user.remark);
        values.put("imageid", user.imageId);
        return dbIntance.insert(DB_TABLENAME, null, values);
    }

    //根据主键id删除
    public void delete(int _id) {
        dbIntance.delete(DB_TABLENAME, "_id=?", new String[]{String.valueOf(_id)});
    }

    //修改user
    public void modify(User user) {
        ContentValues values = new ContentValues();
        values.put("name", user.userName);
        values.put("mobilephone", user.mobilePhone);
        values.put("officephone", user.officePhone);
        values.put("familyphone", user.familyPhone);
        values.put("address", user.address);
        values.put("othercontact", user.otherContact);
        values.put("email", user.email);
        values.put("position", user.position);
        values.put("company", user.company);
        values.put("zipcode", user.zipCode);
        values.put("remark", user.remark);
        values.put("imageid", user.imageId);
        //根据主键来修改
        dbIntance.update(DB_TABLENAME, values, "_id=?", new String[]{String.valueOf(user._id)});
    }

    public ArrayList getAllUser() {
        ArrayList list = new ArrayList();
        Cursor cursor = dbIntance.query(DB_TABLENAME, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            HashMap map = new HashMap();
            map.put("_id", cursor.getInt(cursor.getColumnIndex("_id")));
            map.put("imageid", cursor.getInt(cursor.getColumnIndex("imageid")));
            map.put("name", cursor.getString(cursor.getColumnIndex("name")));
            map.put("mobilephone", cursor.getString(cursor.getColumnIndex("mobilephone")));
            map.put("officephone", cursor.getString(cursor.getColumnIndex("officephone")));
            map.put("familyphone", cursor.getString(cursor.getColumnIndex("familyphone")));
            map.put("address", cursor.getString(cursor.getColumnIndex("address")));
            map.put("othercontact", cursor.getString(cursor.getColumnIndex("othercontact")));
            map.put("email", cursor.getString(cursor.getColumnIndex("email")));
            map.put("position", cursor.getString(cursor.getColumnIndex("position")));
            map.put("company", cursor.getString(cursor.getColumnIndex("company")));
            map.put("zipcode", cursor.getString(cursor.getColumnIndex("zipcode")));
            map.put("remark", cursor.getString(cursor.getColumnIndex("remark")));
            list.add(map);
        }
        return list;
    }

    //通过搜索找到user
    public ArrayList getUserBySearch(String condition) {
        ArrayList list = new ArrayList();
        //select * from user where 1=1 and (name like '%condition%' or mobilephone like '%condition%' or officephone like '%condition%')
        //1=1是为了避免其他查询为空时,这条查询语句报错.不是l是1
        //就是条件永远为真，查出所有数据来
        String sql = "select * from " + DB_TABLENAME +
                " where 1=1 and (name like '%" + condition + "%' or mobilephone like '%" + condition + "%' or officephone like '%"
                + condition + "%')";
        System.out.println(sql);//在logcat中打印出sql查看是否有错
        Cursor cursor = dbIntance.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            HashMap map = new HashMap();
            map.put("_id", cursor.getInt(cursor.getColumnIndex("_id")));
            map.put("imageid", cursor.getInt(cursor.getColumnIndex("imageid")));
            map.put("name", cursor.getString(cursor.getColumnIndex("name")));
            map.put("mobilephone", cursor.getString(cursor.getColumnIndex("mobilephone")));
            map.put("officephone", cursor.getString(cursor.getColumnIndex("officephone")));
            map.put("familyphone", cursor.getString(cursor.getColumnIndex("familyphone")));
            map.put("address", cursor.getString(cursor.getColumnIndex("address")));
            map.put("othercontact", cursor.getString(cursor.getColumnIndex("othercontact")));
            map.put("email", cursor.getString(cursor.getColumnIndex("email")));
            map.put("position", cursor.getString(cursor.getColumnIndex("position")));
            map.put("company", cursor.getString(cursor.getColumnIndex("company")));
            map.put("zipcode", cursor.getString(cursor.getColumnIndex("zipcode")));
            map.put("remark", cursor.getString(cursor.getColumnIndex("remark")));
            list.add(map);
        }
        return list;
    }

    class MyDBHelper extends SQLiteOpenHelper {
        //构造方法
        public MyDBHelper(Context context, String name, int version) {
            super(context, name, null, version);
        }

        //建表:只执行一次，当没有数据库的时候，建表，有的话就去对比VERSION，如果版本号不一样，执行onUpgrade方法
        @Override
        public void onCreate(SQLiteDatabase db) {
            table = new StringBuffer();
            table.append("create table ")//注意空格
                    .append(DB_TABLENAME)
                    .append(" (")//注意空格
                    .append("_id integer primary key autoincrement,")
                    .append("name text,")
                    .append("mobilephone text,")
                    .append("officephone text,")
                    .append("familyphone text,")
                    .append("address text,")
                    .append("othercontact text,")
                    .append("email text,")
                    .append("position text,")
                    .append("company text,")
                    .append("zipcode text,")
                    .append("remark text,")
                    .append("imageid int,")
                    .append("privacy int ")//注意空格
                    .append(")");
            System.out.println(table.toString());
            db.execSQL(table.toString());
        }

        //当VERSION不以言，版本号不一样，执行onUpgrade方法（这个方法里面一般是删除表后执行sql语句）
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            String sql = "drop table if exits " + DB_TABLENAME;
            db.execSQL(sql);
            myDBHelper.onCreate(db);
        }
    }

}
