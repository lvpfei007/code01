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
	
	//��¼��ǰ�ĸ��ļ���
	private File root = new File("/");//��Ŀ¼
	
	//��¼��ǰ·���µ������ļ��е��ļ�����
	private File[] currentFiles;
	
	private SimpleAdapter adapter;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        btnBack = (Button) findViewById(R.id.button_back);//���ؼ�
        tv_dic = (TextView) findViewById(R.id.textView_directory); //Ŀ¼��ʾ
        listView = (ListView) findViewById(R.id.listView_directory);//list�б�
        
        
        
        init();//��ʼ��
        
        btnBack.setOnClickListener(new OnClickListener() {//���ذ�ť�¼�
			
			@Override
			public void onClick(View arg0) {
				//�õ���ǰ���ļ��еĸ��ļ�·��;
				//���ش˳���·�����ĸ�Ŀ¼�ĳ���·���� ����·����û�и�Ŀ¼ �򷵻�null
				File tempParent = root.getParentFile();
				if(tempParent == null){
					//����ӦΪ��ǰ�.this����Ϊthis
					Toast.makeText(BrowserFileActivity.this, "�Ѿ��Ǹ�Ŀ¼��", Toast.LENGTH_SHORT).show();
					return;
				}
				root = tempParent;
				
				//��������Դ(����list)
				updateData();
			}
		});
        
        //listView������
        //listView ����¼�
        listView.setOnItemClickListener(new OnItemClickListener() {
        	
			//View arg1 ��ǰѡ���list�е�item,int arg2 ��ǰitem���±�
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//�����Ϊ�ļ��У�������ļ��в�����Ŀ¼����ǰ�ļ���listFiles
				currentFiles = root.listFiles();
				File selFile = currentFiles[arg2];
				if(selFile.isFile()){
					return;
				}
				File[] tempFiles = selFile.listFiles();
				if(tempFiles==null){
					Toast.makeText(BrowserFileActivity.this, "���ļ��У�", Toast.LENGTH_SHORT).show();
					return;
				}
				root = selFile;
				
				updateData();
			}
		});
    }
    

   /*
    * ��ʼ�� Ϊlist��������� 
    * ֻ��һ�ν���ʱִ����� �Ժ�ֻ��������
    */
    private void init(){
    	currentFiles = root.listFiles();//��ǰ�ļ���listFiles
    	data = new ArrayList<Map<String,Object>>();
    	
    	//�������ݺ���ͼ��ϵ
    	String[] from = {"icon","title","date"};
    	int[] to = {R.id.imageView_file,R.id.textView_fileName,R.id.textView_time};
    	//��ArrayAdapter����
    	adapter = new SimpleAdapter(this, data, R.layout.item, from, to);
    	
    	//��������
    	listView.setAdapter(adapter);
    	
    	updateData();//��������Դ
    }
    
    //Ϊlist�������
    private void updateData(){   	
//    	�����Ϊ�գ�����գ�����list���ݻ�����׷��
//    	���ν���ʱΪ�� ����뷵�ز���ʱ��Ҫ���ԭlist�������Ŀ¼����
    	if(data!=null && data.size()>0){
    		data.clear();
    	}
    	File[] files = root.listFiles();//���rootĿ¼�µ��ļ�����
    	for (File file : files) {//����Ŀ¼
    		//HashMapͨ��hashcode�������ݽ��п��ٲ���
    		//HashMap ���̰߳�ȫ TreeMap ���̰߳�ȫ 
    		//ÿ��item����һ��map����
    		Map<String, Object> f = new HashMap<String,Object>();
    		if(file.isDirectory()){//������ļ���
    			f.put("icon", R.drawable.folder);
    		}else{
    			f.put("icon", R.drawable.file);
    		}
    		f.put("title", file.getName());
    		//file.lastModified() �ļ�����޸�ʱ��
    		f.put("date", DateFormat.format("yyyy��MM��dd�� hh:mm:ss", file.lastModified()));
    		
    		data.add(f);//��ӽ�list
		}
    	
    	//��ʾ��Ŀ¼��ַ
    	tv_dic.setText(root.getAbsolutePath());
    	
    	//֪ͨ����������
    	adapter.notifyDataSetChanged();
    	
    }
    
    /*
     * ��Ӳ˵�
     * onCreateOptionsMenu
     */
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add("�����ļ�").setIcon(R.drawable.ic_launcher);
    	return super.onCreateOptionsMenu(menu);
    }
    
    /*
     * �˵��¼�
     * onMenuItemSelected
     */
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	
    	// ��Ϊ�˵�ֻ��һ�� ��ȥ�˵�����ж�
    	
    	Intent it = new Intent();//��ͼ����ת
    	it.setClass(this, SearchFileActivity.class);
    	startActivity(it);
    	
    	return super.onMenuItemSelected(featureId, item);
    }
}