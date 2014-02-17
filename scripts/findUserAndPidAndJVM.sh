# Find traget PID, user and JVM
BYTEMAN_HOME=<bytemanpkg.dirWork>
export BYTEMAN_HOME

PID=<PID>
JAVA_CMD=`cat /proc/$PID/cmdline| strings | head -1` 
# case of symbolic links
if [[ -L $JAVA_CMD ]] ; then
	JAVA_CMD=`readlink -e  $JAVA_CMD` 
fi

JAVA_HOME=`echo $JAVA_CMD | awk '{split($0,tab,"/bin/java");print tab[1]}'` 
if [[ -L ${JAVA_HOME} ]]; then
	JAVA_HOME=`readlink -e ${JAVA_HOME}`
fi 
indexJre=`echo $JAVA_HOME | awk '{ print index($0, "/jre")}'`
if [[ $indexJre == 0 ]]; then
	JDK_HOME=$JAVA_HOME
else
	JDK_HOME=`echo $JAVA_HOME | awk '{split($0,tab,"/jre");print tab[1]}'` 
fi

VERSION=`${JAVA_CMD}  -version 2>&1 | grep -ioE 'version \"1\.[678]'| grep -ioE '1\.[678]'` 
PROVIDER=
${JAVA_CMD} -version 2>&1 | grep -Ei '(HotSpot|OpenJDK)'    >/dev/null 2>&1
if [[ $? == 0 ]]; then
	PROVIDER=HotSpot
else 
	${JAVA_CMD}  -version 2>&1 | grep -i "IBM" | grep "VM"  >/dev/null 2>&1
	if [[ $? == 0 ]]; then
		PROVIDER=IBM
	fi
fi
${JAVA_CMD} -version 2>&1 | grep -iE 'version \"1\.[678]' >/dev/null 2>&1
			if [[ $? != 0 ]]; then
				# Not a correct version
				JDK_HOME=
			else

			# is there a tools.jar
				if [[ ! -f $JDK_HOME/lib/tools.jar ]] ; then
					JDK_HOME=
				fi

			fi

USER=`ps -fe | grep $PID | grep -v grep |tr -s ' ' | cut -d ' ' -f1 2>&1` 
echo USER=$USER";"PID=$PID";"JDK_HOME=$JDK_HOME";"VERSION=$VERSION";"PROVIDER=$PROVIDER";"
exit 0

