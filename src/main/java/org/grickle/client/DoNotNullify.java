package org.grickle.client;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * This field will not be nullified if its corresponding JSON data cannot be found.
 * 
 * This is useful for safe-default cases for containers. IE
 * 
 * <pre>
 * class Foo
 * {
 *   String name;
 *   @DoNotNullify
 *   List<Foo> children = new LinkedList<foo>();
 * }
 * </pre>
 * 
 * When unpickling the data '{name:"first", children:null}', the children
 */
@Target(ElementType.FIELD)
public @interface DoNotNullify
{

}
