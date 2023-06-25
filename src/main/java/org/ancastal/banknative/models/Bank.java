package org.ancastal.banknative.models;

public class Bank {

	private String playerUUID;
	private String username;
	private String bankName;
	private Double balance;
	private int atms;
	private String interest;
	private Integer activeATMs;

	public Bank(String playerUUID, String username, String bankName, Double balance, Integer atms, String interest, Integer activeATMs) {
		this.playerUUID = playerUUID;
		this.username = username;
		this.bankName = bankName;
		this.balance = balance;
		this.atms = atms;
		this.interest = interest;
		this.activeATMs = activeATMs;
	}

	public Integer getActiveATMs() {
		return activeATMs;
	}

	public void setActiveATMs(Integer activeATMs) {
		this.activeATMs = activeATMs;
	}

	public String getInterest() {
		return interest;
	}

	public void setInterest(String interest) {
		this.interest = interest;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getPlayerUUID() {
		return playerUUID;
	}

	public void setPlayerUUID(String playerUUID) {
		this.playerUUID = playerUUID;
	}

	public int getAtms() {
		return atms;
	}

	public void setAtms(int atms) {
		this.atms = atms;
	}
}