module namespace _ = '_';

import module namespace x = "urn:test.functions";

declare %unit:test function _:testxx(){
    let $resources := x:find-resources()
    return unit:assert-equals($resources/description/data(), "opId description")
};