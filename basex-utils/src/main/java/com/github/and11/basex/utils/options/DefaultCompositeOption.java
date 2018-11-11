package com.github.and11.basex.utils.options;

import com.github.and11.basex.utils.Option;
import com.github.and11.basex.utils.OptionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefaultCompositeOption
        implements CompositeOption {

    private final List<Option> options;

    public DefaultCompositeOption(final Option... options) {
        this.options = new ArrayList<Option>();
        add(options);
    }

    public DefaultCompositeOption() {
        this( new Option[0] );
    }

    public Option[] getOptions() {
        return OptionUtils.expand(options.toArray(new Option[options.size()]));
    }

    public DefaultCompositeOption add(final Option... options) {
        if (options != null) {
            this.options.addAll(Arrays.asList(options));
        }
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("DefaultCompositeOption");
        sb.append("{options=").append(options);
        sb.append('}');
        return sb.toString();
    }

}

