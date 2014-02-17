# for a method defined as : <Type return> com.<packages>.<ClassName>.<method>(Type1,Type2)
# CLASS => com.<packages>.<ClassName>
# METHOD => <Type Return> <method>(Type1,Type2)
# METHODSHORT => <method>(Type1,Type2)
# HELPER => To define a specific HELPER
# COUNTER => a global counter to avoid duplicate name of Rules
# ALIAS => to distinct several Rules with the same template
# LOCATION (optional) => to define a location AT LOCATION
# BINDSTATEMENT (optional) to define avaraibles binding
# DOSTATEMENT (optional) => to define a DOSTATEMENT after DO
# <EXTRAVARIABLE> (optional) => to replace all occurence in tpl by a value tagged with the same thing in byteman.properties ( see getBasicAttributesValues  in byteman.properties)

RULE org.apache.commons.dbcp.BasicDataSource.init <COUNTER>
	CLASS org.apache.commons.dbcp.BasicDataSource
	METHOD <init>
	HELPER jlp.byteman.helper.DbcpPoolManagement
	AT EXIT
	BIND ds:org.apache.commons.dbcp.BasicDataSource=$0;
			
	IF  getTrace().activeRules 
		DO fillHmDs(ds);
		
ENDRULE


RULE org.apache.commons.dbcp.BasicDataSource.createDataSource <COUNTER>
	CLASS org.apache.commons.dbcp.BasicDataSource
	METHOD createDataSource
	HELPER jlp.byteman.helper.DbcpPoolManagement
	AT EXIT
	BIND ds:org.apache.commons.dbcp.BasicDataSource=$!;
			
	IF  getTrace().activeRules 
		DO fillHmDs(ds);
		
ENDRULE




RULE  org.apache.commons.dbcp.BasicDataSource write <COUNTER>
	CLASS org.apache.commons.dbcp.BasicDataSource
	METHOD <METHOD>
	HELPER jlp.byteman.helper.DbcpPoolManagement
	AT ENTRY
	BIND frequenceMeasure:int=Integer.parseInt(getProps().getProperty("dbcpPoolManagement_<ALIAS>.frequenceMeasure","1"));
		count:int=incCountGlobal();
		ds:org.apache.commons.dbcp.BasicDataSource=$0;
		isCongruent:boolean=isCongruent(count,frequenceMeasure);
	IF  getTrace().activeRules  AND isCongruent
	
		DO getTrace().openTrace(getTitle(),"dbcpPoolManagement.log");
		 getTrace().append(getTitle(),currentDate()dbcpPoolToTrace(ds,currentDate()));
				
ENDRULE
