package com.android.hzg.contact;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.ImageView.ScaleType;
import com.android.hzg.contact.db.DBHelper;
import com.android.hzg.contact.entity.User;

/**
 * Created by hzg on 2016/12/9.
 */
public class AddNewActivity extends Activity implements ViewSwitcher.ViewFactory {
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
    Button btn_save;
    Button btn_return;

    int privacy;//用于判断添加的用户是否需要保密。1代表隐私用户，0代表普通用户

    ImageButton imageButton;//头像
    AlertDialog imageChooseDialog;//头像选择对话框0
    View imageChooseView;//图像选择的视图
    Gallery gallery;//头像的Gallery(画廊)
    ImageSwitcher imageSwitcher;//头像的ImageSwitcher
    boolean imageChanged;//判断头像有没有变化
    int currentImagePosition;//用于记录当前选中图像在图像数组的位置
    int previousImagePosition;//用于记录上一次图片的位置
    //图片数组(资源id)
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
        setContentView(R.layout.add_new);
        Intent intent = getIntent();
        //为空的话就是普通用户
        if (intent.getExtras() != null && intent.getExtras().getInt("privacy") == 1) {
            privacy = 1;
        } else {
            privacy = 0;
        }

        init();
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断姓名是否为空，不能为空
                String name = et_name.getText().toString();
                if (name.trim().equals("")) {
                    Toast.makeText(AddNewActivity.this, "姓名不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                //从表单上获取数据
                User user = new User();
                user.userName = name;
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
                //判断头像是否改变，若改变则用当前的位置，若没有改变，则用前一回的位置
                if (imageChanged) {
                    user.imageId = images[currentImagePosition % images.length];
                } else {
                    user.imageId = images[previousImagePosition % images.length];
                }
                //用户类型
                user.privacy = privacy;
                //创建数据库帮助类
                DBHelper helper = new DBHelper(AddNewActivity.this);
                //打开数据库
                helper.openDataBase();
                //把user存储到数据库在里
                long result =  helper.insert(user);

                //判断插入是否成功
                if (result == -1) {
                    Toast.makeText(AddNewActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddNewActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                    setTitle("用户添加成功！");
                }
                setResult(3);//设置返回结果码，表示需要刷新（调用MainActivity方法中的OnActivityResult）
                finish();//销毁当前视图,返回到上一个Activity,也就是Main.activity;
            }
        });
        btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImage();//为gallery装载图片
                initImageChooseDialog();//初始化imageChooseDialog(图片选择的会话)
                imageChooseDialog.show();
            }
        });
    }

    public void init() {
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

        btn_save = (Button) findViewById(R.id.btn_save);
        btn_return = (Button) findViewById(R.id.btn_return);
        imageButton = (ImageButton) findViewById(R.id.image_button);
    }

    public void loadImage() {
        if (imageChooseView == null) {
            LayoutInflater li = LayoutInflater.from(AddNewActivity.this);
            imageChooseView = li.inflate(R.layout.imageswitch, null);
            gallery = (Gallery) imageChooseView.findViewById(R.id.gallery);
            gallery.setAdapter(new ImageAdapter(this));//为Gallery装载图片
            gallery.setSelection(images.length / 2);//默认情况下定义到中间
            imageSwitcher = (ImageSwitcher) imageChooseView.findViewById(R.id.imageswitch);
            imageSwitcher.setFactory(this);//要实现makeView方法
            gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //当前的头像位置为选中的位置
                    currentImagePosition = position;
                    //为imageSwitcher设置图像
                    imageSwitcher.setImageResource(images[position % images.length]);
                }

            });
        }
    }

    public void initImageChooseDialog() {
        if (imageChooseDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("请选择图像").setView(imageChooseView).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    imageChanged = true;
                    imageButton.setImageResource(images[currentImagePosition % images.length]);
                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            imageChooseDialog = builder.create();
        }
    }

    @Override
    public View makeView() {
        ImageView imageView = new ImageView(this);
        imageView.setBackgroundColor(0xff000000);
        imageView.setScaleType(ScaleType.FIT_CENTER);
        imageView.setLayoutParams(new ImageSwitcher.LayoutParams(90, 90));
        return imageView;
    }

    //自定义ViewPager的适配器
    class ImageAdapter extends BaseAdapter {

        private Context context;

        public ImageAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        //gallery从这个方法中拿到image
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView iv = new ImageView(context);
            iv.setImageResource(images[position % images.length]);
            iv.setAdjustViewBounds(true);
            iv.setLayoutParams(new Gallery.LayoutParams(80, 80));
            iv.setPadding(15, 10, 15, 10);
            return iv;
        }
    }
    /**
     * 当退出的时候，回收资源
     */
    @Override
    protected void onDestroy() {
        if (imageSwitcher != null) {
            imageSwitcher = null;
        }
        if (gallery != null) {
            gallery = null;
        }
        if (imageChooseDialog != null) {
            imageChooseDialog = null;
        }
        if (imageChooseView != null) {
            imageChooseView = null;
        }
        if (imageButton != null) {
            imageButton = null;
        }

        super.onDestroy();
    }
}
