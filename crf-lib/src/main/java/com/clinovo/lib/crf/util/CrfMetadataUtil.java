package com.clinovo.lib.crf.util;

/**
 * CRF Metadata Util.
 */
public final class CrfMetadataUtil {

	private enum CrfMetadataTag {

		CRF_SOURCE("(<crfSource>)\\w+(<\\/crfSource>)", "");

		private String regexp;
		private String replacement;

		CrfMetadataTag(String regexp, String replacement) {
			this.regexp = regexp;
			this.replacement = replacement;
		}
	}

	private CrfMetadataUtil(){
	}

	/**
	 * Remove all metadata tags from value.
	 * @param value String
	 * @return String
	 */
	public static String removeAllMetadataTags(String value) {
		for (CrfMetadataTag tag : CrfMetadataTag.values()) {
			value = value.replaceAll(tag.regexp, tag.replacement);
		}
		return value;
	}
}
