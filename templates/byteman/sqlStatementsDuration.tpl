# for a method defined as : <Type return> com.<packages>.<ClassName>.<method>(Type1,Type2)
# CLASS => com.<packages>.<ClassName>
# METHOD => <Type Return> <method>(Type1,Type2)
# METHODSHORT => <method>(Type1,Type2)
# HELPER => To define a specific HELPER
# COUNTER => a global counter to avoid duplicate name of Rules
# ALIAS => to distinct several Rules with the same template
# LOCATION (optional) => to define a location AT LOCATION
# RETURN (optional) => to define a RETURN after DO
# <EXTRAVARIABLE> (optional) => to replace all occurence in tpl by a value tagged with the same thing in byteman.properties ( see getBasicAttributesValues  in byteman.properties)

RULE sqlStatementDuration_1 preparedStatement_1 <COUNTER>
	INTERFACE ^java.sql.Connection
	METHOD prepareCall
	HELPER jlp.byteman.helper.SqlStatementsDuration
	AT EXIT
	BIND sqlStatement:java.sql.Statement=$!;
	#	bool1:boolean=createCountDown(sqlStatement,Integer.parseInt(getProps().getProperty("sqlStatementsDuration_<ALIAS>.countDown","2")));
		bool1:boolean=createCounter(sqlStatement,0);
		
		activeRules:boolean=getTrace().activeRules;
		tabObj:Object[]=$*;
	IF  activeRules 
	DO fillStatementSql(sqlStatement,tabObj);
		
ENDRULE

RULE sqlStatementDuration_2 preparedStatement_2 <COUNTER>
	INTERFACE ^java.sql.Connection
	METHOD prepareStatement
	HELPER jlp.byteman.helper.SqlStatementsDuration
	AT EXIT
	BIND sqlStatement:java.sql.Statement=$!;
		bool1:boolean=createCounter(sqlStatement,0);
		activeRules:boolean=getTrace().activeRules;
		tabObj:Object[]=$*;
	IF  activeRules 
		DO fillStatementSql(sqlStatement,tabObj);
		
ENDRULE

RULE sqlStatementDuration_2 entering execute <COUNTER>
	INTERFACE ^java.sql.Statement
	METHOD execute
	HELPER jlp.byteman.helper.SqlStatementsDuration
	AT ENTRY
	BIND activeRules:boolean=getTrace().activeRules;
		 sqlStatement:java.sql.Statement=$0;
		 bool1:boolean=deleteCounter(sqlStatement);
		 bool2:boolean=createCounter(sqlStatement,0);
		 tabObj:Object[]=$*;
	IF activeRules 
		DO createTimer(Thread.currentThread());
		resetTimer (Thread.currentThread());
		fillStatementSql(sqlStatement,tabObj);
		getTrace().openTrace(getTitle(),"SqlStatementsDuration.log");
ENDRULE


RULE sqlStatementDuration_2 entering executeUpdate <COUNTER>
	INTERFACE ^java.sql.Statement
	METHOD executeUpdate
	HELPER jlp.byteman.helper.SqlStatementsDuration
	AT ENTRY
	BIND activeRules:boolean=getTrace().activeRules;
			sqlStatement:java.sql.Statement=$0;
			tabObj:Object[]=$*;	
			bool1:boolean=deleteCounter(sqlStatement);
			bool2:boolean=createCounter(sqlStatement,0);
	IF activeRules 
	DO createTimer(Thread.currentThread());
		resetTimer (Thread.currentThread());
		fillStatementSql(sqlStatement,tabObj);
		getTrace().openTrace(getTitle(),"SqlStatementsDuration.log");
ENDRULE

RULE sqlStatementDuration_2 entering executeQuery <COUNTER>
	INTERFACE ^java.sql.Statement
	METHOD executeQuery
	HELPER jlp.byteman.helper.SqlStatementsDuration
	AT ENTRY
	BIND activeRules:boolean=getTrace().activeRules;
		sqlStatement:java.sql.Statement=$0;
		bool1:boolean=deleteCounter(sqlStatement);
		bool2:boolean=createCounter(sqlStatement,0);
		tabObj:Object[]=$*;
	IF activeRules 
		DO createTimer(Thread.currentThread());
		resetTimer (Thread.currentThread());
		fillStatementSql(sqlStatement,tabObj);
		getTrace().openTrace(getTitle(),"SqlStatementsDuration.log");
ENDRULE


RULE sqlStatementDuration_2 exiting execute() <COUNTER>
	INTERFACE ^java.sql.Statement
	METHOD execute
	HELPER jlp.byteman.helper.SqlStatementsDuration
	AT EXIT
		BIND duree:long=getElapsedTimeFromTimer(Thread.currentThread());
		take:boolean=isGtOrEq(duree,Long.parseLong(getProps().getProperty("sqlStatementsDuration_<ALIAS>.minDuration","0")));
		sqlStatement:java.sql.Statement=$0;
		activeRules:boolean=getTrace().activeRules;
		sep:String=getProps().getProperty("bytemanpkg.csvSep", ";");
		count:int=incrementCounter(sqlStatement);
		
	IF activeRules  AND take AND isEquals(readCounter(sqlStatement),1)
		DO 
		getTrace().append(getTitle(),currentDate()+sep+sqlDurationToTrace(sqlStatement,"execute",Integer.parseInt(getProps().getProperty("sqlStatementsDuration_<ALIAS>.longMaxReq","400")),Boolean.parseBoolean(getProps().getProperty("sqlStatementsDuration_<ALIAS>.stripBeforeWhereBool","false")),Boolean.parseBoolean(getProps().getProperty("sqlStatementsDuration_<ALIAS>.boolBindParameters","true")),duree));
		
ENDRULE


RULE sqlStatementDuration_2 exiting  executeUpdate()<COUNTER>
	INTERFACE ^java.sql.Statement
	METHOD executeUpdate
	HELPER jlp.byteman.helper.SqlStatementsDuration
	AT EXIT
	BIND duree:long=getElapsedTimeFromTimer(Thread.currentThread());
		take:boolean=isGtOrEq(duree,Long.parseLong(getProps().getProperty("sqlStatementsDuration_<ALIAS>.minDuration","0")));
		sqlStatement:java.sql.Statement=$0;
		activeRules:boolean=getTrace().activeRules;
		sep:String=getProps().getProperty("bytemanpkg.csvSep", ";");
		count:int=incrementCounter(sqlStatement);
		
	IF activeRules  AND take AND isEquals(readCounter(sqlStatement),1)
		DO 
		getTrace().append(getTitle(),currentDate()+sep+sqlDurationToTrace(sqlStatement,"update_pstm",Integer.parseInt(getProps().getProperty("sqlStatementsDuration_<ALIAS>.longMaxReq","400")),Boolean.parseBoolean(getProps().getProperty("sqlStatementsDuration_<ALIAS>.stripBeforeWhereBool","false")),Boolean.parseBoolean(getProps().getProperty("sqlStatementsDuration_<ALIAS>.boolBindParameters","true")),duree));
				
ENDRULE



RULE sqlStatementDuration_2 exiting executeQuery() <COUNTER>
	INTERFACE ^java.sql.Statement
	METHOD executeQuery
	HELPER jlp.byteman.helper.SqlStatementsDuration
	AT EXIT
	BIND duree:long=getElapsedTimeFromTimer(Thread.currentThread());
		take:boolean=isGtOrEq(duree,Long.parseLong(getProps().getProperty("sqlStatementsDuration_<ALIAS>.minDuration","0")));
		sqlStatement:java.sql.Statement=$0;
		activeRules:boolean=getTrace().activeRules;
		sep:String=getProps().getProperty("bytemanpkg.csvSep", ";");
		count:int=incrementCounter(sqlStatement);
		
	IF activeRules  AND take AND isEquals(readCounter(sqlStatement),1)
	DO 
		getTrace().append(getTitle(),currentDate()+sep+sqlDurationToTrace(sqlStatement,"query",Integer.parseInt(getProps().getProperty("sqlStatementsDuration_<ALIAS>.longMaxReq","400")),Boolean.parseBoolean(getProps().getProperty("sqlStatementsDuration_<ALIAS>.stripBeforeWhereBool","false")),Boolean.parseBoolean(getProps().getProperty("sqlStatementsDuration_<ALIAS>.boolBindParameters","true")),duree));
				
ENDRULE

