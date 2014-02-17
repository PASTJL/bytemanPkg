
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

RULE countExecutionMethods_1 <CLASS>.<METHODSHORT> <COUNTER>
	CLASS <CLASS>
	METHOD <METHOD>
	HELPER jlp.byteman.helper.CountExecutionMethods
	AT ENTRY
		BIND fullName:String=$CLASS+".<METHODSHORT>";
			activeRules:boolean=getTrace().activeRules;
			
	IF activeRules
		DO 
			getTrace().openTrace(getTitle(),"countExecutionMethods.log");
			createCounter(fullName);
			incrementCounter(fullName);
			
ENDRULE

RULE countExecutionMethods_2 <CLASS>.<METHODSHORT> <COUNTER>
	CLASS <CLASS>
	METHOD <METHOD>
	HELPER jlp.byteman.helper.CountExecutionMethods
	AT EXIT
		BIND fullName:String=$CLASS+".<METHODSHORT>";
			tracer:boolean=isCongruent(readCounter(fullName) , Integer.parseInt(getProps().getProperty("countExecutionMethods_<ALIAS>.frequency","1")));
			newValue:int=readCounter(fullName);
			activeRules:boolean=getTrace().activeRules;
			sep:String=getProps().getProperty("bytemanpkg.csvSep",";");
		IF activeRules AND tracer
			DO		
				getTrace().append(getTitle(),currentDate()+sep+fullName+sep+Integer.toString(newValue)+sep);

ENDRULE
