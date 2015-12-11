package com.meiriq.xposehook;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.meiriq.xposehook.bean.ConfigHelper;
import com.meiriq.xposehook.tutorial.AppUtils;
import com.meiriq.xposehook.tutorial.Shell;
import com.meiriq.xposehook.utils.L;
import com.meiriq.xposehook.utils.TestUtil;
import com.umeng.update.UmengUpdateAgent;

import java.util.ArrayList;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initView();

        Shell.getRoot();

        UmengUpdateAgent.update(this);


    }

    private void initView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_recycleview);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3,LinearLayoutManager.VERTICAL,false));
        MyAdapter adapter = new MyAdapter(this);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            startActivity(new Intent(this,UninstallActivity.class));
        } else if (id == R.id.nav_gallery) {
            startActivity(new Intent(this,ClearDataActivity.class));
        } else if (id == R.id.nav_slideshow) {
            startActivity(new Intent(this,FindApkActivity.class));
        } else if (id == R.id.nav_manage) {
            startActivity(new Intent(this,RecordAppFileActivity.class));
        } else if (id == R.id.nav_share) {
            startActivity(new Intent(this,SetDataActivity.class));
        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    int[] images = {R.mipmap.ic_launcher,R.mipmap.ic_launcher,R.mipmap.ic_launcher,
            R.mipmap.ic_launcher,R.mipmap.ic_launcher,R.mipmap.ic_launcher};
    String[] texts = {"一键卸载","清除数据","查找apk","记录文件","修改数据","测试是否可运行"};
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

        LayoutInflater inflater;
        public MyAdapter(Context context){
            inflater = LayoutInflater.from(context);
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.item_main,null);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            holder.imageView.setImageResource(images[position]);
            holder.textView.setText(texts[position]);
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position == 0) {
                        // Handle the camera action
                        startActivity(new Intent(MainActivity.this,UninstallActivity.class));
                    } else if (position == 1) {
                        startActivity(new Intent(MainActivity.this,ClearDataActivity.class));
                    } else if (position == 2) {
                        startActivity(new Intent(MainActivity.this,FindApkActivity.class));
                    } else if (position == 3) {
                        startActivity(new Intent(MainActivity.this,RecordAppFileActivity.class));
                    } else if (position == 4) {
                        startActivity(new Intent(MainActivity.this,SetDataActivity.class));
                    } else if (position == 5) {
                        if(TestUtil.testXpose() == 1){
                            Toast.makeText(MainActivity.this,"XposeHook已打开，请放心使用~~~",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this,"XposeHook未加载,请重启手机!!!",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }


        @Override
        public int getItemCount() {
            return texts.length;
        }

        class MyViewHolder extends RecyclerView.ViewHolder{

            ImageView imageView;
            TextView textView;
            LinearLayout linearLayout;

            public MyViewHolder(View itemView) {
                super(itemView);
                linearLayout = (LinearLayout) itemView.findViewById(R.id.ll_all);
                imageView = (ImageView) itemView.findViewById(R.id.imageView);
                textView = (TextView) itemView.findViewById(R.id.textView);
            }
        }
    }


}
