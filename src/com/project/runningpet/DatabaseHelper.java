package com.project.runningpet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	String TAG = "DatabaseHelper";

	//0th col is ALWAYS _id
	
	private static final String DATABASE_NAME = "game_db";
	
	//Stores pet information and status
	static final String TBL_PET_INFO = "petInfo";
	static final String PET_INFO_KEY = "_id";
	static final String PET_INFO_NAME = "name";
	static final String PET_INFO_HEALTH = "health";
	static final String PET_INFO_CONDITION_KEY = "condition_id";
	static final String PET_INFO_GOLD = "gold";
	static final String PET_INFO_DATE_LAST_CHECKED = "dateLastChecked"; 
	static final int PET_INFO_KEY_INDEX = 0;
	static final int PET_INFO_NAME_INDEX = 1;
	static final int PET_INFO_HEALTH_INDEX = 2;
	static final int PET_INFO_CONDITION_KEY_INDEX = 3;
	static final int PET_INFO_GOLD_INDEX = 4;
	static final int PET_INFO_DATE_LAST_CHECKED_INDEX = 5;
	
	//Stores the items the player has bought for the pet
	static final String TBL_PET_FOOD = "petFood";
	static final String PET_FOOD_KEY = "_id";
	static final String PET_FOOD_PET_KEY = "pet_id";
	static final String PET_FOOD_FOOD_KEY = "food_id";
	static final String PET_FOOD_DATE_BOUGHT = "dateBought";
	static final String PET_FOOD_DATE_LAST_CHECKED = "dateLastChecked";
	static final int PET_FOOD_KEY_INDEX = 0;
	static final int PET_FOOD_PET_KEY_INDEX = 1;
	static final int PET_FOOD_FOOD_KEY_INDEX = 2;
	static final int PET_FOOD_DATE_BOUGHT_INDEX = 3;
	static final int PET_FOOD_DATE_LAST_CHECKED_INDEX = 4;
	
	
	//Stores the parameters for each type of run
	static final String TBL_RUN_PARAM = "runParam";
	static final String RUN_PARAM_KEY = "_id";
	static final String RUN_PARAM_DIFFICULTY = "difficulty";
	static final String RUN_PARAM_HP_XCOEFF = "hp_xCoeff";
	static final String RUN_PARAM_GOLD_XCOEFF = "gold_xCoeff";
	static final int RUN_PARAM_KEY_INDEX = 0;
	static final int RUN_PARAM_DIFFICULTY_INDEX = 1;
	static final int RUN_PARAM_HP_XCOEFF_INDEX = 2;	
	static final int RUN_PARAM_GOLD_XCOEFF_INDEX = 3;
	
	//Stores the list of food items, hours of effect, cost
	static final String TBL_FOOD = "food"; 
	static final String FOOD_KEY = "_id";
	static final String FOOD_NAME = "name";
	static final String FOOD_HOURS = "hours";
	static final String FOOD_COST = "cost";
	static final int FOOD_KEY_INDEX = 0;
	static final int FOOD_NAME_INDEX = 1;
	static final int FOOD_HOURS_INDEX = 2;
	static final int FOOD_COST_INDEX = 3;
	
	//Stores the list of pet conditions
	static final String TBL_CONDITION = "condition";
	static final String CONDITION_KEY = "_id";
	static final String CONDITION_NAME = "name";
	static final String CONDITION_HEALTH_EFFECT = "healthEffect";
	static final int CONDITION_KEY_INDEX = 0;
	static final int CONDITION_NAME_INDEX = 1;
	static final int CONDITION_HEALTH_EFFECT_INDEX = 2;
			
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {		
		String sql = "CREATE TABLE " + TBL_PET_INFO + " "
				+ "(" + PET_INFO_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ PET_INFO_NAME + " TEXT, "
				+ PET_INFO_HEALTH + " INTEGER, "
				+ PET_INFO_CONDITION_KEY + " INTEGER, "
				+ PET_INFO_GOLD + " INTEGER, " 
				+ PET_INFO_DATE_LAST_CHECKED + " STRING)";

		db.execSQL(sql);
		
		sql = "CREATE TABLE " + TBL_PET_FOOD + " "
				+ "(" + PET_FOOD_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ PET_FOOD_PET_KEY + " INTEGER, "
				+ PET_FOOD_FOOD_KEY + " INTEGER, " 
				+ PET_FOOD_DATE_BOUGHT + " STRING, " 
				+ PET_FOOD_DATE_LAST_CHECKED + " STRING)";
		db.execSQL(sql);
		
		sql = "CREATE TABLE " + TBL_RUN_PARAM + " "
				+ "(" + RUN_PARAM_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ RUN_PARAM_DIFFICULTY + " TEXT, "
				+ RUN_PARAM_HP_XCOEFF + " REAL, "
				+ RUN_PARAM_GOLD_XCOEFF + " REAL)";
		db.execSQL(sql);
		
		sql = "CREATE TABLE " + TBL_FOOD + " "
				+ "(" + FOOD_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ FOOD_NAME + " TEXT, "
				+ FOOD_HOURS + " REAL, "
				+ FOOD_COST + " INTEGER)";
		db.execSQL(sql);
		
		sql = "CREATE TABLE " + TBL_CONDITION + " "
				+ "(" + CONDITION_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ CONDITION_NAME + " TEXT, "
				+ CONDITION_HEALTH_EFFECT + " REAL)";
		db.execSQL(sql);
			
		populateDatabase(db);		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		android.util.Log.w("Constants",
				"Upgrading database, which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " 
				+ TBL_PET_INFO + " , " + TBL_PET_FOOD + " , " + TBL_RUN_PARAM + " , " + TBL_FOOD + " , " + TBL_CONDITION);
		onCreate(db);
	}
	
	private void populateDatabase(SQLiteDatabase db) {
		addFood(db, "Apple", 4, 1);
		addFood(db, "Cookies", 12, 1);
		addFood(db, "Salad", 8, 2);
		addFood(db, "Chicken", 15, 3);
		addFood(db, "Pizza", 15, 2);
		addFood(db, "Bread", 12, 3);
		
		addRunParam(db, "Easy", 0.6f, 0.15f);
		addRunParam(db, "Medium", 0.5f, 0.25f);
		addRunParam(db, "Hard", 0.4f, 0.35f);
			
		addCondition(db, "Healthy", -10);
		addCondition(db, "Hypertension", -15);
		addCondition(db, "Diabetes", -15);
		addCondition(db, "Obesity", -15);
	}
	
	public void getTableColNames(String table) {
		Cursor ti = getReadableDatabase().rawQuery(String.format("PRAGMA table_info(%s)", table), null);
	    if ( ti.moveToFirst() ) {
	        do {
	            Log.d(TAG, String.format("table: %s | col: %s", table, ti.getString(1)));
	        } while (ti.moveToNext());
	    }
	}
	
	public long addPetInfo(String name) {
		return addPetInfo(getWritableDatabase(), name);
	}	
	private long addPetInfo(SQLiteDatabase db, String name) {
		ContentValues cv  = new ContentValues();			
		cv.put(PET_INFO_NAME, name);
		cv.put(PET_INFO_CONDITION_KEY, 1);
		cv.put(PET_INFO_GOLD, 100);
		cv.put(PET_INFO_HEALTH, 100);
		
		// set the format to sql date time
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Date date = new Date();						
		cv.put(PET_INFO_DATE_LAST_CHECKED, dateFormat.format(date));
		
		return db.insert(TBL_PET_INFO, null, cv);
	}
	
	public long addPetFood(int pet_id, int food_id) {
		return addPetFood(getWritableDatabase(), pet_id, food_id);
	}	
	private long addPetFood(SQLiteDatabase db, int pet_id, int food_id) {
		ContentValues cv  = new ContentValues();
		cv.put(PET_FOOD_PET_KEY, pet_id);
		cv.put(PET_FOOD_FOOD_KEY, food_id);
		
		// set the format to sql date time
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Date date = new Date();						
		cv.put(PET_FOOD_DATE_BOUGHT, dateFormat.format(date));
		cv.put(PET_FOOD_DATE_LAST_CHECKED, dateFormat.format(date));		
		
		return db.insert(TBL_PET_FOOD, null, cv);
	}
	
	public long addFood(String name, float hours, int cost) {
		return addFood(getWritableDatabase(), name, hours, cost);
	}	
	private long addFood(SQLiteDatabase db, String name, float hours, int cost) {
		ContentValues cv  = new ContentValues();
		cv.put(FOOD_NAME, name);
		cv.put(FOOD_HOURS, hours);
		cv.put(FOOD_COST, cost);
		
		return db.insert(TBL_FOOD, null, cv);
	}
	
	public long addRunParam(String difficulty, float hp_xCoeff, float money_xCoeff) {
		return addRunParam(getWritableDatabase(), difficulty, hp_xCoeff, money_xCoeff);		
	}
	private long addRunParam(SQLiteDatabase db, String difficulty, float hp_xCoeff, float gold_xCoeff) {
		ContentValues cv  = new ContentValues();
		cv.put(RUN_PARAM_DIFFICULTY, difficulty);		
		cv.put(RUN_PARAM_HP_XCOEFF, hp_xCoeff);
		cv.put(RUN_PARAM_GOLD_XCOEFF, gold_xCoeff);
		
		return db.insert(TBL_RUN_PARAM, null, cv);
	}
	
	public long addCondition(String name, float effect) {
		return addCondition(getWritableDatabase(), name, effect);
	}
	private long addCondition(SQLiteDatabase db, String name, float effect) {	
		ContentValues cv  = new ContentValues();
		cv.put(CONDITION_NAME, name);
		cv.put(CONDITION_HEALTH_EFFECT, effect);		
		
		return db.insert(TBL_CONDITION, null, cv);
	}
	
	public int updatePetHealth(int pet_id, int health) {
		return updatePetHealth(getWritableDatabase(), pet_id, health);		
	}
	private int updatePetHealth(SQLiteDatabase db, int pet_id, int health) {
		ContentValues cv  = new ContentValues();
		cv.put(PET_INFO_HEALTH, health);
		
		int condition_id = getNewCondition(health);
		cv.put(PET_INFO_CONDITION_KEY, condition_id);
		
		// set the format to sql date time
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Date date = new Date();						
		cv.put(PET_INFO_DATE_LAST_CHECKED, dateFormat.format(date));
		
		String whereClause = String.format("_id = %d", pet_id);
		return db.update(TBL_PET_INFO, cv, whereClause, null);
	}
	
	public int updatePetGold(int pet_id, int gold) {
		return updatePetGold(getWritableDatabase(), pet_id, gold);
	}
	private int updatePetGold(SQLiteDatabase db, int pet_id, int gold) {
		ContentValues cv  = new ContentValues();
		cv.put(PET_INFO_GOLD, gold);
		
		String whereClause = String.format("_id = %d", pet_id);
		return db.update(TBL_PET_INFO, cv, whereClause, null);
	}
	
	public int updatePetFood(int pet_id, int food_id) {
		return updatePetFood(getWritableDatabase(), pet_id, food_id);
	}
	private int updatePetFood(SQLiteDatabase db, int pet_id, int food_id) {
		ContentValues cv  = new ContentValues();
		cv.put(PET_FOOD_PET_KEY, pet_id);
		cv.put(PET_FOOD_FOOD_KEY, food_id);
		
		String whereClause = String.format("%s = %d", PET_INFO_KEY, pet_id);
		return db.update(TBL_PET_INFO, cv, whereClause, null);
	}
	
	public int updatePetFoodHours(int pet_id) {
		return updatePetFoodHours(getWritableDatabase(), pet_id);
	}
	private int updatePetFoodHours(SQLiteDatabase db, int pet_id) {	
		return 1;
	}
	
	public String getPetName(int pet_id) {
		return getPetName(getReadableDatabase(), pet_id);		
	}
	private String getPetName(SQLiteDatabase db, int pet_id) {
		String name = "";
		String whereClause = String.format("%s = %d", PET_INFO_KEY, pet_id);
		Cursor c = db.query(TBL_PET_INFO, new String[] {PET_INFO_KEY, PET_INFO_NAME}, whereClause, null, null, null, null);
		
		if (c.moveToFirst()) 
			name = c.getString(1);
		
		return name;
	}
	
	public int getPetHealth(int pet_id) {
		return getPetHealth(getReadableDatabase(), pet_id);		
	}
	private int getPetHealth(SQLiteDatabase db, int pet_id) {		
		int  health = 50;
		String whereClause = String.format("%s = %d", PET_INFO_KEY, pet_id);
		Cursor c = db.query(TBL_PET_INFO, new String[] {PET_INFO_KEY, PET_INFO_HEALTH}, whereClause, null, null, null, null);
		
		if (c.moveToFirst()) 
			health = c.getInt(1);
		
		return health;		
	}
	
	public int getPetGold(int pet_id) {
		return getPetGold(getReadableDatabase(), pet_id);
	}
	private int getPetGold(SQLiteDatabase db, int pet_id) {		
		int gold = 0;		
		
		String whereClause = String.format("%s = %d", PET_INFO_KEY, pet_id);
		Cursor c = db.query(TBL_PET_INFO, new String[] {PET_INFO_KEY, PET_INFO_GOLD}, whereClause, null, null, null, null);
		
		if (c.moveToFirst()) 
			gold = c.getInt(1);
					
		return gold;
	}
	
	public String getPetConditionName(int pet_id) {
		return getPetConditionName(getReadableDatabase(), pet_id);
	}
	private String getPetConditionName(SQLiteDatabase db, int pet_id) {		
		int condition_id = 0;
		String condition = "";
		
		String whereClause = String.format("%s = %d", PET_INFO_KEY, pet_id);
		Cursor c = db.query(TBL_PET_INFO, new String[] {PET_INFO_KEY, PET_INFO_CONDITION_KEY}, whereClause, null, null, null, null);
		
		if (c.moveToFirst()) 
			condition_id = c.getInt(1);
		
		whereClause = String.format("%s = %d", CONDITION_KEY, condition_id);
		c = db.query(TBL_CONDITION, new String[] {CONDITION_KEY, CONDITION_NAME}, whereClause, null, null, null, null);
		if (c.moveToFirst()) 
			condition = c.getString(1);
		
		return condition;
	}	
	
	public int getPetCondition(int pet_id) {
		return getPetCondition(getReadableDatabase(), pet_id);
	}
	private int getPetCondition(SQLiteDatabase db, int pet_id) {
		int condition_id = 0;		
		
		String whereClause = String.format("%s = %d", PET_INFO_KEY, pet_id);
		Cursor c = db.query(TBL_PET_INFO, new String[] {PET_INFO_KEY, PET_INFO_CONDITION_KEY}, whereClause, null, null, null, null);
		
		if (c.moveToFirst()) 
			condition_id = c.getInt(1);
					
		return condition_id;
	}
	
	public String getRunParamDifficulty(int runParam_id) {
		return getRunParamDifficulty(getReadableDatabase(), runParam_id);
	}
	private String getRunParamDifficulty(SQLiteDatabase db, int runParam_id) {
		String runParamDifficulty = "blah";
		
		String whereClause = String.format("%s = %d", RUN_PARAM_KEY, runParam_id);
		Cursor c = db.query(TBL_RUN_PARAM, new String[] {RUN_PARAM_KEY, RUN_PARAM_DIFFICULTY}, whereClause, null, null, null, null);
		
		if (c.moveToFirst())
			runParamDifficulty = c.getString(1);
		
		return runParamDifficulty;
	}
	
	public int getConditionHealthEffect(int condition_id) {
		return getConditionHealthEffect(getReadableDatabase(), condition_id);
	}
	private int getConditionHealthEffect(SQLiteDatabase db, int condition_id) {
		int conditionHealthEffect = 0;		
		
		String whereClause = String.format("%s = %d", CONDITION_KEY, condition_id);
		Cursor c = db.query(TBL_CONDITION, new String[] {CONDITION_KEY, CONDITION_HEALTH_EFFECT}, whereClause, null, null, null, null);
						
		if (c.moveToFirst()) 
			conditionHealthEffect = c.getInt(1);
					
		return conditionHealthEffect;
	}
	
	public float getRunParamGoldXCoeff(int runParam_id) {
		return getRunParamGoldXCoeff(getReadableDatabase(), runParam_id);
	}
	private float getRunParamGoldXCoeff(SQLiteDatabase db, int runParam_id) {
		float gold_xCoeff = 0;
		
		String whereClause = String.format("%s = %d", RUN_PARAM_KEY, runParam_id);
		Cursor c = db.query(TBL_RUN_PARAM, new String[] {RUN_PARAM_KEY, RUN_PARAM_GOLD_XCOEFF}, whereClause, null, null, null, null);
		
		if (c.moveToFirst())
			gold_xCoeff = c.getFloat(1);
		
		return gold_xCoeff;
	}
	
	public String getPetLastDateChecked(int pet_id) {
		return getPetLastDateChecked(getReadableDatabase(), pet_id);
	}
	private String getPetLastDateChecked(SQLiteDatabase db, int pet_id) {
		String lastDateChecked = "";		
		
		String whereClause = String.format("%s = %d", PET_INFO_KEY, pet_id);
		Cursor c = db.query(TBL_PET_INFO, new String[] {PET_INFO_KEY, PET_INFO_DATE_LAST_CHECKED}, whereClause, null, null, null, null);
		
		if (c.moveToFirst()) {
			Log.d(TAG, "blah");
			lastDateChecked = c.getString(1);
		}
					
		return lastDateChecked;
	}
	
	public long getNumPets() {
		return getNumPets(getReadableDatabase());
	}
	private long getNumPets(SQLiteDatabase db) {
		return DatabaseUtils.queryNumEntries(db, TBL_PET_INFO);		
	}
	
	public String getFoodName(int food_id) {
		return getFoodName(getReadableDatabase(), food_id);
	}
	private String getFoodName(SQLiteDatabase db, int food_id) {
		String name = "";
		String whereClause = String.format("%s = %d", FOOD_KEY, food_id);
		Cursor c = db.query(TBL_FOOD, new String[] {FOOD_KEY, FOOD_NAME}, whereClause, null, null, null, null);
		
		if (c.moveToFirst()) 
			name = c.getString(1);
		
		return name;
	}
	
	public int getFoodCost(int food_id) {
		return getFoodCost(getReadableDatabase(), food_id);
	}
	private int getFoodCost(SQLiteDatabase db, int food_id) {
		int cost = 0;
		String whereClause = String.format("%s = %d", FOOD_KEY, food_id);
		Cursor c = db.query(TBL_FOOD, new String[] {FOOD_KEY, FOOD_COST}, whereClause, null, null, null, null);
		
		if (c.moveToFirst()) 
			cost = c.getInt(1);
		
		return cost;
	}
	
	public int getFoodHours(int food_id) {
		return getFoodHours(getReadableDatabase(), food_id);
	}
	private int getFoodHours(SQLiteDatabase db, int food_id) {
		int hours = 0;
		String whereClause = String.format("%s = %d", FOOD_KEY, food_id);
		Cursor c = db.query(TBL_FOOD, new String[] {FOOD_KEY, FOOD_HOURS}, whereClause, null, null, null, null);
		
		if (c.moveToFirst()) 
			hours = c.getInt(1);
		
		return hours;
	}
	
	public void getAllPetFood() {
		getAllPetFood(getReadableDatabase());
	}
	private void getAllPetFood(SQLiteDatabase db) {
		String whereClause = "";
		Cursor c = db.query(TBL_PET_FOOD, new String[] {PET_FOOD_KEY, PET_FOOD_PET_KEY, PET_FOOD_FOOD_KEY}, whereClause, null, null, null, null);
		
		if (c.moveToFirst()) {
			Log.d(TAG, String.format("pet_id = %d | food_id = %d", c.getInt(PET_FOOD_PET_KEY_INDEX), c.getInt(PET_FOOD_FOOD_KEY_INDEX)));
			while (c.moveToNext()) {
				Log.d(TAG, String.format("pet_id = %d | food_id = %d", c.getInt(PET_FOOD_PET_KEY_INDEX), c.getInt(PET_FOOD_FOOD_KEY_INDEX)));
			}
		}
	}
	
	public int getTotalPetFoodHours(int pet_id) {
		return getTotalPetFoodHours(getReadableDatabase(), pet_id);
	}
	private int getTotalPetFoodHours(SQLiteDatabase db, int pet_id) {
		int totalPetFoodHours = 0;
		
		String sql = String.format("SELECT " + TBL_PET_FOOD + "." + PET_FOOD_PET_KEY + " as _id, " +
				"sum(" + TBL_FOOD + "." + FOOD_HOURS + ") FROM " + TBL_PET_FOOD +
				" INNER JOIN " + TBL_FOOD + " ON " + TBL_FOOD  + "." + FOOD_KEY + " = " + TBL_PET_FOOD + "." + PET_FOOD_FOOD_KEY +
				" WHERE " + TBL_PET_FOOD + "." + PET_FOOD_PET_KEY + " = %d" +
				" GROUP BY " + TBL_PET_FOOD + "." + PET_FOOD_PET_KEY, 
				pet_id) ;
		
		Log.d(TAG, sql);
		
		Cursor c = db.rawQuery(sql, null);
		
		if (c.moveToFirst()) {
			totalPetFoodHours = c.getInt(1);
		}
		
		return totalPetFoodHours;
	}
	
	public long getNumFood() {
		return getNumFood(getReadableDatabase());
	}
	private long getNumFood(SQLiteDatabase db) {
		return DatabaseUtils.queryNumEntries(db, TBL_FOOD);
	}
	
	public long getNumRunParam() {
		return getNumRunParam(getReadableDatabase());
	}
	private long getNumRunParam(SQLiteDatabase db) {
		return DatabaseUtils.queryNumEntries(db, TBL_RUN_PARAM);
	}
	
	public String getConditionName(int condition_id) {
		return getConditionName(getReadableDatabase(), condition_id);
	}
	private String getConditionName(SQLiteDatabase db, int condition_id) {
		String name = "";
		String whereClause = String.format("%s = %d", CONDITION_KEY, condition_id);
		Cursor c = db.query(TBL_CONDITION, new String[] {CONDITION_KEY, CONDITION_NAME}, whereClause, null, null, null, null);
		
		if (c.moveToFirst()) 
			name = c.getString(1);
		
		return name;
	}
	
	public boolean isMaxPetFoodHours(int pet_id) {
		return isMaxPetFoodHours(getReadableDatabase(), pet_id); 
	}
	private boolean isMaxPetFoodHours(SQLiteDatabase db, int pet_id) {
		boolean isMaxPetFoodHours;
		
		int petFoodHours = getTotalPetFoodHours(pet_id);
		if (petFoodHours <= 36)
			isMaxPetFoodHours = false;
		else
			isMaxPetFoodHours = true;
		
		return isMaxPetFoodHours;
	}
	
	private int getNewCondition(int health) {
		int condition_id = -1;
		
		if (health <= 100 && health >= 70)
			condition_id = 1;
		else if (health < 70 && health >= 60) {
			if (isSick(40))
				condition_id = 2;
		}
		else if (health < 60 && health >= 50) {
			if (isSick(50))
				condition_id = 2;
		}
		else if (health < 60 && health >= 50) {
			if (isSick(60))
				condition_id = 2;
		}
		else if (health < 50 && health >= 40) {
			if (isSick(70))
				condition_id = 2;
		}
		else if (health < 40 && health >= 30) {
			if (isSick(80))
				condition_id = 2;
		}
		else if (health < 30 && health >= 00) {
			if (isSick(100))
				condition_id = 2;
		}
		
		return condition_id;
	}
	
	private boolean isSick(float sickChance) {
		boolean sickBool = false;
				
		Random mRandom = new Random();
		int result = mRandom.nextInt(100);
		
		if (result <= sickChance)
			sickBool = true;
		
		return sickBool;
	}
	
	public void updateDb(int pet_id) throws ParseException {
		//Update health
		String dateTime = getPetLastDateChecked(pet_id);		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date lastDate = dateFormat.parse(dateTime);
		float diffDays = getDiffDays(lastDate);		
		
		int oldHealth = Math.round(getPetHealth(pet_id));				
		int conditionHealthEffect = getConditionHealthEffect(getPetCondition(pet_id));		
				
		int foodHours = getTotalPetFoodHours(pet_id);
		int foodHealthEffect = 0;
		if (foodHours == 0)
			foodHealthEffect = -5;
		
		int newHealth = Math.round((float)oldHealth + ((float)conditionHealthEffect + (float)foodHealthEffect) * diffDays);
		
		Log.d(TAG, String.format("oldHealth = %d | conditionHealthEffect = %d | foodHours = %d | foodHealthEffect = %d | newHealth = %d", 
				oldHealth, conditionHealthEffect, foodHours, foodHealthEffect, newHealth));
			
		updatePetHealth(pet_id, newHealth);
		
		//Update food hours
		updatePetFoodHours(pet_id);		
	}	
	
	public float getDiffDays(Date lastDate) {		
		Date nowDate = new Date(); //Current date and time
		
		long diffMillisec = nowDate.getTime() - lastDate.getTime();
		float diffDays = (float)diffMillisec / (1000*60*60*24);
		Log.d(TAG, String.format("last = %s | now = %s | diffMilli = %d | diffDays = %f", lastDate, nowDate, diffMillisec, diffDays));
		
		return diffDays;
	}
	
}