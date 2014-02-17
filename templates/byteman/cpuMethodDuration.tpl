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

RULE cpuDurationMethods_1 <CLASS>.<METHODSHORT> <COUNTER>
	CLASS <CLASS>
	METHOD <METHOD>
	HELPER jlp.byteman.helper.CPUMethodDuration
	AT ENTRY
	BIND id:String=Long.toString(Thread.currentThread().getId());
		idMeth:String=$CLASS+"."+$METHOD+id;
		cpuTime:long=gettMB().getThreadCpuTime(Thread.currentThread().getId());
		activeRules:boolean=getTrace().activeRules;
		
	IF activeRules
		DO getTrace().openTrace(getTitle(),"CPUdurationMethods.log");
		putInHmDebUser(idMeth, cpuTime);
		createTimer(idMeth);
		resetTimer (idMeth);
ENDRULE

RULE cpuDurationMethods_2 <CLASS>.<METHODSHORT> <COUNTER>
	CLASS <CLASS>
	METHOD <METHOD>
	HELPER jlp.byteman.helper.CPUMethodDuration
	
	AT EXIT
			BIND fullName:String=$CLASS+".<METHODSHORT>";
				id:String=Long.toString(Thread.currentThread().getId());
				idMeth:String=$CLASS+"."+$METHOD+id;
				totalDuration:long=getElapsedTimeFromTimer(idMeth);
				cpuUserDeb:long=getInHmDebUser(idMeth);
				duree:long=gettMB().getThreadCpuTime(Thread.currentThread().getId())-cpuUserDeb;
				take:boolean=isGtOrEq(duree,Long.parseLong(getProps().getProperty("CPUdurationMethods_<ALIAS>.minDuration","0")));
				activeRules:boolean=getTrace().activeRules;
				sep:String=getProps().getProperty("bytemanpkg.csvSep",";");
	IF activeRules AND take
		DO 
			getTrace().append(getTitle(),currentDate()+sep+fullName+sep+Long.toString(duree/1000000)+sep+Long.toString(totalDuration)+sep+Long.toString(perCent(duree/1000000,totalDuration))+sep);
			deleteTimer (idMeth);
			delInHmDebUser(idMeth);
			
			
ENDRULE
