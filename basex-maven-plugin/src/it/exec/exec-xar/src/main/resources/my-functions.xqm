module namespace  _ = "urn:test.functions" ;
import module namespace jfunc = "java:my.test.MyTestClass";

(: exec-xar :)
declare function _:test(){
    jfunc:getTestString()
};
