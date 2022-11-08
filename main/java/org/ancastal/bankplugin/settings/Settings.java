package org.ancastal.bankplugin.settings;

import org.mineacademy.fo.settings.SimpleSettings;

import java.util.List;

public class Settings extends SimpleSettings {

	@Override
	protected boolean saveComments() {
		return true;
	}


	public static List<String> CUSTOM_BANK_STRING;

	private static void init() {
		setPathPrefix(null);
		CUSTOM_BANK_STRING = getStringList("CustomBankString");
	}

	public static List<String> getCustomBankString() {
		return CUSTOM_BANK_STRING;
	}
}
