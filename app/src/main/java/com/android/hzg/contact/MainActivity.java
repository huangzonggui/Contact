package com.android.hzg.contact;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.android.hzg.contact.db.DBHelper;
import com.android.hzg.contact.entity.User;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity {
    ArrayList list;//装着listView要显示的数据(map),这个数据是通过数据库拿来的
    SimpleAdapter adapter;//拥有所有数据的Adapter（这个是listView的适配器，注意区分）
    GridView bottomMenuGrid;//屏幕下方的工具栏
    ListView lv_user;
    EditText et_search;
    LinearLayout searchLinearLayout;
    LinearLayout mainLinearLayout;

    ArrayList<Integer> deleteId;
    DBHelper helper;
    String[] bottom_menu_itemName = {"增加", "查找", "删除", "菜单", "退出",};
    int[] bottom_menu_itemImages = {R.drawable.menu_new_user, R.drawable.menu_search, R.drawable.menu_delete, R.drawable.menu_restore, R.drawable.menu_fresh, R.drawable.menu_return};


    //主菜单的视图
    View mainMenuView;

    //主菜单的布局
    GridView mainMenuGrid;

    //主菜单对话框
    AlertDialog mainMenuDialog;

    //菜单的名字
    String[] main_menu_itemName = {"显示所有", "删除所有", "备份数据", "还原数据", "更新", "后退"};
    //主菜单的图片
    int[] main_menu_itemSource = {
            R.drawable.showall,
            R.drawable.menu_delete,
            R.drawable.menu_backup,
            R.drawable.menu_restore,
            R.drawable.menu_fresh,
            R.drawable.menu_return};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainLinearLayout = (LinearLayout) findViewById(R.id.main_ll);
        helper = new DBHelper(this);
        helper.openDataBase();//打开数据库，就打开这一次，因为Helper中的SQLiteDatabase是静态的。
        list = helper.getAllUser();
        //将数据与adapter集合起来
        adapter = new SimpleAdapter(this,
                list,
                R.layout.item_list,
                new String[]{"imageid", "name", "mobilephone"},
                new int[]{R.id.user_image, R.id.tv_name, R.id.tv_mobilephone});
        lv_user = (ListView) findViewById(R.id.lv_userlist);
        lv_user.setAdapter(adapter);//将整合好的adapter交给listview,显示给用户看
        lv_user.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //parent是全部内容，
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap userMap = (HashMap) parent.getItemAtPosition(position);

                Intent intent = new Intent(MainActivity.this, UserDetail.class);
                User user = new User();
                user._id = Integer.parseInt(String.valueOf(userMap.get("_id")));
                user.address = String.valueOf(userMap.get("address"));
                user.company = String.valueOf(userMap.get("company"));
                user.email = String.valueOf(userMap.get("email"));
                user.familyPhone = String.valueOf(userMap.get("familyphone"));
                user.mobilePhone = String.valueOf(userMap.get("mobilephone"));
                user.officePhone = String.valueOf(userMap.get("officephone"));
                user.otherContact = String.valueOf(userMap.get("othercontact"));
                user.position = String.valueOf(userMap.get("position"));
                user.remark = String.valueOf(userMap.get("remark"));
                user.userName = String.valueOf(userMap.get("name"));
                user.zipCode = String.valueOf(userMap.get("zipcode"));
                user.imageId = Integer.parseInt(String.valueOf(userMap.get("imageid")));
                intent.putExtra("user", user);

                if (searchLinearLayout != null && searchLinearLayout.getVisibility() == View.VISIBLE) {
                    searchLinearLayout.setVisibility(View.GONE);
                }

                startActivityForResult(intent, position);
            }
        });

        lv_user.setCacheColorHint(Color.TRANSPARENT);
        //长按监听事件
        lv_user.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (deleteId == null) {
                    deleteId = new ArrayList<Integer>();
                }
                HashMap item = (HashMap) parent.getItemAtPosition(position);
                Integer _id = Integer.parseInt(String.valueOf(item.get("_id")));

                RelativeLayout r = (RelativeLayout) view;
                ImageView markedView = (ImageView) r.getChildAt(2);//？
//                ImageView markedView = (ImageView) findViewById(R.id.user_mark);这个不行
                if (markedView.getVisibility() == View.VISIBLE) {
                    markedView.setVisibility(View.GONE);
                    deleteId.remove(_id);
                } else {
                    markedView.setVisibility(View.VISIBLE);
                    deleteId.add(_id);
                }
                return true;
            }
        });
        //为list添加item选择器？
//        Drawable dbDrawable = getResources().getDrawable(R.drawable.list_bg);
//        lv_user.setSelector(dbDrawable);
    }

    //这个方法用来捕捉手机键盘被按下的事件。
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //点击Menu按钮
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            loadBottomMenu();
            if (bottomMenuGrid.getVisibility() == View.VISIBLE) {
                bottomMenuGrid.setVisibility(View.GONE);
            } else {
                bottomMenuGrid.setVisibility(View.VISIBLE);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void loadBottomMenu() {
        if (bottomMenuGrid == null) {
            bottomMenuGrid = (GridView) findViewById(R.id.gv_buttom_menu);
            bottomMenuGrid.setBackgroundResource(R.drawable.channelgallery_bg);//设置背景
            bottomMenuGrid.setNumColumns(5);//设置每行列数
            bottomMenuGrid.setGravity(Gravity.CENTER);//位置居中
            bottomMenuGrid.setVerticalSpacing(10);//垂直间隔
            bottomMenuGrid.setHorizontalSpacing(10);//水平间隔
            bottomMenuGrid.setAdapter(getMenuAdapter(bottom_menu_itemName, bottom_menu_itemImages));//设置菜单Adapter

            //监听底部菜单选项
            bottomMenuGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //0：添加 1：搜索 2：删除 3：菜单 4：退出
                    switch (position) {
                        case 0: {
                            //清除删除的标记id
//                            if (deleteId != null && deleteId.size() != 0) {
//                                deleteId.clear();
//                            }
                            //置搜索框不可见
                            if (searchLinearLayout != null && searchLinearLayout.getVisibility() == View.VISIBLE) {
                                searchLinearLayout.setVisibility(View.GONE);
                            }

                            Intent intent = new Intent(MainActivity.this, AddNewActivity.class);
                            startActivityForResult(intent, 3);//3 添加
                            break;
                        }
                        case 1: {
                            loadSearchLinearLayout();//加载出搜索框，并对其监听
                            //下面searchLinearLayout不为空是因为上面这个方法加载了这个控件
                            if (searchLinearLayout.getVisibility() == View.VISIBLE) {
                                lv_user.setAdapter(adapter);
                                //既然重新载入了user那么相应的标志也会去掉，那么就要清空deleteId
                                if (deleteId != null) {
                                    deleteId.clear();
                                }
                                searchLinearLayout.setVisibility(View.GONE);
                            } else {
                                searchLinearLayout.setVisibility(View.VISIBLE);
                                et_search.requestFocus();//把输入焦点放在搜索框这个控件上
                                //et_search.selectAll();//选择全部
                                et_search.setText("");//将搜索框中的所有text选中
                            }
                            break;
                        }
                        case 2: {
                            if (searchLinearLayout != null && searchLinearLayout.getVisibility() == View.VISIBLE) {
                                searchLinearLayout.setVisibility(View.GONE);
                            }
                            if (deleteId == null || deleteId.size() == 0) {
                                Toast.makeText(MainActivity.this, "没有标记任何记录\n长按一条记录即可标记", Toast.LENGTH_SHORT).show();
                            } else {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("确定要删除标记的" + deleteId.size() + "个记录吗？")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //链接数据库，删除标记的user
//                                                helper = new DBHelper(MainActivity.this);
//                                                helper.openDataBase();
                                                for (int i = 0; i < deleteId.size(); i++) {
                                                    int _id = deleteId.get(i);
                                                    helper.delete(_id);
                                                }
                                                //重置视图
                                                list = helper.getAllUser();
                                                adapter = new SimpleAdapter(
                                                        MainActivity.this, list,
                                                        R.layout.item_list,
                                                        new String[]{"imageid", "name", "mobilephone"},
                                                        new int[]{R.id.user_image, R.id.tv_name, R.id.tv_mobilephone});
                                                lv_user.setAdapter(adapter);
                                                deleteId.clear();
                                            }
                                        })
                                        .setNegativeButton("取消", null)
                                        .create()
                                        .show();
                            }

                            break;
                        }
                        case 3: {
                            if (searchLinearLayout != null && searchLinearLayout.getVisibility() == View.VISIBLE) {
                                searchLinearLayout.setVisibility(View.GONE);
                            }
                            loadMainMenuDialog();
                            mainMenuDialog.show();
                            break;
                        }
                        case 4: {
                            finish();
                            break;
                        }
                    }
                }
            });
        }
    }

    //点击菜单相应的方法
    private void loadMainMenuDialog() {
        if (mainMenuDialog == null) {
            LayoutInflater li = LayoutInflater.from(this);
            mainMenuView = li.inflate(R.layout.main_menu_grid, null);
            //根据主菜单视图，创建主菜单对话框
            mainMenuDialog = new AlertDialog.Builder(this).setView(mainMenuView).create();
            //根据主菜单视图，拿到视图文件中的GridView，然后再往里面放Adater
            mainMenuGrid = (GridView) mainMenuView.findViewById(R.id.gridview);
            SimpleAdapter menuAdapter = getMenuAdapter(main_menu_itemName, main_menu_itemSource);
            mainMenuGrid.setAdapter(menuAdapter);
            //响应点击事件
            mainMenuGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //0-5:"显示所有", "删除所有", "备份数据", "还原数据", "更新", "后退"
                    switch (position) {
                        case 0:{
                            break;
                        }
                        case 1:{
                            break;
                        }
                        case 2:{
                            break;
                        }
                        case 3:{
                            break;
                        }
                        case 4:{
                            break;
                        }
                        case 5:{
                            mainMenuDialog.dismiss();
                            break;
                        }
                    }
                }
            });
        }
    }

    //点击查找的时候响应的方法，搜索框
    private void loadSearchLinearLayout() {
        if (searchLinearLayout == null) {
            searchLinearLayout = (LinearLayout) findViewById(R.id.ll_search);
            et_search = (EditText) findViewById(R.id.et_search);
            et_search.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    String condition = et_search.getText().toString();//获得搜索条件
//                    if (condition.equals("")) {
//                        lv_user.setAdapter(adapter);//为了当没有填查询条件的时候，ListView中就显示所有信息,但是在下面的查询方法中的查询语句中的where 1=1已经带有这个功能了，在这里没必要
//                    }
//                    helper = new DBHelper(MainActivity.this);
//                    helper.openDataBase();
                    list = helper.getUserBySearch(condition);
                    SimpleAdapter searchAdapter = new SimpleAdapter(MainActivity.this, list, R.layout.item_list, new String[]{"imageid", "name", "mobilephone"}, new int[]{R.id.user_image, R.id.tv_name, R.id.tv_mobilephone});
                    lv_user.setAdapter(searchAdapter);//将查到整合后的用户信息展示出来
                    if (deleteId != null) {
                        deleteId.clear();
                    }
                    if (list.size() == 0) {
                        //没有数据,设置背景图片
                        Drawable nodata_bg = getResources().getDrawable(R.drawable.nodata_bg);
                        mainLinearLayout.setBackgroundDrawable(nodata_bg);
                        setTitle("没有查到任何数据");
                    } else {
                        setTitle("共查到 " + list.size() + "条记录");
                        mainLinearLayout.setBackgroundDrawable(null);
                    }
                    return false;
                }
            });
        }
    }

    private SimpleAdapter getMenuAdapter(String[] menuNameArray, int[] imageResourceArray) {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < menuNameArray.length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("itemImage", imageResourceArray[i]);
            map.put("itemText", menuNameArray[i]);
            data.add(map);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, data, R.layout.item_menu, new String[]{"itemImage", "itemText"}, new int[]{R.id.item_image, R.id.item_text});
        return simpleAdapter;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //将标记的deleteId清除掉
        if (deleteId != null) {
            deleteId.clear();
        }
        super.onActivityResult(requestCode, resultCode, data);
        //当resultCode==3时代表添加了一个用户返回，当resultCode==4的时候代表修改了用户，或者删除了用户，其他条件代表数据没有变化，通过resultCode判断是否需要刷新
        if (resultCode == 3 || resultCode == 4) {
            //重新刷新数据
//            helper = new DBHelper(this);
//            helper.openDataBase();//？源代码没有这一句
            list = helper.getAllUser();
            adapter = new SimpleAdapter(this, list, R.layout.item_list, new String[]{"imageid", "name", "mobilephone"}, new int[]{R.id.user_image, R.id.tv_name, R.id.tv_mobilephone});
        }
        lv_user.setAdapter(adapter);
    }
}
