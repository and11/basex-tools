module namespace  _ = "urn:test.functions" ;
import module namespace jfunc = "java:my.test.MyTestClass";

declare namespace common = "http://my-schemas/common/v1";

declare function _:find-resources(){

    for $element in collection()//*[@common:resource]
        let $id := data($element/@common:resource)
        let $description := jfunc:createDescription($id)

        return <resource id="{$id}"><description>{ $description }</description></resource>
};
