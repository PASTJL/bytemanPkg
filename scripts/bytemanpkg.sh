######## These two environnement Variables must be adapted #########
###
JRE_HOME=/opt/jdk1.7.0_45/jre
## workspace directory must be created before launching thi script
workspace=/opt/eclipse/workspace/workspaceBM
#######################################################

if [[ ! -d $workspace ]]; then
	echo "the workspace : $workspace doesn't exist. Please create the directory or adapt the variable"
	exit 1
fi

#root=/opt/bytemanPkg
root=`dirname $0`/..
echo root=$root
CLASSPATH=$root/bytemanPkg.jar
CLASSPATH=$CLASSPATH:$root/config
CLASSPATH=$CLASSPATH:$root/lib/jsch-0.1.49.jar
CLASSPATH=$CLASSPATH:$root/lib/antlr-runtime-4.1.jar
CLASSPATH=$CLASSPATH:$JRE_HOME/lib/jfxrt.jar
CLASSPATH=$CLASSPATH:$root/lib/bytemancheck.jar
CLASSPATH=$CLASSPATH:$root/lib/jfxmessagebox-1.1.0.jar
CLASSPATH=$CLASSPATH:$root/lib/scaChart.jar
$JRE_HOME/bin/java  -cp $CLASSPATH -Droot=$root -Dworkspace=$workspace -Dconfig.file=$root/config/scaChart.properties -Xms128M -Xmx128M jlp.byteman.packager.Main $*
