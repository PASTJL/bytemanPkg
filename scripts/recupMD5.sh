#!/bin/sh

BYTEMAN_HOME=<bytemanpkg.dirWork>

cd $BYTEMAN_HOME
listFiles="attachMBean.sh  pkgbminstall.sh  pkgbmsubmit.sh  pkgbmunsubmit.sh mybyteman.jar"

listFilesLib=`ls lib/*`

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
export JAVA_HOME
if [[ x$JAVA_HOME == x ]]; then
	
	md5Cmd=`which md5sum 2>/dev/null` 
	if [[ x$md5Cmd == x ]] ; then
		echo " No command md5Sum available , install or set a JVM 1.6 at least"
		exit 1
	else
		ret=
		for file0 in $listFiles; do
			if [[ -f $file ]]; then
				md5=`md5sum $file0 | tr -s ' ' | cut -d ' ' -f1` 
				ret=$ret$file0:$md5";" 
			fi

		done
		for file0 in $listFilesLib; do
			md5=`md5sum $file0 | tr -s ' ' | cut -d ' ' -f1` 
			file0=`echo $file0 | awk '{ n=split($0,array,"/");print array[n] }'` 
			ret=$ret$file0:$md5";" 

		done
		echo $ret
		exit 0
	fi
else
	
	listFiles="attachMBean.sh  pkgbminstall.sh  pkgbmsubmit.sh  pkgbmunsubmit.sh mybyteman.jar"
	listFilesLib=`ls lib/*`
	files=
	for file1 in $listFiles; do
		files=$files$BYTEMAN_HOME/$file1";"
	done
	for file1 in $listFilesLib; do
		files=$files$BYTEMAN_HOME/$file1";"
	done
	
	ret=`$JAVA_HOME/bin/java -classpath  $BYTEMAN_HOME/mybyteman.jar jlp.byteman.helper.tools.GetMD5ForFile $files` 
	echo $ret

fi





