package com.codefiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.codefiles.adapter.AdapterList;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.sax.StartElementListener;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class BrowserFileActivity2 extends Activity implements OnItemLongClickListener, 
		OnClickListener, OnItemClickListener  {
    
	private Button btnBack;
	private TextView tv_dic;
	private Button btnExit;
	
	private Button btnConvert;
	private Button btnJump;
	private Button btnFind;
	private Button btnLastWord;
	private Button btnNextWord;
	
	private EditText editSearch;
	
	private ListView listView;
	private List<Map<String,Object>> data;
	
	private List<Map<String,Object>> dataOrder;
	private AdapterList adapterList;
	
	private static Context context;
	
	private static EditText edtCode;
	private static LinearLayout linearCode;
	private static LinearLayout linearList;
	
	private static int indexFindWord;
	
	//记录点击返回时间
	private long currentStart = 0;
	
	
	//记录当前的父文件夹
	private File root = null;//根目录
	
	//记录当前路径下的所有文件夹的文件数组
	private File[] currentFiles;
	private SimpleAdapter adapter;
	
	//以提取保存文件的格式
	private String sFile = null;
	//记录当前打开的文件路径
	static File selFile = null;

	private View view;
	private TextView tvItem;
	
	private static StringBuilder sb = null;
	
	private static final String TAG = "CodeFiles";
	private static final String EDIT_SIZE = "EditTextSize";
	
	private static String editFindWord;
	
	private static SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	
	private static int sizeStart;
	
	private static final String FileRootStr = "/mnt/sdcard/";
	private List<File> startWithEditList;
	
	private File[] rootStartWithEdit;
	
	private final static int saveShareMode = Context.MODE_WORLD_READABLE+Context.MODE_WORLD_WRITEABLE;
	private String convtSaveFile = "myshareCodeConvt";//文件编码格式存取目录
	private static String selecSaveFile = "myshareCodeSelecion";//文件浏览进度存取目录
	private static String edtLastSaveFile = "myshareEdtLast";//最近打开记录文件share存储目录
	private static final int convtFileDefault = 1;//文件编码格式默认值1:utf-8 2:gbk
	
	private static List<String> edtLastListArray;
	private static int edtLastListNum = 20;//最近打开记录数量上限
	private static int edtThisListNum;//取到的最近打开记录数量
	
	private static boolean openEdtCodeFlag = false;//editText是否打开flag
	
	private final int touchOpenLength = 400;
	
//	private static SpannableStringBuilder style;   
//	private static int editTextLength = 0;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        context = this;
        
        initView();
        init();    
    }
    
    private void initView(){
    	
    	edtCode = (EditText) findViewById(R.id.edtCode);
        linearCode = (LinearLayout) findViewById(R.id.linearCode);
        linearList = (LinearLayout) findViewById(R.id.linearList);
        editSearch = (EditText) findViewById(R.id.edit_Catalog_Search);
        editAddChangeListener();
        
        btnBack = (Button) findViewById(R.id.button_back);//返回键
        btnExit = (Button) findViewById(R.id.button_finish);//退出键
        //返回键  //目录显示
        tv_dic = (TextView) findViewById(R.id.textView_directory); //返回键  //目录显示
        //list列表
        listView = (ListView) findViewById(R.id.listView_directory);//list列表
        view = LayoutInflater.from(this).inflate(R.layout.item, null);
        tvItem = (TextView) view.findViewById(R.id.textView_fileName);
        
        btnConvert = (Button) findViewById(R.id.btnConvert);
        btnJump = (Button) findViewById(R.id.btnJump);
        btnFind = (Button) findViewById(R.id.btnFind);
        btnLastWord = (Button) findViewById(R.id.btnLastWord);
        btnNextWord = (Button) findViewById(R.id.btnNextWord);
        
        edtCode.setOnTouchListener(new MyOnTouch());
        //linearList要添加事件
        //需要在.xml中设置可被选中android:clickable="true" 
        linearList.setOnTouchListener(new MenuTouch());
        listView.setOnTouchListener(new MenuTouch());
        
      //直接返回目录事件
        btnBack.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnConvert.setOnClickListener(this);
        btnJump.setOnClickListener(this);
        btnFind.setOnClickListener(this);
        btnLastWord.setOnClickListener(this);
        btnNextWord.setOnClickListener(this);
        
        //注册list长按事件
        listView.setOnItemLongClickListener(this);
        //listView监听器
        listView.setOnItemClickListener(this);
        
        startWithEditList = new ArrayList<File>();
    }
    
   /*
    * 初始化 为list添加适配器 
    * 只第一次进入时执行添加 以后只更新数据
    */
    private void init(){
    	
    	System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");  
    	
    	preferences = getPreferences(Activity.MODE_WORLD_WRITEABLE);
    	editor = preferences.edit();
    	
    	root = new File(FileRootStr);//根目录初始化
    	data = new ArrayList<Map<String,Object>>();
    	dataOrder = new ArrayList<Map<String,Object>>();
    	
    	edtLastListArray = new ArrayList<String>();
    	for (int i = 0; i < edtLastListNum; i++) {
    		edtLastListArray.add("edtLastList"+i);
		}
    	
    	updateData(0);//更新数据源
    }
    
    //editText搜索监听
    private void editAddChangeListener(){
    	editSearch.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				//大小写都搜索 不区分
				String editSearchStr = editSearch.getText().toString().toLowerCase();
				if("".equals(editSearchStr)){//当editSearch内容被删除时
					rootStartWithEditToNull();
					updateData(1);
					return;
				}
				List<File> rootFileList = getRootFileList();//当前列表数组转换为list
				startWithEditList.clear();
				for (int i=0; i<rootFileList.size(); i++) {
					boolean startWithEditBol = 
							rootFileList.get(i).getName().toLowerCase().startsWith(editSearchStr);
					if(startWithEditBol){
						startWithEditList.add(rootFileList.get(i));
					}
				}
				int startWithEditLength = startWithEditList.size();
				rootStartWithEdit = new File[startWithEditLength];
				for (int i = 0; i < startWithEditLength; i++) {
					rootStartWithEdit[i] = startWithEditList.get(i);
				}
				updateData(1);
			}
		});
    }
    

    
    @Override
    //listView 点击事件
    //View arg1 当前选择的list中的item,int arg2 当前item的下标
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
    	// TODO Auto-generated method stub
    	listOnItemClickEvent(arg2, 0);
    }
    
    private void listOnItemClickEvent(int arg2, int openFileFlag){
    	//若点击为文件夹，则进入文件夹并更新目录及当前文件夹listFiles
    			if(openFileFlag == 0){
    				currentFiles = getRootFileArray();
    			}
    			for (File file : currentFiles) {
    				Log.d(TAG, "onItemClick file.name:"+file.getName());
    			}
    			selFile = currentFiles[arg2];
    			
    			if(selFile.getName().contains(".xml")||selFile.getName().contains(".txt")||
    					selFile.getName().contains(".java")||selFile.getName().contains(".properties")){
    				openFileInEdit(selFile, getCurConvtMode());
    				return;
    			}
    			if(root.isFile()){
    				Toast.makeText(BrowserFileActivity2.this, R.string.null_file, Toast.LENGTH_SHORT).show();
    				return;
    			}
    			final File[] tempFiles = selFile.listFiles();
    			File[] tempFilesNew = tempFiles;
    			if(tempFiles==null && root.isDirectory()){
    				Toast.makeText(BrowserFileActivity2.this, R.string.null_folder, Toast.LENGTH_SHORT).show();
    				return;
    			}
    			
    			if(currentFiles.length != 1 && tempFiles.length == 1 && tempFiles[0].isDirectory()){
    				new AlertDialog.Builder(BrowserFileActivity2.this)
    				.setTitle("快速进入")
    				.setCancelable(true)
    				.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							fastGoToList(tempFiles);
						}
					})
					.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							root = selFile;
		        			rootStartAddEditToNull();
		        			updateData(0);
						}
					})
					.create().show();
    			}else{
    				root = selFile;
        			rootStartAddEditToNull();
        			updateData(0);
    			}
    }
    
    //快速进入目录 当子目录只有一个文件夹时
    private void fastGoToList(File[] tempFiles){
    	while(tempFiles.length == 1 && tempFiles[0].isDirectory()){
			File[] tempFilesList = tempFiles[0].listFiles();
			selFile = tempFiles[0];
			tempFiles = tempFilesList;
		}
    	root = selFile;
		rootStartAddEditToNull();
		updateData(0);
    }
    
    //获取上次保存的文件编码格式
    private int getCurConvtMode(){
    	SharedPreferences share = getSharedPreferences(convtSaveFile, saveShareMode);
		int convtSaveMode = share.getInt(selFile.getName(), convtFileDefault);
		Log.d(TAG, "getCurConvtMode() return convtSaveMode:"+convtSaveMode);
		return convtSaveMode;
    }
    
    //获取相反的编码格式
    private int getCurConvtOppMode(){
    	if(getCurConvtMode() == 1){
    		Log.d(TAG, "getCurConvtOppMode() return 2");
    		return 2;
    	}else if(getCurConvtMode() == 2){
    		Log.d(TAG, "getCurConvtOppMode() return 1");
    		return 1;
    	}
    	Log.d(TAG, "getCurConvtOppMode() return 1");
		return 1;
    }
    
    //文件编码格式保存
    private void saveConvtMode(int convtTheMode){
    	Log.d(TAG, "saveConvtMode() convtTheMode:"+convtTheMode);
    	SharedPreferences share = getSharedPreferences(convtSaveFile, saveShareMode);
    	Editor editor = share.edit();
    	editor.putInt(selFile.getName(), convtTheMode);
    	editor.commit();
    }
    
    @Override
    public void onClick(View arg0) {
    	switch (arg0.getId()) {
		case R.id.button_back:
			String rootStr = "/mnt/sdcard/code/";
			File fileRoot = new File(rootStr);
			if(linearCode.getVisibility()==View.VISIBLE){
				ExitCode();
			}
			rootStartAddEditToNull();
			root = fileRoot;
			//更新数据源(更新list)
			updateData(0);
			break;
		case R.id.button_finish:
			finish();
			break;
		case R.id.btnConvert:
			int curPos = edtCode.getSelectionStart();
			ExitCode();
			openFileInEdit(selFile, getCurConvtOppMode());
			edtCode.setSelection(curPos);
			saveConvtMode(getCurConvtOppMode());
			break;
		case R.id.btnJump:
			dialogEditProgress();
			break;
		case R.id.btnFind://查找
			indexFindWord = 0;
			btnFind.setText("find");
//			style=new SpannableStringBuilder(sb);  
			dialogFindWords();
			break;
		case R.id.btnLastWord://上一个查找结果
			if(!TextUtils.isEmpty(editFindWord)){
				findWords(sb, editFindWord, 2);
			}
			break;
		case R.id.btnNextWord://下一个查找结果
			if(!TextUtils.isEmpty(editFindWord)){
				findWords(sb, editFindWord, 3);
			}
			break;
		default:
			break;
		}
    }
    
    //查找dialog
    private void dialogFindWords(){
    	final EditText edtRename = new EditText(BrowserFileActivity2.this);

		Builder dialogFind = new Builder(BrowserFileActivity2.this);
		dialogFind.setTitle(R.string.textFind);
		dialogFind.setView(edtRename);
		dialogFind.setPositiveButton(R.string.textFind, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				String edtRenameStr = edtRename.getText().toString();//获取要改为的名字
				editFindWord = edtRenameStr.toLowerCase().trim();
				if(editFindWord.isEmpty() || editFindWord.length()>20){
					Toast.makeText(BrowserFileActivity2.this, getString(R.string.forbidTooLong), 1).show();
					return;
				}
//				editTextLength = editFindWord.length();
				btnFind.setText("find("+editFindWord+")");
				findWords(sb, editFindWord, 1);
			}
		});
		dialogFind.setNegativeButton(R.string.cancel, null);
		dialogFind.create().show();
    }
    
    //查找方法实现
    private static void findWords(StringBuilder strBuilder, String wordsFind, int modeInt){
    	Log.d(TAG, "findWords() modeInt:"+modeInt+" indexFindWord:"+indexFindWord);

    	String strTemp = strBuilder.toString();
    	String str = strTemp.toLowerCase();
    	
    	if(modeInt == 1){
    		indexFindWord = indexFindWord + str.indexOf(wordsFind);
    	}
    	if(modeInt == 1 && indexFindWord <= 0){
    		Toast.makeText(context, "未找到", Toast.LENGTH_SHORT).show();
    		editFindWord = null;
    		return;
    	}
    	if(indexFindWord <= 0){
    		return;
    	}
    	switch (modeInt) {
		case 1:
			Log.d(TAG, "findWords() case 1");
			if(indexFindWord < sb.length()){
				edtCode.setSelection(indexFindWord);
			}else{
				String strToFirst = strBuilder.toString();
				String strToFirst2 = strToFirst.toLowerCase();
		    	indexFindWord = strToFirst2.indexOf(wordsFind);
		    	edtCode.setSelection(indexFindWord);
			}
//			editTextSetStyle(indexFindWord, editTextLength);
			break;
		case 2:
			Log.d(TAG, "findWords() case 2");
			String strLast = str.substring(0, indexFindWord);
			indexFindWord = strLast.lastIndexOf(wordsFind);
			if(indexFindWord >= 0){
				edtCode.setSelection(indexFindWord);
			}else if(indexFindWord < 0){
				String strTempFirstToLast = strBuilder.toString();
		    	String strFirstToLast = strTempFirstToLast.toLowerCase();
		    	indexFindWord = strFirstToLast.lastIndexOf(wordsFind);
		    	edtCode.setSelection(indexFindWord);
			}
//			editTextSetStyle(indexFindWord, editTextLength);
			break;
		case 3:
			Log.d(TAG, "findWords() case 3");
			if(indexFindWord < sb.length()){
				indexFindWord += 2;
				String strNext = str.substring(indexFindWord);
				StringBuilder sbNextBuilder = new StringBuilder(strNext);
				findWords(sbNextBuilder, wordsFind, 1);
			}else{
				indexFindWord = 0;
				findWords(sb, wordsFind, 1);
			}			
			break;

		default:
			break;
		}
    }
    
//    private static void editTextSetStyle(final int startIndex, final int length){
//    	AsyncTask<Integer, Integer, Integer> task = new AsyncTask<Integer, Integer, Integer>(){
//			@Override
//			protected Integer doInBackground(Integer... arg0) {
//				style.setSpan(new ForegroundColorSpan(Color.RED),startIndex,startIndex+length,Spannable.SPAN_EXCLUSIVE_INCLUSIVE); 
//				
//				publishProgress(0);
//				return null;
//			}
//			@Override
//			protected void onPostExecute(Integer result) {
//				// TODO Auto-generated method stub
//				super.onPostExecute(result);
//				edtCode.setText(style); 
//			}
//    	};
//    	task.execute(0);   
//    }
    
    private List<File> getRootFileList(){//当前列表数组转换为list
    	return Arrays.asList(getRootFileArray());
    }
    
    private File[] getRootFileArray(){//获取搜索edtSearch后的内容列表
    	if(rootStartWithEdit == null){
    		Log.d(TAG, "rootStartWithEdit == null");
    		return rootToComparator(root);//为当前目录按字母自动排序
    	}else{
    		Log.d(TAG, "rootStartWithEdit != null");
    		return rootStartWithEdit;
    	}
    }
    
    private void rootStartWithEditToNull(){//设置edtSearch内容为空
    	rootStartWithEdit = null;
    }
    
    private void rootStartAddEditToNull(){//设置edtSearch内容为空
    	rootStartWithEdit = null;
    	editSearch.setText("");
    }
    
  //为当前目录按字母自动排序
    @SuppressWarnings("unchecked")
	private File[] rootToComparator(File root){
    	File[] fileTempArray = root.listFiles();
    	List fileListFromArray = Arrays.asList(fileTempArray);
    	try {
    		Collections.sort(fileListFromArray);//字母排序 自动
    		Collections.sort(fileListFromArray, new MyIntComparator());  //文件夹前/文件后 排序
		} catch (Exception e) {
			Toast.makeText(this, getString(R.string.sortError), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
    	
    	final int listSize = fileListFromArray.size();
    	Log.d(TAG, "listSize:"+listSize);
    	return (File[])fileListFromArray.toArray();
    }
    
    //自定义排序
    class MyIntComparator implements Comparator{
        public int compare(Object o1, Object o2) {  

    		File f1 = (File) o1; 
        	File f2 = (File) o2; 
        	
        	String f1FileName = f1.getName();
        	String f2FileName = f2.getName();
        	
        	
        	if(f1.isDirectory() && f2.isFile()){
        		return -1;
        	}else if(f1.isFile() && f2.isDirectory()){
        		return 1;
        	}else if(f1FileName.indexOf(".") ==-1 && f1.isDirectory() && f2FileName.indexOf(".") > -1 && f2.isDirectory()){
        		return -1;
        	}else if(f1FileName.indexOf(".") > -1 && f1.isDirectory() && f2FileName.indexOf(".") == -1 && f2.isDirectory()){
        		return 1;
        	}else if(f1FileName.indexOf(".") > 0 && f1.isFile() && f2FileName.indexOf(".") == 0 && f2.isFile()){
        		return -1;
        	}else if(f1FileName.indexOf(".") == 0 && f1.isFile() && f2FileName.indexOf(".") > 0 && f2.isFile()){
        		return 1;
        	}else{
        		return 0;  	
        	}
        
        }  
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	//设置屏幕超时时间
    	Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 160000);
    }

    //打开目录说明文件
    public void openDetail(File root){
    	boolean flag = false;
    	File[] files = getRootFileArray();
    	if(files == null){
    		Toast.makeText(context, "无文件", 1).show();
    		return;
    	}
    	for (File file : files) {
    		Log.d("lvpfei", "file.getName():"+file.getName());
			if(file.getName().equals("readme.txt")){
				openFileInEdit(file, getCurConvtMode());
				flag = true;
				break;
			}
		}
    	if(!flag){
    		Toast.makeText(context, getString(R.string.toNewReadFile), 1).show();
    	}
    }
    
    
  //返回事件
    private boolean btnBackEvent(){
    	
    	if(!TextUtils.isEmpty(editSearch.getText().toString())){
			editSearch.setText("");
		}
    	
    	//得到当前父文件夹的父文件路径;
    	//返回此抽象路径名的父目录的抽象路径名 若此路径名没有父目录 则返回null
    	//返回时 如果父文件夹的目录数仅为1时，则继续返回
    	File tempParent = root.getParentFile();
    	while (tempParent.listFiles().length == 1) {
    		tempParent = tempParent.getParentFile();
		}
		Log.d(TAG, "tempParent:"+tempParent);
		
		//根目录退出
		if(tempParent.toString().endsWith("mnt")||tempParent==null){
			Log.d(TAG, "根目录退出 finish()");
			finish();
			return false;
		}
		
		root = tempParent;
		
		//更新数据源(更新list)
		updateData(0);
		return false;
    }


    //连按两次退出
	private boolean exitTwo() {
		if(currentStart==0){
			currentStart = System.currentTimeMillis();
			Toast.makeText(BrowserFileActivity2.this, getString(R.string.once_again), Toast.LENGTH_SHORT).show();
		}else{
			long end = System.currentTimeMillis();
			if(end - currentStart < 2000){
				currentStart = System.currentTimeMillis();
				ExitCode();
			}else{
				currentStart = System.currentTimeMillis();
				Toast.makeText(BrowserFileActivity2.this, getString(R.string.once_again), Toast.LENGTH_SHORT).show();
			}
		}
		return false;
	}
    
    //手机按键返回事件
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			//如果editCode打开的情况下
			Log.d("lvpfei", "onKeyDown openEdtCodeFlag:"+openEdtCodeFlag);
			if(openEdtCodeFlag){
				return exitTwo();
			}else{//如果是在目录的情况下
				//返回目录
				return btnBackEvent();
			}
		}
		return super.onKeyDown(keyCode, event);
	}
    
    private void saveEdtCodeProgress(){//保存edtCode浏览进度 
    	SharedPreferences share = getSharedPreferences(selecSaveFile, saveShareMode);
    	Editor editor = share.edit();
    	editor.putInt(selFile.getName(), edtCode.getSelectionStart());
    	editor.commit();
    }
    
    private static void setEdtCodeProgress(EditText edtCode){//设置上次edtCode浏览进度 
    	SharedPreferences share = context.getSharedPreferences(selecSaveFile, saveShareMode);
    	int codeSelecion = share.getInt(selFile.getName(), 0);
    	edtCode.setSelection(codeSelecion);
    }

    //打开文件
    private static void openFileInEdit(File selFile, int formatId){
    	Log.d(TAG, "openFileInEdit() selFile:"+selFile.getName()+" formatId:"+formatId);
    	InputStream is = null;
    	InputStreamReader isr = null;
    	BufferedReader reader = null;
    	try {
    		//文件输入字节流读入文件返回输入字节流
			is = new FileInputStream(selFile);
			//字符输入流读入字节流返回字符输入流
			if(formatId == 1){
				isr = new InputStreamReader(is, "utf-8");
			}else if(formatId == 2){
				isr = new InputStreamReader(is, "gbk");
			}
			//字符流放入缓冲流
			reader = new BufferedReader(isr);
			String line;
			sb = new StringBuilder("");
			//缓冲流内数据读入StringBuilder
			int lineNum = 0;
			while((line=reader.readLine())!=null){
				Log.d(TAG, "lineNum:"+lineNum);
				lineNum++;
				if(line.startsWith("| ")){
					String lineNewNum = null;
					if(line.length()>=9){
						lineNewNum = line.substring(0, 9);
					}else{
						lineNewNum = line;
					}
					int tempShu = lineNewNum.lastIndexOf("|");
					if(lineNewNum.length() >= tempShu + 2){
						line = line.substring(tempShu + 2);
					}
				}
				Log.d(TAG, "NO (line.startsWith(|) line:"+line);
				if(lineNum < 10){
					line = "00" + lineNum+" | "+line;//加上行数
				}else if(lineNum >= 10 && lineNum < 100){
					line = "0" + lineNum+" | "+line;//加上行数
				}else{
					line = lineNum+" | "+line;//加上行数
				}
				Log.d(TAG, line);
				sb.append("| " + line+"\n");
			}
			Log.d(TAG, "while done");
			linearList.setVisibility(View.GONE);
			linearCode.setVisibility(View.VISIBLE);
			int edtSize = preferences.getInt(EDIT_SIZE, 18);
			edtCode.setTextSize(edtSize);
			//StringBuilder中的数据放入edtCode（显示出来）
			edtCode.setText(sb);
			setEdtCodeProgress(edtCode);
			sizeStart = edtSize;
			String selFilePath = selFile.getAbsolutePath().toString().trim();

			saveEdtLastList(selFilePath);
			
			Log.d(TAG, "文件长度:"+sb.length());
			
			openEdtCodeFlag = true;
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(reader!=null){
					reader.close();
				}
				if(isr!=null){
					isr.close();
				}
				if(is!=null){
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    }
    
    //listView滑动事件
    class MenuTouch implements OnTouchListener{
    	
    	//记录滑动事件开始X坐标
    	private float startX = 0;
    	
		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			//滑动退出提示框  调用保存或退出
		  	if(arg1.getAction()==MotionEvent.ACTION_DOWN){
				startX = arg1.getX();
			}else if(arg1.getAction()==MotionEvent.ACTION_UP){
				float endX = arg1.getX();
				if(endX-startX>touchOpenLength){
					openOptionsMenu();
				}else if((endX-startX<-touchOpenLength)){
					dialogEdtLastList();
				}
			}
			return false;
		}
	}
    
    //edtCode滑动事件
    class MyOnTouch implements OnTouchListener{
    	private float startX = 0;
    	
		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			//滑动退出提示框  调用保存或退出
		  	if(arg1.getAction()==MotionEvent.ACTION_DOWN){
				startX = arg1.getX();
			}else if(arg1.getAction()==MotionEvent.ACTION_UP){
				float endX = arg1.getX();
				if((endX-startX>touchOpenLength)){
					dialogMenuSaveExit();
				}else if((endX-startX<-touchOpenLength)){
//					dialogEdit();
					dialogEdtLastList();
				}
			}
			return false;
		}
    	
    }
    
    //最近打开文件列表
    private void dialogEdtLastList(){
    	
    	final Dialog dialogEdtList = new Dialog(this);
    	dialogEdtList.setCancelable(true);
    	dialogEdtList.setTitle(R.string.edtLastOpenList);
    	dialogEdtList.setContentView(R.layout.dialogeditlastlist);
    	
    	ListView listEdtLastList = (ListView) dialogEdtList.findViewById(R.id.listEdtLastFile);
    	final String []edtLastList = (String[])getEdtLastList().toArray(new String[edtThisListNum=(getEdtLastList().size())]);

    	initEdtLastOpenList(dialogEdtList, listEdtLastList, edtLastList);
    	
    	dialogEdtList.show();
    }
    
    private void initEdtLastOpenList(final Dialog dialogEdtList ,
    		ListView listEdtLastList, final String []edtLastList){
    	
    	List<Map<String, Object>> listEdtLastData = 
    			new ArrayList<Map<String,Object>>();
    	
    	for (int i = 0; i < edtThisListNum; i++) {
			Map<String, Object> itemMap = new HashMap<String, Object>();
			itemMap.put("edtLastOpenPath", getEdtLastList().get(i));
			
			listEdtLastData.add(itemMap);
		}
    	
    	SimpleAdapter adapterEdtLast = new SimpleAdapter(this, listEdtLastData, 
    			R.layout.itemedtlast, new String[]{"edtLastOpenPath"}, 
    			new int[]{R.id.textEdtLastOpen});
    	listEdtLastList.setAdapter(adapterEdtLast);
    	
    	listEdtLastList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				File fileEdtLast = new File(edtLastList[arg2]);
				selFile = fileEdtLast;
				if(openEdtCodeFlag){
		    		ExitCode();
		    	}
				openFileInEdit(selFile, getCurConvtMode());
				dialogEdtList.dismiss();
				
			}
		});
    	
    	listEdtLastList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				File fileEdtLast = new File(edtLastList[arg2]);
				currentFiles = fileEdtLast.getParentFile().getParentFile().listFiles();
				listOnItemClickEvent(0, 1);
				dialogEdtList.dismiss();
				return true;
			}
		});
    	
    }
    
    private List<String> getEdtLastList(){
    	List<String> edtLastFileArray = new ArrayList<String>();
    	SharedPreferences share = getSharedPreferences(edtLastSaveFile, saveShareMode);
    	for (int i = 0; i < edtLastListNum; i++) {
    		String edtLastFileName = share.getString(edtLastListArray.get(i), "");
    		if(!"".equals(edtLastFileName)){
    			edtLastFileArray.add(edtLastFileName);
    		}
		}
    	return edtLastFileArray;
    }
    
    private static void saveEdtLastList(String lastedOpenFile){
    	if(TextUtils.isEmpty(lastedOpenFile)){
    		return;
    	}
    	
    	List<String> edtLastFileArray = new ArrayList<String>();
    	SharedPreferences share = context.getSharedPreferences(edtLastSaveFile, saveShareMode);
    	edtLastFileArray.add(lastedOpenFile);
    	for (int i = 0; i < edtLastListNum - 1; i++) {
    		String edtLastFileName = share.getString(edtLastListArray.get(i), "");
    		if(!lastedOpenFile.equals(edtLastFileName) && !"".equals(edtLastFileName)){
    			edtLastFileArray.add(edtLastFileName);
    		}
		}
    	edtThisListNum = edtLastFileArray.size();
    	Editor editor = share.edit();
    	editor.clear();
    	editor.commit();
    	for (int i = 0; i < edtThisListNum; i++) {
    		editor.putString(edtLastListArray.get(i), edtLastFileArray.get(i));
		}
    	editor.commit();
    }
    
  
    
  
    
    @Override
    public void openOptionsMenu() {
    	rootStartAddEditToNull();
    	super.openOptionsMenu();
    }
    
    //保存文件前要修改文件后缀 为txt 然后再改回来
    private File savePathFront(){
    	sFile = selFile.getAbsoluteFile().toString();
    	if(sFile.endsWith(".txt")){
    		return selFile;
    	}else if(sFile.endsWith(".java")){
    		String s = sFile.subSequence(0, sFile.length()-5).toString()+".txt";
    		File f = new File(s);
    		if(selFile.renameTo(f)){
    			return selFile;
    		}
    	}else if(sFile.endsWith(".xml")){
    		String s = sFile.subSequence(0, sFile.length()-4).toString()+".txt";
    		if(selFile.renameTo(new File(s))){
    			return selFile;
    		}
    	}
    	return null;
    }
    
    //保存文件后要删除创建的txt文件 并更新list
    private void savePathEnd(){
    	String s = null;
    	if(sFile.endsWith(".java")){
    		s = sFile.subSequence(0, sFile.length()-5).toString()+".txt";
    	}else if(sFile.endsWith(".xml")){
    		s = sFile.subSequence(0, sFile.length()-4).toString()+".txt";
    	}
    	else if(sFile.endsWith(".txt")){
    		updateData(0);
    		return;
    	}

		File f = new File(s);
    	f.delete();
 
    	
    	updateData(0);
    }
    
    //保存
    private void saveCode(){
    	saveEdtCodeProgress();
    	String code = edtCode.getText().toString();
    	FileOutputStream outStream = null;
    	OutputStreamWriter writer = null;
    	String s = null;
    	try {
    		
    		s = selFile.getAbsoluteFile().toString();
    		
    		Log.d(TAG, "save 路径 ：" + s);
    		
    			//修改后缀名为txt 否则无法保存
        		selFile = savePathFront();
			outStream = new FileOutputStream(selFile.getAbsoluteFile(),true);
			if(getCurConvtMode()==1){
				writer = new OutputStreamWriter(outStream,"utf-8");//gb2312
			}else if(getCurConvtMode()==2){
				writer = new OutputStreamWriter(outStream,"gbk");//gb2312
			}

//			File f = new File("c:/abc.txt");
//			FileWriter fw =  new FileWriter(f);
//			fw.write("");
//			fw.close();

			FileWriter fw = new FileWriter(selFile);
			fw.write("");
			fw.close();

//			selFile.createNewFile();

			writer.write(code);
			writer.flush();
			Toast.makeText(BrowserFileActivity2.this, R.string.saveSuccess, Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(BrowserFileActivity2.this, R.string.saveFail, Toast.LENGTH_SHORT).show();
		}finally{
			try {
				if(writer!=null){
					writer.close();//记得关闭
				}
				if(outStream!=null){
					outStream.close();//记得关闭
				}
				
				//修改回来 并更新list
				savePathEnd();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//记得关闭
			
		}
    	
    }
    
    //退出editCode
    private void ExitCode(){
    	Log.d(TAG, "ExitCode()");
    	saveEdtCodeProgress();
    	saveConvtMode(getCurConvtMode());
    	linearCode.setVisibility(View.GONE);
    	linearList.setVisibility(View.VISIBLE);
    	
    	openEdtCodeFlag = false;
    }

    
    //为list添加内容
    private void updateData(int arrayToBeNull){  
    	
    	Log.d(TAG, "root:"+root);
    	
//    	如果不为空，则清空，否则list内容会向下追加
//    	初次进入时为空 后进入返回操作时需要清空原list后加入新目录数据
    	if(dataOrder!=null && dataOrder.size()>0){
    		dataOrder.clear();
    	}
		if(root==null || !root.exists()){//无目录
			Toast.makeText(this, R.string.listIsNull, Toast.LENGTH_SHORT).show();
			return;
		}
		
		File[] files = null;
		//为当前root 根目录排序
		files  = getRootFileArray();//获得root目录下的文件集合//若edtSearch有内容，则为搜索后目录列表

		if(arrayToBeNull == 0){
			rootStartWithEditToNull();//设置edtSearch内容为空
		}
		
    	for (File file : files) {//遍历目录
    		//HashMap通过hashcode对其内容进行快速查找
    		//HashMap 非线程安全 TreeMap 非线程安全 
    		//每个item都是一个map集合
    		Map<String, Object> f = new HashMap<String,Object>();
    		
    		String fileDetailStr = "";
    		if(file.isDirectory()){//如果是文件夹
    			f.put("icon", R.drawable.folder);
    			File[] fileSonArray = file.listFiles();
    			
    			if(fileSonArray == null){
    				Log.e(TAG, "fileSonArray == null");
    				return;
    			}
    			
    			int fileDirNum = 0;
    			int fileNum = 0;
    			for (int i = 0; i < fileSonArray.length; i++) {
					if(fileSonArray[i].isDirectory()){
						fileDirNum++;
					}else{
						fileNum++;
					}
				}
    			fileDetailStr = fileDirNum + "个文件夹 " + fileNum + "个文件";
    		}else{//文件
    			f.put("icon", R.drawable.file);
    			int fileLength = (int)file.length()/1024;
    			if(fileLength >= 1024){
    				fileLength = fileLength/1024;
    				fileDetailStr = fileLength+" MB";
    			}else{
    				fileDetailStr = fileLength+" KB";
    			}
    		}
    		f.put("title", file.getName());
    		//file.lastModified() 文件最后修改时间
    		f.put("date", DateFormat.format("yyyy年MM月dd日 hh:mm:ss", file.lastModified()));
    		f.put("filedetail", fileDetailStr);
    		dataOrder.add(f);//添加进list
		}
    	
    	//app, src, java, res, main, layout
    	//.txt, .xml
    	
//    	//list 显示 排序 必须与数据排序一致
//    	comparatorList(null, dataOrder, 1);
        
        adapterList = new AdapterList(this, context, dataOrder, root);
    	
    	//绑定适配器
    	listView.setAdapter(adapterList);
    	
    	//显示路径
    	tv_dic.setText(root.getAbsolutePath());
    	
    	//通知更新
    	adapterList.notifyDataSetChanged();
    	
    }
    
    /*
     * 创建菜单
     * onCreateOptionsMenu
     */
    public boolean onCreateOptionsMenu(Menu menu) {  
    	
    	rootStartAddEditToNull();
    	//参数：'组别','item id','order id','string',
    	menu.add(1,1, 1, R.string.search).setIcon(R.drawable.search);
    	menu.add(1,2, 2, R.string.built_new_folder).setIcon(R.drawable.newfolder);
    	menu.add(1,3, 3, R.string.textSize).setIcon(R.drawable.wordsize);
    	menu.add(2,4, 4, R.string.mode).setIcon(R.drawable.setting);
    	menu.add(2,5, 5, R.string.built_new_text).setIcon(R.drawable.newfile);
    	menu.add(2,6, 6, R.string.exit).setIcon(R.drawable.exit);
    	return super.onCreateOptionsMenu(menu);
    }
    
    //滑动退出提示框  调用保存或退出
    private void dialogMenuSaveExit(){
    	Builder builder = new Builder(BrowserFileActivity2.this);
    	builder.setCancelable(true);
		builder.setTitle(R.string.isExitting);
		builder.setCancelable(true);
		builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				saveCode();
				ExitCode();
			}
		});
		
		builder.setNeutralButton(R.string.menu, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				openOptionsMenu();
			}
		});
		
		builder.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				ExitCode();
			}
		});
		
		builder.create().show();
    }
    
    /*
     * 菜单事件
     * onMenuItemSelected
     */
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	//获得item id
    	int itemid = item.getItemId();
    	switch(itemid){
    	case 1://搜索界面
    		Intent it = new Intent();
        	it.setClass(this, SearchFileActivity.class);
        	startActivity(it);
    		break;
    	case 2://新建文件夹
    		final EditText edtNewDir = new EditText(this);
    		Builder dialogNewDir = new Builder(this);
    		dialogNewDir.setTitle(R.string.input_folder_name);
    		dialogNewDir.setView(edtNewDir);
    		dialogNewDir.setPositiveButton(R.string.new_built, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					String dir = root.getAbsoluteFile().toString()+File.separator+edtNewDir.getText().toString();
					File fileDir = new File(dir);
					  if (!fileDir.exists()) {
						  fileDir.mkdirs();
					  }
					  updateData(0);//更新数据源
					  Toast.makeText(BrowserFileActivity2.this, R.string.built_success, Toast.LENGTH_SHORT).show();
				}
			});
    		dialogNewDir.create().show();
    		break;
    	case 3://字体大小
    		if(linearCode.getVisibility()==0){
    			dialogEdit();
    		}else{
    			Toast.makeText(this, R.string.not_open_text, Toast.LENGTH_SHORT).show();
    		}
    		break;
    	
    	case 4:
    		Builder alert = new Builder(BrowserFileActivity2.this);
    		alert.setTitle(R.string.see_mode);
    		alert.setPositiveButton(R.string.mode_day, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					edtCode.setBackgroundColor(Color.WHITE);
					edtCode.setTextColor(Color.BLACK);
					linearList.setBackgroundColor(Color.WHITE);
					tvItem.setTextColor(Color.BLACK);
				}
			});
    		alert.setNegativeButton(R.string.mode_night, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					edtCode.setBackgroundColor(Color.BLACK);
					edtCode.setTextColor(Color.rgb(255, 128, 0));
//					linearList.setBackgroundColor(Color.BLACK);
					tvItem.setTextColor(Color.YELLOW);
				}
			});
    		alert.create().show();
    		break;
    		
    	case 5:
    		final EditText edtNewFile = new EditText(this);
    		Builder dialogNewFile = new Builder(this);
    		dialogNewFile.setTitle(R.string.input_new_text);
    		dialogNewFile.setView(edtNewFile);
    		dialogNewFile.setPositiveButton(R.string.new_built, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					String dir = root.getAbsoluteFile().toString()+File.separator+edtNewFile.getText().toString()+".txt";
					File fileDir = new File(dir);
		
					try {
						fileDir.createNewFile();
					} catch (IOException e) {
						Toast.makeText(BrowserFileActivity2.this, R.string.built_new_text_fail, Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}
					
					  updateData(0);
					  Toast.makeText(BrowserFileActivity2.this, R.string.built_new_text_success, Toast.LENGTH_SHORT).show();
				}
			});
    		dialogNewFile.create().show();
    		break;
    		
    	case 6:
    		finish();
    		break;
    	}
    	
    	
    	
    	return super.onMenuItemSelected(featureId, item);
    }
    
    //editText 进度跳转
    private void dialogEditProgress(){
    	if(sb==null){//若未打开文本 则返回 sb为null
    		Toast.makeText(this, R.string.not_open_text, Toast.LENGTH_SHORT).show();
    		return;
    	}
    	int lengthStr = edtCode.length();
    	
	    final Dialog builder = new Dialog(this);
	    builder.setTitle(R.string.text_jumpto);
	    builder.setContentView(R.layout.dialogeditprogress);
	    final SeekBar seekBar = (SeekBar) builder.findViewById(R.id.seekBar_progress);
	    final int percentage = lengthStr/seekBar.getMax();
	    Log.d(TAG, "lengthStr:"+lengthStr+"  seekBar.getMax:"+seekBar.getMax());
	    Log.d(TAG, "percentage:"+percentage);
	    Button btnSub = (Button) builder.findViewById(R.id.buttonSub_prog);
		Button btnAdd = (Button) builder.findViewById(R.id.buttonAdd_prog);
		Button btnEditYes = (Button) builder.findViewById(R.id.btnEditYes_Prog);
		seekBar.setProgress(edtCode.getSelectionStart()/percentage);
		Log.d(TAG, "edtCode.getSelectionStart():"+edtCode.getSelectionStart());
	    Log.d(TAG, "::"+edtCode.getSelectionStart()/percentage);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
			}
			
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				Log.d(TAG, "文件跳转进度至:"+" getProgress:"+arg0.getProgress()+" percentage:"+percentage);
				edtCode.setSelection(arg0.getProgress()*percentage);
			}
		});
		btnSub.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(seekBar.getProgress()>=1){
					seekBar.setProgress(seekBar.getProgress()-1);
				}
			}
		});
		btnAdd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(seekBar.getProgress()<seekBar.getMax()-1){
					seekBar.setProgress(seekBar.getProgress()+1);
				}
			}
		});
		btnEditYes.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				builder.dismiss();
			}
		});
		builder.show();
    }
    
    //编辑字体dialog
    private void dialogEdit(){   
    	
    	final Dialog builder = new Dialog(this);
    	builder.setCancelable(true);
    	builder.setTitle(R.string.textSize);
    	builder.setContentView(R.layout.dialogedit);
    	
    	final SeekBar seekBar = (SeekBar) builder.findViewById(R.id.seekBar1);
    	Button btnSub = (Button) builder.findViewById(R.id.buttonSub);
    	Button btnAdd = (Button) builder.findViewById(R.id.buttonAdd);
    	Button btnEditYes = (Button) builder.findViewById(R.id.btnEditYes);
    	
    	seekBar.setProgress(sizeStart);
    	
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
			}

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				edtCode.setTextSize(seekBar.getProgress());
				
				editor.putInt(EDIT_SIZE, seekBar.getProgress());
				editor.commit();
			}
		});
        
        btnAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				seekBar.setProgress(seekBar.getProgress() + 1);

			}
		});
        
        btnSub.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				seekBar.setProgress(seekBar.getProgress() - 1);

			}
		});
        
        btnEditYes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				builder.dismiss();
			}
		});

    	builder.show();
    }

    
    //list长按事件
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		
		//取得文件
		File[] fileItems = getRootFileArray();
		final File fileItem = fileItems[arg2];
		
		//提示操作框
		final Dialog dialogMenu = new Dialog(this);
		dialogMenu.setContentView(R.layout.dialogfile);
		dialogMenu.setCanceledOnTouchOutside(true);
		dialogMenu.setTitle(R.string.document_function);
		
		Button btnParent = (Button) dialogMenu.findViewById(R.id.btn_parent);
		final Button btnChild = (Button) dialogMenu.findViewById(R.id.btn_child);
		Button btnRename = (Button) dialogMenu.findViewById(R.id.btn_reName);
		Button btnDel = (Button) dialogMenu.findViewById(R.id.btn_del);
		
		//移动至父文件夹
		btnParent.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				dialogMenu.dismiss();
				if(fileItem.isFile()){
					
					copyFile(fileItem.getAbsoluteFile().toString(), root.getParentFile().getAbsoluteFile().toString()+File.separator+fileItem.getName());
					//先删除再更新list
					fileItem.delete();
					updateData(0);
					 Toast.makeText(BrowserFileActivity2.this, R.string.move_success, Toast.LENGTH_SHORT).show();
				}else{
					copyFolder(fileItem.getAbsoluteFile().toString(), root.getParentFile().getAbsoluteFile().toString()+File.separator+fileItem.getName());
					//先删除再更新list
					delFirs(fileItem);
					updateData(0);
					 Toast.makeText(BrowserFileActivity2.this, R.string.move_success, Toast.LENGTH_SHORT).show();
				}
			}
		});

		
		//移动至子文件夹
		btnChild.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				dialogMenu.dismiss();
				final EditText edtNewName = new EditText(BrowserFileActivity2.this);
				Builder dialogChild = new Builder(BrowserFileActivity2.this);
				dialogChild.setTitle(R.string.moved_document_name);
				dialogChild.setView(edtNewName);
				dialogChild.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						String newName = edtNewName.getText().toString();
						File[] fileListToChild = getRootFileArray();
						for(int i=0;i<fileListToChild.length; i++){
							if(fileListToChild[i].getName().equals(newName)){
								if(fileItem.isFile()){
									
									copyFile(fileItem.getAbsoluteFile().toString(), root.getAbsoluteFile().toString()+File.separator+newName+File.separator+fileItem.getName());
									
									fileItem.delete();
									updateData(0);
									Toast.makeText(BrowserFileActivity2.this, "文件移动成功", Toast.LENGTH_SHORT).show();
									
								}else{
									copyFolder(fileItem.getAbsoluteFile().toString(), root.getAbsoluteFile().toString()+File.separator+newName+File.separator+fileItem.getName());
									//先删除再更新list
									delFirs(fileItem);
									updateData(0);
									
									Toast.makeText(BrowserFileActivity2.this, "文件夹移动成功", Toast.LENGTH_SHORT).show();
									
									
								}
							}
						}
					}
				});
				
				dialogChild.setNegativeButton(R.string.move_success, null);
				dialogChild.create().show();

			}
		});
		
		//重命名
		btnRename.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				final EditText edtRename = new EditText(BrowserFileActivity2.this);
				
				dialogMenu.dismiss();
				Builder dialogRename = new Builder(BrowserFileActivity2.this);
				dialogRename.setTitle(R.string.input_new_name);
				dialogRename.setView(edtRename);
				dialogRename.setPositiveButton(R.string.change, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						String parent = root.getAbsoluteFile().toString();
						String edtRenameStr = edtRename.getText().toString();//获取要改为的名字
						String fileItemNameStr = fileItem.getName();//获取待改文件的名字
						File fileReName = null;
						if(fileItemNameStr.contains(".")){
							int indexStrFileNamePoint = fileItemNameStr.indexOf(".");
							String strFileNameDr = fileItemNameStr.substring(indexStrFileNamePoint);
							fileReName = new File(parent+File.separator+edtRenameStr + "" + strFileNameDr);
						}else{
							fileReName = new File(parent+File.separator+edtRenameStr);
						}
						fileItem.renameTo(fileReName);
						updateData(0);
						Toast.makeText(BrowserFileActivity2.this, R.string.change_success, Toast.LENGTH_SHORT).show();
					}
				});
				dialogRename.setNegativeButton(R.string.cancel, null);
				dialogRename.create().show();
				
			}
		});
		
		
		//删除文件夹
		btnDel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				dialogMenu.dismiss();
				Builder dialogDel = new Builder(BrowserFileActivity2.this);
				dialogDel.setTitle(R.string.isSureDelete);
				
				dialogDel.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						if(fileItem.isFile()){
							fileItem.delete();
							updateData(0);
							Toast.makeText(BrowserFileActivity2.this, R.string.delete_success, Toast.LENGTH_SHORT).show();
						}else{
							//先删除再更新list
							delFirs(fileItem);
							updateData(0);
							Toast.makeText(BrowserFileActivity2.this, R.string.delete_success, Toast.LENGTH_SHORT).show();
						}
						
					}
				});
				dialogDel.setNegativeButton(R.string.cancel, null);
				dialogDel.create().show();
				
			}
		});
	
		dialogMenu.show();

		//更新适配器
		
		return true;
	}
	
	//删除文件
	private void delFirs(File fileItem){
		File[] firs = fileItem.listFiles();
		for (File file : firs) {
			if(file.isFile()){
				file.delete();
			}else{
				delFirs(file);
			}
		}
		
		fileItem.delete();
	}
	
	
    ///////////////////////复制文件//////////////////////////////
    /** 
     * 复制单个文件 
     * @param oldPath String 原文件路径 如：c:/fqf.txt 
     * @param newPath String 复制后路径 如：f:/fqf.txt 
     * @return boolean 
     */
   public boolean copyFile(String oldPath, String newPath) { 
       boolean isok = true;
       try { 
    	   
    	   int bytesum = 0; 
           int byteread = 0; 
           File oldfile = new File(oldPath); 
           if (oldfile.exists()) { //文件存在时 
               InputStream inStream = new FileInputStream(oldPath); //读入原文件
               FileOutputStream fs = new FileOutputStream(newPath); 
               byte[] buffer = new byte[1024]; 
               int length; 
               while ( (byteread = inStream.read(buffer)) != -1) { 
                   bytesum += byteread; //字节数 文件大小 
                 
                   fs.write(buffer, 0, byteread); 
               } 
               fs.flush(); 
               fs.close(); 
               inStream.close(); 
           }
           else
           {
            isok = false;
           }
       } 
       catch (Exception e) { 
      
//           e.printStackTrace(); 

           isok = false;
       } 
       
       return isok;
 
   } 
 
   /** 
     * 复制整个文件夹内容 
     * @param oldPath String 原文件路径 如：c:/fqf 
     * @param newPath String 复制后路径 如：f:/fqf/ff 
     * @return boolean 
     */
   public boolean copyFolder(String oldPath, String newPath) { 
       boolean isok = true;
       try { 
           (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹 
           File a=new File(oldPath); 
           String[] file=a.list(); 
           File temp=null; 
           for (int i = 0; i < file.length; i++) { 
               if(oldPath.endsWith(File.separator)){ 
                   temp=new File(oldPath+file[i]); 
               } 
               else
               { 
                   temp=new File(oldPath+File.separator+file[i]); 
               } 
 
               if(temp.isFile()){ 
                   FileInputStream input = new FileInputStream(temp); 
                   FileOutputStream output = new FileOutputStream(newPath + "/" + 
                           (temp.getName()).toString()); 
                   int len = input.available(); 
                   byte[] b = new byte[len]; 
                  
                   while ( (len = input.read(b)) != -1) { 
                       output.write(b, 0, len); 
                   } 
                   output.flush(); 
                   output.close(); 
                   input.close(); 
               } 
               if(temp.isDirectory()){//如果是子文件夹 
                   copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i]); 
               } 
           } 
       } 
       catch (Exception e) { 
            isok = false;
       } 
       return isok;
   }

}