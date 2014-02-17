
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

RULE countMethodsCalls_1 <CLASS>.<METHODSHORT> <COUNTER>
	CLASS <CLASS>
	METHOD <METHOD>
	HELPER jlp.byteman.helper.CountMethodsCalls
	AT ENTRY
		BIND fullName:String=$CLASS+".<METHODSHORT>";
		vrai:boolean=createCounter(fullName);
		tracer:boolean=isCongruent(readCounter(fullName) , Integer.parseInt(getProps().getProperty("countMethodsCalls_<ALIAS>.frequency","1")));
		newValue:int=incrementCounter(fullName);
		sep:String=getProps().getProperty("bytemanpkg.csvSep",";");
		open:boolean=getTrace().openTrace(getTitle(),"countMethodsCalls.log");
		activeRules:boolean=getTrace().activeRules;
	IF activeRules AND tracer
		DO 
			getTrace().append(getTitle(),currentDate()+sep+fullName+sep+Integer.toString(newValue)+sep);
			
ENDRULE
