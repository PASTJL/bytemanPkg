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

RULE getBasicAttributesValues <CLASS>.<METHODSHORT> <COUNTER>
	CLASS <CLASS>
	METHOD <METHOD>
	HELPER jlp.byteman.helper.GetBasicAttributesValues
	AT ENTRY
	BIND frequenceMeasure:int=Integer.parseInt(getProps().getProperty("getBasicAttributesValues_<ALIAS>.frequenceMeasure","1"));
		rankObjectToTrack:Object=$<RANK_OBJECT>;
		noMatter1:boolean=createCounter($CLASS+"."+$METHOD,1);
		count:int=readCounter($CLASS+"."+$METHOD);
		isCongruent:boolean=isCongruent(count,frequenceMeasure);
		alias:String="<ALIAS>";
		countBis:int=incrementCounter($CLASS+"."+$METHOD);
		activeRules:boolean=getTrace().activeRules;
	IF  activeRules AND isCongruent
		DO attributesToTrace(rankObjectToTrack, getProps(),alias,count,frequenceMeasure);
ENDRULE

