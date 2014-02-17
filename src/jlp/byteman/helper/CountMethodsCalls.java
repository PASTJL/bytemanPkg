package jlp.byteman.helper;

import org.jboss.byteman.rule.Rule;

public class CountMethodsCalls extends MyHelper {
	private  static  String sep ;
	private static String title;

	public  String getTitle() {
		return title;
	}
	protected CountMethodsCalls(Rule rule) {
		super(rule);
		sep= getProps().getProperty("bytemanpkg.csvSep", ";");
		title="date"+sep+"fullMethodName"+sep+"countMethodsCalls(unit)"+sep;
	}

}
