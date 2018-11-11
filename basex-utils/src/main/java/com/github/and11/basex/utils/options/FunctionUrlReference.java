package com.github.and11.basex.utils.options;

public interface FunctionUrlReference extends UrlReference {
    FunctionUrlReference name(String name);

    FunctionUrlReference mode(String mode);

    FunctionUrlReference namespace(String namespace);

    FunctionUrlReference arguments(String... arguments);
}
