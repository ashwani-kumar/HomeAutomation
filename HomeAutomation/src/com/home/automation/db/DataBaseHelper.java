package com.home.automation.db;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.home.automation.SlidingMenuUtility.Control_Type;
import com.home.automation.data.AC;
import com.home.automation.data.AppllianceCntrl;
import com.home.automation.data.Controls;
import com.home.automation.data.DummyContent;
import com.home.automation.data.Fan;
import com.home.automation.data.HomeArea;
import com.home.automation.data.Lights;
import com.home.automation.data.RGBLight;
import com.home.automation.data.SetTopBox;
import com.home.automation.data.TV;

public class DataBaseHelper extends SQLiteOpenHelper{

	//The Android's default system path of your application database.
	private static String DB_PATH = "/data/data/com.home.automation/databases/";

	private static String DB_NAME = "automation_db";

	private SQLiteDatabase myDataBase; 

	private final Context myContext;


	/**
	 * Constructor
	 * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
	 * @param context
	 */
	public DataBaseHelper(Context context) {

		super(context, DB_NAME, null, 1);
		this.myContext = context;
	}	

	 private static DataBaseHelper instance = null;
	  
	   public static DataBaseHelper getInstance( Context cxt) {
	      if(instance == null) {
	         instance = new DataBaseHelper(cxt);
	      }
	      return instance;
	   }
	
	
	
	/**
	 * Creates a empty database on the system and rewrites it with your own database.
	 * */
	public void createDataBase() throws IOException{

		boolean dbExist = checkDataBase();

		if(dbExist){
			//do nothing - database already exist
		}else{

			//By calling this method and empty database will be created into the default system path
			//of your application so we are gonna be able to overwrite that database with our database.
			this.getReadableDatabase();

			try {

				copyDataBase();

			} catch (IOException e) {

				throw new Error("Error copying database");

			}
		}

	}

	/**
	 * Check if the database already exist to avoid re-copying the file each time you open the application.
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase(){

		SQLiteDatabase checkDB = null;

		try{
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

		}catch(SQLiteException e){

			//database does't exist yet.

		}

		if(checkDB != null){

			checkDB.close();

		}

		return checkDB != null ? true : false;
	}

	/**
	 * Copies your database from your local assets-folder to the just created empty database in the
	 * system folder, from where it can be accessed and handled.
	 * This is done by transfering bytestream.
	 * */
	private void copyDataBase() throws IOException{

		//Open your local db as the input stream
		InputStream myInput = myContext.getAssets().open(DB_NAME);

		// Path to the just created empty db
		String outFileName = DB_PATH + DB_NAME;

		//Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);

		//transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer))>0){
			myOutput.write(buffer, 0, length);
		}

		//Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();

	}

	public void openDataBase() throws SQLException{

		//Open the database
		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

	}


	public  List<HomeArea> extractHomeArea() throws SQLException{

		//String TABLE_HOME = "HOME_MASTER";	
		List<HomeArea> homeAreaList = new ArrayList<HomeArea>();
		// Table Columns names
        final String ID = "id";
		final String VALUE = "VALUE";
		final String IS_ACTIVE= "IS_ACTIVE";
		final String NAME= "NAME";

		//Open the database
		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);

		String selectQuery = "SELECT HOME_UNITS._id AS id, HOME_UNITS.NAME AS NAME,HOME_MASTER.VALUE AS VALUE,HOME_UNITS.IS_ACTIVE AS IS_ACTIVE FROM HOME_MASTER  INNER JOIN HOME_UNITS ON HOME_UNITS.UNIT_TYPE_ID = HOME_MASTER._id";
		Cursor cursor = myDataBase.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				HomeArea area = new HomeArea();
				area.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ID))));
				area.setAreaName(cursor.getString(cursor.getColumnIndex(NAME)));
				area.setAreaType(cursor.getString(cursor.getColumnIndex(VALUE)));
				area.setIS_ACTIVE(Boolean.valueOf(cursor.getString(cursor.getColumnIndex(IS_ACTIVE))));
				homeAreaList.add(area);
			} while (cursor.moveToNext());
		}

		close();
		return homeAreaList;
	}
	public  Map<String, AppllianceCntrl> extractControlStrings() throws SQLException{
		String myPath = DB_PATH + DB_NAME;
		//DummyContent dummycontent = DummyContent.getInstance();
		myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
		//SELECT APPLIANCES.UID,APPLIANCES.addr,APPLIANCES.Contricom,APPLIANCES.Actcom,APPLIANCES.Dadd,APPLIANCES.Cmnddata FROM APPLIANCES
		String selectQuery = "SELECT APPLIANCES.NAME, APPLIANCES.UID,APPLIANCES.addr,APPLIANCES.Contricom,APPLIANCES.Actcom,APPLIANCES.Dadd,APPLIANCES.Cmnddata FROM APPLIANCES" ;
		Cursor cursor = myDataBase.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				AppllianceCntrl appliancectrl = new AppllianceCntrl();
				appliancectrl.setCntrlName(cursor.getString(cursor.getColumnIndex("NAME")));
				Log.d("Home_automation", "name"+cursor.getString(cursor.getColumnIndex("NAME")));
				appliancectrl.setUID(cursor.getString(cursor.getColumnIndex("UID")));
				Log.d("Home_automation", "UID"+cursor.getString(cursor.getColumnIndex("UID")));
				appliancectrl.setAddr(cursor.getString(cursor.getColumnIndex("Addr")));
				appliancectrl.setActcom(cursor.getString(cursor.getColumnIndex("Actcom")));
				appliancectrl.setCmnddata(cursor.getString(cursor.getColumnIndex("Cmnddata")));
				appliancectrl.setContricom(cursor.getString(cursor.getColumnIndex("Contricom")));
				appliancectrl.setDadd(cursor.getString(cursor.getColumnIndex("Dadd")));
				DummyContent.getCntrlStrings().put(cursor.getString(cursor.getColumnIndex("NAME")),appliancectrl);
			} while (cursor.moveToNext());
		}
		
		return DummyContent.getCntrlStrings();
		
	}
	
	


	public  Controls extractControls() throws SQLException{

		final String IS_ACTIVE= "ISACTIVE";
		int ACindex=0;
		int TVindex=0;
		int LIGHTindex=0;
		int FANindex=0;
		int STBindex=0;
		int RGBindex=0;
		Controls mycontrols = new Controls();
		//Open the database
		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
		//SELECT APPLIANCES.NAME AS NAME, APPLIANCES_MASTER.VALUE AS VALUE,HOME_UNITS.NAME AS HOME_AREA, HOME_MASTER.VALUE AS HOME_AREA_TYPE, APPLIANCES.iS_ACTIVE as ISACTIVE  FROM APPLIANCES inner join APPLIANCES_MASTER ON APPLIANCES.APPLIANCE_TYPE_ID = APPLIANCES_MASTER._id inner join HOME_UNITS on APPLIANCES.UNIT_ID = HOME_UNITS._id inner join HOME_MASTER on HOME_UNITS.UNIT_TYPE_ID=HOME_MASTER._id
		String selectQuery = "SELECT APPLIANCES.NAME AS NAME, APPLIANCES_MASTER.VALUE AS VALUE,HOME_UNITS.NAME AS HOME_AREA, HOME_MASTER.VALUE AS HOME_AREA_TYPE, APPLIANCES.iS_ACTIVE as ISACTIVE  FROM APPLIANCES inner join APPLIANCES_MASTER ON APPLIANCES.APPLIANCE_TYPE_ID = APPLIANCES_MASTER._id inner join HOME_UNITS on APPLIANCES.UNIT_ID = HOME_UNITS._id inner join HOME_MASTER on HOME_UNITS.UNIT_TYPE_ID=HOME_MASTER._id" ;
		Cursor cursor = myDataBase.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				if(cursor.getString(cursor.getColumnIndex("VALUE")).equalsIgnoreCase("FAN")) {
					Fan fanObject = new Fan();
					fanObject.setName(cursor.getString(cursor.getColumnIndex("NAME")));
					fanObject.setState(Integer.valueOf(cursor.getString(cursor.getColumnIndex(IS_ACTIVE))));
					fanObject.setAreaName(cursor.getString(cursor.getColumnIndex("HOME_AREA")));
					fanObject.setAreaType(cursor.getString(cursor.getColumnIndex("HOME_AREA_TYPE")));
					mycontrols.setmFan(fanObject, FANindex);
					FANindex++;
				}
					if(cursor.getString(cursor.getColumnIndex("VALUE")).equalsIgnoreCase("AC")) {
						AC acObject = new AC();
						acObject.setName(cursor.getString(cursor.getColumnIndex("NAME")));
						acObject.setState(Integer.valueOf(cursor.getString(cursor.getColumnIndex(IS_ACTIVE))));
						acObject.setAreaName(cursor.getString(cursor.getColumnIndex("HOME_AREA")));
						acObject.setAreaType(cursor.getString(cursor.getColumnIndex("HOME_AREA_TYPE")));
						mycontrols.setmAC(acObject, ACindex);
						ACindex++;
						}

						if(cursor.getString(cursor.getColumnIndex("VALUE")).equalsIgnoreCase("Light")){
							Lights lightObject = new Lights();
							lightObject.setName(cursor.getString(cursor.getColumnIndex("NAME")));
							lightObject.setState(Integer.valueOf(cursor.getString(cursor.getColumnIndex(IS_ACTIVE))));
							lightObject.setAreaName(cursor.getString(cursor.getColumnIndex("HOME_AREA")));
							lightObject.setAreaType(cursor.getString(cursor.getColumnIndex("HOME_AREA_TYPE")));
							mycontrols.setmLights(lightObject, LIGHTindex);
							LIGHTindex++;
						}
						if(cursor.getString(cursor.getColumnIndex("VALUE")).equalsIgnoreCase("RGB Light")){
							RGBLight RGBObject = new RGBLight();
							RGBObject.setName(cursor.getString(cursor.getColumnIndex("NAME")));
							RGBObject.setState(Integer.valueOf(cursor.getString(cursor.getColumnIndex(IS_ACTIVE))));
							RGBObject.setAreaName(cursor.getString(cursor.getColumnIndex("HOME_AREA")));
							RGBObject.setAreaType(cursor.getString(cursor.getColumnIndex("HOME_AREA_TYPE")));
							mycontrols.setmRGB(RGBObject, RGBindex);
							RGBindex++;
						}
						if(cursor.getString(cursor.getColumnIndex("VALUE")).equalsIgnoreCase("TV")){
							TV tvObject = new TV();
							tvObject.setName(cursor.getString(cursor.getColumnIndex("NAME")));
							tvObject.setState(Integer.valueOf(cursor.getString(cursor.getColumnIndex(IS_ACTIVE))));
							tvObject.setAreaName(cursor.getString(cursor.getColumnIndex("HOME_AREA")));
							tvObject.setAreaType(cursor.getString(cursor.getColumnIndex("HOME_AREA_TYPE")));
							mycontrols.setmTv(tvObject, TVindex);
							TVindex++;
						}
						if(cursor.getString(cursor.getColumnIndex("VALUE")).equalsIgnoreCase("SetTop Box")){
							SetTopBox STBObject = new SetTopBox();
							STBObject.setName(cursor.getString(cursor.getColumnIndex("NAME")));
							STBObject.setState(Integer.valueOf(cursor.getString(cursor.getColumnIndex(IS_ACTIVE))));
							STBObject.setAreaName(cursor.getString(cursor.getColumnIndex("HOME_AREA")));
							STBObject.setAreaType(cursor.getString(cursor.getColumnIndex("HOME_AREA_TYPE")));
							mycontrols.setmSTB(STBObject, STBindex);
							STBindex++;
						}
			} while (cursor.moveToNext());
		}
		close();
		return mycontrols;
	}




	@Override
	public synchronized void close() {

		if(myDataBase != null)
			myDataBase.close();

		super.close();

	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}



	public int updateDBControlState(boolean isChecked, String controlName,
			String areaName) {
		ContentValues contentValues = new ContentValues();
		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
		int switchVal=(isChecked)?1:0;
		contentValues.put("IS_ACTIVE", switchVal);
		int val = myDataBase.update("APPLIANCES", contentValues, "NAME= \'"+controlName+"\' AND UNIT_ID in (select hOME_UNITS._id from hOME_UNITS where NAME =\'"+areaName+"\') ", null);
		Log.d("Home.automation", "rows updated is"+val);
		String upadateQuery = "UPDATE APPLIANCES SET IS_ACTIVE="+switchVal+" WHERE NAME= \'"+controlName+"\' AND UNIT_ID in (select hOME_UNITS._id from hOME_UNITS where NAME =\'"+areaName+"\') ";
		Log.d("Home.automation", "query is"+upadateQuery);
		close();
		return val;
	}



	public String getControlString(Control_Type contolChildType, String controlName,boolean isChecked,String textFromUI) {
		if(DummyContent.cntrlStrings.containsKey(controlName)){
		AppllianceCntrl contrlobject = DummyContent.cntrlStrings.get(controlName);
		String initialString= "$"+contrlobject.getUID().trim()+contrlobject.getAddr().trim()+contrlobject.getContricom().trim()+contrlobject.getActcom().trim();
		String restString=replacer(contolChildType.name(),contrlobject.getDadd(),contrlobject.getCmnddata(),isChecked,textFromUI);
		String cntrlstr=initialString+restString+"?";
		return cntrlstr;
		}else{
			return "$?";
		}
		
	}



	private String replacer(String controlName, String dadd, String cmnddata,
			boolean isChecked, String textFromUI) {
		if(controlName==Control_Type.Fan.name()){
			if(isChecked){
				cmnddata="1";
			}else{
				cmnddata="0";
			}
		}else if(controlName==Control_Type.Light.name()){
			String[] splitdadd = dadd.split("(?!^)");
			if(isChecked){
				splitdadd[1]="1";
			}else{
				splitdadd[1]="0";
			}
			dadd="";
			for (String string : splitdadd) {
				dadd=dadd+string;	
			}
		}else if(controlName==Control_Type.TV.name()){
			if(isChecked && (textFromUI.equalsIgnoreCase("On") || textFromUI.equalsIgnoreCase("Off"))){
				cmnddata="PN";
			}
			if(isChecked && (textFromUI.equalsIgnoreCase("Mute") || textFromUI.equalsIgnoreCase("Vol"))){
				cmnddata="MU";
			}
		}else if(controlName==Control_Type.AC.name()){
			if(isChecked){
				dadd="PN";
			}else{
				dadd="PF";
			}
		}
		
		return dadd+ cmnddata;
	}



	public String getControlString(Control_Type controlChildType, String controlName, String textFromUI) {
		if(DummyContent.cntrlStrings.containsKey(controlName)){
		AppllianceCntrl contrlobject = DummyContent.cntrlStrings.get(controlName);
		 String initialString= "$"+contrlobject.getUID().trim()+contrlobject.getAddr().trim()+contrlobject.getContricom().trim()+contrlobject.getActcom().trim();
		String restString=replacer(controlChildType,controlName,contrlobject.getDadd(),contrlobject.getCmnddata(),textFromUI);
		String cntrlstr=initialString+restString+"?";
		return cntrlstr;
		}else{
			return "$?";
		}
	}



	private String replacer(Control_Type controlChildType, String controlName, String dadd, String cmnddata,
			CharSequence textFromUI) {
		int acInt;

		if(controlChildType==Control_Type.Fan){
			cmnddata=textFromUI.toString();
		}else if(controlChildType==Control_Type.Light){
			
		}else if(controlChildType==Control_Type.TV){
			cmnddata=textFromUI.toString();
		}else if(controlChildType==Control_Type.AC){
			dadd= textFromUI.toString();
			try {
				 acInt=Integer.parseInt(textFromUI.toString());
			} catch (NumberFormatException e) {
				dadd="MO";
				if(textFromUI.toString().contains("HOT")){
					cmnddata="1";
				}else if(textFromUI.toString().contains("cold")){
					cmnddata="2";
				}
				return dadd+ cmnddata;
			}
			if(acInt<16){
				cmnddata=textFromUI.toString();
				dadd="FN";
			}
		}
		
		return dadd+ cmnddata;
	}




	// Add your public helper methods to access and get content from the database.
	// You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
	// to you to create adapters for your views.

}