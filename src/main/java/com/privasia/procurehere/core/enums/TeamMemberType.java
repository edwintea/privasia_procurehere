package com.privasia.procurehere.core.enums;

public enum TeamMemberType {
	Editor("Editor"), Viewer("Viewer"), Associate_Owner("Associate Owner");

	private String value;
	
	TeamMemberType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	
}
