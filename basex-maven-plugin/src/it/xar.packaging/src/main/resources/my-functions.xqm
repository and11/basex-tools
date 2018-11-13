module namespace  _ = "urn:test.functions" ;
import module namespace jfunc = "java:my.test.MyTestClass";

(: xar.packaging :)
declare function _:test(){
    jfunc:getTestString()
};
