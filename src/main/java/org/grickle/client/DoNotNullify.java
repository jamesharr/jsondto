package org.grickle.client;

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
public @interface DoNotNullify
{

}
