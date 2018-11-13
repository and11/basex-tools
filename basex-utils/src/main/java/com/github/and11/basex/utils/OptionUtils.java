/*
 * Copyright 2008 Alin Dreghiciu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.and11.basex.utils;

import com.github.and11.basex.utils.Option;
import com.github.and11.basex.utils.options.CompositeOption;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OptionUtils
{

    private OptionUtils()
    {
        // utility class
    }

    public static Option[] expand(final Option... options )
    {
        final List<Option> expanded = new ArrayList<>();
        if( options != null )
        {
            for( Option option : options )
            {
                if( option != null )
                {
                    if( option instanceof CompositeOption )
                    {
                        expanded.addAll( Arrays.asList( ( (CompositeOption) option ).getOptions() ) );
                    }
                    else
                    {
                        expanded.add( option );
                    }
                }
            }
        }
        return expanded.toArray( new Option[expanded.size()] );
    }

    public static Option[] combine( final Option[] options1,
                                    final Option... options2 )
    {
        int size1 = 0;
        if( options1 != null && options1.length > 0 )
        {
            size1 += options1.length;
        }
        int size2 = 0;
        if( options2 != null && options2.length > 0 )
        {
            size2 += options2.length;
        }
        final Option[] combined = new Option[size1 + size2];
        if( size1 > 0 )
        {
            System.arraycopy( options1, 0, combined, 0, size1 );
        }
        if( size2 > 0 )
        {
            System.arraycopy( options2, 0, combined, size1, size2 );
        }
        return combined;
    }

    @SuppressWarnings( "unchecked" )
    public static <T extends Option> T[] filter( final Class<T> optionType,
                                                 final Option... options )
    {
        final List<T> filtered = new ArrayList<T>();
        for( Option option : expand( options ) )
        {
            if( optionType.isAssignableFrom( option.getClass() ) )
            {
                filtered.add( (T) option );
            }
        }
        final T[] result = (T[]) Array.newInstance( optionType, filtered.size() );
        return filtered.toArray( result );
    }

    public static Option[] remove( final Class<? extends Option> optionType,
                                   final Option... options )
    {
        final List<Option> filtered = new ArrayList<Option>();
        for( Option option : expand( options ) )
        {
            if( !optionType.isAssignableFrom( option.getClass() ) )
            {
                filtered.add( option );
            }
        }
        return filtered.toArray( new Option[filtered.size()] );
    }

}