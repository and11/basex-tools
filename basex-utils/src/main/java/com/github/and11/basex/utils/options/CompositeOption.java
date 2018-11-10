package com.github.and11.basex.utils.options;

import com.github.and11.basex.utils.Option;

public interface CompositeOption extends Option {
    Option[] getOptions();
}
