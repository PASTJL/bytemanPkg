<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
	<META HTTP-EQUIV="CONTENT-TYPE" CONTENT="text/html; charset=windows-1252">
	<TITLE></TITLE>
	<META NAME="GENERATOR" CONTENT="LibreOffice 4.1.1.2 (Windows)">
	<META NAME="AUTHOR" CONTENT="JLP ">
	<META NAME="CREATED" CONTENT="20131011;8384800">
	<META NAME="CHANGED" CONTENT="20140123;91613620000000">
	<META NAME="CHANGEDBY" CONTENT="JLP ">
	<META NAME="CHANGEDBY" CONTENT="JLP ">
	<META NAME="CHANGEDBY" CONTENT="JLP ">
	<META NAME="CHANGEDBY" CONTENT="JLP ">
</HEAD>
<BODY LANG="fr-FR" DIR="LTR">
<P STYLE="margin-bottom: 0cm"><BR>
</P>
<H1><B>What is BytemanPkg&nbsp;?</B></H1>
<P LANG="en-GB" STYLE="margin-bottom: 0cm"><B>BytemanPkg</B> is a
front-end GUI for Byteman, that constructs a Byteman Rules script
file, with the help of template Rules and custom Helpers. All is
packaged in a unique jar ( byteman agent, properties, script rule,
and mandatory helpers), and uploaded to the target servers.</P>
<P LANG="en-GB">The running JVM target needs to be instrumented with
a javaagent, or with a dynamic install/submit  and some other tips
depending on the running WAS.</P>
<P LANG="en-GB">The tested WAS servers are ( need at leat a JVM 1.6) 
:</P>
<UL>
	<LI><P LANG="en-GB"><B>JBOSS 7.2 / JBOSS -EAP-6.2 (*1)</B></P>
	<LI><P LANG="en-GB"><B>JOnAS 5.2.3</B></P>
	<LI><P LANG="en-GB"><B>TOMCAT 7.0.47 / 6.0.37</B></P>
	<LI><P LANG="en-GB"><B>WebSphere 8.5.5 (*2)</B></P>
	<LI><P LANG="en-GB"><B>WebLogic 12c (*3)</B></P>
	<LI><P LANG="en-GB"><B>GlassFish 4.0 ( JEE7) (*4) needs JVM 1.7.</B></P>
	<LI><P LANG="en-GB"><B>Eclipse/Jetty 9.1 </B>
	</P>
</UL>
<P LANG="en-GB"><BR><BR>
</P>
<P LANG="en-GB" STYLE="font-weight: normal">I have only tested these
versions of these WAS, it may run also with others versions with a
JVM 1.6+.</P>
<P LANG="en-GB"><B>(*1) JBOSS and JBOSS-EAP are  RedHat trademarks</B></P>
<P><SPAN LANG="en-GB"><B>(*2) IBM&reg; and  WebSphere&reg; are  IBM
</B></SPAN><SPAN LANG="en-GB"><B>t</B></SPAN><SPAN LANG="en-GB"><B>rademarks</B></SPAN></P>
<P><SPAN LANG="en-GB"><B>(*3) Oracle&reg; WebLogic Server is an
Oracle </B></SPAN><SPAN LANG="en-GB"><B>t</B></SPAN><SPAN LANG="en-GB"><B>rademark</B></SPAN></P>
<P LANG="en-GB"><B>(*4) Glassfish has a dual license
<A HREF="https://glassfish.java.net/public/CDDL+GPL.htm">https://glassfish.java.net/public/CDDL+GPL.htm</A></B></P>
<P LANG="en-GB"><BR><BR>
</P>
<P LANG="en-GB"><SPAN STYLE="font-weight: normal">I will test </SPAN><B>Wildfly
( New Open Source of JBOSS) </B><SPAN STYLE="font-weight: normal">at
the first stable  version ( RC or GA).</SPAN><SPAN STYLE="font-weight: normal">
</SPAN>
</P>
<P LANG="en-GB"><BR><BR>
</P>
<P LANG="en-GB">For every of  these  WASs, the configugation  wil be
detailed more further  in this document  
</P>
<P LANG="en-GB" STYLE="margin-left: 0.5cm"><BR><BR>
</P>
<P LANG="en-GB" STYLE="margin-left: 0.5cm">The product <B>BytemanPkg</B>
is a kind of workbench that :</P>
<UL>
	<LI><P LANG="en-GB">is organized in projects</P>
	<LI><P LANG="en-GB">has a library of Rule templates that can be
	extended</P>
	<LI><P LANG="en-GB">has for each Rule, if necessary, a custom helper
	( that extends <B>MyHelper</B>)   or by default <B>MyHelper</B> that
	extends the build-in helper : <B>org.jboss.byteman.rule.helper.Helper</B></P>
	<LI><P LANG="en-GB">generates a file script of Byteman Rules (
	byteman.btm)</P>
	<LI><P LANG="en-GB">generates a properties file with general
	parameters and custom parameters for Rule/Helper</P>
	<LI><P LANG="en-GB">packages the custom javagent <B>mybyteman.jar</B>
	in a unique jar</P>
	<LI><P LANG="en-GB">uploads optionally the javaagent</P>
	<LI><P LANG="en-GB">the javagent can be monitored : start/stop
	tracing ( using IF Rule statement) , flushing the trace outputs
	(Custom Helper) by a JMX/RMI connection (JConsole or a script with
	the Attach API).</P>
	<LI><P><FONT COLOR="#c5000b"><SPAN LANG="en-GB"><B>(new V1.1)</B></SPAN></FONT><SPAN LANG="en-GB">
	dynamic submission / monitoring in the &ldquo;</SPAN><SPAN LANG="en-GB"><B>Remote
	Actions&rdquo; </B></SPAN><SPAN LANG="en-GB">Tab.</SPAN><FONT COLOR="#c5000b"><SPAN LANG="en-GB"><B>
	</B></SPAN></FONT><FONT COLOR="#c5000b"><SPAN LANG="en-GB"><B>Only
	tested on Linux, not tested with others *nix or cygwin.</B></SPAN></FONT></P>
	<LI><P><FONT COLOR="#c5000b"><SPAN LANG="en-GB"><B>(new V1.</B></SPAN></FONT><FONT COLOR="#c5000b"><SPAN LANG="en-GB"><B>2.0</B></SPAN></FONT><FONT COLOR="#c5000b"><SPAN LANG="en-GB"><B>)</B></SPAN></FONT><FONT COLOR="#000000"><SPAN LANG="en-GB"><B>
	</B></SPAN></FONT><FONT COLOR="#000000"><SPAN LANG="en-GB"><SPAN STYLE="font-weight: normal">Adding
	charting based on JfreeChart</SPAN></SPAN></FONT></P>
	<LI><P><FONT COLOR="#c5000b"><SPAN LANG="en-GB"><B>V</B></SPAN></FONT><FONT COLOR="#c5000b"><SPAN LANG="en-GB"><B>ersion
	1.2.1 .</B></SPAN></FONT><FONT COLOR="#c5000b"><SPAN LANG="en-GB"><SPAN STYLE="font-weight: normal">
	I</SPAN></SPAN></FONT><FONT COLOR="#000000"><SPAN LANG="en-GB"><SPAN STYLE="font-weight: normal">mproving
	remote submission with sudo/su on Jsch, correcting bugs with
	multitheading in Helpers. </SPAN></SPAN></FONT><FONT COLOR="#000000"><SPAN LANG="en-GB"><SPAN STYLE="font-weight: normal">Cha</SPAN></SPAN></FONT><FONT COLOR="#000000"><SPAN LANG="en-GB"><SPAN STYLE="font-weight: normal">r</SPAN></SPAN></FONT><FONT COLOR="#000000"><SPAN LANG="en-GB"><SPAN STYLE="font-weight: normal">ting
	version 1.1.0</SPAN></SPAN></FONT><FONT COLOR="#000000"><SPAN LANG="en-GB"><SPAN STYLE="font-weight: normal">.
	Improving this documentation. </SPAN></SPAN></FONT>
	</P>
	<LI><P><FONT COLOR="#ff0000"><SPAN LANG="en-GB"><B>Version 1.2.2</B></SPAN></FONT><FONT COLOR="#000000"><SPAN LANG="en-GB"><SPAN STYLE="font-weight: normal">
	Integrate</SPAN></SPAN></FONT><FONT COLOR="#000000"><SPAN LANG="en-GB"><SPAN STYLE="font-weight: normal">s</SPAN></SPAN></FONT><FONT COLOR="#000000"><SPAN LANG="en-GB"><SPAN STYLE="font-weight: normal">
	JDK IBM wit</SPAN></SPAN></FONT><FONT COLOR="#000000"><SPAN LANG="en-GB"><SPAN STYLE="font-weight: normal">h</SPAN></SPAN></FONT><FONT COLOR="#000000"><SPAN LANG="en-GB"><SPAN STYLE="font-weight: normal">
	javagent and JMX monitoring ( local and remote)</SPAN></SPAN></FONT></P>
	<LI><P><FONT COLOR="#ff0000"><SPAN LANG="en-GB"><B>Version 1.2.</B></SPAN></FONT><FONT COLOR="#ff0000"><SPAN LANG="en-GB"><B>4.1
	</B></SPAN></FONT><FONT COLOR="#000000"><SPAN LANG="en-GB"><SPAN STYLE="font-weight: normal">Integrate</SPAN></SPAN></FONT><FONT COLOR="#000000"><SPAN LANG="en-GB"><SPAN STYLE="font-weight: normal">s</SPAN></SPAN></FONT><FONT COLOR="#000000"><SPAN LANG="en-GB"><SPAN STYLE="font-weight: normal">
	</SPAN></SPAN></FONT><FONT COLOR="#000000"><SPAN LANG="en-GB"><SPAN STYLE="font-weight: normal">Byteman
	2.1.4.1. </SPAN></SPAN></FONT><FONT COLOR="#000000"><SPAN LANG="en-GB"><SPAN STYLE="font-weight: normal">Improving
	this documentation.</SPAN></SPAN></FONT><FONT COLOR="#000000"><SPAN LANG="en-GB"><SPAN STYLE="font-weight: normal">Tested
	with more WAS and OpenJDK 8.</SPAN></SPAN></FONT></P>
	<LI><P><FONT COLOR="#ff0000"><SPAN LANG="en-GB"><B>Version 1.2.</B></SPAN></FONT><FONT COLOR="#ff0000"><SPAN LANG="en-GB"><B>4.</B></SPAN></FONT><FONT COLOR="#ff0000"><SPAN LANG="en-GB"><B>2
	</B></SPAN></FONT><FONT COLOR="#ff0000"><SPAN LANG="en-GB"><B> </B></SPAN></FONT><FONT COLOR="#000000"><SPAN LANG="en-GB"><SPAN STYLE="font-weight: normal">Bug
	Dynamic submission for System Properties ( Change from
	pkgbmsubmit.sh to pkgbminstall.sh)</SPAN></SPAN></FONT></P>
	<LI><P LANG="en-GB"><FONT COLOR="#ff0000"><B>Version 1.2.</B></FONT><FONT COLOR="#ff0000"><B>5</B></FONT><FONT COLOR="#ff0000"><B>
	</B></FONT><FONT COLOR="#000000"><B> </B></FONT><FONT COLOR="#000000"><SPAN STYLE="font-weight: normal">Correcting
	a Byteman when typeReturn is before a method name. Adding -p &lt;PORT&gt;
	to </SPAN></FONT><FONT COLOR="#000000"><SPAN STYLE="font-weight: normal">pkgbmsubmit.sh
	</SPAN></FONT><FONT COLOR="#000000"><SPAN STYLE="font-weight: normal">and
	</SPAN></FONT><FONT COLOR="#000000"><SPAN STYLE="font-weight: normal">
	</SPAN></FONT><FONT COLOR="#000000"><SPAN STYLE="font-weight: normal">
	</SPAN></FONT><FONT COLOR="#000000"><SPAN STYLE="font-weight: normal">pkgb</SPAN></FONT><FONT COLOR="#000000"><SPAN STYLE="font-weight: normal">un</SPAN></FONT><FONT COLOR="#000000"><SPAN STYLE="font-weight: normal">msubmit.sh</SPAN></FONT><FONT COLOR="#000000"><SPAN STYLE="font-weight: normal">scripts</SPAN></FONT></P>
	<LI><P LANG="en-GB" ALIGN=LEFT STYLE="margin-bottom: 0cm; border: none; padding: 0cm; font-weight: normal">
	<FONT COLOR="#000000"><FONT COLOR="#ff0000"><B>Version 1.2.</B></FONT><FONT COLOR="#ff0000"><B>6
	</B></FONT><FONT COLOR="#ff0000"><B> </B></FONT>New ScaChart v 1.1.1
	( improving scale handling in charts)</FONT></P>
</UL>
<P LANG="en-GB" ALIGN=LEFT STYLE="margin-bottom: 0cm; border: none; padding: 0cm; font-weight: normal">
<BR>
</P>
<P LANG="en-GB" STYLE="margin-left: 0.5cm">The tool <B>BytemanPkg</B>
is developed in Java 1.7 ( use of JFX for GUI, Oracle JDK 7 needed 
for JFX, JFX will be certainly introduced with OpenJDK 8 ), but the
byte code generated for the agent and helpers is a byte code targeted
for JMV 1.6. So the JVM needed for <B>BytemanPkg</B> is a JVM 1.7,
and to run javaagent (or submiting by Attach API)  on  the target
WAS,  is a JVM 1.6+.</P>
<P LANG="en-GB" STYLE="margin-left: 0.5cm"><BR><BR>
</P>
<P LANG="en-GB" STYLE="margin-left: 0.5cm"><SPAN STYLE="font-style: normal"><B>Nota
: </B></SPAN>I have tested also with OpenJDK 8 and OpenJFX 8 ( the
Two projects are separated), and it runs also correctly. 
</P>
<P LANG="en-GB" STYLE="margin-left: 0.5cm"><A NAME="result_box2"></A>The
2 projects must be built and so after  downloading a bunch of rpms on
my Fedora 19 desktop, I <SPAN LANG="en">succeeded</SPAN>.</P>
<P LANG="en-GB" STYLE="margin-left: 0.5cm">The how-tos are in the 2
Urls below:</P>
<P LANG="en-GB" STYLE="margin-left: 0.5cm">OpenJDK =&gt;
<A HREF="http://hg.openjdk.java.net/jdk8/jdk8/raw-file/tip/README-builds.html#hg">http://hg.openjdk.java.net/jdk8/jdk8/raw-file/tip/README-builds.html#hg</A></P>
<P LANG="en-GB" STYLE="margin-left: 0.5cm">OpenJFX =&gt;
<A HREF="https://wiki.openjdk.java.net/display/OpenJFX/Building+OpenJFX">https://wiki.openjdk.java.net/display/OpenJFX/Building+OpenJFX</A></P>
<P LANG="en-GB" STYLE="margin-left: 0.5cm"><BR><BR>
</P>
<H1>Installing / Building</H1>
<P>The whole branch master contains a binary archive <B>bytemanPkg.zip</B>
that you can install directly.You need to download the entire zip
Github archive.</P>
<P>To build, you need Ant or Ant integrated in eclipse KEPLER/JUNO 
</P>
</BODY>
</HTML>