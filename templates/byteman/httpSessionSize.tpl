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

RULE httpSessionSize_1 with HttpSessionEvent <COUNTER>
	INTERFACE javax.servlet.http.HttpSessionListener
	METHOD sessionDestroyed(javax.servlet.http.HttpSessionEvent)
	HELPER jlp.byteman.helper.HttpSessionSize
	AT ENTRY
	BIND boolSerialization:boolean=getPrimBoolean(getProps().getProperty("httpSessionSize_<ALIAS>.boolSerialization","true"));
		frequenceMeasure:int=Integer.parseInt(getProps().getProperty("httpSessionSize_<ALIAS>.frequenceMeasure","1"));
		strategy:String=getProps().getProperty("httpSessionSize_<ALIAS>.strategy","shallowSize");
		unbool:boolean=createCounter($CLASS+"."+$METHOD);
		count:int=readCounter($CLASS+"."+$METHOD);
		isCongruent:boolean=isCongruent(count,frequenceMeasure);
		hev:HttpSessionEvent=$1;
		activeRules:boolean=getTrace().activeRules;
		countBis:int=incrementCounter($CLASS+"."+$METHOD);
		sep:String=getProps().getProperty("bytemanpkg.csvSep", ";");
	IF  activeRules AND isCongruent
		DO 
			getTrace().openTrace(getTitle(),"HttpSessionSize.log");
			getTrace().append(getTitle(),currentDate()+sep+toTrace(hev,"HttpSessionEvent.sessionDestroyed",boolSerialization,strategy,count));
		
ENDRULE

RULE httpSessionSizeSimple_1 with HttpSession.expire <COUNTER>
	INTERFACE javax.servlet.http.HttpSession
	METHOD expire()
	HELPER jlp.byteman.helper.HttpSessionSizeSimple
	AT ENTRY
	BIND boolSerialization:boolean=getPrimBoolean(getProps().getProperty("httpSessionSize_<ALIAS>.boolSerialization","true"));
		frequenceMeasure:int=Integer.parseInt(getProps().getProperty("httpSessionSize_<ALIAS>.frequenceMeasure","1"));
		strategy:String=getProps().getProperty("httpSessionSize_<ALIAS>.strategy","shallowSize");
		unbool:boolean=createCounter($CLASS+"."+$METHOD);
		count:int=readCounter($CLASS+"."+$METHOD);
		isCongruent:boolean=isCongruent(count,frequenceMeasure);
		countBis:int=incrementCounter($CLASS+"."+$METHOD);
		sess:HttpSession=$0;
		activeRules:boolean=getTrace().activeRules;
		sep:String=getProps().getProperty("bytemanpkg.csvSep", ";");
	IF activeRules AND isCongruent
		DO
			getTrace().openTrace(getTitle(),"HttpSessionSize.log");
			getTrace().append(getTitle(),currentDate()+sep+toTrace(sess,"HttpSession.expire",boolSerialization,strategy,count));
		
ENDRULE

RULE httpSessionSizeSimple_2 with HttpSession.invalidate <COUNTER>
	INTERFACE javax.servlet.http.HttpSession
	METHOD invalidate()
	HELPER jlp.byteman.helper.HttpSessionSizeSimple
	AT ENTRY
	BIND boolSerialization:boolean=getPrimBoolean(getProps().getProperty("httpSessionSize_<ALIAS>.boolSerialization","true"));
		frequenceMeasure:int=Integer.parseInt(getProps().getProperty("httpSessionSize_<ALIAS>.frequenceMeasure","1"));
		unbool:boolean=createCounter($CLASS+"."+$METHOD);
		count:int=readCounter($CLASS+"."+$METHOD);
		strategy:String=getProps().getProperty("httpSessionSize_<ALIAS>.strategy","shallowSize");
		isCongruent:boolean=isCongruent(count,frequenceMeasure);
		sess:HttpSession=$0;
		activeRules:boolean=getTrace().activeRules;
		countBis:int=incrementCounter($CLASS+"."+$METHOD);
		sep:String=getProps().getProperty("bytemanpkg.csvSep", ";");
	IF  activeRules AND isCongruent
		DO 
			getTrace().openTrace(getTitle(),"HttpSessionSize.log");
			getTrace().append(getTitle(),currentDate()+sep+toTrace(sess,"HttpSession.invalidate",boolSerialization,strategy,count));
		
ENDRULE