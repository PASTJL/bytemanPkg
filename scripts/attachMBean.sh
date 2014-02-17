# Determiner une JVM 1.6+ sur la machine
BYTEMAN_HOME=<bytemanpkg.dirWork>
export BYTEMAN_HOME
echo "BYTEMAN_HOME=$BYTEMAN_HOME"


OS=`uname`
case  $# in 
	0) 
		echo "usage :"
		echo "$0 <PID> <OPER>"
		echo "or"
		echo  "$0 <OPER>"
		exit 1;;
	1) 
		echo $1 | grep -E '[0-9]+'
			if [[ $? == 0 ]]; then
				echo "usage :" 
				echo "$0 <PID> <OPER>"
				echo "or"
				echo  "$0 <OPER>"
				exit 1
			fi
		;;
	2) 
		echo $1 | grep -E '[0-9]+'
			if [[ $? != 0 ]]; then
				echo "usage :"
				echo "$0 <PID> <OPER>"
				echo "or"
				echo  "$0 <OPER>"
				exit 1
			fi
		;;
	
esac



if [[ $OS == Linux ]]; then
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
			$javaCmd  -version 2>&1 | grep -iE 'version \"1\.[678]' >/dev/null 2>&1
			if [[ x$JAVA_HOME == x && $? == 0 ]]; then
				JAVA_HOME=$JAVA_HOMETMP
				if [[ -L ${JAVA_HOME} ]]; then
					JAVA_HOME=`readlink -e ${JAVA_HOME}`
				fi 
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
			JAVA_HOME=`echo $JAVA| awk '{ split($0,tab,"/bin/java");print tab[1]}'`
			if [[ -L ${JAVA_HOME} ]]; then
				JAVA_HOME=`readlink -e ${JAVA_HOME}`
			fi 
			# is there a tools.jar
				if [[ ! -f $JAVA_HOME/lib/tools.jar ]] ; then
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
			$JAVA_HOME/bin/java  -version 2>&1 | grep -iE 'version \"1\.[678]' >/dev/null 2>&1
			if [[ "$JAVA_HOME" == "" && $? == 0 ]]; then
				JAVA_HOME=`echo $toolsjar | awk '{ split($0,tab,"/bin/java");print tab[1]}'`
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
	echo " no available JVM 1.6+ "
	echo " Set and export a correct JAVA_HOME pointing to a JDK 1.6+. A JDK is mandatory"
	exit 1
fi
export JAVA_HOME
echo JAVA_HOME=$JAVA_HOME
OLD_PATH=$PATH
PATH=$JAVA_HOME/bin:$PATH
export PATH
## Test if first argument is an integer (PID)
echo $1 | grep -E '[0-9]+'
if [[ $? == 0 ]]; then
	OPER=$2
	PID=`echo $1 | grep -oE '[0-9]+'| tr -s ' '`
else
	OPER=$1
	PID=<PID>
fi

PID=$PID
echo PID=$PID
AGENT_PATH=$BYTEMAN_HOME/mybyteman.jar
echo AGENT_PATH=$AGENT_PATH
PROVIDER=HotSpot
${JAVA_HOME}/bin/java  -version 2>&1 | grep -i "IBM" | grep "VM"  >/dev/null 2>&1
	if [[ $? == 0 ]]; then
		PROVIDER=IBM
	fi
# Treat case of IBM JDK
if [[ $PROVIDER == IBM ]]; then
	echo "launching ${JAVA_HOME}/bin/java -DPROVIDER=IBM -classpath ${AGENT_PATH}:${JAVA_HOME}/lib/tools.jar jlp.byteman.helper.tools.AttachJMX $PID $OPER"
	${JAVA_HOME}/bin/java -DPROVIDER=IBM -classpath ${AGENT_PATH}:${JAVA_HOME}/lib/tools.jar jlp.byteman.helper.tools.AttachJMX $PID $OPER
else
	${JAVA_HOME}/bin/java -DPROVIDER=HotSpot -classpath ${AGENT_PATH}:${JAVA_HOME}/lib/tools.jar jlp.byteman.helper.tools.AttachJMX $PID $OPER
fi
PATH=$OLD_PATH
export PATH	
