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
		
		//����
		btn_sc.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				putData();
			}
		});
		
		//listView��ӵ���¼�
		list_sc.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//listView��ÿһ��Ϊһ��TextView;
				//�õ��������TextView
				TextView tv = (TextView) arg1;
				//�õ�����������(·��);
				String filePath = tv.getText().toString();
				//new һ�� ImageView
				ImageView iv = new ImageView(SearchFileActivity.this);
				//����BitmapFactory���Դ�һ��ָ���ļ���
					//����decodeFile()���Bitmap
				//Ҳ���Զ����ͼƬ��Դ��
					//����decodeResource()���Bitmap
				Bitmap bp = BitmapFactory.decodeFile(filePath);
				//ʹ��bitmapͼƬ
				iv.setImageBitmap(bp);
				
				//����һ��Dialog
				Dialog dialog = new Dialog(SearchFileActivity.this);
				dialog.setTitle("Ԥ��");
				dialog.setContentView(iv);//����ͼƬ
				
				dialog.show();//��ʾ
			}
		});
		
		//��ʼ��ListView;
		initListView();
	}
	
	//�����ϵ
	//��ʼ��ListView
	private void initListView(){
		//����List
		fileList = new ArrayList<String>();
		//����������
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,fileList);
		//���������
		list_sc.setAdapter(adapter);
	}
	
	//������� ����ListView
	public void putData(){
		//����ǰ�����
		fileList.clear();
		//������Ŀ¼
		findFile(new File("/mnt/"));
		//֪ͨ����������
		//��list��getAdapter()�����õ��������������ص���һ��listAdapter
		//��ҪתΪ�˴���Ҫ��ArrayAdapter<String>�󣬿�ʹ�÷���֪ͨ����
		((ArrayAdapter<String>)list_sc.getAdapter()).notifyDataSetChanged();
	}
	
	//�ļ���������
	//����Ϊ��ǰ������Ŀ¼
	public void findFile(File file){
		//�ж�Ŀ¼�ǿ�
		if(file==null){
			return;
		}
		//�жϸ�Ŀ¼�Ƿ����ļ���
		if(file.isDirectory()){
			//ȡ���ļ������ļ��б�list
			//ʹ���ļ�������
			File[] files = file.listFiles(new MyFileFilter());
			//�ж��ļ����Ƿ�Ϊ��
			if(files!=null){
				//�����ļ������ļ�
				for (File file2 : files) {
					//�ݹ�
					findFile(file2);
				}
			}
		}else{//����Ϊ�ļ��� ��ȡ���ļ�Ŀ¼
//			System.out.println(file.getAbsolutePath());
			fileList.add(file.getAbsolutePath());
		}

	}
	
	//�����ļ������� ָ����������
	class MyFileFilter implements FileFilter{

		@Override
		public boolean accept(File arg0) {//��д��������
			if(arg0.isDirectory()){//������ļ��У�Ҫ(����true)
				return true;
			}
			//��ò�ѯ�ؼ���
			String key = edit_sc.getText().toString();
			//������������ļ�����
			String fileName = arg0.getName();
			
			//TextUtils.isEmpty,����ֵ��һ��boolean
			//�������ڲ���Ϊ��null�����ߣ�""��ʱ������true
			//�������Ͷ�ûѡ
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
