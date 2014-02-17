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

RULE org.apache.commons.dbcp.PoolingDataSource.init <COUNTER>
	CLASS org.apache.commons.dbcp.PoolingDataSource
	METHOD <init>(org.apache.commons.pool.ObjectPool)
	HELPER jlp.byteman.helper.DbcpPoolManagementPoolingDS
	AT EXIT
	BIND pool:org.apache.commons.pool.ObjectPool=$1;
		ds:org.apache.commons.dbcp.PoolingDataSource=$0;
		activeRules:boolean=getTrace().activeRules;
		poolIsNotNull:boolean=isNotNull(pool);
	IF   activeRules AND poolIsNotNull
			
		DO System.out.println("Rule init. getTrace().activeRules=> "+getTrace().activeRules);
		fillHmDs(ds,pool);
		
ENDRULE


RULE org.apache.commons.dbcp.PoolingDataSource.setPool <COUNTER>
	CLASS  org.apache.commons.dbcp.PoolingDataSource
	METHOD setPool(org.apache.commons.pool.ObjectPool)
	HELPER jlp.byteman.helper.DbcpPoolManagementPoolingDS
	AT EXIT
	BIND ds:org.apache.commons.dbcp.PoolingDataSource=$0;
		pool:org.apache.commons.pool.ObjectPool=$1;	
		activeRules:boolean=getTrace().activeRules;
	IF  activeRules
		DO 	fillHmDs(ds,pool);
		
ENDRULE




RULE  org.apache.commons.dbcp.PoolingDataSource write <COUNTER>
	CLASS  org.apache.commons.dbcp.PoolingDataSource
	METHOD getConnection
	HELPER jlp.byteman.helper.DbcpPoolManagementPoolingDS
	AT EXIT
	BIND frequenceMeasure:int=Integer.parseInt(getProps().getProperty("dbcpPoolManagementPoolingDS_<ALIAS>.frequenceMeasure","1"));
		ds:org.apache.commons.dbcp.PoolingDataSource=$0;
		noMatter1:boolean=createCounter(ds);
		count:int=incrementCounter(ds);
		conn:Object=$! ;
		isCongruent:boolean=isCongruent(count,frequenceMeasure);
		activeRules:boolean=getTrace().isActiveRules();
	IF activeRules AND isCongruent
		DO 	getTrace().openTrace(getTitle(),"dbcpPoolManagementPoolingDS.log");
			getTrace().append(getTitle(),dbcpPoolToTrace(ds,currentDate(),conn,count,frequenceMeasure));
				
ENDRULE
