package org.grickle.client;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Denote the field as optional. If the field is Optional, then the lack of JSON
 * data will cause this field to be left at its default value.
 */
@Target(ElementType.FIELD)
public @interface Optional
{

}
