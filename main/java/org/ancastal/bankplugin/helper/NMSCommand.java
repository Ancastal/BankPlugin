package org.ancastal.bankplugin.helper;


import org.mineacademy.fo.command.SimpleCommand;

public abstract class NMSCommand extends SimpleCommand {

	/**
	 * Create a new {@link NMSCommand} with the given label.
	 *
	 * @param label
	 */
	protected NMSCommand(String label) {
		super(label);
	}

}

