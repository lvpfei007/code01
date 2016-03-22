package com.codefiles;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.codefiles.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class BrowserFileActivity extends Activity {
    
	private Button btnBack;
	private TextView tv_dic;
	
	private ListView listView;
	private List<Map<String,Object>> data;
	
	//记录当前的父文件夹
	private File root = new File("/");//根目录
	
	//记录当前路径下的所有文件夹的文件数组
	private File[] currentFiles;
	
	private SimpleAdapter adapter;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        btnBack = (Button) findViewById(R.id.button_back);//返回键
        tv_dic = (TextView) findViewById(R.id.textView_directory); //目录显示
        listView = (ListView) findViewById(R.id.listView_directory);//list列表
        
        
        
        init();//初始化
        
        btnBack.setOnClickListener(new OnClickListener() {//返回按钮事件
			
			@Override
			public void onClick(View arg0) {
				//得到当前父文件夹的父文件路径;
				//返回此抽象路径名的父目录的抽象路径名 若此路径名没有父目录 则返回null
				File tempParent = root.getParentFile();
				if(tempParent == null){
					//参数应为当前活动.this，不为this
					Toast.makeText(BrowserFileActivity.this, "已经是根目录了", Toast.LENGTH_SHORT).show();
					return;
				}
				root = tempParent;
				
				//更新数据源(更新list)
				updateData();
			}
		});
        
        //listView监听器
        //listView 点击事件
        listView.setOnItemClickListener(new OnItemClickListener() {
        	
			//View arg1 当前选择的list中的item,int arg2 当前item的下标
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//若点击为文件夹，则进入文件夹并更新目录及当前文件夹listFiles
				currentFiles = root.listFiles();
				File selFile = currentFiles[arg2];
				if(selFile.isFile()){
					return;
				}
				File[] tempFiles = selFile.listFiles();
				if(tempFiles==null){
					Toast.makeText(BrowserFileActivity.this, "空文件夹！", Toast.LENGTH_SHORT).show();
					return;
				}
				root = selFile;
				
				updateData();
			}
		});
    }
    

   /*
    * 初始化 为list添加适配器 
    * 只第一次进入时执行添加 以后只更新数据
    */
    private void init(){
    	currentFiles = root.listFiles();//当前文件夹listFiles
    	data = new ArrayList<Map<String,Object>>();
    	
    	//适配数据和视图关系
    	String[] from = {"icon","title","date"};
    	int[] to = {R.id.imageView_file,R.id.textView_fileName,R.id.textView_time};
    	//与ArrayAdapter区别
    	adapter = new SimpleAdapter(this, data, R.layout.item, from, to);
    	
    	//绑定适配器
    	listView.setAdapter(adapter);
    	
    	updateData();//更新数据源
    }
    
    //为list添加内容
    private void updateData(){   	
//    	如果不为空，则清空，否则list内容会向下追加
//    	初次进入时为空 后进入返回操作时需要清空原list后加入新目录数据
    	if(data!=null && data.size()>0){
    		data.clear();
    	}
    	File[] files = root.listFiles();//获得root目录下的文件集合
    	for (File file : files) {//遍历目录
    		//HashMap通过hashcode对其内容进行快速查找
    		//HashMap 非线程安全 TreeMap 非线程安全 
    		//每个item都是一个map集合
    		Map<String, Object> f = new HashMap<String,Object>();
    		if(file.isDirectory()){//如果是文件夹
    			f.put("icon", R.drawable.folder);
    		}else{
    			f.put("icon", R.drawable.file);
    		}
    		f.put("title", file.getName());
    		//file.lastModified() 文件最后修改时间
    		f.put("date", DateFormat.format("yyyy年MM月dd日 hh:mm:ss", file.lastModified()));
    		
    		data.add(f);//添加进list
		}
    	
    	//显示父目录地址
    	tv_dic.setText(root.getAbsolutePath());
    	
    	//通知适配器更新
    	adapter.notifyDataSetChanged();
    	
    }
    
    /*
     * 添加菜单
     * onCreateOptionsMenu
     */
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add("搜索文件").setIcon(R.drawable.ic_launcher);
    	return super.onCreateOptionsMenu(menu);
    }
    
    /*
     * 菜单事件
     * onMenuItemSelected
     */
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	
    	// 因为菜单只有一个 略去菜单编号判断
    	
    	Intent it = new Intent();//意图类跳转
    	it.setClass(this, SearchFileActivity.class);
    	startActivity(it);
    	
    	return super.onMenuItemSelected(featureId, item);
    }
}