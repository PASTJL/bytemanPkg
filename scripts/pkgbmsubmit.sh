#!/bin/bash
#
# JBoss, Home of Professional Open Source
# Copyright 2009-11, Red Hat and individual contributors
# by the @authors tag. See the copyright.txt in the distribution for a
# full listing of individual contributors.
#
# This is free software; you can redistribute it and/or modify it
# under the terms of the GNU Lesser General Public License as
# published by the Free Software Foundation; either version 2.1 of
# the License, or (at your option) any later version.
#
# This software is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
# Lesser General Public License for more details.
# You should have received a copy of the GNU Lesser General Public
# License along with this software; if not, write to the Free
# Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
# 02110-1301 USA, or see the FSF site: http://www.fsf.org.
#
# @authors Andrew Dinn
#
# shell script which submits a request to the Byteman agent listener
# either to list, install or uninstall rule scripts
#
# usage: bmsubmit [-o outfile] [-p port] [-h host] [-l|-u] [script1 . . . scriptN]
#        bmsubmit [-o outfile] [-p port] [-h host] [-b | -s] bootjar1 . . .
#        bmsubmit [-o outfile] [-p port] [-h host] -c
#        bmsubmit [-o outfile] [-p port] [-h host] -y [prop1[=[value1]]. . .]
#        bmsubmit [-o outfile] [-p port] [-h host] -v
#   -o redirects output from System.out to outfile
#   -p specifies the listener port (default 9091)
#   -h specifies the listener host name (default localhost)
#   -l (default) install rules in script1 . . . scriptN
#      with no scripts list all installed rules
#   -u uninstall rules in script1 . . . scriptN
#      with no scripts uninstall all installed rules
#
#   -b install jar files bootjar1 etc into bootstrap classpath
#
#   -s install jar files bootjar1 etc into system classpath
#
#   -c print the jars that have been added to the system and boot classloaders
#
#   -y with no args list all byteman config system properties
#      with args modifies specified byteman config system properties
#        prop=value sets system property 'prop' to value
#        prop= sets system property 'prop' to an empty string
#        prop unsets system property 'prop'
#
#   -v print the version of the byteman agent and this client 
#
# use BYTEMAN_HOME to locate installed byteman release
# script modified by Jean-Louis PASTUREL to integrate it in bytemanPkg

## Using bytemanPkg to determine the byteman home => property => bytemanpkg.dirWork
BYTEMAN_HOME=<bytemanpkg.dirWork>
export BYTEMAN_HOME
echo "BYTEMAN_HOME=$BYTEMAN_HOME"



# the byteman and byteman-submit jars should be in ${BYTEMAN_HOME}/lib
if [ -r ${BYTEMAN_HOME}/lib/byteman.jar ]; then
    BYTEMAN_JAR=${BYTEMAN_HOME}/lib/byteman.jar
else
    echo "Cannot locate byteman jar"
    exit
fi
if [ -r ${BYTEMAN_HOME}/lib/byteman-submit.jar ]; then
    BYTEMAN_SUBMIT_JAR=${BYTEMAN_HOME}/lib/byteman-submit.jar
else
    echo "Cannot locate byteman-submit jar"
    exit
fi
os=`uname` 
if [[ $os == Linux ]] ; then
	PS="ps -fewww"
else
	PS="ps -fe"		
fi
if [[ x$JAVA_HOME != x ]]; then
	if [[ -L ${JAVA_HOME} ]]; then
		JAVA_HOME=`readlink -e ${JAVA_HOME}`
	fi 
	${JAVA_HOME}/bin/java  -version 2>&1 | grep -iE 'version \"1\.[678]' >/dev/null 2>&1
			if [[ $? != 0 ]]; then
				# Not a correct version
				JAVA_HOME=
			else

			# is there a tools.jar
				if [[ ! -f $JAVA_HOME/lib/tools.jar ]] ; then
					JAVA_HOME=
				fi

			fi
fi  

if [[ x$JAVA_HOME == x ]]; then
		#tryin with running java
		
	 javas=`$PS | grep java | grep -v grep | tr -s ' ' | cut -d ' ' -f8  `
	
	for jv in $javas; do
		if [[ -L $jv ]] ; then
			jv=`readlink -e  $jv` 
		fi
		echo $jv | grep -E '^/' >/dev/null 2>&1
		 if [[ $? == 0 ]]; then
			$jv -server -version 2>&1 | grep -iE 'version \"1\.[678]' >/dev/null 2>&1
			if [[ "$JAVA_HOME" == "" && $? == 0 ]]; then
				
				JAVA_HOME=`echo $jv | awk '{ split($0,tab,"/bin/java");print tab[1]}'`
				if [[ -L ${JAVA_HOME} ]]; then
					JAVA_HOME=`readlink -e ${JAVA_HOME}`
				fi 
				echo running  JAVA_HOME=$JAVA_HOME
				# is there a tools.jar
				if [[ ! -f $JAVA_HOME/lib/tools.jar ]] ; then
					JAVA_HOME=
				fi
			fi
		 fi
		 
	 done

	if [[ x$JAVA_HOME == x  && $OS == Linux ]]; then
		# Recherche a l aide des  PID javas executant
		
		pids=`$PS| grep java | grep -v grep | tr -s ' ' | cut -d ' ' -f2 `
		echo pids = $pids
		for pid in $pids; do
			javaCmd=`strings  /proc/$pid/cmdline |head -1 | tr '\n' ' '`
			if [[ -L $javaCmd ]] ; then
				javaCmd=`readlink -e  $javaCmd` 
			fi
			JAVA_HOMETMP=`echo $javaCmd | awk '{ split($0,tab,"/bin/java");print tab[1]}'`
			if [[ -L ${JAVA_HOMETMP} ]]; then
				JAVA_HOMETMP=`readlink -e ${JAVA_HOMETMP}`
			fi 
			$javaCmd  -version 2>&1 | grep -iE 'version \"1\.[678]' >/dev/null 2>&1
			if [[ x$JAVA_HOME == x && $? == 0 ]]; then
				JAVA_HOME=$JAVA_HOMETMP
				# is there a tools.jar
				if [[ ! -f $JAVA_HOME/lib/tools.jar ]] ; then
					JAVA_HOME=
				fi

			fi
		 
		 
	 done
	fi
	
fi

if [[ x$JAVA_HOME == x ]]; then
	# Etude du java disponible par which
	which java >/dev/null 2>&1
	if [[ $? == 0 ]]; then
		JAVA=`which java` 2>/dev/null
		if [[ -L $JAVA ]] ; then
			JAVA=`readlink -e  $JAVA` 
		fi
		$JAVA -server -version 2>&1 | grep -iE 'version \"1\.[678]' >/dev/null 2>&1
		if [[ $? == 0 ]]; then
			JAVA_HOME=`echo $JAVA | awk '{ split($0,tab,"/bin/java");print tab[1]}'`
			if [[ -L ${JAVA_HOME} ]]; then
				JAVA_HOME=`readlink -e ${JAVA_HOME}`
			fi 
			# is there a tools.jar
				if [[ ! -d $JAVA_HOME/lib/tools.jar ]] ; then
					JAVA_HOME=
				fi
		fi
		
	fi

fi

if [[ x$JAVA_HOME == x ]]; then
	# Recherche par Locate
	javas=`locate bin/java | grep -E "java$"`
	for jv in $javas; do
		if [[ -L $jv ]] ; then
			jv=`readlink -e  $jv` 
		fi
		echo $jv | grep -E '^/' >/dev/null 2>&1
		 if [[ $? == 0 ]]; then
			$jv -server -version 2>&1 | grep -iE 'version \"1\.[678]' >/dev/null 2>&1
			if [[ "$JAVA_HOME" == "" && $? == 0 ]]; then
				JAVA_HOME=`echo $jv | awk '{ split($0,tab,"/bin/java");print tab[1]}'`
				if [[ -L ${JAVA_HOME} ]]; then
					JAVA_HOME=`readlink -e ${JAVA_HOME}`
				fi 
				# is there a tools.jar
				if [[ ! -f $JAVA_HOME/lib/tools.jar ]] ; then
					JAVA_HOME=
				fi

			fi
		 fi
		 
	 done
fi



if [[ x$JAVA_HOME == x ]]; then
	# Recherche par find cibles cas desespere /usr/ /opt/  ~ 
echo searh by find
	javas=`find -L /usr/ /opt/  ~ -maxdepth 4 -type f -name "java" 2>/dev/null`
	for jv in $javas; do
		if [[ -L $jv ]] ; then
			jv=`readlink -e  $jv` 
		fi
		echo $jv | grep -E '^/' >/dev/null 2>&1
		 if [[ $? == 0 ]]; then
			$jv  -version 2>&1 | grep -iE 'version \"1\.[678]' >/dev/null 2>&1
			if [[ "$JAVA_HOME" == "" && $? == 0 ]]; then
				JAVA_HOME=`echo $jv | awk '{ split($0,tab,"/bin/java");print tab[1]}'`
				if [[ -L ${JAVA_HOME} ]]; then
					JAVA_HOME=`readlink -e ${JAVA_HOME}`
				fi 
				# is there a tools.jar
				if [[ ! -f $JAVA_HOME/lib/tools.jar ]] ; then
					JAVA_HOME=
				fi

			fi
		 fi
		 
	 done
fi

# Ultime cas recherche /lib/tools.jar
if [[ x$JAVA_HOME == x ]]; then
	echo recherche par tools.jar
	# Recherche par find cibles cas desespere /usr/ /opt/  ~ 
	toolsjars=`find -L /usr/ /opt/  ~ -maxdepth 4 -type f -name "tools.jar" | grep /lib/tools.jar 2>/dev/null`
	for toolsjar in $toolsjars; do
		echo $toolsjar | grep -E '^/' >/dev/null 2>&1
		 if [[ $? == 0 ]]; then
			JAVA_HOME=`echo $toolsjar | awk '{ split($0,tab,"/lib/tools.jar");print tab[1]}'`
			if [[ -L ${JAVA_HOME} ]]; then
				JAVA_HOME=`readlink -e ${JAVA_HOME}`
			fi 
			$JAVA_HOME/bin/java  -version 2>&1 | grep -iE 'version \"1\.[678]' >/dev/null 2>&1
			if [[ "$JAVA_HOME" == "" && $? == 0 ]]; then
				JAVA_HOME=`echo $toolsjar | awk '{ split($0,tab,"/bin/java");print tab[1]}'`
				# is there a tools.jar
				if [[ ! -f $JAVA_HOME/lib/tools.jar ]] ; then
					JAVA_HOME=
				fi

			fi

		fi

	done
fi
if [[ x$JAVA_HOME == x ]]; then
	echo " no available JVM 1.6+ "
	echo " Set and export a correct JAVA_HOME pointing to a JDK 1.6+. A JDK is mandatory"
	exit 1
fi
export JAVA_HOME
echo JAVA_HOME=$JAVA_HOME
OLD_PATH=$PATH
PATH=$JAVA_HOME/bin:$PATH
export PATH
# we also need a tools jar from JAVA_HOME
if [ -z "$JAVA_HOME" ]; then
     echo "please set JAVA_HOME"
     exit
fi
# on Linux we need to add the tools jar to the path
# this is not currently needed on a Mac
OS=`uname`
if [ ${OS} != "Darwin" ]; then
  if [ -r ${JAVA_HOME}/lib/tools.jar ]; then
      TOOLS_JAR=${JAVA_HOME}/lib/tools.jar
  else
      echo "Cannot locate tools jar"
      exit
  fi
fi


for opt in $BYTEMAN_JAVA_OPTS; do
	${JAVA_HOME}/bin/java -classpath ${BYTEMAN_JAR}:${BYTEMAN_SUBMIT_JAR} org.jboss.byteman.agent.submit.Submit -p <PORT>  -y $opt
done
${JAVA_HOME}/bin/java  -classpath ${BYTEMAN_JAR}:${BYTEMAN_SUBMIT_JAR} org.jboss.byteman.agent.submit.Submit -p <PORT>  -l $BYTEMAN_HOME/bytemanpkg.btm
if [[ $? == 0 ]]; then
	echo "Bravo ! => operation submit rules : bytemanpkg.btm  OK"
else
	echo "Shame on me ! => operation submit rules : bytemanpkg.btm failed"
fi
PATH=$OLD_PATH
export PATH	
