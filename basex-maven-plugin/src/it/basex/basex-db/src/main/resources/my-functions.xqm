module namespace  _ = "http://some/namespace" ;
(:import module namespace jfunc = "java:my.test.MyTestClass";:)

declare function _:test(){
    <some>data</some>
};

(: basex-db :)
declare function _:getFromDatas($path){
    collection($path)/*
};
