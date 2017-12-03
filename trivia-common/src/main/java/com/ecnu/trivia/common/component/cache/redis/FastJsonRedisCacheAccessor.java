/** Created by Jack Chen at 11/19/2014 */
package com.ecnu.trivia.common.component.cache.redis;

import com.ecnu.trivia.common.component.serialize.FastJsonSerializer;
import com.ecnu.trivia.common.component.serialize.Serializer;

/** @author Jack Chen */
public abstract class FastJsonRedisCacheAccessor extends AbstractRedisCacheAccessor
{
    @Override
    protected Serializer supportJsonSerializer() {
        return new FastJsonSerializer();
    }
}
