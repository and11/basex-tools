import module namespace mns = '${namespace}';

declare namespace file = "http://expath.org/ns/file";

declare variable $targetFile external;
declare variable $outputMethod external;

<#list arguments as arg>
declare variable $${arg.name} external;
</#list>


	file:write(
			$targetFile,
			mns:${function}(${functionArgsStr}),
	<output:serialization-parameters>
		<output:method value="{$outputMethod}"/>
		<output:omit-xml-declaration value="no"/>
		<output:indents value="4"/>
		<output:indent value="yes"/>

	</output:serialization-parameters>
	)
