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

RULE org.hibernate.Session.disconnect <COUNTER>
	INTERFACE  ^org.hibernate.Session
	METHOD disconnect()
	HELPER jlp.byteman.helper.JpaSessionSize
	AT ENTRY
	BIND boolSerialization:boolean=getPrimBoolean(getProps().getProperty("jpaSessionSize_<ALIAS>.boolSerialization","false"));
		frequenceMeasure:int=Integer.parseInt(getProps().getProperty("jpaSessionSize_<ALIAS>.frequenceMeasure","1"));
		noMatter1:boolean=createCounter($CLASS+"."+$METHOD,1);
		count:int=readCounter($CLASS+"."+$METHOD);
		strategy:String=getProps().getProperty("jpaSessionSize_<ALIAS>.strategy","shallowSize");
		isCongruent:boolean=isCongruent(count,frequenceMeasure);
		sess:Object=$0;
		activeRules:boolean=getTrace().activeRules;
		countBis:int=incrementCounter($CLASS+"."+$METHOD);
		sep:String=getProps().getProperty("bytemanpkg.csvSep", ";");
	IF  activeRules AND isCongruent
		DO 	getTrace().openTrace(getTitle(),"JpaSessionSize.log");
			getTrace().append(getTitle(),currentDate()+sep+sizeJpaSessionToTrace(sess, boolSerialization,"org.hibernate.Session.disconnect",strategy,frequenceMeasure,count));
		
ENDRULE

RULE  org.hibernate.Session.close <COUNTER>
	INTERFACE   ^org.hibernate.Session
	METHOD close()
	HELPER jlp.byteman.helper.JpaSessionSize
	AT ENTRY
	BIND boolSerialization:boolean=getPrimBoolean(getProps().getProperty("jpaSessionSize_<ALIAS>.boolSerialization","false"));
		frequenceMeasure:int=Integer.parseInt(getProps().getProperty("jpaSessionSize_<ALIAS>.frequenceMeasure","1"));
		noMatter1:boolean=createCounter($CLASS+"."+$METHOD,1);
		count:int=readCounter($CLASS+"."+$METHOD);
		strategy:String=getProps().getProperty("jpaSessionSize_<ALIAS>.strategy","shallowSize");
		isCongruent:boolean=isCongruent(count,frequenceMeasure);
		sess:Object=$0;
		activeRules:boolean=getTrace().activeRules;
		countBis:int=incrementCounter($CLASS+"."+$METHOD);
		sep:String=getProps().getProperty("bytemanpkg.csvSep", ";");
	IF  activeRules AND isCongruent
		DO 	getTrace().openTrace(getTitle(),"JpaSessionSize.log");
			getTrace().append(getTitle(),currentDate()+sep+sizeJpaSessionToTrace(sess, boolSerialization,"org.hibernate.Session.close",strategy,frequenceMeasure,count));
		
ENDRULE

RULE  javax.persistence.EntityManager.close <COUNTER>
	INTERFACE   ^javax.persistence.EntityManager
	METHOD close()
	HELPER jlp.byteman.helper.JpaSessionSize
	AT ENTRY
	BIND boolSerialization:boolean=getPrimBoolean(getProps().getProperty("jpaSessionSize_<ALIAS>.boolSerialization","false"));
		frequenceMeasure:int=Integer.parseInt(getProps().getProperty("jpaSessionSize_<ALIAS>.frequenceMeasure","1"));
		noMatter1:boolean=createCounter($CLASS+"."+$METHOD,1);
		count:int=readCounter($CLASS+"."+$METHOD);
		strategy:String=getProps().getProperty("jpaSessionSize_<ALIAS>.strategy","shallowSize");
		isCongruent:boolean=isCongruent(count,frequenceMeasure);
		activeRules:boolean=getTrace().activeRules;
		sess:Object=$0;
		countBis:int=incrementCounter($CLASS+"."+$METHOD);
		sep:String=getProps().getProperty("bytemanpkg.csvSep", ";");
	IF  activeRules AND isCongruent
		DO 	getTrace().openTrace(getTitle(),"JpaSessionSize.log");
			getTrace().append(getTitle(),currentDate()+sep+sizeJpaSessionToTrace(sess, boolSerialization,"javax.persistence.EntityManager.close",strategy,frequenceMeasure,count));
		
ENDRULE