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






RULE  org.apache.commons.dbcp.BasicDataSource write <COUNTER>
	CLASS org.apache.commons.dbcp.BasicDataSource
	METHOD getConnection
	HELPER jlp.byteman.helper.DbcpPoolManagementBasicDS
	AT EXIT
	BIND frequenceMeasure:int=Integer.parseInt(getProps().getProperty("dbcpPoolManagementBasicDS_<ALIAS>.frequenceMeasure","1"));
		ds:org.apache.commons.dbcp.BasicDataSource=$0;
		noMatter1:boolean=createCounter(ds);
		count:int=incrementCounter(ds);
		activeRules:boolean=getTrace().activeRules;
		isCongruent:boolean=isCongruent(count,frequenceMeasure);
	IF  activeRules  AND isCongruent
	
	DO getTrace().openTrace(getTitle(),"dbcpPoolManagementBasicDS.log");
		 getTrace().append(getTitle(),dbcpPoolToTrace(ds,currentDate(),count,frequenceMeasure));
				
ENDRULE
