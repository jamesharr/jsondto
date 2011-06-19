package org.grickle.jsondto.client;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Denote the field as optional. If the field is Optional, then the lack of a key
 * in a JSON object will cause this field to be left at its default value instead of
 * being set to null.
 */
@Target(ElementType.FIELD)
public @interface Optional
{

}
