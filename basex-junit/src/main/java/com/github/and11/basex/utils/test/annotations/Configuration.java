package com.github.and11.basex.utils.test.annotations;

import com.github.and11.basex.utils.options.CompositeOption;
import com.github.and11.basex.utils.options.DefaultCompositeOption;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.METHOD, ElementType.TYPE } )

public @interface Configuration {
    Class<? extends CompositeOption>[] extend() default { DefaultCompositeOption.class };
}
