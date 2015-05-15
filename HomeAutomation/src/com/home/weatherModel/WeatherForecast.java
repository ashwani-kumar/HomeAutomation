/*
 * Copyright (C) 2013 Surviving with Android (http://www.survivingwithandroid.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.home.weatherModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Francesco
 *
 */
public class WeatherForecast {

	private List<DayForecast> daysForecast = new ArrayList<DayForecast>();
	
	public void addForecast(DayForecast forecast) {
		daysForecast.add(forecast);
		System.out.println("Add forecast ["+forecast+"]");
	}
	
	public DayForecast getForecast(int dayNum) {
		if(daysForecast.size()>0){
		return daysForecast.get(dayNum);
		}
		return null;
	}
}
