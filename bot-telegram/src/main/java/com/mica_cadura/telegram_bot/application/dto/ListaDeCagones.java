package com.mica_cadura.telegram_bot.application.dto;

import java.util.ArrayList;
import java.util.List;

public class ListaDeCagones {
	
	private List<Cagon> listaCagones = new ArrayList<>();

	public List<Cagon> getListaCagones() {
		return listaCagones;
	}

	public void setListaCagones(Cagon cagon) {
		listaCagones.add(cagon);
	}
	
}
