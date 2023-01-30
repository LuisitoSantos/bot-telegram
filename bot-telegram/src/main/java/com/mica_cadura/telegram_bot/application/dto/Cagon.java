package com.mica_cadura.telegram_bot.application.dto;

public class Cagon {

	private String name;
	
	private String realName;

	private Integer cacaMensual;

	private Integer cacaAnual;
	
	private Integer cagonDelMes;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getCacaMensual() {
		return cacaMensual;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public void setCacaMensual(Integer cacaMensual) {
		this.cacaMensual = cacaMensual;
	}

	public Integer getCacaAnual() {
		return cacaAnual;
	}

	public void setCacaAnual(Integer cacaAnual) {
		this.cacaAnual = cacaAnual;
	}

	public Integer getCagonDelMes() {
		return cagonDelMes;
	}

	public void setCagonDelMes(Integer cagonDelMes) {
		this.cagonDelMes = cagonDelMes;
	}
}
