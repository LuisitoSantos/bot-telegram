package com.mica_cadura.telegram_bot.application.dto;

import java.util.Comparator;

public class MyComparatorAnual implements Comparator<Cagon>{
	
	@Override
	public int compare(Cagon o1, Cagon o2) {
		return o1.getCacaAnual().compareTo(o2.getCacaAnual());
	}
}
