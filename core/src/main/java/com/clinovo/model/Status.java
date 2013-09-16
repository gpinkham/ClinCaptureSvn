package com.clinovo.model;

public class Status {

	public enum DictionaryType {

		UNKNOWN, EXTERNAL, CUSTOM;

		public static DictionaryType getType(int type) {

			for (DictionaryType x : DictionaryType.values()) {

				if (x.ordinal() == type)
					return x;
			}

			return UNKNOWN;
		}
	}
	
	public enum CodeStatus {
		CODED, NOT_CODED, UNKNOWN
	}
}
