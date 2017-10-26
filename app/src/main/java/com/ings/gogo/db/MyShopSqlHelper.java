package com.ings.gogo.db;

import com.ings.gogo.utils.LogUtils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MyShopSqlHelper extends SQLiteOpenHelper {

	public MyShopSqlHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL("create table mycar(_id integer primary key autoincrement,"
				+ "name TEXT," + "price TEXT," + "num TEXT," + "image TEXT,"
				+ "proid TEXT," + "istoday TEXT)");

		LogUtils.e("���ݿ�onCreate ������", "���ݿ�onCreate ������");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		// db.execSQL("drop table if exists student");
		System.out.println("onUpgrade ������" + oldVersion + "--" + newVersion);

	}

}