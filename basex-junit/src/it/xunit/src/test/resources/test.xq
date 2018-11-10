module namespace _ = '_';

import module namespace x = "test";

declare %unit:test function _:testxx(){
    unit:fail(concat('test1 ', x:export()))
};