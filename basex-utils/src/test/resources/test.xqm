module namespace _ = "test";
import module namespace db = "http://basex.org/modules/db";

declare function _:export($path as xs:string){
    trace(collection($path), 'dddd ')
};

declare function _:create(){
<root>
    <a></a>
    <b></b>
</root>
};