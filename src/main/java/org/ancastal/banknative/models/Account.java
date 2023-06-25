package org.ancastal.banknative.models;

public class Account {

	private String uuid;
	private String accountHolder;
	private String bankName;
	private Double balance;


	public Account(String uuid, String accountHolder, String bankName, Double balance) {
		this.uuid = uuid;
		this.accountHolder = accountHolder;
		this.bankName = bankName;
		this.balance = balance;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public String getAccountHolder() {
		return accountHolder;
	}

	public void setAccountHolder(String accountHolder) {
		this.accountHolder = accountHolder;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

}