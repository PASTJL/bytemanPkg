
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

RULE mockMethodsWithTrace_1 <CLASS>.<METHODSHORT> <COUNTER>
	CLASS <CLASS>
	METHOD <METHOD>
	HELPER <HELPER>
	
	<LOCATION>
		BIND fullName:String=$CLASS+".<METHODSHORT>";
		vrai:boolean=createCounter(fullName);
		newValue:int=incrementCounter(fullName);
		activeRules:boolean=getTrace().activeRules;
		sep:String=getProps().getProperty("bytemanpkg.csvSep", ";");
		title:String="date"+sep+"fullMethodName"+sep+"nb of mocking (unit)"+sep;
	IF activeRules
		DO 
			getTrace().openTrace(title,"mockMethodsWithTrace.log");
			getTrace().append(title,currentDate()+sep+fullName+sep+Integer.toString(newValue)+sep);
<DOSTATEMENT>			
ENDRULE
