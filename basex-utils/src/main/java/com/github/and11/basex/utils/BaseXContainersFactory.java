package com.github.and11.basex.utils;

import com.github.and11.basex.utils.options.InitializationOption;

public interface BaseXContainersFactory {
    BaseXContainer createContainer(InitializationOption... options) throws BaseXContainer.BaseXContainerException;
}
