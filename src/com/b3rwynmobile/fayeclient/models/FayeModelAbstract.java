package com.b3rwynmobile.fayeclient.models;

import com.google.gson.Gson;

/**
 * @author Ademar Alves de Oliveira
 * @Apr 23, 2013
 * @email ademar111190@gmail.com
 */
public class FayeModelAbstract {

	/**
	 * to string automatically generate a json string
	 */
	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
