######## These some environnement Variables must be adapted #########
###
JDK_HOME=/opt/openJDK_8_X64_86

## workspace directory must be created before launching thi script
workspace=/opt/eclipse/workspace/workspaceBM

# Path to jfxrt.jar archive  not necessary  with JDK8-ea  downloaded from https://jdk8.java.net/download.html (jfxrt.jar is embedded)
# but mandatory with OpenJDK because it is a separated project => OpenJFX  built from this document : https://wiki.openjdk.java.net/display/OpenJFX/Building+OpenJFX
JFXRT_HOME=/opt/rt/build/linux-sdk/rt/lib/ext

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
CLASSPATH=$CLASSPATH:$JFXRT_HOME/jfxrt.jar
CLASSPATH=$CLASSPATH:$root/lib/antlr-runtime-4.1.jar
CLASSPATH=$CLASSPATH:$root/lib/bytemancheck.jar
CLASSPATH=$CLASSPATH:$root/lib/jfxmessagebox-1.1.0.jar
CLASSPATH=$CLASSPATH:$root/lib/scaChart.jar
$JDK_HOME/bin/java   -classpath $CLASSPATH -Droot=$root -Dworkspace=$workspace -Dconfig.file=$root/config/scaChart.properties -Xms128M -Xmx128M jlp.byteman.packager.Main $*
