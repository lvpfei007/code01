  package com.codefiles;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import com.codefiles.R;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SearchFileActivity extends Activity {
	private CheckBox check_jpg;
	private CheckBox check_png;
	private CheckBox check_gif;
	private EditText edit_sc;
	private Button btn_sc;
	private ListView list_sc;
	
	private List<String> fileList;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		
		check_jpg = (CheckBox) findViewById(R.id.checkBox_jpg);
		check_png = (CheckBox) findViewById(R.id.checkBox_png);
		check_gif = (CheckBox) findViewById(R.id.checkBox_gif);
		edit_sc = (EditText) findViewById(R.id.editText_sc);
		btn_sc = (Button) findViewById(R.id.button_sc);
		list_sc = (ListView) findViewById(R.id.listView_sc);
		
		//搜索
		btn_sc.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				putData();
			}
		});
		
		//listView添加点击事件
		list_sc.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//listView的每一项为一个TextView;
				//得到被点击的TextView
				TextView tv = (TextView) arg1;
				//得到点击项的文字(路径);
				String filePath = tv.getText().toString();
				//new 一个 ImageView
				ImageView iv = new ImageView(SearchFileActivity.this);
				//利用BitmapFactory可以从一个指定文件中
					//利用decodeFile()解出Bitmap
				//也可以定义的图片资源中
					//利用decodeResource()解出Bitmap
				Bitmap bp = BitmapFactory.decodeFile(filePath);
				//使用bitmap图片
				iv.setImageBitmap(bp);
				
				//创建一个Dialog
				Dialog dialog = new Dialog(SearchFileActivity.this);
				dialog.setTitle("预览");
				dialog.setContentView(iv);//放入图片
				
				dialog.show();//显示
			}
		});
		
		//初始化ListView;
		initListView();
	}
	
	//适配关系
	//初始化ListView
	private void initListView(){
		//定义List
		fileList = new ArrayList<String>();
		//定义适配器
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,fileList);
		//添加适配器
		list_sc.setAdapter(adapter);
	}
	
	//填充数据 更新ListView
	public void putData(){
		//遍历前先清空
		fileList.clear();
		//搜索根目录
		findFile(new File("/mnt/"));
		//通知适配器更新
		//由list的getAdapter()方法得到适配器，但返回的是一个listAdapter
		//需要转为此处需要的ArrayAdapter<String>后，可使用方法通知更新
		((ArrayAdapter<String>)list_sc.getAdapter()).notifyDataSetChanged();
	}
	
	//文件搜索方法
	//参数为当前的搜索目录
	public void findFile(File file){
		//判断目录非空
		if(file==null){
			return;
		}
		//判断该目录是否是文件夹
		if(file.isDirectory()){
			//取得文件夹内文件列表list
			//使用文件过滤器
			File[] files = file.listFiles(new MyFileFilter());
			//判断文件夹是否为空
			if(files!=null){
				//遍历文件夹内文件
				for (File file2 : files) {
					//递归
					findFile(file2);
				}
			}
		}else{//若不为文件夹 则取得文件目录
//			System.out.println(file.getAbsolutePath());
			fileList.add(file.getAbsolutePath());
		}

	}
	
	//定义文件过滤器 指定搜索规则
	class MyFileFilter implements FileFilter{

		@Override
		public boolean accept(File arg0) {//编写搜索规则
			if(arg0.isDirectory()){//如果是文件夹，要(返回true)
				return true;
			}
			//获得查询关键字
			String key = edit_sc.getText().toString();
			//获得搜索到的文件名称
			String fileName = arg0.getName();
			
			//TextUtils.isEmpty,返回值是一个boolean
			//当括号内参数为（null）或者（""）时，返回true
			//三种类型都没选
			if(!check_jpg.isChecked()&&!check_png.isChecked()&&!check_gif.isChecked()){
				if(fileName.contains(key)||TextUtils.isEmpty(key)){
					return true;
				}else{
					return false;
				}
			}
			
			if(check_jpg.isChecked()){
				if(fileName.contains(key)||TextUtils.isEmpty(key)){
					return true;
				}else{
					return false;
				}
			}
			
			if(check_png.isChecked()){
				if(fileName.contains(key)||TextUtils.isEmpty(key)){
					return true;
				}else{
					return false;
				}
			}
			
			if(check_gif.isChecked()){
				if(fileName.contains(key)||TextUtils.isEmpty(key)){
					return true;
				}else{
					return false;
				}
			}
			return false;
		}
		
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
