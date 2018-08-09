package se.oru.aass.semrob.setting;

public enum NumericRelation {
	BIGGER_THAN (1),
	SMALLER_THAN (2),
	EQUAL (0);
	
	int code;
	private NumericRelation(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}	
}
