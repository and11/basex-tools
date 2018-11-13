module namespace  _ = "urn:test.functions" ;
import module namespace jfunc = "java:my.test.MyTestClass";

(: basex-xar :)
declare function _:test(){
    jfunc:getTestString()
};
