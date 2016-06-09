package com.clinovo.enums;

/**
 * Enum that is crated in order to support boolean values in oracle.
 */
public enum BooleanEnum {
	TRUE, FALSE;

	/**
	 * Convert enum entity to boolean result.
	 * @param entity BooleanEnum entity.
	 * @return boolean
	 */
	public static boolean toBoolean(BooleanEnum entity) {
		return entity.equals(BooleanEnum.TRUE);
	}

	/**
	 * Get enum entity that corresponds to boolean value.
	 * @param bool boolean
	 * @return enum entity
	 */
	public static BooleanEnum getEntity(boolean bool) {
		return bool ? BooleanEnum.TRUE : BooleanEnum.FALSE;
	}
}
