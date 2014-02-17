/*Copyright 2013 Jean-Louis PASTUREL 
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*  limitations under the License.
*/
package jlp.byteman.helper;

import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.WeakHashMap;

import org.jboss.byteman.rule.Rule;



public class SqlStatementsDuration extends MyHelper {
	private static Map<Statement, String> statementSql =  java.util.Collections.synchronizedMap(new WeakHashMap<Statement, String>());

	private static long rank = 0;
	private  static  String sep ;
	private static String title;

	public  String getTitle() {
		return title;
	}

	protected SqlStatementsDuration(Rule rule) {
		super(rule);
		sep= getProps().getProperty("bytemanpkg.csvSep", ";");
		title="date"+sep+"typeStatement"+sep+"SQL"+sep+"rank"+sep+"duree(ms)"+sep;
	}

	public void fillStatementSql(Statement statement, Object[] tabObj) {
		synchronized (SqlStatementsDuration.class) {
		
			if( null == tabObj || tabObj.length<2){
				return;
			}
			else if (null != statement && !statementSql.containsKey(statement) && null != tabObj[1]
					&& (tabObj[1].toString().trim().length() > 1))
				statementSql.put(statement,tabObj[1].toString());

		}
	}

	public String sqlDurationToTrace(Statement statement,
			String typeSql, int longMaxReq, boolean stripBeforeWhereBool,
			boolean boolBindParameters, long duree) {
		synchronized (SqlStatementsDuration.class){  
			
		// Sauver le sql
		String sqlStr = "";

		String retour = sep+sep+sep+sep;
//		System.out.println("longMaxReq="+longMaxReq);
//		System.out.println("stripBeforeWhereBool="+stripBeforeWhereBool);
//		System.out.println("boolBindParameters="+boolBindParameters);
		
	
			if (statementSql.containsKey(statement)) {
				
				
				boolean vraiPst=statement instanceof java.sql.PreparedStatement;
				
				
				if (vraiPst && boolBindParameters ) {
					// TODO
					// sqlStr=statementSql.get(statement);
					//sqlStr = statement.toString();
					sqlStr =replaceBindingParam((PreparedStatement) statement);
					
				} else {
					sqlStr = statementSql.get(statement);
					
				}

			} else {
				sqlStr = "No sql string available";

			}

			rank++;

			if (stripBeforeWhereBool) {
				sqlStr = stripAfterWhere(sqlStr);
			}

			// String
			// sql=stripAfterWhere((String)statementSql.get(statement));
			if (longMaxReq > 0 && sqlStr.length() > longMaxReq) {
				sqlStr = sqlStr.substring(0, longMaxReq);
			}
			// Suppression des sauts de lignes eventuels
			sqlStr = sqlStr.replaceAll("\\r*\\n+\\r*", " | ");
			retour = new StringBuilder()
			.append(typeSql).append(";")
					// .append(thisJoinPoint.getThis().getClass().getSimpleName()).append(" : ")
					.append(sqlStr).append(sep).append(Long.toString(rank))
					.append(sep).append(duree).append(";").toString();

			
			return retour;
		}

		
	}

	/**
	 * To group sensibly and to avoid recording sensitive data, I don't record
	 * the where clause (only used for dynamic SQL since parameters aren't
	 * included in prepared statements)
	 * 
	 * @return subset of passed SQL up to the where clause
	 */
	public final  String stripAfterWhere(String sql) {
		for (int i = 0; i < sql.length() - 4; i++) {
			if (sql.charAt(i) == 'w' || sql.charAt(i) == 'W') {
				if (sql.substring(i + 1, i + 5).equalsIgnoreCase("here")) {
					sql = sql.substring(0, i);
				}
			}
		}
		return sql;
	}

	public final  String replaceBindingParam(PreparedStatement statement) {
		String sqlStr=statementSql.get(statement);
		ParameterMetaData parammtdt=null;
		int nbParams=0;
		try {
			 parammtdt=statement.getParameterMetaData();
			 nbParams= parammtdt.getParameterCount() ;
			 if(nbParams==0){
				 return sqlStr;
			 }
		} catch (SQLException e) {
			 return sqlStr;
		}
		// recherche de la chaine =\s+? dans sqlStr
		
		
		
		
		return statement.toString();
	}
}
