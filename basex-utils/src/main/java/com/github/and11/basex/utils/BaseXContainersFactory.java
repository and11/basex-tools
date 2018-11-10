package com.github.and11.basex.utils;

public interface BaseXContainersFactory {
    BaseXContainer createContainer(Option[] options) throws BaseXContainer.BaseXContainerException;
}
