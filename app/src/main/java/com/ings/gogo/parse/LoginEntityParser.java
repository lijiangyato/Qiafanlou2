package com.ings.gogo.parse;

import com.google.gson.Gson;
import com.ings.gogo.base.BaseParser;
import com.ings.gogo.entity.LoginEntity;

public class LoginEntityParser extends BaseParser {
	@Override
	public Object parse(String result) {
		Gson gson = new Gson();
		Object entity = gson.fromJson(result, LoginEntity.class);
		return entity;
	}
}
