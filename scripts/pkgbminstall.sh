#!/bin/bash
#
# JBoss, Home of Professional Open Source
# Copyright 2010-11, Red Hat and individual contributors
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
# shell script which can be used to install the Byteman agent into
# a JVM which was started without the agent. This provides an
# alternative to using the -javaagent java command line flag
#
# usage: bminstall [-p port] [-h host] [-b] [-Dname[=value]]* pid
#   pid is the process id of the target JVM
#   -h host selects the host name or address the agent listener binds to
#   -p port selects the port the agent listener binds to
#   -b adds the byteman jar to the bootstrap classpath
#   -s sets an access-all-areas security policy for the Byteman agent code
#   -Dname=value can be used to set system properties whose name starts with "org.jboss.byteman."
#   expects to find a byteman agent jar in BYTEMAN_HOME
#
# use BYTEMAN_HOME to locate installed byteman release
# script modified by Jean-Louis PASTUREL to integrate it in bytemanPkg

## Using bytemanPkg to determine the byteman home => property => bytemanpkg.dirWork
BYTEMAN_HOME=<bytemanpkg.dirWork>
export BYTEMAN_HOME
echo "BYTEMAN_HOME=$BYTEMAN_HOME"

# check that we can find  the byteman jar via BYTEMAN_HOME

# the Install class is in the byteman-install jar
if [ -r ${BYTEMAN_HOME}/lib/byteman.jar ]; then
    BYTEMAN_JAR=${BYTEMAN_HOME}/lib/byteman.jar
else
    echo "Cannot locate mybyteman jar"
    exit
fi
# the Install class is in the byteman-install jar
if [ -r ${BYTEMAN_HOME}/lib/byteman-install.jar ]; then
    BYTEMAN_INSTALL_JAR=${BYTEMAN_HOME}/lib/byteman-install.jar
else
    echo "Cannot locate byteman install jar"
    exit
fi
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
				if [[ -L $jv ]] ; then
					jv=`readlink -e  $jv` 
				fi
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
	echo find  tools.jar
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
# allow for extra java opts via setting BYTEMAN_JAVA_OPTS
# attach class will validate arguments
# setting PID
PID=<PID>
export PID
echo PID=$PID
# Current user
currentUser=`whoami`
javaUser=`ps -f -p $PID | grep $PID | tr -s ' ' | cut -d ' ' -f1`
if [[ "$currentUser" != "$javaUser" ]] ; then
	echo "Humm !! the current user $currentUser is different from the javaUser $javaUser"
else
	echo "All is right!  the current user $currentUser is equal to the javaUser $javaUser"

fi 
BYTEMAN_JAVA_OPTS="<bytemanOpts>"
SYSTEM_PROPS="-Dorg.jboss.byteman.allow.config.updates=true "
for opt in $BYTEMAN_JAVA_OPTS; do
	SYSTEM_PROPS=$SYSTEM_PROPS" -D$opt "
done
${JAVA_HOME}/bin/java -classpath ${BYTEMAN_INSTALL_JAR}:${TOOLS_JAR} org.jboss.byteman.agent.install.Install  -p <PORT> -b -h localhost $SYSTEM_PROPS $PID
# allow for extra java opts via setting BYTEMAN_JAVA_OPTS
# Submit class will validate arguments
BYTEMAN_BOOT="<BYTEMAN_BOOT>"
BYTEMAN_SYS="$BYTEMAN_HOME/mybyteman.jar <BYTEMAN_SYS>"
BYTEMAN_JAVA_OPTS="<bytemanOpts>"
echo BYTEMAN_BOOT=$BYTEMAN_BOOT
echo BYTEMAN_SYS=" $BYTEMAN_SYS"
if [[ x$BYTEMAN_BOOT != x ]] ; then
	${JAVA_HOME}/bin/java  -classpath ${BYTEMAN_JAR}:${BYTEMAN_SUBMIT_JAR} org.jboss.byteman.agent.submit.Submit -p <PORT> -b  $BYTEMAN_BOOT
fi

if [[ x$BYTEMAN_SYS != x ]] ; then
	${JAVA_HOME}/bin/java -classpath ${BYTEMAN_JAR}:${BYTEMAN_SUBMIT_JAR} org.jboss.byteman.agent.submit.Submit -p <PORT> -s  $BYTEMAN_SYS
fi

if [[ $? == 0 ]]; then
	echo "Bravo ! => operation install agent OK"
else
	echo "Shame on me ! => operation install agent failed"
fi


PATH=$OLD_PATH
export PATH	
