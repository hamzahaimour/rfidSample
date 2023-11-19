package com.atid.app.rfid.types;

//import com.atid.lib.types.IEnumType;

public enum FileDataSeperatorType implements IEnumType {
	Tab ('\t', "Tab('\\t')"),
	Semicolon (';', "Semicolon(';')"),
	Comma (',', "Comma(',')"),
	Space (' ', "Space(' ')");

	private final int mCode;
	private final String mName;

	private FileDataSeperatorType(int code, String name) {
		mCode = code;
		mName = name;
	}

	@Override
	public int getCode() {
		return mCode;
	}

	@Override
	public String toString() {
		return mName;
	}

	public static FileDataSeperatorType valueOf(int code) {
		for (FileDataSeperatorType item : values()) {
			if ( item.getCode() == code )
				return item;
		}
		return FileDataSeperatorType.Comma;
	}
}
