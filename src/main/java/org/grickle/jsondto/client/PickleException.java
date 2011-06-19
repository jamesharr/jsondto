package org.grickle.jsondto.client;

/**
 * Generic pickle exception with a message.
 */
public class PickleException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public PickleException(String msg)
    {
        super(msg);
    }
}
