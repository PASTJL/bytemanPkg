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

RULE durationMethods_1 <CLASS>.<METHODSHORT> <COUNTER>
	CLASS <CLASS>
	METHOD <METHOD>
	HELPER jlp.byteman.helper.DurationMethods
	AT ENTRY
		BIND activeRules:boolean=getTrace().activeRules;
		void1:boolean=createTimer(Thread.currentThread());
		void2:long=resetTimer (Thread.currentThread());
	IF activeRules
		DO getTrace().openTrace(getTitle(),"durationMethods.log");
		
		
ENDRULE

RULE durationMethods_2 <CLASS>.<METHODSHORT> <COUNTER>
	CLASS <CLASS>
	METHOD <METHOD>
	HELPER jlp.byteman.helper.DurationMethods
	
	AT EXIT
			BIND fullName:String=$CLASS+".<METHODSHORT>";
				duree:long=getElapsedTimeFromTimer(Thread.currentThread());
				take:boolean=isGtOrEq(duree,Long.parseLong(getProps().getProperty("durationMethods_<ALIAS>.minDuration","0")));
				activeRules:boolean=getTrace().activeRules;
				sep:String=getProps().getProperty("bytemanpkg.csvSep", ";");
	IF activeRules AND take
		DO 
			getTrace().append(getTitle(),currentDate()+sep+fullName+sep+Long.toString(duree)+sep);
ENDRULE
