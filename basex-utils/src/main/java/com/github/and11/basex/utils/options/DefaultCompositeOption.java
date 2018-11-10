package com.github.and11.basex.utils.options;

import com.github.and11.basex.utils.Option;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefaultCompositeOption
        implements CompositeOption {

    /**
     * Composite options (cannot be null).
     */
    private final List<Option> m_options;

    /**
     * Constructor.
     *
     * @param options composite options (can be null or no option specified)
     */
    public DefaultCompositeOption(final Option... options) {
        m_options = new ArrayList<Option>();
        add(options);
    }

    /**
     * Constructor.
     */
    public DefaultCompositeOption() {
        this(new Option[0]);
    }

    /**
     * {@inheritDoc}
     */
    public Option[] getOptions() {
        return OptionUtils.expand(m_options.toArray(new Option[m_options.size()]));
    }

    /**
     * Adds options.
     *
     * @param options composite options to be added (can be null or no options specified)
     * @return itself, for fluent api usage
     */
    public DefaultCompositeOption add(final Option... options) {
        if (options != null) {
            m_options.addAll(Arrays.asList(options));
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("DefaultCompositeOption");
        sb.append("{options=").append(m_options);
        sb.append('}');
        return sb.toString();
    }

}

